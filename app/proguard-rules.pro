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

# Keep Compose classes
-keep class androidx.compose.** { *; }
-keep class androidx.compose.ui.** { *; }
-keep class androidx.compose.material3.** { *; }

# Keep Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Keep Android lifecycle components
-keep class androidx.lifecycle.** { *; }

# Keep audio-related classes
-keep class android.media.** { *; }
-keep class android.media.AudioManager { *; }
-keep class android.media.MediaPlayer { *; }

# Keep sensor-related classes
-keep class android.hardware.SensorManager { *; }
-keep class android.hardware.Sensor { *; }
-keep class android.hardware.SensorEvent { *; }

# Keep application classes
-keep class com.bikebell.app.** { *; }
-keep class com.example.bikebellandroid.** { *; } 