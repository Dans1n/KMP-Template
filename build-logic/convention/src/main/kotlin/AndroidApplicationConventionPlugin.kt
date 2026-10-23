import com.android.build.api.dsl.ApplicationExtension
import com.bystrov.sto.convention.configureKotlinAndroid
import com.bystrov.sto.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
            }

            extensions.configure<ApplicationExtension> {
                namespace = "com.bystrov.sto"

                defaultConfig {
                    applicationId = "com.bystrov.sto"
                    targetSdk = libs.findVersion("android-targetSdk").get().toString().toInt()
                    versionCode = 1
                    versionName = "1.0"
                }

                packaging {
                    resources {
                        excludes += "/META-INF/{AL2.0,LGPL2.1}"
                    }
                }

                buildTypes {
                    getByName("release") {
                        isMinifyEnabled = false
                    }
                }

                configureKotlinAndroid(this)
            }
        }
    }
}
