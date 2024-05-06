import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.vanniktech.maven.publish.AndroidSingleVariantLibrary

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
    id("signing")
    id("com.vanniktech.maven.publish")
}

android {
    namespace = "top.sunhy.component.jsbridge"
    compileSdk = 34

    defaultConfig {
        minSdk = 19
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

val pomGroupId = properties["GROUP"] as String
val pomArtifactId = properties["POM_ARTIFACT_ID"] as String
val pomVersion = properties["VERSION_NAME"] as String
val pomDesc = properties["POM_DESCRIPTION"] as String
val pomUrl = properties["POM_URL"] as String
val packaging = properties["POM_PACKAGING"] as String
val developerId = properties["POM_DEVELOPER_ID"] as String
val developerName = properties["POM_DEVELOPER_NAME"] as String
val developerEmail = properties["POM_DEVELOPER_EMAIL"] as String

afterEvaluate {
    if (packaging == "aar") {
        extensions.getByType(LibraryAndroidComponentsExtension::class).finalizeDsl {
            it.publishing {
                singleVariant("release") {
                    withSourcesJar()
                }
            }
        }
    } else {
        extensions.getByType<JavaPluginExtension>().apply {
            withJavadocJar()
            withSourcesJar()
        }
    }
    extensions.configure<PublishingExtension>("publishing") {
        publications {
            create<MavenPublication>("mavenJava") {
                this.groupId = pomGroupId
                this.artifactId = pomArtifactId
                this.version = pomVersion
                when (packaging) {
                    "aar" -> from(components["release"])
                    "jar" -> from(components["java"])
                }
                pom {
                    this.name.set(pomArtifactId)
                    this.description.set(pomDesc)
                    this.packaging = packaging
                    this.url.set(pomUrl)
                    developers {
                        developer {
                            this.id.set(developerId)
                            this.name.set(developerName)
                            this.email.set(developerEmail)
                        }
                    }
                }
            }
        }
        repositories {
            maven {
                name = "local"
                url = File(rootDir, "repos").toURI()
            }
        }
    }
}

mavenPublishing {
    configure(
        AndroidSingleVariantLibrary(
            variant = "release",
            sourcesJar = true,
            publishJavadocJar = true,
        )
    )
}


