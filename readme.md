# 目录
    
* [一 插件配置](#library_setting_init)
    * [1.1 引用插件](#build_setting)
    * [1.2 JCenter支持](#jcenter_setting)
      * [1.2.1 local.properties文件中配置jCenter基本信息](#jcenter_account_setting)
      * [1.2.2 gradle.properties 文件中配置发布信息](#jcenter_group_setting)
* [二 Gradle参数说明](#gradle_setting_describe)

* [三 示例工程](#demo) 
* [四 注意事项](#notifications)
* [五 同步更新到MavenCentral](#sync_maven_central)

# 一 插件配置<a name="library_setting_init"/>

## 1.1 引用插件<a name="build_setting"/>

````
buildscript {
    repositories {
        jcenter()
    }
    
    dependencies {
        classpath "com.android.tools.build:gradle:1.3.1"
        classpath "com.github.comsince:publish-plugin:1.+"
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
+ `bintray.vcsUrl=`  你开源的项目地址
+ `bintray.gpg.password` [详情参看](https://github.com/bintray/gradle-bintray-plugin#buildgradle)
+ `oss_user`
+ `oss_password`  如果要同步到MavenCentral,需要到这里[申请](https://issues.sonatype.org/secure/Dashboard.jspa)账户


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

# 四 参考文档
* [同步发布到Maven Central的注意事项](http://central.sonatype.org/pages/requirements.html)
* [Publish AAR to jCenter and Maven Central](https://gist.github.com/lopspower/6f62fe1492726d848d6d)
* [BinTray Plugin](https://github.com/bintray/gradle-bintray-plugin)

# 五 同步更新到MavenCentral
**NOTE:**插件支持自动同步到MavenCentral，需要你按照如下步骤操作
  
* [sonatype.org](https://issues.sonatype.org/secure/Dashboard.jspa)注册账户
* 新建一个MavenCentral的issue,大概要等待一周的审核时间
  Create a Sonatype account for Maven Central,注意填写如下信息
  * ```Project```: Community Support - Open Source Project Repository Hosting
  * ```Issue Type```: New Project
  * ```Summary```: Your library's name in summary, for example, The Cheese Library
  * ```Group Id```: Put the root GROUP_ID, for example, com.inthecheeselibrary . After you got an approval, every single library starts with com.inthecheeselibrary will be allowed to upload to repository, for example, com.inthecheeselibrary.somelib
  * ```Project URL```: Put a URL of any library you plan to distribute, for example, https://github.com/nuuneoi/FBLikeAndroid
  * ```SCM URL```: URL of Source Control, for example, https://github.com/nuuneoi/FBLikeAndroid.git
  
* GPG 公钥和私钥申请配置
   * [GPG入门教程](http://www.ruanyifeng.com/blog/2013/07/gpg.html)
   * [GPG 使用说明](http://central.sonatype.org/pages/working-with-pgp-signatures.html)
* [Jcenter MavenCentral同步配置](https://inthecheesefactory.com/blog/how-to-upload-library-to-jcenter-maven-central-as-dependency/en)
* local.properties 配置相关的信息如下:
    + `bintray.gpg.password` [详情参看](https://github.com/bintray/gradle-bintray-plugin#buildgradle)
    + `oss_user`
    + `oss_password`  如果要同步到MavenCentral,需要到这里[申请](https://issues.sonatype.org/secure/Dashboard.jspa)账户
    
    
* local.properties 配置模板

```
#  你的bintray 用户名
bintray.user=
#  你的bintray api key          
bintray.apikey=        
#  你创建的bintray repo名称
bintray.repo=          
#  你所要发布的库的名称
bintray.name=  
# 你的开源项目地址
bintray.vcsUrl=
#  新版的jcenter,需要提供你创建的组织名称，不然无法上传成功
bintray.userOrg=       
#  [详情参看](https://github.com/bintray/gradle-bintray-plugin#buildgradle)
bintray.gpg.password = 
#  oss账户名
oss_user=   
#  如果要同步到MavenCentral,需要到这里[申请](https://issues.sonatype.org/secure/Dashboard.jspa)账户
oss_password=
          
# 同步到MavenCentral必须配置
developer_id = 
developer_name=
developer_email=
```