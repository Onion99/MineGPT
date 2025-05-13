import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import java.util.Locale

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    // ---- compose desktop hot reload -> https://github.com/JetBrains/compose-hot-reload ------
    //id("org.jetbrains.compose.hot-reload") version "1.0.0-alpha08"
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    jvm("desktop")
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "composeApp"
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
            }
        }
        binaries.executable()
    }
    
    sourceSets {
        val jvmMain by creating {
            dependencies {
                //implementation(fileTree(mapOf("dir" to "path/path", "include" to listOf("*.jar"))))
            }
        }
        val desktopMain by getting{
            dependsOn(jvmMain)
        }
        
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            // ---- KMP DEFAULT------
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            // ---- KMP CUSTOM------
            implementation(libs.compose.navigation)
            implementation(libs.compose.material3.adaptive)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            api(libs.koin.core)
            implementation(libs.koin.compose.viewmodel)
            // ---- Network Image ------
            implementation(libs.coil.network.ktor)
            implementation(libs.coil.compose)
            val fileKitVersion = "0.10.0-beta03"
            implementation("io.github.vinceglb:filekit-core:${fileKitVersion}")
            implementation("io.github.vinceglb:filekit-dialogs:${fileKitVersion}")
            implementation("io.github.vinceglb:filekit-dialogs-compose:${fileKitVersion}")
            implementation("io.github.vinceglb:filekit-coil:${fileKitVersion}")
            val lottieVersion = "2.0.0-rc04"
            implementation("io.github.alexzhirkevich:compottie:$lottieVersion")
            implementation("io.github.alexzhirkevich:compottie-dot:$lottieVersion")
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}

android {
    namespace = "org.onion.gpt"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.onion.gpt"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "org.onion.gpt.MainKt"

        // 为 desktopRunDebug 调试运行提供工作目录与库目录
        val taskName = project.gradle.startParameter.taskNames.firstOrNull() ?: ""
        if (taskName.contains("desktopRun")) {
            jvmArgs += "-Duser.dir=${rootProject.extra["desktopCurrentDir"]}"
            jvmArgs += "-Djava.library.path=${rootProject.extra["cppLibsDir"]}"
        }

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.onion.gpt"
            packageVersion = "1.0.0"
        }
    }
}

// ------------------------------------------------------------------------
// 添加CMake 构建任务
// CMake学习笔记 | 模块化项目管理(一) https://mp.weixin.qq.com/s/aLjOBjNmTZDJRrcJ14qKsg
// CMake学习笔记 | 常用基础指令总结(三) https://mp.weixin.qq.com/s/-a1KeIq_6RyIl0EnM71J8w
// CMake调用不同生成器(Unix Makefiles/Ninja)构建及编译C/C++项目(四) - https://mp.weixin.qq.com/s/9GUOKmVzfnzS9VG6DIaj1w
// ------------------------------------------------------------------------
val desktopPlatforms = listOf("windows", "macos", "linux")
desktopPlatforms.forEach { platform ->
    tasks.register("buildNativeLibFor${platform.capitalize()}") {
        println("配置 buildNativeLibFor${platform.capitalize()} 任务")
        
        doFirst {
            println("开始构建 $platform 平台的原生库")
        }
        // ---- Mine Window System Environment Path------
        // Mingw64 D:\MyApp\Code\mingw64\bin
        // CMAKE C:\Program Files\CMake\bin
        // KDK -> D:\MyApp\Code\KDK\kotlinc\bin
        // JAVA -> HOME C:\Users\Administrator\.jdks\corretto-17.0.11
        doLast {
            val cmakeGenerator = when(platform) {
                "windows" -> "MinGW Makefiles" /*没装VisualStudio 不支持 "Visual Studio 17 2022"*/
                "macos" -> "Xcode"
                "linux" -> "Unix Makefiles"
                else -> "Unix Makefiles"
            }
            
            // 平台特定配置
            val cmakeOptions = when(platform) {
                // ---- 生成器 "MinGW Makefiles" 不支持"-A" 参数 ------
                //"windows" -> mutableListOf("-A", "x64")
                "macos" -> mutableListOf("-DCMAKE_OSX_ARCHITECTURES=arm64;x86_64")
                else -> mutableListOf()
            }
            // 显式指定编译器 (如果它们不在 PATH 中，或者你想确保使用特定的编译器)
            // cmakeOptions.add("-DCMAKE_C_COMPILER=D:/MyApp/Code/mingw64/bin/gcc.exe")
            // cmakeOptions.add("-DCMAKE_CXX_COMPILER=D:/MyApp/Code/mingw64/bin/g++.exe")
            // 如果 mingw32-make 也不在 PATH 中，可能还需要指定
            // opts.add("-DCMAKE_MAKE_PROGRAM=C:/msys64/mingw64/bin/mingw32-make.exe")
            // 检查当前平台
            val isCurrentPlatform = when(platform) {
                "windows" -> System.getProperty("os.name").lowercase(Locale.getDefault()).contains("windows")
                "macos" -> System.getProperty("os.name").lowercase(Locale.getDefault()).contains("mac")
                "linux" -> System.getProperty("os.name").lowercase(Locale.getDefault()).contains("linux")
                else -> false
            }
            
            if (isCurrentPlatform) {
                println("正在为当前平台 $platform 构建原生库")
                
                exec {
                    // 这个目录可能需要调整，取决于 CMakeLists.txt 的位置
                    workingDir = file("$rootDir/cpp/gguf.cpp")
                    val cmd = mutableListOf("cmake",  "-S", ".", "-B", "build-$platform", "-G", cmakeGenerator)
                    cmd.addAll(cmakeOptions)
                    commandLine(cmd)
                }
                
                exec {
                    workingDir = file("$rootDir/cpp/gguf.cpp")
                    commandLine("cmake", "--build", "build-$platform", "--config", "Release")
                }
                
                // 迁移到JVM资源目录
                copy {
                    from("${rootProject.extra["cppLibsDir"]}")
                    include("*.dll","*.dll.a", "*.so", "*.dylib")
                    into("${rootProject.extra["jvmResourceLibDir"]}")
                }
                
                println("$platform 平台原生库构建完成")
            } else {
                println("跳过非当前平台 $platform 的构建")
            }
        }
    }
}

