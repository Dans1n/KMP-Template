#!/usr/bin/env python3
import os
import sys
import argparse


def find_root_package(project_root: str) -> str:
    domain_dir = os.path.join(project_root, "core", "domain", "src", "commonMain", "kotlin")
    if not os.path.exists(domain_dir):
        return "com.bystrov.sto"

    curr = domain_dir
    path_parts = []
    while os.path.exists(curr):
        children = [d for d in os.listdir(curr) if os.path.isdir(os.path.join(curr, d))]
        if len(children) == 1:
            child = children[0]
            if child == "core":
                break
            path_parts.append(child)
            curr = os.path.join(curr, child)
        else:
            break

    return ".".join(path_parts) if path_parts else "com.bystrov.sto"


def to_pascal_case(text: str) -> str:
    parts = text.replace("-", "_").split("_")
    return "".join(p.capitalize() for p in parts if p)


def to_camel_case(text: str) -> str:
    pascal = to_pascal_case(text)
    return pascal[0].lower() + pascal[1:] if pascal else ""


def main():
    parser = argparse.ArgumentParser(description="Create a new feature module in the feature/ directory.")
    parser.add_argument("-n", "--name", type=str, help="Feature module name (e.g. profile, user-details)")

    args = parser.parse_args()

    raw_name = args.name
    if not raw_name:
        raw_name = input("Enter feature module name (e.g. profile): ").strip()

    if not raw_name:
        print("❌ Error: Feature module name is required.")
        sys.exit(1)

    raw_name = raw_name.lower().strip()
    pascal_name = to_pascal_case(raw_name)
    camel_name = to_camel_case(raw_name)

    project_root = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
    feature_dir = os.path.join(project_root, "feature", raw_name)

    if os.path.exists(feature_dir):
        print(f"⚠️ Feature module ':feature:{raw_name}' already exists at {feature_dir}")
        sys.exit(0)

    root_package = find_root_package(project_root)
    feature_package = f"{root_package}.feature.{raw_name}"
    package_path = feature_package.replace(".", os.sep)

    kotlin_src_dir = os.path.join(feature_dir, "src", "commonMain", "kotlin", package_path)
    os.makedirs(kotlin_src_dir, exist_ok=True)

    # 1. build.gradle.kts
    build_gradle_path = os.path.join(feature_dir, "build.gradle.kts")
    with open(build_gradle_path, "w", encoding="utf-8") as f:
        f.write(
            f"""plugins {{
    alias(libs.plugins.convention.cmp.feature)
}}

kotlin {{
    sourceSets {{
        commonMain.dependencies {{
            implementation(projects.core.domain)
            implementation(projects.core.designsystem)
            implementation(projects.core.presentation)
        }}
    }}
}}
"""
        )

    # 2. Screen.kt
    screen_path = os.path.join(kotlin_src_dir, f"{pascal_name}Screen.kt")
    with open(screen_path, "w", encoding="utf-8") as f:
        f.write(
            f"""package {feature_package}

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
fun {pascal_name}Screen(
    modifier: Modifier = Modifier,
    viewModel: {pascal_name}ViewModel = koinViewModel()
) {{
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {{
        Text(text = "{pascal_name} Screen: ${{uiState.title}}")
    }}
}}
"""
        )

    # 3. ViewModel.kt
    view_model_path = os.path.join(kotlin_src_dir, f"{pascal_name}ViewModel.kt")
    with open(view_model_path, "w", encoding="utf-8") as f:
        f.write(
            f"""package {feature_package}

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class {pascal_name}UiState(
    val title: String = "{pascal_name} Feature"
)

class {pascal_name}ViewModel : ViewModel() {{
    private val _uiState = MutableStateFlow({pascal_name}UiState())
    val uiState: StateFlow<{pascal_name}UiState> = _uiState.asStateFlow()
}}
"""
        )

    # 4. Module.kt
    module_path = os.path.join(kotlin_src_dir, f"{pascal_name}Module.kt")
    with open(module_path, "w", encoding="utf-8") as f:
        f.write(
            f"""package {feature_package}

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val {camel_name}Module = module {{
    viewModelOf(::{pascal_name}ViewModel)
}}
"""
        )

    # 5. Register in settings.gradle.kts
    settings_path = os.path.join(project_root, "settings.gradle.kts")
    include_stmt = f'include(":feature:{raw_name}")'

    with open(settings_path, "r", encoding="utf-8") as f:
        settings_content = f.read()

    if include_stmt not in settings_content:
        with open(settings_path, "a", encoding="utf-8") as f:
            f.write(f"\n{include_stmt}\n")

    print(f"\n✅ Successfully created feature module ':feature:{raw_name}'!")
    print(f"   Files generated in: {feature_dir}")
    print(f"   Package: {feature_package}\n")


if __name__ == "__main__":
    main()
