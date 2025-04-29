package org.onion.gpt.ui.screen.home

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.onion.gpt.llm.LLMReader
import org.onion.gpt.llm.LLMTalker
import org.onion.gpt.ui.screen.home.model.ChatMessage
import kotlin.time.measureTime

class ChatViewModel  : ViewModel() {

    private lateinit var llmReader:LLMReader
    private lateinit var llmTalker:LLMTalker

    private var initLLM = false
    fun initLLM(){
        if(initLLM) return
        initLLM = true
        val modelPath = "D:\\models\\llama2-7b-chat.gguf"
        val minP = 0.05f
        val temperature = 1.0f
        val systemPrompt = "You are a helpful assistant"
        viewModelScope.launch(Dispatchers.Default) {
            // ---- read chatTemplate and contextSize ------
            llmReader = LLMReader()
            llmReader.loadModel(modelPath)
            val contextSize = llmReader.getContextSize()
            val chatTemplate = llmReader.getChatTemplate()
            llmTalker = LLMTalker()
            llmTalker.create(modelPath,minP,temperature,true,contextSize!!,chatTemplate!!,4,true,
                useMlock = false
            )
            llmTalker.addSystemPrompt(systemPrompt)
        }
    }

    private var responseGenerationJob: Job? = null
    private var isInferenceOn: Boolean = false
    fun getTalkerResponse(query: String, onCancelled: () -> Unit, onError: (Throwable) -> Unit){
        runCatching {
            responseGenerationJob = viewModelScope.launch(Dispatchers.Default) {
                isInferenceOn = true
                val responseContent = StringBuilder()
                val duration = measureTime {
                    llmTalker.getResponse(query).collect { newContent ->
                        responseContent.append(newContent)
                        generateResponseIncrementally(newContent,responseContent.toString())
                    }
                }
                isGenerating.value = false
            }
        }.getOrElse { exception ->
            isInferenceOn = false
            if(exception is CancellationException){
                onCancelled()
            }else onError(exception)
        }
    }
    private suspend fun generateResponseIncrementally(appendContent: String,responseContent: String) {
        for (i in appendContent.indices) {
            if (!isGenerating.value) break
            _currentChatMessages[_currentChatMessages.lastIndex] = ChatMessage(responseContent.take(responseContent.length - (appendContent.length - i)), false)
            delay(5)
        }
        isInferenceOn = false
    }


    // ========================================================================================
    //                              Chat Message State
    // ========================================================================================

    /** Current active chat conversation messages */
    private val _currentChatMessages = mutableStateListOf<ChatMessage>()
    val currentChatMessages: SnapshotStateList<ChatMessage> = _currentChatMessages

    /** Flag indicating if response generation is in progress */
    val isGenerating = mutableStateOf(false)

    // region Message Handling & Generation
    // ========================================================================================
    //                          Public Message Methods
    // ========================================================================================
    fun sendMessage(message: String, isUser: Boolean = true) {
        viewModelScope.launch {
            if(isGenerating.value) stopGeneration()
            if(message.isBlank()) return@launch
            _currentChatMessages.add(ChatMessage(message, isUser))
            _currentChatMessages.add(ChatMessage("", false))
            isGenerating.value = true
            getTalkerResponse(message,{},{})
        }

    }

    fun stopGeneration() {
        isGenerating.value = false
    }
}