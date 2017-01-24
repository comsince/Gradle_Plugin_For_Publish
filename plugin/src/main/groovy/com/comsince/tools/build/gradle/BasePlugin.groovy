package com.comsince.tools.build.gradle
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

import static Constants.*

public abstract class BasePlugin implements Plugin<Project> {

    /**default logger for print log**/
    def logger
    /**property read from local.property*/
    def propLocal
    /**gradle project property*/
    def prop
    def gradleAfter
    /**which flavor you want publish*/
    def libaryFlavor

    @Override
    void apply(Project project) {
        logger = project.logger
        
        if (!Utils.gradleAfter(project.gradle.gradleVersion, Constants.LATEST_SUPPORTED_GRADLE) ) {
            def err = """\
            |* publish plugin requires gradle $Constants.LATEST_SUPPORTED_GRADLE(not included) later,
            |* file located here 
            |*     ${project.rootDir}${File.separator}gradle${File.separator}wrapper${File.separator}gradle-wrapper.properties
            |* change distributionUrl to example as below
            |*     distributionUrl=https\\://services.gradle.org/distributions/gradle-2.8-all.zip
            |* <a href="file:///${project.rootDir}${File.separator}gradle${File.separator}wrapper${File.separator}gradle-wrapper.properties">Change It Now</a>
            """.stripMargin()
            logger.error err
            throw new GradleException(err)
        }


        def plugin = project.plugins.findPlugin('android-library')
        def isLibrary = false
        if (plugin) {
            isLibrary = true
        }

        if (!plugin) {
            throw new GradleException(
                'You must apply the Android library plugin before using the publisher plugin')
        }
        
        applyUsefullMethods(project)

        if (isLibrary) {
            configLibraryProperties(project)
            configLibrary(project)
            //only library configure proguard file
            configProguardFiles(project)
        }

        project.afterEvaluate {
            evaluatePlugin(project)
        }
    }


    def configLibraryProperties(Project project){
        libaryFlavor = project.getProperties().get("flavor")
    }



    def evaluatePlugin(Project project) {
        // we can't access project.android here,
        // since android plugin is already evaluated
        // but variant.all task is good to use in the whole gradle lifecycle
        // logger.quiet ":afterEvaluate -> evaluatePlugin for ${project.name}"
        configProguard(project)

    }

    /**
     * config library
     * @param project
     * */
    protected abstract void configLibrary(Project project)


    def configProguard(Project project) {
        project.android.buildTypes.all { buildtype ->
            logger.quiet "* configureProguard buildtype name ${buildtype.name} ${buildtype.minifyEnabled}"
            /*if (buildtype.minifyEnabled) {
                project.configurations.proguard.each { File file ->
                    logger.quiet "* ${buildtype.name} proguardFile ${file.name}"
                    buildtype.proguardFile file
                }
            }*/

            if(project.plugins.findPlugin('android-library') != null &&buildtype.minifyEnabled && buildtype.name == "release" && !buildtype.consumerProguardFiles){
                throw new Exception("** If your library requires release build type config minifyEnable true, define the rules \n" +
                        "** inside ${project.projectDir}${File.separator}artifactory-proguard-rules.pro")
            }

        }
    }



    /**
     * define useful closure method
     * */
    def void applyUsefullMethods(Project project) {
        Properties localProp = new Properties()
        def localPropFinder = { dir ->
            File ret = new File("${dir}${File.separator}${FILE_LOCAL_PROPERTIES}")
            ret?.exists() ? ret : null
        }

        def rootLocalPropFile = localPropFinder(project.rootDir)
        if (rootLocalPropFile) {
            localProp.load(rootLocalPropFile.newDataInputStream())
        }

        def localPropFile = localPropFinder(project.projectDir)
        if (localPropFile) {
            Properties tmpProp = new Properties()
            tmpProp.load(localPropFile.newDataInputStream())
            localProp.putAll(tmpProp)
        }

        propLocal = { localProp?.get it }
        prop = { project.properties.get(it) }

        gradleAfter = { Utils.gradleAfter(project.gradle.gradleVersion, it) }


    }


    /***
     * config consumeProguard file before evaluatePlugin
     * nor consumerProguardFiles id invalid
     * */
    def configProguardFiles(Project project){
        String customizedProguardFileName = prop(LIB_PROGUARD)
        def findProguardFileInDir = { dir, fileName ->
            String proguardFilePath = "${dir}${File.separator}artifactory-proguard-rules.pro"
            if (fileName) {
                if(fileName.endsWith('.pro')) {
                    proguardFilePath = "${dir}${File.separator}${fileName}"
                } else {
                    proguardFilePath = "${dir}${File.separator}${fileName}.pro"
                }
            }
            File proFile = new File(proguardFilePath)
            proFile?.exists() ? proFile : null
        }
        def proguardFile = findProguardFileInDir(project.projectDir, customizedProguardFileName) ?:
                findProguardFileInDir(project.rootDir, customizedProguardFileName)
        project.android.buildTypes.all { buildtype ->
            logger.quiet "* befor evaluate buildtype name ${buildtype.name} minifyEnabled ${buildtype.minifyEnabled}"
            if (buildtype.name == 'release' && proguardFile?.exists()){
                logger.quiet "* ${buildtype.name} proguardFile consumerProguardFiles ${proguardFile.absolutePath}"
                buildtype.consumerProguardFiles  proguardFile.name
            }
        }
    }
}
