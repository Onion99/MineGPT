package org.onion.gpt.llm

actual class LLMReader {
    companion object{
        init {
            System.loadLibrary("")
        }
    }

    actual fun loadModel(modelPath: String) {
    }

    actual fun getContextSize(): Long? {
        TODO("Not yet implemented")
    }

    actual fun getChatTemplate(): String? {
        TODO("Not yet implemented")
    }
}