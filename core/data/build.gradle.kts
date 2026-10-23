plugins {
    alias(libs.plugins.convention.kmp.library)
    alias(libs.plugins.convention.room)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.domain)

            implementation(libs.kotlin.stdlib)

            implementation(libs.koin.core)
            implementation(libs.touchlab.kermit)
            implementation(libs.bundles.ktor.common)

            implementation(libs.datastore)
            implementation(libs.datastore.preferences)
        }
    }
}
