#!/usr/bin/env python3
import os
import sys
import re
import argparse
import shutil

OLD_PACKAGE = "com.bystrov.sto"
OLD_NAME = "STO"

TEXT_EXTENSIONS = {
    ".kt", ".kts", ".toml", ".xml", ".xcconfig", ".md", ".json",
    ".properties", ".plist", ".html", ".puml", ".java", ".gradle"
}

IGNORED_DIRS = {
    ".git", ".idea", ".gradle", "build", ".artifacts", "__pycache__"
}


def replace_in_file(file_path: str, new_name: str, new_package: str):
    try:
        with open(file_path, "r", encoding="utf-8") as f:
            content = f.read()
    except (UnicodeDecodeError, PermissionError):
        return

    updated = content
    # Replace root package
    updated = updated.replace(OLD_PACKAGE, new_package)
    # Replace root project name in settings.gradle.kts / docs
    if os.path.basename(file_path) in ("settings.gradle.kts", "README.md", "AGENT.md", "Config.xcconfig"):
        updated = updated.replace(f'rootProject.name = "{OLD_NAME}"', f'rootProject.name = "{new_name}"')
        updated = updated.replace(f'PRODUCT_BUNDLE_IDENTIFIER={OLD_PACKAGE}.{OLD_NAME}', f'PRODUCT_BUNDLE_IDENTIFIER={new_package}.{new_name}')
        updated = updated.replace(OLD_NAME, new_name)

    if updated != content:
        with open(file_path, "w", encoding="utf-8") as f:
            f.write(updated)


def process_text_replacements(root_dir: str, new_name: str, new_package: str):
    print(f"🔄 Updating references in text files...")
    for dirpath, dirnames, filenames in os.walk(root_dir):
        # Filter out ignored directories
        dirnames[:] = [d for d in dirnames if d not in IGNORED_DIRS]

        for filename in filenames:
            ext = os.path.splitext(filename)[1]
            if ext in TEXT_EXTENSIONS or filename in ("AGENT.md", "README.md", "Config.xcconfig"):
                file_path = os.path.join(dirpath, filename)
                replace_in_file(file_path, new_name, new_package)


def move_package_directories(root_dir: str, new_package: str):
    print("📁 Moving Kotlin/Java package directories...")
    old_rel_path = os.path.join(*OLD_PACKAGE.split("."))
    new_rel_path = os.path.join(*new_package.split("."))

    if old_rel_path == new_rel_path:
        return

    # Find all occurrences of the old package path
    matching_dirs = []
    for dirpath, dirnames, _ in os.walk(root_dir):
        dirnames[:] = [d for d in dirnames if d not in IGNORED_DIRS]
        if dirpath.endswith(old_rel_path):
            matching_dirs.append(dirpath)

    for old_dir in matching_dirs:
        parent_base = old_dir[:-len(old_rel_path)]
        new_dir = os.path.join(parent_base, new_rel_path)

        if old_dir == new_dir:
            continue

        print(f"  Moving: {old_dir} -> {new_dir}")
        os.makedirs(os.path.dirname(new_dir), exist_ok=True)

        # Copy contents to new location
        if os.path.exists(new_dir):
            for item in os.listdir(old_dir):
                s = os.path.join(old_dir, item)
                d = os.path.join(new_dir, item)
                if os.path.isdir(s):
                    shutil.copytree(s, d, dirs_exist_ok=True)
                else:
                    shutil.copy2(s, d)
            shutil.rmtree(old_dir)
        else:
            shutil.move(old_dir, new_dir)

        # Clean up empty parent folders left behind
        curr = os.path.dirname(old_dir)
        while curr and curr.startswith(parent_base) and len(curr) > len(parent_base):
            if os.path.exists(curr) and not os.listdir(curr):
                os.rmdir(curr)
                curr = os.path.dirname(curr)
            else:
                break


def main():
    parser = argparse.ArgumentParser(description="Initialize a new project from STO template.")
    parser.add_argument("-n", "--name", type=str, help="New Project Name (e.g. MyAwesomeApp)")
    parser.add_argument("-p", "--package", type=str, help="New Root Package Name (e.g. com.company.myawesomeapp)")

    args = parser.parse_args()

    new_name = args.name
    if not new_name:
        new_name = input(f"Enter New Project Name (e.g. MyAwesomeApp): ").strip()

    new_package = args.package
    if not new_package:
        new_package = input(f"Enter New Package Name (e.g. com.company.myawesomeapp): ").strip()

    if not new_name or not new_package:
        print("❌ Error: Both project name and package name are required.")
        sys.exit(1)

    project_root = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))

    print(f"\n🚀 Initializing new project:")
    print(f"  Name:    {OLD_NAME} -> {new_name}")
    print(f"  Package: {OLD_PACKAGE} -> {new_package}")
    print(f"  Root:    {project_root}\n")

    process_text_replacements(project_root, new_name, new_package)
    move_package_directories(project_root, new_package)

    print("\n✅ Project initialization complete!")
    print("Next steps:")
    print("  1. Sync Gradle in Android Studio.")
    print("  2. Run './gradlew build' to verify everything compiles cleanly.\n")


if __name__ == "__main__":
    main()
