package com.bystrov.sto.convention

import org.gradle.api.Project

internal fun Project.configureAndroidTarget() {
    val compileSdkVal = libs.findVersion("android-compileSdk").get().toString().toInt()
    val minSdkVal = libs.findVersion("android-minSdk").get().toString().toInt()
    val pkgName = pathToPackageName()

    fun configureTarget(obj: Any) {
        val cls = obj.javaClass
        cls.methods.firstOrNull { it.name == "setCompileSdk" && it.parameterCount == 1 }?.invoke(obj, compileSdkVal)
        cls.methods.firstOrNull { it.name == "setMinSdk" && it.parameterCount == 1 }?.invoke(obj, minSdkVal)
        cls.methods.firstOrNull { it.name == "setNamespace" && it.parameterCount == 1 }?.invoke(obj, pkgName)
    }

    extensions.findByName("android")?.let { configureTarget(it) }
    extensions.findByName("kotlin")?.let { kotlinExt ->
        kotlinExt.javaClass.methods.firstOrNull { it.name == "getAndroid" }?.invoke(kotlinExt)?.let { configureTarget(it) }
        (kotlinExt as? org.gradle.api.plugins.ExtensionAware)?.extensions?.findByName("android")?.let { configureTarget(it) }
    }
}
