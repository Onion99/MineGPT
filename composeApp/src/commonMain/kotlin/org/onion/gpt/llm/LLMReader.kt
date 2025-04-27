package org.onion.gpt.llm

expect class LLMReader(){
    fun loadModel(modelPath: String)
    fun getContextSize():Long?
    fun getChatTemplate():String?
}