import com.company.template.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class CmpLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.company.template.convention.kmp.library")
                apply("org.jetbrains.kotlin.plugin.compose")
                apply("org.jetbrains.compose")
            }

            val composeAssetsDir = layout.buildDirectory.dir("generated/assets/compose/resources/androidMain")

            val copyComposeResourcesTask = tasks.matching { it.name.endsWith("ComposeResourcesToAndroidAssets") }

            copyComposeResourcesTask.configureEach {
                val getOutputDir = javaClass.methods.firstOrNull { m -> m.name == "getOutputDirectory" }
                val outputDirProp = getOutputDir?.invoke(this) as? DirectoryProperty
                if (outputDirProp != null && !outputDirProp.isPresent) {
                    outputDirProp.set(composeAssetsDir)
                }
            }

            extensions.configure<KotlinMultiplatformExtension> {
                sourceSets.named("androidMain") {
                    resources.srcDir(composeAssetsDir)
                }
            }

            tasks.matching {
                it.name == "processAndroidMainJavaRes" ||
                it.name == "preAndroidMainBuild" ||
                it.name == "compileAndroidMain"
            }.configureEach {
                dependsOn(copyComposeResourcesTask)
            }

            dependencies {
                add("commonMainImplementation", libs.findLibrary("compose-ui").get())
                add("commonMainImplementation", libs.findLibrary("compose-foundation").get())
                add("commonMainImplementation", libs.findLibrary("compose-material3").get())
                add("commonMainImplementation", libs.findLibrary("compose-components-resources").get())
                add("commonMainImplementation", libs.findLibrary("compose-uiToolingPreview").get())
                add("androidMainImplementation", libs.findLibrary("compose-uiTooling").get())
            }
        }
    }
}
