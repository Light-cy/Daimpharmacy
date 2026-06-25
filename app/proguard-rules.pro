# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# --- Database & Models (Room, Moshi, Firebase) ---
-keep class com.example.data.** { *; }

# --- Firebase Rules ---
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Keep Firebase classes from being obfuscated or removed
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.tasks.** { *; }

# Keep Firestore mapping models
-keepclassmembers class * {
    @com.google.firebase.firestore.PropertyName <fields>;
    @com.google.firebase.firestore.PropertyName <methods>;
}

# --- Room Database Rules ---
-keep class * extends androidx.room.RoomDatabase
-keep class * extends androidx.room.RoomDatabase$Callback
-dontwarn androidx.room.RoomDatabase

# --- Moshi Rules ---
-keep class com.squareup.moshi.** { *; }
-keep class * extends com.squareup.moshi.JsonAdapter
-keep @com.squareup.moshi.JsonClass class * { *; }
-keep class *JsonAdapter { *; }
-dontwarn com.squareup.moshi.**

# --- Retrofit Rules ---
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepclassmembers class * {
    @retrofit2.http.** <methods>;
}
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

# --- Coil Rules ---
-keep class coil.** { *; }
-dontwarn coil.**
