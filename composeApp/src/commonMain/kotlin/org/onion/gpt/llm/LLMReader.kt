package org.onion.gpt.llm

expect class LLMReader(){
    suspend fun getLLMFilePath():String
    fun loadModel(modelPath: String)
    fun getContextSize():Long?
    fun getChatTemplate():String?
}