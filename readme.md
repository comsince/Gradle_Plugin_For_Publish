# 目录
    
* [一 插件配置](#library_setting_init)
    * [1.1 引用插件](#build_setting)
    * [1.2 JCenter支持](#jcenter_setting)
      * [1.2.1 local.properties文件中配置jCenter基本信息](#jcenter_account_setting)
      * [1.2.2 gradle.properties 文件中配置发布信息](#jcenter_group_setting)
* [二 Gradle参数说明](#gradle_setting_describe)

* [三 示例工程](#demo) 
* [四 注意事项](#notifications)

# 一 插件配置<a name="library_setting_init"/>

## 1.1 引用插件<a name="build_setting"/>

````
buildscript {
    repositories {
        jcenter()
    }
    
    dependencies {
        classpath "com.android.tools.build:gradle:1.3.1"
        classpath "com.comsince.github:publish-plugin:1.0.0"
        classpath "com.jfrog.bintray.gradle:gradle-bintray-plugin:1.4"
    }
}

apply plugin: 'com.android.library'

// 在apply android gradle插件之后apply本插件
apply plugin: 'com.comsince.publisher'

````


## 1.2 JCenter支持<a name="jcenter_setting"/>
 
引用过本插件的Library工程, 可以打包aar,javadoc,jar 发布到 Jcenter
可以在 Android Studio 的gradle任务图形化界面中双击 publishing -> *bintrayUpload*

会失败, 因为没有配置 帐号, 密码

### 1.2.1 local.properties文件中配置jCenter基本信息<a name="jcenter_account_setting"/>

+ `bintray.user=comsince`  # 你的bintray 用户名
+ `bintray.apikey=your api key` # 你的bintray api key
+ `bintray.repo=` #  你创建的bintray repo名称
+ `bintray.name=` 你所要发布的库的名称
+ `bintray.userOrg=`  新版的jcenter,需要提供你创建的组织名称，不然无法上传成功 


**NOTE:** 有关JCenter申请可[参考](register_jcenter.md)

### 1.2.2 gradle.properties 文件中配置发布信息<a name="jcenter_group_setting"/>

Library开发者发布的时候, 可以在 工程里面修改以上数据, 也可以通过 `gradle.properties` 更改

+ `libGroup=com.comsince.github`  
+ `libId=publish-plugin`
+ `libVersion=1.0.0`

*默认的 Group 是工程的 包名*
*默认的 artifactId 是library工程的 项目名*
*默认的 version 是library工程的 versionName*  


此时通过 Android Studio 图形面板, 或者gradle命令

````
./gradlew bintrayUpload
````


# 二 Gradle参数说明<a name="gradle_setting_describe"/>

**gradle.properties**: build.gradle同级目录下

+ `libGroup=com.comsince.github`  # 建议不配置, 默认使用工程包名

    配置 aar 的group, 如果不配置, 默认使用 Library工程的 包名
    
+ `libId=publisher` 

    配置 aar 以及 proguard 的 id, 如果不配置, 默认使用 Library工程的 项目名

+ `libVersion=1.1` # 建议不配置, 默认使用工程版本名VersionName

    配置 aar的 版本号, 如果不配置, 默认使用 Library工程的 versionName  
    
**local.properties**: build.gradle同级目录下, 

local.properties文件应该在.gitignore中屏蔽掉, 此文件仅用于本地开发

+ `bintray.user=comsince`  # 你的bintray 用户名
+ `bintray.apikey=your api key` # 你的bintray api key
+ `bintray.repo=` #  你创建的bintray repo名称
+ `bintray.name=` 你所要发布的库的名称
+ `bintray.userOrg=`  新版的jcenter,需要提供你创建的组织名称，不然无法上传成功 
+  `bintray.vcsUrl=`  你开源的项目地址


## 三 示例工程<a name="demo"/>
    
* [Demo](PublisherDemo)

## 注意事项<a name="notifications"/>

* 对于超大型应用, lintVital时OOM, 请按如下配置  
    gradle.properties 中开启   
    
    ````groovy
    org.gradle.jvmargs=-Xmx2048m -XX:MaxPermSize=512m -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8
    org.gradle.parallel=true   
    ````
    
* 对于引用了很多模块的超大型应用, dex时OOM, 请按如下配置

    build.gradle中
    
    ````groovy
    android {
        dexOptions {
            jumboMode true
            incremental true
            preDexLibraries false
            javaMaxHeapSize "4g"
        }
    }
    ```` 
    
## [更新日志](CHANGELOG.md)

# 参考文档
* [如何发布library](https://inthecheesefactory.com/blog/how-to-upload-library-to-jcenter-maven-central-as-dependency/en)