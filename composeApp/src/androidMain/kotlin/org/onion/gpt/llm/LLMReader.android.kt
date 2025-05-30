package org.onion.gpt.llm

import android.net.Uri
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.context
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import androidx.core.net.toUri
import io.github.vinceglb.filekit.name
import java.io.File
import java.io.FileOutputStream

actual class LLMReader {
    init {
        System.loadLibrary("ggufreader")
    }

    private var nativeHandle: Long = 0L

    actual suspend fun getLLMFilePath(): String{
        val androidFile = FileKit.openFilePicker(type = FileKitType.File(listOf("gguf")))
        FileKit.context.contentResolver.openInputStream((androidFile?.absolutePath() ?: return "").toUri()).use { inputStream ->
            FileOutputStream(File(FileKit.context.filesDir, androidFile.name)).use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
        }
        return File(FileKit.context.filesDir, androidFile.name).absolutePath
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