tasks.register("buildNativeLibsIfNeeded") {
    doFirst {
        val libFile = when {
            System.getProperty("os.name").lowercase(Locale.getDefault()).contains("windows") -> file("${rootProject.extra["cppLibsDir"]}/libsmollm.dll")
            System.getProperty("os.name").lowercase(Locale.getDefault()).contains("mac") -> file("${rootProject.extra["cppLibsDir"]}/libsmollm.dylib")
            else -> file("${rootProject.extra["cppLibsDir"]}/libsmollm.so")
        }
        
        if (!libFile.exists()) {
            println("原生库不存在，开始构建...")
            // 触发当前平台的构建任务
            val currentPlatform = when {
                System.getProperty("os.name").lowercase(Locale.getDefault()).contains("windows") -> "Windows"
                System.getProperty("os.name").lowercase(Locale.getDefault()).contains("mac") -> "Macos"
                System.getProperty("os.name").lowercase(Locale.getDefault()).contains("linux") -> "Linux"
                else -> ""
            }
            if (currentPlatform.isNotEmpty()) {
                tasks.getByName("buildNativeLibFor$currentPlatform").actions.forEach { it.execute(this) }
            }
        } else {
            println("原生库已存在，跳过构建")
            val jvmLibFile = when {
                System.getProperty("os.name").lowercase(Locale.getDefault()).contains("windows") -> file("${rootProject.extra["jvmResourceLibDir"]}/libsmollm.dll")
                System.getProperty("os.name").lowercase(Locale.getDefault()).contains("mac") -> file("${rootProject.extra["jvmResourceLibDir"]}/libsmollm.dylib")
                else -> file("${rootProject.extra["jvmResourceLibDir"]}/libsmollm.so")
            }
            if (!jvmLibFile.exists()){
                println("原生库还没有迁移JVM资源目录,现在迁移")
                copy {
                    from("${rootProject.extra["cppLibsDir"]}")
                    include("*.dll","*.dll.a", "*.so", "*.dylib")
                    into("${rootProject.extra["jvmResourceLibDir"]}")
                }
            }
        }
    }
}

// 让desktopRun依赖这个CMake构建任务
tasks.matching { it.name.contains("desktopRun") }.configureEach {
    dependsOn("buildNativeLibsIfNeeded")
}
// ------------------------------------------------------------------------
// 市场量化愈加激进,非强合力板块,就是围绕着补涨涨跌日周期进行,眼见为实,跌就是跌,涨就是涨
// 你的观察经历足够多的时间了吗
// ------------------------------------------------------------------------