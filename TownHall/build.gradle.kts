// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin) apply false
    alias(libs.plugins.google.gms.google.services) apply false
}

extra.apply {
    set("appCompatVersion", "1.5.1")
    set("constraintLayoutVersion", "2.1.4")
    set("coreTestingVersion", "2.1.0")
    set("lifecycleVersion", "2.3.1")
    set("materialVersion", "1.7.0")
    set("roomVersion", "2.4.3")
    // testing
    set("junitVersion", "4.13.2")
    set("espressoVersion", "3.5.0")
    set("androidxJunitVersion", "1.1.2")
}

