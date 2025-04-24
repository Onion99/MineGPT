cmake_minimum_required(VERSION 3.22.1)
project("smollm")

# 获取当前路径的上一级目录
get_filename_component(PARENT_DIR "${CMAKE_CURRENT_SOURCE_DIR}" DIRECTORY)
# 最终确定项目输出动态链接库的目录,我这里是D:\Coding\Happy\MineGPT\cpp\libs
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY "${PARENT_DIR}/libs") # .exe and .dll
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY "${PARENT_DIR}/libs") # .so and .dylib
set(CMAKE_ARCHIVE_OUTPUT_DIRECTORY "${PARENT_DIR}/libs") # .lib and .a
message(STATUS "最终生成目录: ${GRANDPARENT_DIR}")

# 兼容Android平台 include<jni.h> ,需找到 JDK 安装目录（你也可以写死路径）
if(NOT DEFINED ENV{JAVA_HOME})
    message(FATAL_ERROR "Please set JAVA_HOME environment variable.")
endif()

set(JAVA_INCLUDE_DIR "$ENV{JAVA_HOME}/include")
set(JAVA_INCLUDE_DIR2 "$ENV{JAVA_HOME}/include/win32")  # Windows平台需要包含这个额外目录

include_directories(${JAVA_INCLUDE_DIR} ${JAVA_INCLUDE_DIR2})

# Build
set(LLAMA_DIR "../llama.cpp")
set(GGML_DIR ${LLAMA_DIR}/ggml)
set(COMMON_DIR ${LLAMA_DIR}/common)

