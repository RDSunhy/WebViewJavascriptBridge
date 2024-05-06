pluginManagement {
    repositories {
        maven { url = uri("repos") }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url = uri("repos") }
        google()
        mavenCentral()
    }
}

rootProject.name = "WebViewJavascriptBridge"
include(":app")
include(":jsbridge")
