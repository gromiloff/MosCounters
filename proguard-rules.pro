#--------------------------------------------------------------------------------------------------#
#   general
#--------------------------------------------------------------------------------------------------#
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable

#--------------------------------------------------------------------------------------------------#
#   appsee
#--------------------------------------------------------------------------------------------------#
-keep class com.appsee.** { *; }
-dontwarn com.appsee.** 
#--------------------------------------------------------------------------------------------------#
#   crashlytics
#--------------------------------------------------------------------------------------------------#
-keep public class * extends java.lang.Exception
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**
#--------------------------------------------------------------------------------------------------#
#   facebook
#--------------------------------------------------------------------------------------------------#
-keep class com.facebook.** { *; }
#--------------------------------------------------------------------------------------------------#
#   firebase
#--------------------------------------------------------------------------------------------------#
-keep class com.google.firebase.auth.FirebaseAuth
#-keepclasseswithmembers class * {
#    public static FirebaseAuth getInstance ();
#}
#--------------------------------------------------------------------------------------------------#
#   Glide
#--------------------------------------------------------------------------------------------------#
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule
#--------------------------------------------------------------------------------------------------#
#   Google Play Services 4.3.23
#--------------------------------------------------------------------------------------------------#
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}
#--------------------------------------------------------------------------------------------------#
#   Gson
#--------------------------------------------------------------------------------------------------#
-dontwarn sun.misc.**
-keep class com.google.gson.examples.android.model.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
#--------------------------------------------------------------------------------------------------#
#   realm
#--------------------------------------------------------------------------------------------------#
-keep class io.realm.annotations.RealmModule
-keep @io.realm.annotations.RealmModule class *
-keep class io.realm.internal.Keep
-keep @io.realm.internal.Keep class *
-dontwarn javax.**
-dontwarn io.realm.**
#--------------------------------------------------------------------------------------------------#
#   Retrolambda
#--------------------------------------------------------------------------------------------------#
-dontwarn java.lang.invoke.*
#--------------------------------------------------------------------------------------------------#
#   Rx
#--------------------------------------------------------------------------------------------------#
-keep class rx.schedulers.Schedulers {
    public static <methods>;
}
-keep class rx.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class rx.schedulers.TestScheduler {
    public <methods>;
}
-keep class rx.schedulers.Schedulers {
    public static ** test();
}
#--------------------------------------------------------------------------------------------------#
#   retrofit2
#--------------------------------------------------------------------------------------------------#
-dontnote retrofit2.Platform
-dontwarn retrofit2.Platform$Java8
-keepattributes Exceptions
-keepclassmembernames,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
#--------------------------------------------------------------------------------------------------#
#   Android support lib
#--------------------------------------------------------------------------------------------------#
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }

-keep public class android.support.design.R$* { *; }

-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }
-keep public class android.support.v7.widget.RoundRectDrawable { *; }

-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}
#--------------------------------------------------------------------------------------------------#
#   Zoon feature classes
#--------------------------------------------------------------------------------------------------#
-keep public class ru.zoon.app.onboarding.*