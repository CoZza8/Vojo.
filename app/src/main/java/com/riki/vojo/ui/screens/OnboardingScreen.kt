package com.riki.vojo.ui.screens

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.riki.vojo.Translations
import com.riki.vojo.ui.theme.*
import kotlinx.coroutines.launch

data class OnboardingPage(
    val emoji: String,
    val titleKey: String,
    val descKey: String,
    val bgGradient: List<Color>
)

@Composable
fun OnboardingScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val pages = listOf(
        OnboardingPage(
            emoji = "🗺️",
            titleKey = "onboard_explore_title",
            descKey = "onboard_explore_desc",
            bgGradient = listOf(Color(0xFF0D1B0E), Color(0xFF1A2E14), Color(0xFF0D1B0E))
        ),
        OnboardingPage(
            emoji = "⚔️",
            titleKey = "onboard_quests_title",
            descKey = "onboard_quests_desc",
            bgGradient = listOf(Color(0xFF1A1510), Color(0xFF251E10), Color(0xFF1A1510))
        ),
        OnboardingPage(
            emoji = "📖",
            titleKey = "onboard_learn_title",
            descKey = "onboard_learn_desc",
            bgGradient = listOf(Color(0xFF0D0F1B), Color(0xFF141A2E), Color(0xFF0D0F1B))
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })

    fun finishOnboarding() {
        context.getSharedPreferences("vojo_prefs", Context.MODE_PRIVATE)
            .edit().putBoolean("onboarding_done", true).apply()
        navController.navigate("map_screen") {
            popUpTo("onboarding_screen") { inclusive = true }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(VojoBlack)) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val p = pages[page]
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(p.bgGradient)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Emoji icon
                    Text(p.emoji, fontSize = 80.sp)
                    Spacer(Modifier.height(32.dp))

                    // Title
                    Text(
                        Translations.t(p.titleKey),
                        color = VojoGold,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        letterSpacing = 2.sp,
                        lineHeight = 36.sp
                    )
                    Spacer(Modifier.height(16.dp))

                    // Description
                    Text(
                        Translations.t(p.descKey),
                        color = VojoTextGrey,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )
                }
            }
        }

        // Bottom controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Page indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                repeat(pages.size) { i ->
                    Box(
                        modifier = Modifier
                            .size(if (pagerState.currentPage == i) 12.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (pagerState.currentPage == i) VojoGold
                                else VojoGold.copy(alpha = 0.25f)
                            )
                    )
                }
            }

            // Button
            if (pagerState.currentPage == pages.size - 1) {
                // Last page — Start button
                Button(
                    onClick = { finishOnboarding() },
                    colors = ButtonDefaults.buttonColors(containerColor = VojoGold),
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("🏛️", fontSize = 18.sp)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        Translations.t("start_exploring"),
                        color = VojoBlack,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                }
            } else {
                // Next button
                Button(
                    onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
                    colors = ButtonDefaults.buttonColors(containerColor = VojoGold),
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        Translations.t("next"),
                        color = VojoBlack,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                }
                Spacer(Modifier.height(12.dp))
                TextButton(onClick = { finishOnboarding() }) {
                    Text(
                        Translations.t("skip"),
                        color = VojoTextGrey,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
