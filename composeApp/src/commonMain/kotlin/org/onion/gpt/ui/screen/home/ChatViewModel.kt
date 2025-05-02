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
        // ------------------------------------------------------------------------
        // 作用: 在生成文本时，Top-P 采样是一种策略。它会先累加所有 token 的概率，然后将概率低于这个 minP 值的 token 截断，
        // 之后再从剩余的 token 中按概率采样。它控制了生成文本的多样性。•说明: 如果 minP 设置为 0.9，那么只有累积概率达到 90% 的 tokens 才会被考虑采样。这可以防止模型生成低概率的、不常见的词，从而提高文本质量
        // ------------------------------------------------------------------------
        val minP = 0.05f
        // ------------------------------------------------------------------------
        //  较高的 temperature 适合创意写作，较低的 temperature 适合需要准确性的任务，如代码生成
        // ------------------------------------------------------------------------
        val temperature = 1.0f
        // ------------------------------------------------------------------------
        //  mmap 是一种高效的文件访问方式，它可以将文件映射到内存，从而减少数据复制，提高加载速度。通常建议设置为 true
        // ------------------------------------------------------------------------
        val useMmap = true
        // ------------------------------------------------------------------------
        //  mlock 可以将模型数据锁定在 RAM 中，防止被交换到硬盘，这可以进一步提高推理速度。但也可能导致内存使用过高ck
        // ------------------------------------------------------------------------
        val useMlock = true
        val systemPrompt = "You are a helpful assistant"
        viewModelScope.launch(Dispatchers.Default) {
            // ---- read chatTemplate and contextSize ------
            llmReader = LLMReader()
            llmReader.loadModel(modelPath)
            val contextSize = llmReader.getContextSize()
            val chatTemplate = llmReader.getChatTemplate()
            llmTalker = LLMTalker()
            llmTalker.create(modelPath,minP,temperature,true,contextSize!!,chatTemplate!!,6,useMmap,
                useMlock = useMlock
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