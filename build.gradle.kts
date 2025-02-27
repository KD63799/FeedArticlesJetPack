// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        val nav_version = "2.5.3"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version")
    }
}

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    val hiltVersion = "2.55"
    id("com.google.dagger.hilt.android") version hiltVersion apply false
}