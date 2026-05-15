-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class cz.krokviak.kalky.**$$serializer { *; }
-keepclassmembers class cz.krokviak.kalky.** {
    *** Companion;
}
-keepclasseswithmembers class cz.krokviak.kalky.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep class io.ktor.** { *; }
-keep class kotlinx.coroutines.** { *; }
-dontwarn io.ktor.**
-dontwarn org.slf4j.**

-keep class app.cash.sqldelight.** { *; }
-keep class cz.krokviak.kalky.db.** { *; }
-keep class cz.krokviak.kalky.Food_items { *; }
-keep class cz.krokviak.kalky.Food_items$* { *; }
-keep class cz.krokviak.kalky.Personal_info { *; }
-keep class cz.krokviak.kalky.Personal_info$* { *; }
-keep class cz.krokviak.kalky.Nutrient_settings { *; }
-keep class cz.krokviak.kalky.Nutrient_settings$* { *; }

-keep class org.koin.** { *; }
-keep class * extends org.koin.core.module.Module
-keepclassmembers class ** {
    public <init>();
}

-keepattributes Signature, *Annotation*
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-keep class com.google.android.play.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**
-dontwarn com.google.android.play.**

-keep class com.google.firebase.crashlytics.** { *; }
-keep class com.google.firebase.components.** { *; }

-keep class androidx.camera.** { *; }
-dontwarn androidx.camera.**

-keep class com.google.mlkit.** { *; }
-keep class com.google.android.gms.internal.mlkit_vision_barcode.** { *; }
-dontwarn com.google.mlkit.**

-keep class com.patrykandpatrick.vico.** { *; }
-dontwarn com.patrykandpatrick.vico.**
-keep class coil3.** { *; }
-dontwarn coil3.**

-keep class cz.krokviak.kalky.barcode.data.** { *; }
-keep class cz.krokviak.kalky.common.entities.** { *; }

-keep class kotlin.Metadata { *; }
-keep class kotlin.reflect.** { *; }
-dontwarn kotlin.reflect.jvm.internal.**

-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.platform.** { *; }
