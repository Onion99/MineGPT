package org.onion.gpt.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import minegpt.composeapp.generated.resources.Res
import minegpt.composeapp.generated.resources.ic_help
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.onion.gpt.ui.screen.home.model.ChatMessage
import org.onion.gpt.ui.screen.home.model.TextModel
import org.onion.gpt.ui.utils.Animations

@Composable
fun HomeScreen() {
    Box(modifier = Modifier.fillMaxSize().background(Color.Blue)) {
        val chatViewModel = koinViewModel<ChatViewModel>()
        val chatMessages = chatViewModel.currentChatMessages
        var text by remember { mutableStateOf("") }
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current
        var attachButtonClicked by remember { mutableStateOf(false) }
        ChatMessagesList(chatMessages = chatMessages)
        AskAnythingField(
            modifier = Modifier.align(Alignment.BottomStart),
            onAttachClick = { attachButtonClicked = true },
            onSendClick = {
                if (chatViewModel.isGenerating.value) {
                    chatViewModel.stopGeneration()
                } else {
                    if (text.isNotEmpty()) {
                        chatViewModel.sendMessage(text)
                        text = ""
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                }
            },
            text = text,
            onTextChange = { text = it },
            isGenerating = chatViewModel.isGenerating.value
        )
    }
}

@Composable
private fun ChatMessagesList(chatMessages: List<ChatMessage>) {
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Track scroll position to show/hide button
    val showScrollButton by remember {
        derivedStateOf {
            val layoutInfo = lazyListState.layoutInfo
            val totalItems = chatMessages.size
            if (totalItems == 0) false else {
                val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                lastVisibleItem < totalItems - 1
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 70.dp, bottom = 80.dp)
        ) {
            items(chatMessages) { message ->
                ChatBubble(
                    message = message.message,
                    isUser = message.isUser,
                    aiIcon = Res.drawable.ic_help
                )
            }
        }

        ScrollToBottomButton(
            onClick = {
                coroutineScope.launch {
                    lazyListState.animateScrollToItem(chatMessages.lastIndex)
                }
            },
            visibility = showScrollButton,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 100.dp, end = 8.dp)
        )
    }

    // Existing auto-scroll logic
    val lastMessageLength by remember(chatMessages.size) {
        derivedStateOf { chatMessages.lastOrNull()?.message?.length ?: 0 }
    }

    LaunchedEffect(chatMessages.size, lastMessageLength) {
        if (chatMessages.isNotEmpty()) {
            val lastIndex = chatMessages.lastIndex
            val scrollThreshold = 3
            val layoutInfo = lazyListState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if ((visibleItems.lastOrNull()?.index ?: 0) >= lastIndex - scrollThreshold) {
                lazyListState.scrollToItem(lastIndex)
            }
        }
    }
}

// Extracted scroll-to-bottom button component
@Composable
private fun ScrollToBottomButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    visibility: Boolean
) {
    AnimatedVisibility(
        visible = visibility,
        enter = Animations.slideFadeIn(),
        exit = Animations.slideFadeOut(),
        modifier = modifier
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                )
                .shadow(4.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.KeyboardDoubleArrowDown,
                contentDescription = "Scroll to bottom",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun AskAnythingField(
    text: String,
    isGenerating: Boolean,
    modifier: Modifier = Modifier,
    onAttachClick: () -> Unit,
    onSendClick: () -> Unit,
    onTextChange: (String) -> Unit
) {
    Box(modifier) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 56.dp)
                .padding(start = 16.dp, end = 8.dp, top = 16.dp, bottom = 16.dp),
            shape = MaterialTheme.shapes.extraLarge,
            placeholder = {
                Text(
                    text = "Ask anything",
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            leadingIcon = {
                AttachIcon(onAttachClick = onAttachClick)
            },
            trailingIcon = {
                if (text.isNotEmpty()) {
                    ClearIcon(
                        show = text.isNotEmpty(),
                        onClick = { onTextChange("") }
                    )
                }
            },
            singleLine = true
        )

        SendStopButton(
            isGenerating = isGenerating,
            modifier = Modifier
                .padding(end = 16.dp)
                .size(40.dp)
                .align(Alignment.CenterEnd),
            onClick = onSendClick,
        )
    }
}
@Composable
private fun AttachIcon(
    onAttachClick: () -> Unit
) = IconButton(onAttachClick) {
    Icon(
        imageVector = Icons.Filled.AttachFile,
        contentDescription = "Attachment",
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(20.dp)
    )
}

@Composable
private fun ClearIcon(
    show: Boolean,
    onClick: () -> Unit
) {
    if (show) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Clear",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
@Composable
fun SendStopButton(
    isGenerating: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier

    ) {
        Icon(
            imageVector = if (isGenerating) Icons.Filled.Stop
            else Icons.AutoMirrored.Filled.Send,
            contentDescription = if (isGenerating) "Stop generation" else "Send message",
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun ChatBubble(
    message: String,
    isUser: Boolean,
    aiIcon: DrawableResource
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = if (isUser) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onBackground.copy(
                        alpha = 0.5f
                    ),
                )
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            ChatBubbleIcon(isUser = isUser, aiIcon = aiIcon)


            ChatBubbleMessage(
                message = message,
                isUser = isUser
            )
        }
    }
}

@Composable
private fun ChatBubbleIcon(
    isUser: Boolean,
    aiIcon: DrawableResource
) {
    if (isUser) {
        UserIcon()
    } else {
        AiProviderIcon(aiIcon = aiIcon)
    }
}

@Composable
private fun UserIcon() {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(
                MaterialTheme.colorScheme.background,
                CircleShape
            ) // Background for the icon
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "User Icon",
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun AiProviderIcon(aiIcon: DrawableResource) {
    Image(
        painter = painterResource(aiIcon),
        contentDescription = "AI Icon",
        modifier = Modifier.size(30.dp)
    )
}

@Composable
private fun ChatBubbleMessage(
    message: String,
    isUser: Boolean
) {
    if (isUser) {
        UserMessage(message = message)
    } else {
        AiMessage(
            message = message
        )
    }
}

@Composable
private fun UserMessage(message: String) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 8.dp) // Add top padding to align with the icon
    )
}

@Composable
private fun AiMessage(
    message: String
) {

    Text(
        text = message,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 8.dp)
    )
}