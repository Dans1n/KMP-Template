package com.company.template.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension
) {
    with(commonExtension) {
        buildFeatures.compose = true

        dependencies {
            add("debugImplementation", libs.findLibrary("compose-uiToolingPreview").get())
            add("debugImplementation", libs.findLibrary("compose-uiTooling").get())
        }
    }
}
