import androidx.room.gradle.RoomExtension
import com.bystrov.sto.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class RoomConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.google.devtools.ksp")
                apply("androidx.room")
            }

            extensions.configure<RoomExtension> {
                schemaDirectory("$projectDir/schemas")
            }

            dependencies {
                add("commonMainApi", libs.findLibrary("androidx-room-runtime").get())
                add("commonMainApi", libs.findLibrary("sqlite-bundled").get())
            }

            configurations.configureEach {
                if (name.startsWith("ksp") && !name.contains("Test")) {
                    this@with.dependencies.add(name, libs.findLibrary("androidx-room-compiler").get())
                }
            }
        }
    }
}
