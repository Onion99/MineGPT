package org.onion.gpt.llm

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.onion.gpt.utils.NativeLibraryLoader

actual class LLMTalker {
    init {
        NativeLibraryLoader.loadFromResources("smollm")
        //System.loadLibrary("libsmollm") // if desktopRunDebug run here
    }
    private var nativePtr = 0L

    actual fun create(
        modelPath: String,
        minP: Float,
        temperature: Float,
        storeChats: Boolean,
        contextSize: Long,
        chatTemplate: String,
        nThreads: Int,
        useMmap: Boolean,
        useMlock: Boolean
    ): Boolean {
        nativePtr =loadModel(
            modelPath,
            minP,
            temperature,
            storeChats,
            contextSize,
            chatTemplate,
            nThreads,
            useMmap,
            useMlock,
        )
        return nativePtr != 0L
    }

    actual fun addUserMessage(message: String) {
        verifyHandle()
        addChatMessage(nativePtr, message, "user")
    }

    actual fun addSystemPrompt(prompt: String) {
        verifyHandle()
        addChatMessage(nativePtr, prompt, "system")
    }

    actual fun addAssistantMessage(message: String) {
        verifyHandle()
        addChatMessage(nativePtr, message, "assistant")
    }

    actual fun getResponseGenerationSpeed():Float {
        verifyHandle()
        return getResponseGenerationSpeed(nativePtr)
    }

    actual fun getContextLengthUsed():Int{
        verifyHandle()
        return getContextSizeUsed(nativePtr)
    }

    actual fun getResponse(query: String): Flow<String> = flow{
        verifyHandle()
        startCompletion(nativePtr, query)
        var piece = completionLoop(nativePtr)
        while (piece != "[EOG]") {
            emit(piece)
            piece = completionLoop(nativePtr)
        }
        stopCompletion(nativePtr)
    }

    actual fun close() {
        if (nativePtr != 0L) {
            close(nativePtr)
            nativePtr = 0L
        }
    }

    private fun verifyHandle() {
        assert(nativePtr != 0L) { "Model is not loaded. Use LLMTalker.create to load the model" }
    }

    private external fun loadModel(modelPath: String, minP: Float, temperature: Float, storeChats: Boolean, contextSize: Long, chatTemplate: String, nThreads: Int, useMmap: Boolean, useMlock: Boolean, ): Long

    private external fun addChatMessage(modelPtr: Long, message: String, role: String, )

    private external fun getResponseGenerationSpeed(modelPtr: Long): Float

    private external fun getContextSizeUsed(modelPtr: Long): Int

    private external fun close(modelPtr: Long)

    private external fun startCompletion(modelPtr: Long, prompt: String, )

    private external fun completionLoop(modelPtr: Long): String

    private external fun stopCompletion(modelPtr: Long)
}