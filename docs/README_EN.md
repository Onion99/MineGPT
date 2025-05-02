[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![ComposeMultiplatform](https://img.shields.io/badge/Compose_Multiplatform-1.7.3-blue.svg?style=flat)](https://www.jetbrains.com/compose-multiplatform/)

![ProjectBanner](project_cover.webp)

README 文件語言: [中文](/docs/README_CHT.md) | [English](/README.md)

## 📜 Intro

MineGPT is a lightweight local SLM (Small Language Model) chat application built with Kotlin Multiplatform. It aims to provide a cross-platform, performant, and user-friendly AI assistant experience.

![ProjectApp](project_chat.webp)

## 🚀 Features & Roadmap
Planned enhancements for future releases:

- 🖥️ Enhanced desktop support

- ⚙️ CMake integration via JNA

- 🌐 Cross-platform compatibility (more targets)

- 🌍 Multilingual UI and interactions

- 💾 Persistent data storage

- ⬇️ In-app download of SLM models (GGUF format)

## 🧩 Built With
This project wouldn’t be possible without the amazing work of the following technologies and libraries:
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

## 🙏 Acknowledgements

* [ggerganov/llama.cpp](https://github.com/ggerganov/llama.cpp) A pure C/C++ framework for running LLMs with a simple C-style API. Supports the native GGUF format for efficient inference with GGML/llama.cpp.
* [shubham0204/SmolChat-Android](https://github.com/shubham0204/SmolChat-Android) A minimal Android application for running local SLM models. MineGPT drew valuable inspiration and ideas from this project.
