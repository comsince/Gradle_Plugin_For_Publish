// Constants.groovy
package com.comsince.tools.build.gradle

class Constants {
    /** 此处配置插件强制检查的buildType */
    static final List<String> RELEASE_BUILD_TYPES = ['release']
    static final List<String> MASSEMBLE_BUILD_TYPES = ['debug', 'release']
    /** 此处配置运营商 */
    /** 此处配置插件强制高于gradle的版本(不包含) */
    static final String LATEST_SUPPORTED_GRADLE = '2.7'

    // gradle.properties for android libraray
    static final String LIB_PROGUARD = 'libProguard'
    static final String LIB_GROUP = 'libGroup'
    static final String LIB_ID = 'libId'
    static final String LIB_VERSION = 'libVersion'

    static final String DISTRIBUTE = 'distribute'
    static final String CLASSIFIER_JAR = 'jar'
    static final String CLASSIFIER_JAVADOC = 'javadoc'
    static final String CLASSIFIER_SOURCES = 'sources'
    static final String CLASSIFIER_AAR = 'aar'

    static final List<String> DEFAULT_DISTRIBUTIONS = [CLASSIFIER_JAR, CLASSIFIER_JAVADOC, CLASSIFIER_SOURCES, CLASSIFIER_AAR]

    // local.properties
    public static final String FILE_LOCAL_PROPERTIES = 'local.properties'

    static final String ARTIFACTORY_USER = 'artifactory.user'
    static final String ARTIFACTORY_PASSWORD = 'artifactory.password'

    static final String ARTIFACTORY_URL = "artifactory.url"
    static final String ARTIFACTORY_REPO_KEY = "artifactory.repo.key"

    // binary setting
    static final String BINTRAY_USER = 'bintray.user';
    static final String BINTRAY_API_KEY = 'bintray.apikey';

    static final String BINTRAY_REPO = "bintray.repo";
    static final String BINTRAY_NAME = "bintray.name";
    static final String BINTRAY_USER_ORG = "bintray.userOrg";
    static final String BINTRAY_VCS_URL = "bintray.vcsUrl";
    static final String BINTRAY_GPG_PASSWORD = "bintray.gpg.password";

    static final String OSS_USER = "oss_user";
    static final String OSS_PASSWORD = "oss_password";

    static final String GROUP_VERIFICATION = 'verification';


    // for generate maven pom info
    static final String DEVELOPER_ID = "developer_id";
    static final String DEVELOPER_NAME = "developer_name";
    static final String DEVELOPER_EMAIL = "developer_email";


}