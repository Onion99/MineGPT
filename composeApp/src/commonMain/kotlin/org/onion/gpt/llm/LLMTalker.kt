package org.onion.gpt.llm

import kotlinx.coroutines.flow.Flow

expect class LLMTalker() {
    fun create(modelPath: String, minP: Float, temperature: Float,
               storeChats: Boolean, contextSize: Long, chatTemplate: String,
               nThreads: Int, useMmap: Boolean, useMlock: Boolean, ): Boolean

    fun addUserMessage(message: String)
    fun addSystemPrompt(prompt: String)
    fun addAssistantMessage(message: String)
    fun getResponseGenerationSpeed():Float
    fun getContextLengthUsed():Int
    fun getResponse(query: String): Flow<String>
    fun close()
}