package org.onion.gpt.ui.navigation.destination

import androidx.navigation.NamedNavArgument
import minegpt.composeapp.generated.resources.Res
import minegpt.composeapp.generated.resources.home
import minegpt.composeapp.generated.resources.ic_help
import minegpt.composeapp.generated.resources.unknown
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

sealed class Screen(
    val route: String,
    val iconRes: DrawableResource = Res.drawable.ic_help,
    val textRes: StringResource = Res.string.unknown,
    val navArguments: List<NamedNavArgument> = emptyList()
)