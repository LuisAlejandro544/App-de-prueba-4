# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Preserve Line Numbers and Sources for meaningful crash logs (optional but highly recommended)
-keepattributes SourceFile,LineNumberTable,Signature,InnerClasses,EnclosingMethod,RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations,AnnotationDefault

# =========================================================================
# Application Specific Rules
# =========================================================================

# Prevent obfuscation of all data models (crucial for Room DB & JSON mapping)
-keep class com.example.data.model.** { *; }

# Keep database-related helper/database classes intact
-keep class com.example.data.database.** { *; }

# Also keep services or actions in service if they rely on reflection or runtime inspection
-keep class com.example.service.** { *; }

# =========================================================================
# Jetpack Room Database Rules
# =========================================================================
-keep class * extends androidx.room.RoomDatabase
-keep class * implements androidx.room.RoomOpenHelper
-dontwarn androidx.room.paging.**

# =========================================================================
# Moshi JSON Converter Rules
# =========================================================================
# Keep fields annotated with @Json to prevent renaming/stripping
-keepclassmembers class * {
    @com.squareup.moshi.Json <fields>;
}

# Keep the generated Moshi adapters
-keep class *JsonAdapter { *; }
-keep class * implements com.squareup.moshi.JsonAdapter { *; }

# Keep annotations on Moshi classes
-keep @com.squareup.moshi.JsonClass class * { *; }

# =========================================================================
# Retrofit & OkHttp Rules
# =========================================================================
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepclasseswithmembers interface * {
    @retrofit2.http.** <methods>;
}

# OkHttp rules
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**

