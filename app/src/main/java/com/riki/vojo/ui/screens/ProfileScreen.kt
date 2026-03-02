package com.riki.vojo.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.riki.vojo.*
import com.riki.vojo.presentation.ProViewModel
import com.riki.vojo.presentation.ProViewModelFactory
import com.riki.vojo.ui.components.*
import com.riki.vojo.ui.theme.*
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = ProViewModelFactory(LocalContext.current)
    )
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("vojo_prefs", Context.MODE_PRIVATE) }

    val isUserPro by viewModel.isProUser.collectAsState()
    val selectedAvatarIndex by viewModel.selectedAvatarIndex.collectAsState()
    val availableAvatars = viewModel.availableAvatars

    val currentAvatarResId = remember(selectedAvatarIndex, isUserPro) {
        availableAvatars.find { it.id == selectedAvatarIndex }?.resId ?: com.riki.vojo.R.drawable.colosseum
    }

    val totalLearned = LearningManager.getTotalLearnedCount(context)
    val swipes = LearningManager.refreshAndGetTotal(context)
    val stampsCount = PassportData.stamps.size
    val lastStamp = PassportData.stamps.firstOrNull()

    var userName by remember { mutableStateOf(prefs.getString("user_nick", "Vojo Explorer") ?: "Vojo Explorer") }
    var showGoProDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf(userName) }

    val explorerLevel = (stampsCount * 2) + (totalLearned / 5)

    // ── Edit Profile Dialog ──
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            containerColor = VojoDarkGrey,
            title = { Text(Translations.t("commander_profile"), color = VojoGold, fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { if (it.length <= 15) tempName = it },
                        label = { Text(Translations.t("nickname"), color = VojoGold) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                            focusedBorderColor = VojoGold, unfocusedBorderColor = Color.Gray
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(20.dp))
                    Text(Translations.t("select_avatar"), color = VojoTextGrey, fontSize = 12.sp)
                    Spacer(Modifier.height(12.dp))
                    Box(modifier = Modifier.height(200.dp)) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(availableAvatars) { avatar ->
                                AvatarSelectionItem(
                                    resId = avatar.resId,
                                    isSelected = (selectedAvatarIndex == avatar.id)
                                ) { viewModel.selectAvatar(avatar.id) }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        userName = tempName
                        prefs.edit().putString("user_nick", userName).apply()
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VojoGold)
                ) {
                    Text(text = Translations.t("save"), color = VojoBlack, fontWeight = FontWeight.ExtraBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("CANCEL", color = VojoTextGrey)
                }
            }
        )
    }

    // ── Main Screen ──
    Scaffold(
        containerColor = VojoBlack,
        bottomBar = { VojoBottomBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(Translations.t("commander_center"), fontSize = 14.sp, color = VojoGold, letterSpacing = 3.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(24.dp))

            // Avatar
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier.clickable { tempName = userName; showEditDialog = true }
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(currentAvatarResId)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.size(110.dp).clip(CircleShape).background(VojoDarkGrey).border(2.dp, VojoGold, CircleShape),
                    contentScale = ContentScale.Crop
                )
                Surface(
                    color = VojoGold, shape = CircleShape,
                    modifier = Modifier.size(32.dp).border(2.dp, VojoBlack, CircleShape)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(explorerLevel.toString(), color = VojoBlack, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Text(userName, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = VojoTextWhite, modifier = Modifier.padding(top = 16.dp))
            Text("Rome Scout • Level $explorerLevel", color = VojoGold, fontSize = 12.sp)

            Spacer(Modifier.height(32.dp))

            // Stats
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                ProfileStatCard(Translations.t("stamps"), stampsCount.toString(), VojoGold)
                ProfileStatCard(Translations.t("phrases"), totalLearned.toString(), Color(0xFF4CAF50))
                ProfileStatCard(Translations.t("power"), swipes.toString(), Color(0xFF2196F3))
            }

            Spacer(Modifier.height(32.dp))

            // Achievements
            Text(Translations.t("achievements"), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start, color = VojoTextGrey, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                AchievementIcon(icon = Icons.Default.LocalPizza, name = "Foodie", unlocked = stampsCount >= 5, hint = "Collect 5 food stamps")
                AchievementIcon(icon = Icons.Default.HistoryEdu, name = "Scholar", unlocked = totalLearned >= 20, hint = "Learn 20 new words!")
                AchievementIcon(icon = Icons.Default.Camera, name = "Paparazzi", unlocked = stampsCount >= 10, hint = "Document 10 historic sites")
                AchievementIcon(icon = Icons.Default.Star, name = "Elite", unlocked = explorerLevel >= 15, hint = "Reach Scout Level 15")
            }

            Spacer(Modifier.height(32.dp))

            // Last discovery
            if (lastStamp != null) {
                Text(Translations.t("last_discovery"), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start, color = VojoTextGrey, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = VojoDarkGrey)) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        if (lastStamp.image != null) {
                            Image(bitmap = lastStamp.image.asImageBitmap(), null, Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
                        }
                        Column(Modifier.padding(start = 16.dp)) {
                            Text(lastStamp.placeName, color = Color.White, fontWeight = FontWeight.Bold)
                            Text(lastStamp.date, color = VojoTextGrey, fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // ── SETTINGS ──
            Button(
                onClick = { navController.navigate("settings_screen") },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VojoDarkGrey),
                border = BorderStroke(1.dp, VojoGold.copy(alpha = 0.3f))
            ) {
                Icon(Icons.Default.Settings, null, tint = VojoGold, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(12.dp))
                Text(Translations.t("settings"), color = VojoTextWhite, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(24.dp))

            // ── AFFILIATE HUB ──
            Text("🧳 TRAVEL SERVICES", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start, color = VojoGold, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
            Spacer(Modifier.height(12.dp))
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = VojoDarkGrey), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = { openVojoArticle(context, "https://www.booking.com") },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003580)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Hotel, null, tint = Color.White)
                        Spacer(Modifier.width(12.dp))
                        Text("🏨 Book Hotels", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { openVojoArticle(context, "https://www.getyourguide.com") },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5533)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.ConfirmationNumber, null, tint = Color.White)
                        Spacer(Modifier.width(12.dp))
                        Text("🎟️ Book Guides & Tickets", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // PRO button
            if (!isUserPro) {
                Button(
                    onClick = { showGoProDialog = true },
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VojoGold)
                ) {
                    Icon(Icons.Default.Stars, null, tint = VojoBlack)
                    Spacer(Modifier.width(12.dp))
                    Text(Translations.t("upgrade_pro"), color = VojoBlack, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                }
                Spacer(Modifier.height(12.dp))
            }

            // Edit profile button
            Button(
                onClick = { tempName = userName; showEditDialog = true },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VojoDarkGrey),
                border = BorderStroke(1.dp, VojoGold.copy(alpha = 0.5f))
            ) {
                Icon(Icons.Default.Edit, null, tint = VojoGold, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(12.dp))
                Text(Translations.t("edit_profile"), color = VojoTextWhite, fontWeight = FontWeight.Bold)
            }

            // ── DEBUG ONLY: PRO Toggle (stripped in release builds) ──
            if (com.riki.vojo.BuildConfig.DEBUG) {
                Spacer(Modifier.height(24.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A0000)),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("🔧 DEBUG MODE", color = Color.Red, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                            Text(
                                if (isUserPro) "PRO ✅ Active" else "FREE Mode",
                                color = if (isUserPro) Color(0xFF4CAF50) else Color.Gray,
                                fontSize = 14.sp, fontWeight = FontWeight.Bold
                            )
                        }
                        Switch(
                            checked = isUserPro,
                            onCheckedChange = { viewModel.toggleProDebug() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF4CAF50),
                                checkedTrackColor = Color(0xFF1B5E20),
                                uncheckedThumbColor = Color.Gray,
                                uncheckedTrackColor = Color.DarkGray
                            )
                        )
                    }
                }
            }
        }
    }

    // ── PRO Purchase Dialog ──
    if (showGoProDialog) {
        AlertDialog(
            onDismissRequest = { showGoProDialog = false },
            containerColor = Color(0xFF0D0D0D),
            shape = RoundedCornerShape(24.dp),
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Crown badge
                    Surface(
                        color = VojoGold.copy(alpha = 0.15f),
                        shape = CircleShape,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("👑", fontSize = 32.sp)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "VOJO PRO",
                        color = VojoGold,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 28.sp,
                        letterSpacing = 4.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "The Ultimate Rome Experience",
                        color = VojoTextGrey,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth().height(1.dp)
                            .background(Brush.horizontalGradient(listOf(Color.Transparent, VojoGold.copy(alpha = 0.4f), Color.Transparent)))
                    )
                }
            },
            text = {
                Column {
                    // Feature list
                    val features = listOf(
                        "🏛️" to "Interactive Roman Quests",
                        "🚫" to "No Ads — Ever",
                        "📖" to "Unlimited Italian Phrases",
                        "🗺️" to "Offline Maps Download",
                        "🌙" to "Night Mode for Party-Goers",
                        "👤" to "4 Premium Avatars",
                        "🏆" to "Secret Hidden Gems Unlocked",
                        "⭐" to "Priority New Features"
                    )
                    features.forEach { (emoji, text) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(emoji, fontSize = 18.sp)
                            Spacer(Modifier.width(12.dp))
                            Text(text, color = Color.White, fontSize = 14.sp)
                        }
                    }
                    Spacer(Modifier.height(16.dp))

                    // Price badge — also the buy button
                    Surface(
                        color = VojoGold,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().clickable {
                            // Launch Google Play Billing purchase flow
                            (context as? android.app.Activity)?.let { activity ->
                                com.riki.vojo.billing.BillingManager.launchPurchase(activity)
                            }
                            showGoProDialog = false
                        }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("ONE-TIME PURCHASE", color = VojoBlack, fontSize = 10.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Stars, null, tint = VojoBlack, modifier = Modifier.size(24.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("€4.99", color = VojoBlack, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                            }
                            Text("No subscriptions • Forever yours", color = VojoBlack.copy(alpha = 0.7f), fontSize = 11.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    TextButton(onClick = { showGoProDialog = false }) {
                        Text(Translations.t("maybe_later"), color = VojoTextGrey, fontSize = 13.sp)
                    }
                }
            }
        )
    }
}

@Composable
fun AvatarSelectionItem(resId: Int, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(60.dp).clip(CircleShape)
            .background(if (isSelected) VojoGold.copy(alpha = 0.3f) else Color.Transparent)
            .border(2.dp, if (isSelected) VojoGold else Color.Transparent, CircleShape)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(resId)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier.fillMaxSize().clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun AchievementIcon(icon: ImageVector, name: String, unlocked: Boolean, hint: String) {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable {
            val message = if (unlocked) "Unlocked: $name!" else "How to achieve: $hint"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    ) {
        Box(
            modifier = Modifier.size(50.dp)
                .background(if (unlocked) VojoGold.copy(alpha = 0.2f) else Color.DarkGray.copy(alpha = 0.1f), CircleShape)
                .border(1.dp, if (unlocked) VojoGold else Color.Transparent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = name, tint = if (unlocked) VojoGold else Color.DarkGray, modifier = Modifier.size(28.dp))
        }
        Spacer(Modifier.height(4.dp))
        Text(name, fontSize = 10.sp, color = if (unlocked) Color.White else Color.DarkGray)
    }
}