if(EXISTS "${CMAKE_CURRENT_SOURCE_DIR}/../../.git")
    set(GIT_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../../.git")
    # Is git submodule
    if(NOT IS_DIRECTORY "${GIT_DIR}")
        file(READ ${GIT_DIR} REAL_GIT_DIR_LINK)
        string(REGEX REPLACE "gitdir: (.*)\n$" "\\1" REAL_GIT_DIR ${REAL_GIT_DIR_LINK})
        string(FIND "${REAL_GIT_DIR}" "/" SLASH_POS)
        if (SLASH_POS EQUAL 0)
            set(GIT_DIR "${REAL_GIT_DIR}")
        else()
            set(GIT_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../../${REAL_GIT_DIR}")
        endif()
    endif()
    if(EXISTS "${GIT_DIR}/index")
        set(GIT_INDEX "${GIT_DIR}/index")
    else()
        message(WARNING "Git index not found in git repository.")
        set(GIT_INDEX "")
    endif()
else()
    message(WARNING "Git repository not found; to enable automatic generation of build info, make sure Git is installed and the project is a Git repository.")
    set(GIT_INDEX "")
endif()
# Add a custom command to rebuild build-info.cpp when .git/index changes
add_custom_command(
        OUTPUT "${PARENT_DIR}/llama.cpp/common/build-info.cpp"
        COMMENT "Generating build details from Git"
        COMMAND ${CMAKE_COMMAND} -DMSVC=${MSVC} -DCMAKE_C_COMPILER_VERSION=${CMAKE_C_COMPILER_VERSION}
        -DCMAKE_C_COMPILER_ID=${CMAKE_C_COMPILER_ID} -DCMAKE_VS_PLATFORM_NAME=${CMAKE_VS_PLATFORM_NAME}
        -DCMAKE_C_COMPILER=${CMAKE_C_COMPILER} -P "${PARENT_DIR}/llama.cpp/common/cmake/build-info-gen-cpp-v2.cmake"
        WORKING_DIRECTORY "${COMMON_DIR}/.."
        DEPENDS "${COMMON_DIR}/build-info.cpp.in" ${GIT_INDEX}
        VERBATIM
)
set(SMOLLM_SOURCES
        ${GGML_DIR}/src/ggml.c
        ${GGML_DIR}/src/ggml-alloc.c
        ${GGML_DIR}/src/ggml-backend.cpp
        ${GGML_DIR}/src/ggml-threading.cpp
        ${GGML_DIR}/src/ggml-quants.c
        ${GGML_DIR}/src/ggml-backend-reg.cpp
        ${GGML_DIR}/src/ggml-opt.cpp
        ${GGML_DIR}/src/ggml-cpu/ggml-cpu.c
        ${GGML_DIR}/src/ggml-cpu/ggml-cpu.cpp
        ${GGML_DIR}/src/ggml-cpu/ggml-cpu-aarch64.cpp
        ${GGML_DIR}/src/ggml-cpu/ggml-cpu-quants.c
        ${GGML_DIR}/src/ggml-cpu/ggml-cpu-traits.cpp
        ${GGML_DIR}/src/gguf.cpp

        ${LLAMA_DIR}/src/llama.cpp
        ${LLAMA_DIR}/src/llama-vocab.cpp
        ${LLAMA_DIR}/src/llama-grammar.cpp
        ${LLAMA_DIR}/src/llama-sampling.cpp
        ${LLAMA_DIR}/src/llama-context.cpp
        ${LLAMA_DIR}/src/llama-model.cpp
        ${LLAMA_DIR}/src/llama-model-loader.cpp
        ${LLAMA_DIR}/src/llama-impl.cpp
        ${LLAMA_DIR}/src/llama-mmap.cpp
        ${LLAMA_DIR}/src/llama-hparams.cpp
        ${LLAMA_DIR}/src/llama-kv-cache.cpp
        ${LLAMA_DIR}/src/llama-batch.cpp
        ${LLAMA_DIR}/src/llama-arch.cpp
        ${LLAMA_DIR}/src/llama-adapter.cpp
        ${LLAMA_DIR}/src/llama-chat.cpp
        ${LLAMA_DIR}/src/llama-graph.cpp
        ${LLAMA_DIR}/src/unicode.h
        ${LLAMA_DIR}/src/unicode.cpp
        ${LLAMA_DIR}/src/unicode-data.cpp
        ${LLAMA_DIR}/src/llama-io.h
        ${LLAMA_DIR}/src/llama-io.cpp

        ${COMMON_DIR}/arg.cpp
        ${COMMON_DIR}/base64.hpp
        ${COMMON_DIR}/build-info.cpp
        ${COMMON_DIR}/common.cpp
        ${COMMON_DIR}/console.cpp
        ${COMMON_DIR}/json-schema-to-grammar.cpp
        ${COMMON_DIR}/json.hpp
        ${COMMON_DIR}/log.cpp
        ${COMMON_DIR}/ngram-cache.cpp
        ${COMMON_DIR}/sampling.cpp
        ${COMMON_DIR}/chat.cpp
        ${COMMON_DIR}/chat.h

        LLMInference.cpp
        smollm.cpp
)
set(GGUF_READER_SOURCES
        ${GGML_DIR}/src/ggml.c
        ${GGML_DIR}/src/ggml-alloc.c
        ${GGML_DIR}/src/ggml-backend.cpp
        ${GGML_DIR}/src/ggml-threading.cpp
        ${GGML_DIR}/src/ggml-quants.c
        ${GGML_DIR}/src/ggml-backend-reg.cpp
        ${GGML_DIR}/src/ggml-opt.cpp
        ${GGML_DIR}/src/ggml-cpu/ggml-cpu.c
        ${GGML_DIR}/src/ggml-cpu/ggml-cpu.cpp
        ${GGML_DIR}/src/ggml-cpu/ggml-cpu-aarch64.cpp
        ${GGML_DIR}/src/ggml-cpu/ggml-cpu-quants.c
        ${GGML_DIR}/src/ggml-cpu/ggml-cpu-traits.cpp
        ${GGML_DIR}/src/gguf.cpp
        GGUFReader.cpp
)

# compiling for different CPU extensions for Arm64 (aarch64)
# See docs/build_arm_flags.md for more details

function(build_library target_name)
    add_library(
            ${target_name}
            SHARED
            ${SMOLLM_SOURCES}
    )
    target_include_directories(
            ${target_name}
            PUBLIC
            ${GGML_DIR}/include
            ${GGML_DIR}/src
            ${GGML_DIR}/src/ggml-cpu
            ${LLAMA_DIR}/include
            ${COMMON_DIR}
    )

    # set the compile options for the target `ggml`
    # targets `llama` and `common` are linked to `ggml`,
    # thus inheriting the same compile options

    # -fvisibility=hidden: hide all symbols by default
    # -fvisibility-inlines-hidden: hide all inline symbols by default
    target_compile_options(
            ${target_name}
            PUBLIC
            -fvisibility=hidden -fvisibility-inlines-hidden
    )
    # -ffunction-sections: place each function in its own section
    # -fdata-sections: place each data member in its own section
    target_compile_options(
            ${target_name}
            PUBLIC
            -ffunction-sections -fdata-sections
    )
    # 只在 Android 平台链接这个库
    if (ANDROID)
        target_link_libraries(${target_name} android log)
    endif()
    #或者更通用
    #[[if(CMAKE_SYSTEM_NAME STREQUAL "Android")
        target_link_libraries(${target_name} android log)
    endif()]]
    # -Wl,--gc-sections: remove unused sections (garbage collection)
    # -flto: link-time optimization
    # -Wl,--exclude-libs,ALL: exclude all libraries
    target_link_options(
            ${target_name}
            PRIVATE
            -Wl,--gc-sections -flto
            -Wl,--exclude-libs,ALL
    )
