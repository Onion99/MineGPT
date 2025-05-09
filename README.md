[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![ComposeMultiplatform](https://img.shields.io/badge/Compose_Multiplatform-1.7.3-blue.svg?style=flat)](https://www.jetbrains.com/compose-multiplatform/)

![ProjectBanner](docs/project_cover.webp)

README 文件語言: [English](/docs/README_EN.md) | [中文](/README.md)

## 📜 简介

MineGPT 是一个基于Kotlin Multiplatform 开发的本地小型语言模型(SLM)对话应用

![ProjectApp](docs/project_chat.webp)

## 💠 测试模型

> 可以从这里下载 https://huggingface.co/models?library=gguf

- https://huggingface.co/legraphista/Qwen2-7B-Instruct-IMat-GGUF/blob/main/Qwen2-7B-Instruct.IQ1_M.gguf
- https://huggingface.co/unsloth/DeepSeek-R1-Distill-Qwen-1.5B-GGUF/resolve/main/DeepSeek-R1-Distill-Qwen-1.5B-Q2_K.gguf

## 🎮 编译

1. Android Studio, 去菜单栏 **Run** > **Edit Configurations** > **New** > **Gradle**.
2. 配置下面:
- Run: `desktopRun -DmainClass=org.onion.gpt.MainKt --quiet`

## 🚀未来计划

- 🖥️ 先进一步完善Desktop的使用
- 🌐 更多平台支持
- 🌍 多语言
- 💾 数据存储
- ⬇️ 应用下载SLM模型(GGUF模型)

## 🧩 依赖组件
感谢以下开发者,谢谢你们的付出
- Kotlin Multiplatform (KMP)
- Compose Multiplatform (CMP)
- JetBrains Adaptive Layout
- JetBrains Navigation Compose
- JetBrains Lifecycle ViewModel
- Koin
- Ktor
- Coil
- BuildKonfig
- Okio I/O
- llama.cpp

## 🙏 感谢

* [ggerganov/llama.cpp](https://github.com/ggerganov/llama.cpp) 是一个纯 C/C++ 框架，用于执行机器学习模型。它提供了一个原始的 C 样式 API 来与 LLM 交互转换为 GGML/llama.cpp 原生的 GGUF 格式
* [shubham0204/SmolChat-Android](https://github.com/shubham0204/SmolChat-Android) 是一个Android平台上运行SLM模型的开源项目,本项目也学习于此
