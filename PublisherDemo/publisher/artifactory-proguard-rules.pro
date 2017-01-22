# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# 防止内部类被混淆，无法访问,务必加上，不然外部引用无法使用内部类
    -dontwarn com.meizu.cloud.pushsdk.**
    -keep class com.meizu.cloud.pushsdk.**{*;}

    -dontwarn com.meizu.cloud.pushinternal.**
    -keep class com.meizu.cloud.pushinternal.**{*;}


