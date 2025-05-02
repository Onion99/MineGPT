[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![ComposeMultiplatform](https://img.shields.io/badge/Compose_Multiplatform-1.7.3-blue.svg?style=flat)](https://www.jetbrains.com/compose-multiplatform/)

![ProjectBanner](docs/project_cover.webp)

README 文件語言: [中文](/docs/README_CHT.md) | [English](/README.md)

## 简介

MineGPT 是一个基于Kotlin Multiplatform 开发的本地小型语言模型(SLM)对话应用


## 未来计划

- 先进一步完善Desktop的使用
- 添加JNA来构建CMake
- 更多平台支持
- 多语言
- 数据存储
- 应用下载SLM模型(GGUF模型)

## 依赖组件
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

## 感谢

* [ggerganov/llama.cpp](https://github.com/ggerganov/llama.cpp) 是一个纯 C/C++ 框架，用于执行机器学习模型。它提供了一个原始的 C 样式 API 来与 LLM 交互转换为 GGML/llama.cpp 原生的 GGUF 格式
* [shubham0204/SmolChat-Android](https://github.com/shubham0204/SmolChat-Android) 是一个Android平台上运行SLM模型的开源项目,本项目也学习于此
