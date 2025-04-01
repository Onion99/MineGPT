package org.onion.gpt.ui.screen.home

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.onion.gpt.ui.screen.home.model.ChatMessage
import org.onion.gpt.ui.screen.home.model.generateLongResponse

class ChatViewModel  : ViewModel() {
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

    /**
     * Adds a new message to the chat and initiates AI response
     * @param message The text message content
     * @param isUser Flag indicating if the message is from the user
     */
    fun sendMessage(message: String, isUser: Boolean = true) {
        if (isGenerating.value) stopGeneration()
        _currentChatMessages.add(ChatMessage(message, isUser))
        if (isUser) initiateResponseGeneration()
    }


    // ========================================================================================
    //                          Private Generation Methods
    // ========================================================================================
    /** Full response being generated (simulated) */
    private var fullResponse: String = ""
    private fun initiateResponseGeneration() {
        fullResponse = generateLongResponse()
        _currentChatMessages.add(ChatMessage("", false))
        isGenerating.value = true

        viewModelScope.launch {
            try {
                generateResponseIncrementally(_currentChatMessages.lastIndex)
            } finally {
                resetGenerationState()
            }
        }
    }
    private suspend fun generateResponseIncrementally(aiMessageIndex: Int) {
        for (i in fullResponse.indices) {
            if (!isGenerating.value) break
            _currentChatMessages[aiMessageIndex] = ChatMessage(fullResponse.take(i + 1), false)
            delay(5)
        }
    }
    private fun resetGenerationState() {
        isGenerating.value = false
    }

    /**
     * Stops ongoing response generation
     */
    fun stopGeneration() {
        isGenerating.value = false
    }
}