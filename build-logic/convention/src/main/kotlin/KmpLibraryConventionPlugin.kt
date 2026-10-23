import com.bystrov.sto.convention.configureKotlinMultiplatform
import com.bystrov.sto.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class KmpLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.kotlin.multiplatform.library")
                apply("org.jetbrains.kotlin.multiplatform")
                apply("org.jetbrains.kotlin.plugin.serialization")
            }

            configureKotlinMultiplatform()

            dependencies {
                add("commonMainImplementation", libs.findLibrary("kotlinx-serialization-json").get())
                add("commonTestImplementation", libs.findLibrary("kotlin-test").get())
            }
        }
    }
}
