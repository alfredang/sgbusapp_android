# kotlinx.serialization: keep generated serializers for the LTA DataMall response models.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class **$$serializer { *; }
-keepclasseswithmembers class com.alfredang.sgbus.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.alfredang.sgbus.**$$serializer { *; }
-keepclassmembers class com.alfredang.sgbus.** {
    *** Companion;
}

# OkHttp optional platform integrations.
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
