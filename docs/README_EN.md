[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![ComposeMultiplatform](https://img.shields.io/badge/Compose_Multiplatform-1.7.3-blue.svg?style=flat)](https://www.jetbrains.com/compose-multiplatform/)

![ProjectBanner](project_cover.webp)

README : [中文](/docs/README_CHT.md) | [English](/README.md)

## 📜 Intro

MineGPT is a lightweight local SLM (Small Language Model) chat application built with Kotlin Multiplatform.

![ProjectApp](project_chat.webp)

## 💠 Test Models

> Download from here https://huggingface.co/models?library=gguf

- https://huggingface.co/legraphista/Qwen2-7B-Instruct-IMat-GGUF/blob/main/Qwen2-7B-Instruct.IQ1_M.gguf
- https://huggingface.co/unsloth/DeepSeek-R1-Distill-Qwen-1.5B-GGUF/resolve/main/DeepSeek-R1-Distill-Qwen-1.5B-Q2_K.gguf

<div align="center">
  <img src="./shot_1.gif" height=466/>
  <img src="./shot_2.gif" height=466/>
</div>

## 🎮 Run

#### Android
Select your test equipment and Run it
#### Desktop
1. Android Studio, go to **Run** > **Edit Configurations** > **New** > **Gradle**.
2. Configure as follows:
- Run: `desktopRun -DmainClass=org.onion.gpt.MainKt --quiet`

## 🚀 Features & Roadmap

- 🌐 IOS Support
- 🌍 Multi-Language
- 💾 Persistent data storage

## 🧩 Library
Thanks to all the contributors who made KMP/CMP possible!
- Kotlin Multiplatform (KMP)
- Compose Multiplatform (CMP)
- JetBrains Adaptive Layout
- JetBrains Navigation Compose
- JetBrains Lifecycle ViewModel
- Koin
- Coil
- llama.cpp
- FileKt
- Compottie

## 🙏 Special Thanks
* [ggerganov/llama.cpp](https://github.com/ggerganov/llama.cpp) A pure C/C++ framework for running LLMs with a simple C-style API. Supports the native GGUF format for efficient inference with GGML/llama.cpp.
* [shubham0204/SmolChat-Android](https://github.com/shubham0204/SmolChat-Android) A minimal Android application for running local SLM models. MineGPT drew valuable inspiration and ideas from this project.
