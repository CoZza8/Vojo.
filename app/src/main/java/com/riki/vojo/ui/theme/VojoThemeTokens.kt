package com.riki.vojo.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

// ── Vojo Design Tokens ──
val VojoBlack = Color(0xFF121212)
val VojoDarkGrey = Color(0xFF1E1E1E)
val VojoGold = Color(0xFFFFD700)
val VojoTextWhite = Color(0xFFEEEEEE)
val VojoTextGrey = Color(0xFFAAAAAA)

// Quest Map palette
val RomeGreen = Color(0xFF2D5016)
val RomeSand = Color(0xFFC4A265)
val RomeStone = Color(0xFF8B7355)
val QuestBg = Color(0xFF0D1B0E)

enum class SortOption { NONE, RATING, NEAREST, CHEAPEST }

var isNightMode by mutableStateOf(false)
