package com.riki.vojo.ui.screens

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.riki.vojo.Translations
import com.riki.vojo.ui.theme.*

data class LangOption(val lang: Translations.Lang, val flag: String, val name: String)

@Composable
fun WelcomeScreen(navController: NavController) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("vojo_prefs", Context.MODE_PRIVATE) }

    var selectedLang by remember {
        mutableStateOf(
            Translations.Lang.valueOf(prefs.getString("app_lang", "EN") ?: "EN")
        )
    }

    LaunchedEffect(Unit) {
        val savedLang = prefs.getString("app_lang", "EN") ?: "EN"
        Translations.currentLang = Translations.Lang.valueOf(savedLang)
    }

    Box(modifier = Modifier.fillMaxSize().background(VojoBlack)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.TravelExplore, contentDescription = null, tint = VojoGold, modifier = Modifier.size(100.dp))
            Spacer(modifier = Modifier.height(24.dp))
            Text("VOJO", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = VojoTextWhite, letterSpacing = 4.sp)
            Text(Translations.t("explore_collect_learn"), fontSize = 18.sp, color = VojoTextGrey)
            Spacer(modifier = Modifier.height(32.dp))

            Text(Translations.t("select_language"), color = VojoTextGrey, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf(
                    LangOption(Translations.Lang.EN, "🇬🇧", "English"),
                    LangOption(Translations.Lang.IT, "🇮🇹", "Italiano"),
                    LangOption(Translations.Lang.ES, "🇪🇸", "Español"),
                    LangOption(Translations.Lang.PL, "🇵🇱", "Polski"),
                ).forEach { opt ->
                    Surface(
                        color = if (selectedLang == opt.lang) VojoGold else VojoDarkGrey,
                        shape = RoundedCornerShape(12.dp),
                        border = if (selectedLang == opt.lang) null else BorderStroke(1.dp, Color.Gray),
                        modifier = Modifier.clickable { selectedLang = opt.lang }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(opt.flag, fontSize = 24.sp)
                            Text(
                                opt.name, fontSize = 10.sp,
                                color = if (selectedLang == opt.lang) VojoBlack else VojoTextWhite
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = {
                    Translations.currentLang = selectedLang
                    prefs.edit().putString("app_lang", selectedLang.name).apply()
                    val onboardingDone = prefs.getBoolean("onboarding_done", false)
                    if (onboardingDone) {
                        navController.navigate("map_screen")
                    } else {
                        navController.navigate("onboarding_screen")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = VojoGold),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(Translations.t("start_journey"), color = VojoBlack, fontWeight = FontWeight.Bold)
            }
        }
    }
}
