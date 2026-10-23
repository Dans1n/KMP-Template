plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
}

tasks.register("createFeatureModule") {
    group = "generation"
    description = "Generates a new feature module in the feature/ directory"

    doLast {
        val nameInput = project.findProperty("name")?.toString()
            if (nameInput.isNullOrBlank()) {
                throw IllegalArgumentException("Feature module name is required. Pass it via -Pname=<name> (e.g. ./gradlew createFeatureModule -Pname=profile)")
            }

        val rawName = nameInput.trim().lowercase()
        val pascalCaseName = rawName.split("-", "_").joinToString("") { part ->
            part.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
        val camelCaseName = pascalCaseName.replaceFirstChar { it.lowercase() }

        val rootDir = project.rootDir
        val featureDir = File(rootDir, "feature/$rawName")
        if (featureDir.exists()) {
            println("⚠️ Feature module ':feature:$rawName' already exists at ${featureDir.path}")
            return@doLast
        }

        val domainDir = File(rootDir, "core/domain/src/commonMain/kotlin")
        var curr = domainDir
        val pathParts = mutableListOf<String>()
        while (curr.exists()) {
            val children = curr.listFiles { f -> f.isDirectory } ?: break
            if (children.size == 1) {
                val child = children[0]
                if (child.name == "core") break
                pathParts.add(child.name)
                curr = child
            } else {
                break
            }
        }
        val rootPackage = if (pathParts.isNotEmpty()) pathParts.joinToString(".") else "com.bystrov.sto"
        val featurePackage = "$rootPackage.feature.$rawName"
        val packagePath = featurePackage.replace('.', '/')

        val kotlinSrcDir = File(featureDir, "src/commonMain/kotlin/$packagePath")
        kotlinSrcDir.mkdirs()

        // 1. Create build.gradle.kts
        File(featureDir, "build.gradle.kts").writeText(
            """
            plugins {
                alias(libs.plugins.convention.cmp.feature)
            }

            kotlin {
                sourceSets {
                    commonMain.dependencies {
                        implementation(projects.core.domain)
                        implementation(projects.core.designsystem)
                        implementation(projects.core.presentation)
                    }
                }
            }
            """.trimIndent() + "\n"
        )

        // 2. Create Screen.kt
        File(kotlinSrcDir, "${pascalCaseName}Screen.kt").writeText(
            """
            package $featurePackage

            import androidx.compose.foundation.layout.Box
            import androidx.compose.foundation.layout.fillMaxSize
            import androidx.compose.material3.Text
            import androidx.compose.runtime.Composable
            import androidx.compose.runtime.getValue
            import androidx.compose.ui.Alignment
            import androidx.compose.ui.Modifier
            import androidx.lifecycle.compose.collectAsStateWithLifecycle
            import org.koin.compose.viewmodel.koinViewModel

            @Composable
            fun ${pascalCaseName}Screen(
                modifier: Modifier = Modifier,
                viewModel: ${pascalCaseName}ViewModel = koinViewModel()
            ) {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                Box(
                    modifier = modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "$pascalCaseName Screen: ${'$'}{uiState.title}")
                }
            }
            """.trimIndent() + "\n"
        )

        // 3. Create ViewModel.kt
        File(kotlinSrcDir, "${pascalCaseName}ViewModel.kt").writeText(
            """
            package $featurePackage

            import androidx.lifecycle.ViewModel
            import kotlinx.coroutines.flow.MutableStateFlow
            import kotlinx.coroutines.flow.StateFlow
            import kotlinx.coroutines.flow.asStateFlow

            data class ${pascalCaseName}UiState(
                val title: String = "$pascalCaseName Feature"
            )

            class ${pascalCaseName}ViewModel : ViewModel() {
                private val _uiState = MutableStateFlow(${pascalCaseName}UiState())
                val uiState: StateFlow<${pascalCaseName}UiState> = _uiState.asStateFlow()
            }
            """.trimIndent() + "\n"
        )

        // 4. Create Module.kt
        File(kotlinSrcDir, "${pascalCaseName}Module.kt").writeText(
            """
            package $featurePackage

            import org.koin.core.module.dsl.viewModelOf
            import org.koin.dsl.module

            val ${camelCaseName}Module = module {
                viewModelOf(::${pascalCaseName}ViewModel)
            }
            """.trimIndent() + "\n"
        )

        // 5. Register in settings.gradle.kts
        val settingsFile = File(rootDir, "settings.gradle.kts")
        val includeStatement = "include(\":feature:$rawName\")"
        val settingsContent = settingsFile.readText()
        if (!settingsContent.contains(includeStatement)) {
            settingsFile.appendText("\n$includeStatement\n")
        }

        println("✅ Successfully created feature module ':feature:$rawName'!")
        println("   Files generated at: ${featureDir.path}")
        println("   Package: $featurePackage")
    }
}
