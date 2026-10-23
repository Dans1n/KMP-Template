import org.gradle.kotlin.dsl.compileOnly
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.implementation
import org.gradle.kotlin.dsl.`kotlin-dsl`

plugins {
    `kotlin-dsl`
}

group = "com.company.template.convention.buildlogic"

dependencies {
    compileOnly(libs.plugins.kotlinMultiplatform.get().run { "$pluginId:$pluginId.gradle.plugin:${version.requiredVersion}" })
    compileOnly(libs.plugins.androidMultiplatformLibrary.get().run { "$pluginId:$pluginId.gradle.plugin:${version.requiredVersion}" })
    compileOnly(libs.plugins.composeCompiler.get().run { "$pluginId:$pluginId.gradle.plugin:${version.requiredVersion}" })
    compileOnly(libs.plugins.composeMultiplatform.get().run { "$pluginId:$pluginId.gradle.plugin:${version.requiredVersion}" })
    compileOnly(libs.plugins.androidApplication.get().run { "$pluginId:$pluginId.gradle.plugin:${version.requiredVersion}" })
    implementation(libs.plugins.kotlinSerialization.get().run { "$pluginId:$pluginId.gradle.plugin:${version.requiredVersion}" })
    implementation(libs.ksp.gradlePlugin)
    implementation(libs.androidx.room.gradle.plugin)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "com.company.template.convention.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidComposeApplication") {
            id = "com.company.template.convention.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("cmpApplication") {
            id = "com.company.template.convention.cmp.application"
            implementationClass = "CmpApplicationConventionPlugin"
        }
        register("kmpLibrary") {
            id = "com.company.template.convention.kmp.library"
            implementationClass = "KmpLibraryConventionPlugin"
        }
        register("cmpLibrary") {
            id = "com.company.template.convention.cmp.library"
            implementationClass = "CmpLibraryConventionPlugin"
        }
        register("cmpFeature") {
            id = "com.company.template.convention.cmp.feature"
            implementationClass = "CmpFeatureConventionPlugin"
        }
        register("room") {
            id = "com.company.template.convention.room"
            implementationClass = "RoomConventionPlugin"
        }
    }
}
