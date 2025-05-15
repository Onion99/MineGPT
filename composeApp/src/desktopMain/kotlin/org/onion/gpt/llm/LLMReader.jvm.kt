package org.onion.gpt.llm

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.openFilePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.onion.gpt.utils.NativeLibraryLoader

actual class LLMReader {
    init {
        NativeLibraryLoader.loadFromResources("ggufreader")
        //System.loadLibrary("libggufreader") if desktopRunDebug run here
    }

    private var nativeHandle: Long = 0L

    actual suspend fun getLLMFilePath(): String{
        return FileKit.openFilePicker()?.file?.absolutePath ?: ""
    }

    actual fun loadModel(modelPath: String) {
        nativeHandle = getGGUFContextNativeHandle(modelPath)
    }

    actual fun getContextSize(): Long? {
        assert(nativeHandle != 0L) { "Use GGUFReader.load() to initialize the reader" }
        val contextSize = getContextSize(nativeHandle)
        return if (contextSize == -1L) {
            null
        } else {
            contextSize
        }
    }

    actual fun getChatTemplate(): String? {
        assert(nativeHandle != 0L) { "Use GGUFReader.load() to initialize the reader" }
        val chatTemplate = getChatTemplate(nativeHandle)
        return chatTemplate.ifEmpty {
            null
        }
    }

    // ------------------------------------------------------------------------
    //  Returns the native handle (pointer to gguf_context created on the native side)
    // ------------------------------------------------------------------------
    private external fun getGGUFContextNativeHandle(modelPath: String): Long

    // ------------------------------------------------------------------------
    // Read the context size (in no. of tokens) from the GGUF file, given the native handle
    // ------------------------------------------------------------------------
    private external fun getContextSize(nativeHandle: Long): Long

    // ------------------------------------------------------------------------
    // Read the chat template from the GGUF file, given the native handle
    // ------------------------------------------------------------------------
    private external fun getChatTemplate(nativeHandle: Long): String
}