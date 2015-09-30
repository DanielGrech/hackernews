## Common ##

-useuniqueclassmembernames

-dontwarn javax.annotation.**
-dontwarn javax.inject.**
-dontwarn kotlin.**
-dontwarn sun.misc.Unsafe
-keepattributes *Annotation*
-keepattributes Signature
-keepclassmembers enum * { *; }

## Support Lib ##

-dontwarn android.support.v7.**
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }

## Stetho ##
-dontwarn com.facebook.stetho.**

## Test frameworks ##

-dontwarn org.mockito.**
-dontwarn org.junit.**
-dontwarn org.robolectric.**

## Retrofit (with OkHttp) ##
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**
-dontwarn okio.**
-dontwarn retrofit.**
-keep class retrofit.** { *; }

## RxJava ##
-dontwarn rx.**
-keep class rx.** { *; }

## Serialization ##

-keep class com.dgsd.hackernews.model.** { *; }
-keep class com.squareup.wire.** { *; }
-keep class hackernews.** { *; }
