# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# --------- Auto-generated suppress warnings ----------
-dontwarn com.google.android.gms.ads.identifier.AdvertisingIdClient$Info
-dontwarn com.google.android.gms.ads.identifier.AdvertisingIdClient
-dontwarn com.google.android.gms.location.FusedLocationProviderClient
-dontwarn com.google.android.gms.location.LocationServices
-dontwarn kotlinx.android.parcel.Parcelize
-dontwarn kotlinx.parcelize.Parcelize
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE

# --------- Preserve generic type signatures and annotations ----------
-keepattributes Signature
-keepattributes *Annotation*

# --------- Yuno SDK ----------
-keep class com.yuno.** { *; }
-dontwarn com.yuno.**

# --------- Gson — R8 full mode compatibility ----------
-keep class com.google.gson.** { *; }
-keep,allowobfuscation,allowshrinking,allowoptimization class * extends com.google.gson.reflect.TypeToken
-dontwarn com.google.gson.**

# --------- Retrofit — R8 full mode compatibility ----------
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
-dontwarn retrofit2.**

# --------- OkHttp + Okio — Suppress warnings ----------
-dontwarn okhttp3.**
-dontwarn okio.**