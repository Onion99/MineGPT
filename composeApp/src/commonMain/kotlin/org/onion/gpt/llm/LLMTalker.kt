package org.onion.gpt.llm

expect class LLMTalker() {
    fun create(modelPath: String, minP: Float, temperature: Float,
               storeChats: Boolean, contextSize: Long, chatTemplate: String,
               nThreads: Int, useMmap: Boolean, useMlock: Boolean, ): Boolean

    fun addUserMessage(message: String)
    fun addSystemPrompt(prompt: String)
    fun addAssistantMessage(message: String)
    fun getResponseGenerationSpeed()
    fun getContextLengthUsed()
    fun getResponse(query: String)
    fun close()
}