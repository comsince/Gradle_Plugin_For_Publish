package com.comsince.tools.build.gradle

import com.comsince.tools.build.gradle.task.Aar
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Javadoc
import static Constants.*
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.artifacts.ExternalModuleDependency

class PublishConfigerPlugin extends BasePlugin {


    @Override
    protected void configLibrary(Project project) {
        String artifactoryUser = prop(ARTIFACTORY_USER) ?: propLocal(ARTIFACTORY_USER)
        String artifactoryPassword = prop(ARTIFACTORY_PASSWORD) ?: propLocal(ARTIFACTORY_PASSWORD)
        String usr = prop(BINTRAY_USER) ?: propLocal(BINTRAY_USER)
        String psw = prop(BINTRAY_API_KEY) ?: propLocal(BINTRAY_API_KEY)
        if (!usr || !psw) {
            def err = """\
                |*Support bintray Upload Set your bintray user name and apikey inside
                |*     local.properties
                |* Eg:
                |*     bintray.user=
                |*     bintray.key=
                |*
                |* PLEASE make SURE that the local.properties file is IGNORED inside your .gitignore file!
                |*
                """.stripMargin()

            logger.error err
        }

        def mass = [:]
        MASSEMBLE_BUILD_TYPES.each {
            logger.quiet "inside build type ${it.capitalize()}"
            mass."$it" = project.tasks.create("massemble${it.capitalize()}")
        }
        
        boolean resolved = false
        project.android.libraryVariants.matching {
            logger.quiet "inside build type ${it.buildType.name}"

            RELEASE_BUILD_TYPES.contains(it.buildType.name)
        }.all { variant ->
            if (resolved) {
                return
            }
            resolved = true

            List<String> distributions;
            def distributionsRule = prop(DISTRIBUTE) ?: propLocal(DISTRIBUTE)
            distributions = distributionsRule?.toString()?.tokenize(',')*.trim() ?: DEFAULT_DISTRIBUTIONS
            if (!distributions) {
                throw new GradleException(
                        "Distributions cannot be empty, or you are using $DISTRIBUTE in a wrong way")
            }

            boolean needDistributeJavadoc = distributions.contains(CLASSIFIER_JAVADOC)
            def javadocsTask, javadocsJarTask;
            if (needDistributeJavadoc) {
                javadocsTask = project.task("androidJavadocs", type: Javadoc, dependsOn: variant.javaCompile) {
                    failOnError false
                    source = variant.javaCompile.source
                    def androidJar = "${project.android.sdkDirectory}/platforms/${project.android.compileSdkVersion}/android.jar"
                    classpath = project.files(variant.javaCompile.classpath.files, androidJar)
                    options {
                        links "http://docs.oracle.com/javase/7/docs/api/"
                        linksOffline "http://d.android.com/reference", "${project.android.sdkDirectory}/docs/reference"
                    }
                    exclude '**/BuildConfig.java'
                    exclude '**/R.java'
                }


                javadocsJarTask = project.task("androidJavadocsJar", type: Jar, dependsOn: javadocsTask) {
                    classifier = 'javadoc'
                    from javadocsTask.destinationDir
                }
            }

            // Sources Jar Task
            boolean needDistributeSources = distributions.contains(CLASSIFIER_SOURCES)
            def sourcesJarTask;
            if (needDistributeSources) {
                sourcesJarTask = project.task("androidSourcesJar", type: Jar) {
                    classifier = 'sources'
                    from variant.javaCompile.source
                }
            }




            //
            // properties等extension操作必须在 maven publishing 之外进行, 否则报错
            // 
            String libGroup = prop(LIB_GROUP) ?: variant.applicationId
            String libId = prop(LIB_ID) ?: project.rootProject.getName()
            String libVersion = prop(LIB_VERSION) ?: variant.mergedFlavor.versionName

            String currentFlavor = variant.flavorName
            //for no flavor library project use mergedFlavor
            if(currentFlavor == ""){
                logger.quiet "merged flavor ${variant.mergedFlavor.name}"
                currentFlavor = variant.mergedFlavor.name
            }
            if(libaryFlavor != null){
                currentFlavor = libaryFlavor;
            }
            logger.quiet "[Artifactory Info] current setting flavor ${currentFlavor} "
            if(currentFlavor != "main"){
                libGroup = prop(LIB_GROUP + ".${currentFlavor}") ?: variant.applicationId
                libId = prop(LIB_ID + ".${currentFlavor}") ?: project.rootProject.getName()
                libVersion = prop(LIB_VERSION + ".${currentFlavor}") ?: variant.mergedFlavor.versionName
            }

            logger.quiet "* Prepare library artifactory for $libGroup:$libId:$libVersion"
            
            project.plugins.apply 'com.jfrog.artifactory'
            project.plugins.apply 'maven-publish'   // 深坑: apply此插件后要立刻配置 publishing, 不能读取extension, 否则报错
            project.plugins.apply "com.jfrog.bintray"

            //distribute jar task
            boolean needDistributeJar = distributions.contains(CLASSIFIER_JAR)
            def jarTask;
            if (needDistributeJar) {
                jarTask = project.task("androidReleaseJar", type: Jar, dependsOn: variant.assemble) {
                    from "$project.buildDir/intermediates/classes/release/"
                    //exclude '**/BuildConfig.class'
                    exclude '**/R.class'
                    exclude '**/R$*.class'
                }
            }

            boolean needDistributeAar = distributions.contains(CLASSIFIER_AAR)
            def aarTask
            if (needDistributeAar) {
                aarTask = project.task("androidReleaseAar", type: Aar, dependsOn: variant.assemble) {
                    from "${project.buildDir}${File.separator}outputs${File.separator}aar${File.separator}${project.getName()}-release.aar"
                }
            }

            // Create the pom configuration:
            def pomConfig = {

                // Add your description here
                name libGroup+":"+libId+":"+libVersion // TODO
                url propLocal(BINTRAY_VCS_URL)

                licenses {
                    license {
                        name "The Apache Software License, Version 2.0"
                        url "http://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
                developers {
                    developer {
                        id propLocal(DEVELOPER_ID)
                        name propLocal(DEVELOPER_NAME)
                        email propLocal(DEVELOPER_EMAIL)
                    }
                }

                scm {
                    connection propLocal(BINTRAY_VCS_URL)
                    developerConnection propLocal(BINTRAY_VCS_URL)
                    url propLocal(BINTRAY_VCS_URL)
                }
            }
            
            project.publishing {
                publications {
                    all(MavenPublication) {
                        groupId libGroup
                        version = libVersion
                        artifactId libId
                        pom.withXml {
                            asNode().appendNode('description', "use compile \"$groupId:$artifactId:$version\" to depend on this library.")
                            asNode().children().last() + pomConfig
                            def depNodes = asNode().appendNode('dependencies')
                            project.configurations.findAll { 
                                'compile' == it.name || 'releaseCompile' == it.name || "${currentFlavor}Compile" == it.name
                            }.collectMany { it.allDependencies }.toSet().findAll { 
                                it instanceof ProjectDependency || 
                                it instanceof ExternalModuleDependency
                            }.each { dep -> 
                                logger.quiet "* append dependency compile '${dep.group}:${dep.name}:${dep.version}' to pom"
                                def depNode = depNodes.appendNode('dependency')
                                depNode.appendNode('groupId', dep.getGroup())
                                depNode.appendNode('artifactId', dep.getName())
                                depNode.appendNode('version', dep.getVersion())
                                depNode.appendNode('scope', 'compile')
                            }
                        }
                        // Tell maven to prepare the generated "*.aar" file for publishing
                        /*if(currentFlavor == "main"){
                            artifact("${project.buildDir}${File.separator}outputs${File.separator}aar${File.separator}${project.getName()}-release.aar")
                        } else {
                            artifact("${project.buildDir}${File.separator}outputs${File.separator}aar${File.separator}${project.getName()}-${currentFlavor}-release.aar")
                        }*/

                        if (needDistributeAar) {
                            artifact aarTask
                        }
                        if (needDistributeJar) {
                            artifact jarTask
                        }
//                        artifact distJarTask
                        if (needDistributeSources) {
                            artifact sourcesJarTask
                        }
                        if (needDistributeJavadoc) {
                            artifact javadocsJarTask
                        }
                  }

                }
            }
            
            project.artifactory {
                String artUrl = propLocal(ARTIFACTORY_URL) ?: 'http://oss.jfrog.org/artifactory'
                String artRepoKey = propLocal(ARTIFACTORY_REPO_KEY) ?: 'libs-release-local'
                logger.quiet "current artifactory url ${artUrl} repo key ${artRepoKey}"
                contextUrl = artUrl
                publish {
                    repository {
                        // The Artifactory repository key to publish to
                        repoKey = artRepoKey

                        username = artifactoryUser
                        password = artifactoryPassword
                    }
                    defaults {
                        // Tell the Artifactory Plugin which artifacts should be published to Artifactory.
                        publications('all')
                        publishArtifacts = true

                        // Properties to be attached to the published artifacts.
                        properties = ['qa.level': 'basic', 'dev.team': 'core']
                        // Publish generated POM files to Artifactory (true by default)
                        publishPom = true
                    }
                }
            }


            project.bintray {
                user = propLocal(BINTRAY_USER)
                key = propLocal(BINTRAY_API_KEY)
                publications = ['all']
                pkg {
                    repo = propLocal(BINTRAY_REPO)
                    name = propLocal(BINTRAY_NAME)
                    userOrg = propLocal(BINTRAY_USER_ORG)
                    licenses = ['Apache-2.0']
                    vcsUrl = propLocal(BINTRAY_VCS_URL)
                    publicDownloadNumbers = true
                    publish = true
                    version {
                        name = libVersion
                        if(propLocal(BINTRAY_GPG_PASSWORD)){
                            gpg {
                                sign = true //Determines whether to GPG sign the files. The default is false
                                passphrase = propLocal(BINTRAY_GPG_PASSWORD)
                                //Optional. The passphrase for GPG signing'
                            }
                        }

                        if(propLocal(OSS_USER) && propLocal(OSS_PASSWORD)){
                            //Optional configuration for Maven Central sync of the version
                            mavenCentralSync {
                                sync = true //[Default: true] Determines whether to sync the version to Maven Central.
                                user = propLocal(OSS_USER) //OSS user token: mandatory
                                password = propLocal(OSS_PASSWORD) //OSS user password: mandatory
                                close = '1' //Optional property. By default the staging repository is closed and artifacts are released to Maven Central. You can optionally turn this behaviour off (by puting 0 as value) and release the version manually.
                            }
                        }

                    }
                }
            }

            def publishTask = project.getTasksByName('artifactoryPublish', false).find { true }
            if (publishTask) {
                publishTask.dependsOn /*variant.assemble*/ "assemble${(libaryFlavor?.capitalize())?:""}Release"
                mass."${variant.buildType.name}".dependsOn publishTask
            }
        }
    }

}
