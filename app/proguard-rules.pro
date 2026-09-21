
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

-dontwarn kotlin.**
-dontwarn kotlinx.**
-keep class kotlin.Metadata { *; }
-keep class kotlin.reflect.** { *; }
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepclassmembers class kotlin.Lazy { *; }
-keepclassmembers class **$Companion { *; }

-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}
-dontwarn kotlinx.coroutines.**

-dontwarn androidx.compose.**
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }
-keepclassmembers class ** {
    @androidx.compose.runtime.Composable <methods>;
}

-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-keepclassmembers class * extends androidx.room.RoomDatabase {
    <methods>;
}
-dontwarn androidx.room.paging.**

-keep class com.natsuki.qingjizhang.BillEntity { *; }

-keep class androidx.datastore.*.** { *; }
-keepclassmembers class * extends androidx.datastore.preferences.protobuf.GeneratedMessageLite { *; }
-dontwarn androidx.datastore.**

-keep class * extends androidx.work.Worker { *; }
-keep class * extends androidx.work.CoroutineWorker { *; }
-keep class * extends androidx.work.ListenableWorker { *; }
-keep class androidx.work.** { *; }
-dontwarn androidx.work.**

-keep class com.natsuki.qingjizhang.ReminderWorker { *; }

-keep class top.yukonga.miuix.** { *; }
-dontwarn top.yukonga.miuix.**

-keep class dev.chrisbanes.haze.** { *; }
-dontwarn dev.chrisbanes.haze.**

-keepattributes RuntimeVisibleAnnotations
-keepclassmembers class ** {
    *** Companion;
}
-keepclasseswithmembers class ** {
    kotlinx.serialization.KSerializer serializer(...);
}
-dontwarn kotlinx.serialization.**

-dontwarn androidx.**
-keep class androidx.core.app.NotificationCompat { *; }
-keep class androidx.core.app.NotificationManagerCompat { *; }

-keep class android.os.Vibrator { *; }
-keep class android.os.VibrationEffect { *; }

-keep class com.natsuki.qingjizhang.BillEntity { *; }
-keep class com.natsuki.qingjizhang.SavingsGoal { *; }
-keep class com.natsuki.qingjizhang.CategoryStore { *; }
-keep class com.natsuki.qingjizhang.ThemePreferences { *; }
-keep class com.natsuki.qingjizhang.BackupManager { *; }

-keep @androidx.annotation.Keep class * { *; }
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}