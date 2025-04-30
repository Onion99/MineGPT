package org.onion.gpt.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import minegpt.composeapp.generated.resources.ic_avatar_sytem
import minegpt.composeapp.generated.resources.ic_avatar_user
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.onion.gpt.ui.screen.home.model.ChatMessage
import org.onion.gpt.ui.theme.MediumOutlinedTextField
import org.onion.gpt.ui.theme.MediumText
import org.onion.gpt.ui.utils.Animations

@Composable
fun HomeScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val chatViewModel = koinViewModel<ChatViewModel>()
        val chatMessages = chatViewModel.currentChatMessages
        var text by remember { mutableStateOf("") }
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()

        coroutineScope.launch {
            chatViewModel.initLLM()
        }
        ChatMessagesList(chatMessages = chatMessages)
        AskAnythingField(
            modifier = Modifier.align(Alignment.BottomStart),
            onAttachClick = {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("功能暂不可用")
                }
            },
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

        // Snackbar Host
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp),
            snackbar = { snackbarData ->
                Snackbar(
                    snackbarData,
                    modifier = Modifier
                        .widthIn(min = 100.dp, max = 300.dp)
                        .heightIn(min = 40.dp, max = 120.dp)
                        .padding(8.dp),
                    shape = RoundedCornerShape(26.dp),
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
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
                .padding(top = 70.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(chatMessages) { message ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = if (message.isUser) 64.dp else 16.dp,
                            end = if (message.isUser) 16.dp else 64.dp,
                            top = 4.dp,
                            bottom = 4.dp
                        )
                ) {
                    ChatBubble(
                        message = message.message,
                        isUser = message.isUser
                    )
                }
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
                .padding(bottom = 100.dp, end = 16.dp)
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
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                )
                .shadow(6.dp, CircleShape)
                .size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.KeyboardDoubleArrowDown,
                contentDescription = "滚动到底部",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(24.dp)
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
    Box(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = 8.dp,
                shape = MaterialTheme.shapes.extraLarge,
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.extraLarge
            )
    ) {
        MediumOutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 56.dp)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            shape = MaterialTheme.shapes.extraLarge,
            placeholder = {
                MediumText(
                    text = "有什么可以帮您？",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
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
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            )
        )

        SendStopButton(
            isGenerating = isGenerating,
            modifier = Modifier
                .padding(end = 12.dp)
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
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            )
    ) {
        Icon(
            imageVector = if (isGenerating) Icons.Filled.Stop
            else Icons.AutoMirrored.Filled.Send,
            contentDescription = if (isGenerating) "停止生成" else "发送消息",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun ChatBubble(
    message: String,
    isUser: Boolean
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = if (isUser)
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                    else
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(
                        topStart = if (isUser) 16.dp else 2.dp,
                        topEnd = if (isUser) 2.dp else 16.dp,
                        bottomStart = 16.dp,
                        bottomEnd = 16.dp
                    )
                )
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ChatBubbleIcon(isUser = isUser)

            ChatBubbleMessage(
                message = message,
                isUser = isUser
            )
        }
    }
}

@Composable
private fun ChatBubbleIcon(isUser: Boolean) {
    if (isUser) {
        UserIcon()
    } else {
        AiProviderIcon()
    }
}

@Composable
private fun UserIcon() {
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                shape = CircleShape
            )
            .padding(6.dp)
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_avatar_user),
            contentDescription = "用户头像",
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun AiProviderIcon() {
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(
                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
                shape = CircleShape
            )
            .padding(6.dp)
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_avatar_sytem),
            contentDescription = "AI头像",
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun ChatBubbleMessage(
    message: String,
    isUser: Boolean
) {
    if (isUser) {
        UserMessage(message = message)
    } else {
        AiMessage(message = message)
    }
}

@Composable
private fun UserMessage(message: String) {
    MediumText(
        text = message,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = Modifier
            .padding(top = 4.dp, end = 8.dp)
            .fillMaxWidth()
    )
}

@Composable
private fun AiMessage(
    message: String
) {
    MediumText(
        text = message,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .padding(top = 4.dp, end = 8.dp)
            .fillMaxWidth()
    )
}