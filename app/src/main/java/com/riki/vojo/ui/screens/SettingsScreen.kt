package com.riki.vojo.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.riki.vojo.Translations
import com.riki.vojo.ui.components.VojoBottomBar
import com.riki.vojo.ui.theme.*

@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("vojo_prefs", Context.MODE_PRIVATE) }
    val isProUser = prefs.getBoolean("is_pro_user", false)

    Scaffold(
        containerColor = VojoBlack,
        bottomBar = { VojoBottomBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Header
            Text(
                "⚙️ ${Translations.t("settings")}",
                fontSize = 24.sp,
                color = VojoTextWhite,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                Translations.t("settings_subtitle"),
                color = VojoTextGrey,
                fontSize = 13.sp
            )

            Spacer(Modifier.height(24.dp))

            // ── PRO Status ──
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isProUser) Color(0xFF1A2A1A) else VojoDarkGrey
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, if (isProUser) Color(0xFF4CAF50).copy(alpha = 0.5f) else VojoGold.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(if (isProUser) "👑" else "🔒", fontSize = 24.sp)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                if (isProUser) "VOJO PRO" else Translations.t("free_version"),
                                color = if (isProUser) VojoGold else VojoTextWhite,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp
                            )
                            Text(
                                if (isProUser) Translations.t("all_features_unlocked") else Translations.t("upgrade_for_more"),
                                color = VojoTextGrey,
                                fontSize = 12.sp
                            )
                        }
                    }
                    if (isProUser) {
                        Surface(
                            color = Color(0xFF4CAF50).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "ACTIVE",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color = Color(0xFF4CAF50),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            // ── FAQ Section ──
            Text(
                Translations.t("faq_title"),
                color = VojoGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )
            Spacer(Modifier.height(12.dp))

            val faqItems = listOf(
                Translations.t("faq_q1") to Translations.t("faq_a1"),
                Translations.t("faq_q2") to Translations.t("faq_a2"),
                Translations.t("faq_q3") to Translations.t("faq_a3"),
                Translations.t("faq_q4") to Translations.t("faq_a4"),
                Translations.t("faq_q5") to Translations.t("faq_a5"),
                Translations.t("faq_q6") to Translations.t("faq_a6"),
                Translations.t("faq_q7") to Translations.t("faq_a7")
            )

            faqItems.forEach { (question, answer) ->
                FaqCard(question = question, answer = answer)
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(28.dp))

            // ── App Info ──
            Text(
                Translations.t("app_info"),
                color = VojoGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )
            Spacer(Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = VojoDarkGrey),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    InfoRow(Icons.Default.Info, Translations.t("version"), "1.0.0")
                    HorizontalDivider(color = Color.DarkGray.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 8.dp))
                    InfoRow(Icons.Default.Person, Translations.t("developer"), "Vojo Team")
                    HorizontalDivider(color = Color.DarkGray.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 8.dp))
                    InfoRow(Icons.Default.Email, Translations.t("contact"), "support@vojoapp.com")
                }
            }

            Spacer(Modifier.height(28.dp))

            // ── Links ──
            Text(
                Translations.t("links"),
                color = VojoGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )
            Spacer(Modifier.height(12.dp))

            SettingsLinkButton(
                icon = Icons.Default.Star,
                text = Translations.t("rate_app"),
                onClick = {
                    try {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.riki.vojo")))
                    } catch (_: Exception) {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.riki.vojo")))
                    }
                }
            )
            Spacer(Modifier.height(8.dp))
            SettingsLinkButton(
                icon = Icons.Default.Security,
                text = Translations.t("privacy_policy"),
                onClick = {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://vojoapp.com/privacy")))
                }
            )
            Spacer(Modifier.height(8.dp))
            SettingsLinkButton(
                icon = Icons.Default.Description,
                text = Translations.t("terms_of_service"),
                onClick = {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://vojoapp.com/terms")))
                }
            )

            Spacer(Modifier.height(32.dp))

            // ── Footer ──
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("VOJO", color = VojoGold, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 4.sp)
                Text(
                    Translations.t("made_with_love"),
                    color = VojoTextGrey,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(4.dp))
                Text("© 2025 Vojo. All rights reserved.", color = VojoTextGrey.copy(alpha = 0.5f), fontSize = 10.sp)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun FaqCard(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = VojoDarkGrey),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    question,
                    color = VojoTextWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = VojoGold,
                    modifier = Modifier.size(24.dp)
                )
            }
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth().height(1.dp)
                            .background(Brush.horizontalGradient(listOf(Color.Transparent, VojoGold.copy(alpha = 0.3f), Color.Transparent)))
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        answer,
                        color = VojoTextGrey,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = VojoGold, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Text(label, color = VojoTextGrey, fontSize = 13.sp, modifier = Modifier.weight(1f))
        Text(value, color = VojoTextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SettingsLinkButton(icon: ImageVector, text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = VojoDarkGrey),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = VojoGold, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Text(text, color = VojoTextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, null, tint = VojoTextGrey, modifier = Modifier.size(20.dp))
        }
    }
}
