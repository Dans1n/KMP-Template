import com.bystrov.sto.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class CmpFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.bystrov.sto.convention.cmp.library")
            }

            dependencies {
                add("commonMainImplementation", libs.findLibrary("compose-runtime").get())
                add("commonMainImplementation", libs.findLibrary("androidx-lifecycle-viewmodelCompose").get())
                add("commonMainImplementation", libs.findLibrary("androidx-lifecycle-runtimeCompose").get())

                add("androidMainImplementation", libs.findLibrary("navigation3-ui").get())

                add("commonMainImplementation", libs.findLibrary("koin-compose").get())
                add("commonMainImplementation", libs.findLibrary("koin-compose-viewmodel").get())
                add("androidMainImplementation", libs.findLibrary("koin-android").get())
            }
        }
    }
}
