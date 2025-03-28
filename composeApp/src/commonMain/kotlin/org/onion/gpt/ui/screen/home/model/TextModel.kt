package org.onion.gpt.ui.screen.home.model

import org.jetbrains.compose.resources.DrawableResource



data class TextModel(
    val id: Int = nextId++,
    val title: String,
    val image: DrawableResource,
    val description: String,
    val provider: String
)
private var nextId = 0
