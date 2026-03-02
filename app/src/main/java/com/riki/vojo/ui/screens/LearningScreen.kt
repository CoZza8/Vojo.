package com.riki.vojo.ui.screens

import android.speech.tts.TextToSpeech
import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.riki.vojo.LearningData
import com.riki.vojo.LearningManager
import com.riki.vojo.Translations
import com.riki.vojo.ui.components.DarkFilterChip
import com.riki.vojo.ui.components.VojoBottomBar
import com.riki.vojo.ui.theme.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningScreen(navController: NavController, tts: TextToSpeech?, isProUser: Boolean = false) {
    val context = LocalContext.current

    var swipesAvailable by remember { mutableIntStateOf(if (isProUser) 999 else LearningManager.refreshAndGetTotal(context)) }
    var learnedCount by remember { mutableIntStateOf(LearningManager.getTotalLearnedCount(context)) }
    var currentIndex by remember { mutableIntStateOf(learnedCount) }
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = remember {
        listOf("All") + LearningData.allPhrases.map { it.category }.distinct().sorted()
    }

    val filteredPhrases = remember(selectedCategory) {
        if (selectedCategory == "All") LearningData.allPhrases
        else LearningData.allPhrases.filter { it.category == selectedCategory }
    }

    val currentPhrase = if (currentIndex < filteredPhrases.size) filteredPhrases[currentIndex] else null

    Scaffold(
        containerColor = VojoBlack,
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(Translations.t("italian"), color = VojoTextWhite) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = VojoBlack),
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = VojoTextWhite)
                        }
                    }
                )
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = VojoBlack,
                    contentColor = VojoGold,
                    indicator = { tabPositions ->
                        if (selectedTab < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = VojoGold
                            )
                        }
                    }
                ) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 },
                        text = { Text(Translations.t("flashcards"), fontWeight = FontWeight.Bold) })
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 },
                        text = { Text("${Translations.t("learned_words")} ($learnedCount)", fontWeight = FontWeight.Bold) })
                }
            }
        },
        bottomBar = { VojoBottomBar(navController) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            if (selectedTab == 0) {
                // ── Category filter chips ──
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories.size) { index ->
                        val cat = categories[index]
                        DarkFilterChip(
                            selected = selectedCategory == cat,
                            label = cat,
                            onClick = {
                                selectedCategory = cat
                                currentIndex = 0
                            },
                            isSmall = true
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                Card(colors = CardDefaults.cardColors(containerColor = VojoDarkGrey), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(Translations.t("daily_words_left"), color = VojoTextGrey, fontSize = 12.sp)
                            if (isProUser) {
                                Text("∞ PRO", color = VojoGold, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text("$swipesAvailable", color = VojoGold, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                                    Text(" / 10", color = VojoTextGrey, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp))
                                }
                            }
                        }
                        if (swipesAvailable > 10 && !isProUser) {
                            Text("${Translations.t("bonus_active")} 🚀", color = Color.Green, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        if (isProUser) {
                            Text("👑 UNLIMITED", color = VojoGold, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))

                if (swipesAvailable > 0 && currentPhrase != null) {
                    Text(Translations.t("tap_listen_swipe"), color = VojoTextGrey, fontSize = 14.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().weight(1f)) {
                        SwipeableFlashcard(
                            phrase = currentPhrase,
                            onSwipeNext = {
                                if (!LearningData.learnedPhrases.contains(currentPhrase)) {
                                    LearningData.learnedPhrases.add(0, currentPhrase)
                                }
                                LearningManager.consumeSwipe(context)
                                swipesAvailable = if (isProUser) 999 else LearningManager.refreshAndGetTotal(context)
                                learnedCount = LearningManager.getTotalLearnedCount(context)
                                currentIndex++
                            },
                            onPlayAudio = {
                                tts?.speak(currentPhrase.translated, TextToSpeech.QUEUE_FLUSH, null, null)
                            }
                        )
                    }
                } else if (swipesAvailable <= 0) {
                    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Lock, null, tint = VojoGold, modifier = Modifier.size(80.dp))
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(Translations.t("daily_limit"), color = VojoTextWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Come back tomorrow for +5 words\nor visit Monuments to get Bonuses!", color = VojoTextGrey, textAlign = TextAlign.Center, lineHeight = 24.sp)
                        Spacer(modifier = Modifier.height(24.dp))

                        // ── PRO Upsell Card ──
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1610)),
                            shape = RoundedCornerShape(20.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, VojoGold.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("👑", fontSize = 32.sp)
                                Spacer(Modifier.height(8.dp))
                                Text("UNLOCK UNLIMITED PHRASES", color = VojoGold, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, letterSpacing = 1.sp)
                                Spacer(Modifier.height(8.dp))
                                Text("No daily limits • Learn at your pace", color = VojoTextGrey, fontSize = 12.sp)
                                Spacer(Modifier.height(16.dp))
                                Button(
                                    onClick = {
                                        (context as? android.app.Activity)?.let { activity ->
                                            com.riki.vojo.billing.BillingManager.launchPurchase(activity)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = VojoGold),
                                    modifier = Modifier.fillMaxWidth().height(50.dp),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Text("👑", fontSize = 16.sp)
                                    Spacer(Modifier.width(8.dp))
                                    Text("GET PRO — €4.99", color = Color(0xFF0C0A08), fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { selectedTab = 1 }, colors = ButtonDefaults.buttonColors(containerColor = VojoDarkGrey), modifier = Modifier.fillMaxWidth().height(50.dp)) {
                            Text(Translations.t("review_learned"), color = VojoTextWhite)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { navController.navigate("map_screen") }, colors = ButtonDefaults.buttonColors(containerColor = VojoDarkGrey), modifier = Modifier.fillMaxWidth().height(50.dp), border = androidx.compose.foundation.BorderStroke(1.dp, VojoGold.copy(alpha = 0.3f))) {
                            Text(Translations.t("go_to_map"), color = VojoTextWhite, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CheckCircle, null, tint = VojoGold, modifier = Modifier.size(64.dp))
                        Text(text = Translations.t("all_phrases_learned"), color = VojoTextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // ── Learned Words tab ──
                if (LearningData.learnedPhrases.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(Translations.t("no_words_yet"), color = VojoTextGrey, textAlign = TextAlign.Center)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(LearningData.learnedPhrases) { phrase ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = VojoDarkGrey),
                                modifier = Modifier.fillMaxWidth().clickable {
                                    tts?.speak(phrase.translated, TextToSpeech.QUEUE_FLUSH, null, null)
                                }
                            ) {
                                Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(phrase.translated, color = VojoTextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                        if (phrase.pronunciation != null) Text("/${phrase.pronunciation}/", color = VojoGold, fontSize = 12.sp, fontStyle = FontStyle.Italic)
                                        Text(phrase.original, color = VojoTextGrey, fontSize = 14.sp)
                                    }
                                    Icon(Icons.AutoMirrored.Filled.VolumeUp, null, tint = VojoGold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SwipeableFlashcard(phrase: LearningData.Phrase, onSwipeNext: () -> Unit, onPlayAudio: () -> Unit) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val width = 300.dp
    val draggableState = rememberDraggableState { delta -> offsetX += delta }

    LaunchedEffect(offsetX) {
        if (offsetX > 300f || offsetX < -300f) {
            onSwipeNext()
            offsetX = 0f
        }
    }

    Card(
        modifier = Modifier
            .width(width)
            .height(400.dp)
            .offset { IntOffset(offsetX.roundToInt(), 0) }
            .draggable(orientation = Orientation.Horizontal, state = draggableState, onDragStopped = { if (offsetX > -300f && offsetX < 300f) offsetX = 0f })
            .clickable { onPlayAudio() },
        colors = CardDefaults.cardColors(containerColor = VojoDarkGrey),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(phrase.category.uppercase(), color = VojoGold, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Spacer(modifier = Modifier.height(32.dp))
            Text(phrase.translated, color = VojoTextWhite, fontSize = 32.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, lineHeight = 40.sp)
            Spacer(modifier = Modifier.height(16.dp))
            if (phrase.pronunciation != null) Text("/${phrase.pronunciation}/", color = Color.Gray, fontSize = 16.sp, fontStyle = FontStyle.Italic)
            Spacer(modifier = Modifier.height(32.dp))
            Text(phrase.original, color = VojoTextGrey, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(48.dp))
            Icon(Icons.AutoMirrored.Filled.VolumeUp, null, tint = VojoGold, modifier = Modifier.size(32.dp))
            Text("Tap to listen", color = Color.DarkGray, fontSize = 10.sp)
        }
    }
}
