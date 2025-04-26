    pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT) // Hoặc cấu hình khác
    repositories {
        google()        // Đảm bảo có dòng này
        mavenCentral()
    }
}

rootProject.name = "Duan_appbanhang"
include(":app")
 