endfunction()


function(build_library_arm64 target_name cpu_flags)
    build_library(${target_name})
    target_compile_options(
            ${target_name}
            PUBLIC
            -DGGML_USE_CPU -DGGML_USE_CPU_AARCH64 ${cpu_flags} -O3
    )
endfunction()

function(build_library_armv7a target_name cpu_flags fpu fpu_abi)
    build_library(${target_name})
    target_compile_options(
            ${target_name}
            PUBLIC
            -DGGML_USE_CPU ${cpu_flags} ${fpu} ${fpu_abi} -O3
    )
endfunction()

function(build_library_universal target_name)
    build_library(${target_name})
    target_compile_options(
            ${target_name}
            PUBLIC
            -DGGML_USE_CPU -O3
    )
endfunction()

build_library_universal("smollm")
if ("${ANDROID_ABI}" STREQUAL "armeabi-v7a")
    build_library_armv7a("smollm_v7a" "-march=armv7-a" "-mfpu=neon-vfpv4" "-mfloat-abi=softfp")
endif()
if ("${ANDROID_ABI}" STREQUAL "arm64-v8a")
    build_library_arm64("smollm_v8" "-march=armv8-a")
    # Targets for Arm-v8.2a
    build_library_arm64("smollm_v8_2_fp16" "-march=armv8.2-a+fp16")
    build_library_arm64("smollm_v8_2_fp16_dotprod" "-march=armv8.2-a+fp16+dotprod")

    # Targets for Arm-v8.4a
    build_library_arm64("smollm_v8_4_fp16_dotprod" "-march=armv8.4-a+fp16+dotprod")
    build_library_arm64("smollm_v8_4_fp16_dotprod_sve" "-march=armv8.4-a+fp16+dotprod+sve")
    build_library_arm64("smollm_v8_4_fp16_dotprod_i8mm" "-march=armv8.4-a+fp16+dotprod+i8mm")
    build_library_arm64("smollm_v8_4_fp16_dotprod_i8mm_sve" "-march=armv8.4-a+fp16+dotprod+i8mm+sve")
endif()

# library target for GGUFReader
set(TARGET_NAME_GGUF_READER ggufreader)
add_library(${TARGET_NAME_GGUF_READER} SHARED ${GGUF_READER_SOURCES})
target_include_directories(
        ${TARGET_NAME_GGUF_READER}
        PUBLIC
        ${GGML_DIR}/include
        ${GGML_DIR}/src
        ${GGML_DIR}/src/ggml-cpu
)
target_compile_options(
        ${TARGET_NAME_GGUF_READER}
        PUBLIC
        -fvisibility=hidden -fvisibility-inlines-hidden -ffunction-sections -fdata-sections
)
target_link_options(
        ${TARGET_NAME_GGUF_READER}
        PRIVATE
        -Wl,--gc-sections -flto
        -Wl,--exclude-libs,ALL
)