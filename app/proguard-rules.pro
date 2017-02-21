# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in F:\Users\Administrator\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
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

#指定代码的压缩级别
-optimizationpasses 5
#混淆时是否要记录日志
-verbose
#忽略警告
-ignorewarning

-keep class com.bc.ywjphone.readily.database.dao.AccountBookDao{
    public void onCreate(android.database.sqlite.SQLiteDatabase);
}
-keep class com.bc.ywjphone.readily.database.dao.CategoryDao{
  public void onCreate(android.database.sqlite.SQLiteDatabase);
}
-keep class com.bc.ywjphone.readily.database.dao.CreateViewDao{
    *;
}
-keep class com.bc.ywjphone.readily.database.dao.PayoutDao{
  public void onCreate(android.database.sqlite.SQLiteDatabase);
}
-keep class com.bc.ywjphone.readily.database.dao.UserDao{
  public void onCreate(android.database.sqlite.SQLiteDatabase);
}
-keep class org.achartengine.**{*;}
#移除log assumenosideeffects表示忽略它的作用
-assumenosideeffects class android.util.Log{
	public static int v(java.lang.String,java.lang.String);
	public static int d(java.lang.String,java.lang.String);
	public static int i(java.lang.String,java.lang.String);
}