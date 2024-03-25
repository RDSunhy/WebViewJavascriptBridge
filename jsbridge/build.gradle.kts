plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

android {
    namespace = "top.sunhy.component.jsbridge"
    compileSdk = 34

    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
}

dependencies {
    compileOnly("com.google.code.gson:gson:2.10.1")
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "top.sunhy.component"
            artifactId = "jsbridge"
            version = "1.0.0"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}