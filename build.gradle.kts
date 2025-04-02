// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id ("com.android.application" )version "8.9.1" apply false
    id ("com.android.library")version "8.9.1" apply false
    id ("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id ("com.google.gms.google-services") version "4.4.2" apply false
    id("androidx.compose.compiler") version "1.5.15" apply false


}
buildscript {
    dependencies {
        // Add this line if not already added
        classpath(libs.gradle)
    }
}