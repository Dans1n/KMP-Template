import com.bystrov.sto.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class CmpLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.bystrov.sto.convention.kmp.library")
                apply("org.jetbrains.kotlin.plugin.compose")
                apply("org.jetbrains.compose")
            }

            extensions.configure<KotlinMultiplatformExtension> {
                sourceSets.named("androidMain") {
                    resources.srcDir(layout.buildDirectory.dir("generated/assets/copyAndroidMainComposeResourcesToAndroidAssets"))
                    resources.srcDir(layout.buildDirectory.dir("generated/compose/resourceGenerator/preparedResources/commonMain"))
                }
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
