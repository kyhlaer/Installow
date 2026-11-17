# Installow R8 / ProGuard kurallari
-keepattributes *Annotation*,Signature,JavascriptInterface

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-keep class com.installow.app.model.** { *; }
-keep class com.installow.app.util.SecurityCheckItem { *; }
