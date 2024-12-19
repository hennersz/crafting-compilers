rootProject.name = "klox"
// settings.gradle.kts
pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }

    // Highly recommended, see https://docs.gradle.org/current/userguide/declaring_repositories.html#sub:centralized-repository-declaration
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}