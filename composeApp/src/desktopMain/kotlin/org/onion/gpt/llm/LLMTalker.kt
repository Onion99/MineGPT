package org.onion.gpt.llm

actual class LLMTalker {
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
        TODO("Not yet implemented")
    }

    actual fun addUserMessage(message: String) {
    }

    actual fun addSystemPrompt(prompt: String) {
    }

    actual fun addAssistantMessage(message: String) {
    }

    actual fun getResponseGenerationSpeed() {
    }

    actual fun getContextLengthUsed() {
    }

    actual fun getResponse(query: String) {
    }

    actual fun close() {
    }

}