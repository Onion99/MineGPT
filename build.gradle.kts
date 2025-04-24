plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
}
// Project Dir
val dirProject: Directory = layout.projectDirectory
// Cpp
val dirCpp by extra(dirProject.dir("cpp"))
val cppLibsDir by extra(dirCpp.dir("libs"))
// composeApp Dir
val dirApp by extra(dirProject.dir(projects.composeApp.name))
val dirAppSrc by extra(dirApp.dir("src"))
val dirAppBuild by extra(dirApp.dir("build"))
// composeApp Desktop Dir
val desktopDir by extra(dirAppSrc.dir("desktopMain"))
val desktopCurrentDir by extra(dirAppBuild.dir("desktopRun"))