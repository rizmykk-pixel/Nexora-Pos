import org.gradle.api.initialization.resolve.RepositoriesMode

pluginManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        google()
    }
}

rootProject.name = "NexovaPos"
include(":app")

// Core Modules
include(":core:common")
include(":core:model")
include(":core:ui")
include(":core:design")
include(":core:money")
include(":core:quantity")
include(":core:dates")
include(":core:network")
include(":core:database")
include(":core:sync")
include(":core:offline")
include(":core:hardware")
include(":core:designsystem")
include(":core:domain")
include(":core:data")

// Feature Modules
include(":feature:auth")
include(":feature:home")
include(":feature:pos")
include(":feature:catalog")
include(":feature:reports")
