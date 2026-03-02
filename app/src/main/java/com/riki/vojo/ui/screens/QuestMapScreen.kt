package com.riki.vojo.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.riki.vojo.*
import com.riki.vojo.ui.components.VojoBottomBar
import com.riki.vojo.ui.theme.*
import io.github.sceneview.SceneView
import io.github.sceneview.math.Position
import io.github.sceneview.math.Scale
import io.github.sceneview.node.ModelNode

// ── Premium Roman color palette ──
private val QuestDarkBg = Color(0xFF0C0A08)
private val QuestCardBg = Color(0xFF1A1610)
private val QuestAccent = Color(0xFFD4AF37)       // Imperial gold
private val QuestAccentDim = Color(0xFF8B7340)
private val QuestComplete = Color(0xFF4E8C3F)
private val QuestLocked = Color(0xFF252220)
private val QuestRoad = Color(0xFF2A1F15)
private val QuestRoadEdge = Color(0xFF3D2E1F)
private val QuestTextPrimary = Color(0xFFF5EDD6)
private val QuestTextSecondary = Color(0xFF9E9482)
private val QuestMarble = Color(0xFF1E1B16)
private val QuestBronze = Color(0xFFCD7F32)
private val QuestRedAccent = Color(0xFF8B2500)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestMapScreen(isUserPro: Boolean, navController: NavController? = null) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val prefs = remember { context.getSharedPreferences("vojo_quest_prefs", Context.MODE_PRIVATE) }

    var completedQuests by remember {
        mutableStateOf(prefs.getStringSet("completed_quests", emptySet())?.toSet() ?: emptySet())
    }
    var currentQuestIndex by remember { mutableIntStateOf(prefs.getInt("current_quest_index", 0)) }
    var activeQuest by remember { mutableStateOf<QuestMonument?>(null) }
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    var showResult by remember { mutableStateOf(false) }
    var showFunFact by remember { mutableStateOf(false) }
    var showProDialog by remember { mutableStateOf(false) }
    var showUnlockReward by remember { mutableStateOf(false) }
    var showRateDialog by remember { mutableStateOf(false) }

    // Delay SceneView rendering to prevent flash on tab switch
    var showScene by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(800)
        showScene = true
    }

    val monuments = QuestData.monuments

    fun completeQuest(questId: String) {
        val updated = completedQuests + questId
        completedQuests = updated
        val nextIndex = (currentQuestIndex + 1).coerceAtMost(monuments.size - 1)
        currentQuestIndex = nextIndex
        prefs.edit()
            .putStringSet("completed_quests", updated)
            .putInt("current_quest_index", nextIndex)
            .apply()
        // Show interstitial ad for free users after quest completion
        if (!isUserPro) {
            (context as? android.app.Activity)?.let { activity ->
                com.riki.vojo.ads.AdManager.showInterstitial(activity, isUserPro)
            }
        }
    }

    fun isQuestAvailable(quest: QuestMonument, index: Int): Boolean {
        if (index == 0) return true
        if (quest.isPro && !isUserPro) return false
        return index <= currentQuestIndex
    }

    fun isCompleted(questId: String) = completedQuests.contains(questId)

    // ── Animations ──
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.08f,
        animationSpec = infiniteRepeatable(animation = tween(1200, easing = EaseInOutCubic), repeatMode = RepeatMode.Reverse),
        label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(1200, easing = EaseInOutCubic), repeatMode = RepeatMode.Reverse),
        label = "pulseAlpha"
    )
    val glowRadius by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 6f,
        animationSpec = infiniteRepeatable(animation = tween(1500, easing = EaseInOutCubic), repeatMode = RepeatMode.Reverse),
        label = "glow"
    )

    Scaffold(
        containerColor = QuestDarkBg,
        bottomBar = { if (navController != null) VojoBottomBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF1A1510),
                            Color(0xFF0F0D0A),
                            Color(0xFF141008),
                            Color(0xFF0C0A08)
                        )
                    )
                )
        ) {
            // ══════════════════════════════════════
            // ── ROMAN HEADER with laurel wreath ──
            // ══════════════════════════════════════
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF1C1810), Color(0xFF12100A))
                        )
                    )
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("🏛️", fontSize = 22.sp)
                            Column {
                                Text(
                                    "VIA ROMANA",
                                    color = QuestAccent,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 3.sp
                                )
                                Text(
                                    "⚔️ ${Translations.t("via_romana_subtitle")} ⚔️",
                                    color = QuestTextSecondary,
                                    fontSize = 10.sp,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                        // Trophy counter
                        Surface(
                            color = QuestCardBg,
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, QuestAccent.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("🏆", fontSize = 14.sp)
                                Text(
                                    "${completedQuests.size}/${monuments.size}",
                                    color = QuestAccent,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // ── Progress bar ──
                    val progress = completedQuests.size.toFloat() / monuments.size.toFloat()
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF1A1510))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(progress)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(QuestBronze, QuestAccent, Color(0xFFFFD700))
                                        )
                                    )
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                if (completedQuests.size >= monuments.size) "🏆 ${Translations.t("all_quests_complete")}" else "🔓 ${Translations.t("complete_to_unlock")}",
                                color = if (completedQuests.size >= monuments.size) QuestAccent else QuestTextSecondary,
                                fontSize = 9.sp,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                "${(progress * 100).toInt()}%",
                                color = QuestAccent,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Gold divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color.Transparent, QuestAccent.copy(alpha = 0.5f), QuestAccent, QuestAccent.copy(alpha = 0.5f), Color.Transparent)
                            )
                        )
                )
            }

            // ══════════════════════════════════
            // ── 3D Scene View (Colosseum) ──
            // ══════════════════════════════════
            Box(
                modifier = Modifier
                    .weight(0.35f)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF12100A), QuestDarkBg, Color(0xFF0D0B08))
                        )
                    )
            ) {
                if (showScene) {
                    AndroidView(
                        factory = { ctx ->
                            SceneView(ctx).apply {
                                setBackgroundColor(android.graphics.Color.parseColor("#0C0A08"))
                                try {
                                    val modelNode = ModelNode(
                                        modelInstance = modelLoader.createModelInstance(
                                            assetFileLocation = "Colosseum.glb"
                                        ),
                                        scaleToUnits = 1.0f
                                    ).apply {
                                        position = Position(x = 0f, y = -0.5f, z = -2.5f)
                                        scale = Scale(0.8f)
                                    }
                                    addChildNode(modelNode)

                                    val scoutNode = ModelNode(
                                        modelInstance = modelLoader.createModelInstance(
                                            assetFileLocation = "uploads_files_3335707_Spartan_base_mesh.obj"
                                        ),
                                        scaleToUnits = 0.3f
                                    ).apply {
                                        val scoutX = -1.5f + (currentQuestIndex * 0.15f)
                                        position = Position(x = scoutX, y = -0.8f, z = -2.0f)
                                        scale = Scale(0.15f)
                                    }
                                    addChildNode(scoutNode)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize().background(QuestDarkBg),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = QuestAccent,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Gradient fades
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .align(Alignment.BottomCenter)
                        .background(Brush.verticalGradient(colors = listOf(Color.Transparent, QuestDarkBg)))
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                        .align(Alignment.TopCenter)
                        .background(Brush.verticalGradient(colors = listOf(Color(0xFF12100A), Color.Transparent)))
                )
            }

            // ══════════════════════════════
            // ── Roman Quest Trail ──
            // ══════════════════════════════
            val scrollState = rememberScrollState()
            val nodeSize = 68.dp
            val trailWidth = (monuments.size * 130).dp

            Box(
                modifier = Modifier
                    .weight(0.65f)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(QuestDarkBg, Color(0xFF100E0A), Color(0xFF0E0C08), QuestDarkBg)
                        )
                    )
                    .horizontalScroll(scrollState)
                    .padding(vertical = 4.dp)
            ) {
                // Road texture background
                Box(
                    modifier = Modifier
                        .width(trailWidth)
                        .fillMaxHeight()
                        .background(
                            Brush.verticalGradient(
                                listOf(QuestDarkBg, Color(0xFF110F0B), Color(0xFF0F0D09), QuestDarkBg)
                            )
                        )
                )

                // Road lines
                Canvas(modifier = Modifier.width(trailWidth).fillMaxHeight()) {
                    val pathY = size.height * 0.40f
                    val segW = (size.width - 140.dp.toPx()) / (monuments.size - 1).coerceAtLeast(1)
                    val startX = 70.dp.toPx()
                    val endX = size.width - 70.dp.toPx()

                    // Road shadow
                    drawLine(
                        color = Color(0xFF0A0805),
                        start = Offset(startX, pathY + 5.dp.toPx()),
                        end = Offset(endX, pathY + 5.dp.toPx()),
                        strokeWidth = 36.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    // Main cobblestone road
                    drawLine(
                        color = QuestRoad,
                        start = Offset(startX, pathY),
                        end = Offset(endX, pathY),
                        strokeWidth = 30.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    // Road edges
                    drawLine(
                        color = QuestRoadEdge.copy(alpha = 0.4f),
                        start = Offset(startX, pathY),
                        end = Offset(endX, pathY),
                        strokeWidth = 26.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    // Center line
                    drawLine(
                        color = Color(0xFF4A3A2A).copy(alpha = 0.25f),
                        start = Offset(startX, pathY),
                        end = Offset(endX, pathY),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    // Progress segments
                    for (i in 0 until monuments.size - 1) {
                        val fromX = startX + i * segW
                        val toX = startX + (i + 1) * segW
                        val completed = completedQuests.contains(monuments[i].id)
                        drawLine(
                            color = if (completed) QuestAccent.copy(alpha = 0.6f) else Color(0xFF2C2C2E).copy(alpha = 0.15f),
                            start = Offset(fromX, pathY),
                            end = Offset(toX, pathY),
                            strokeWidth = 3.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }

                    // Milestone markers at 5, 10, 15, 20
                    listOf(4, 9, 14, 19).forEach { milestoneIdx ->
                        if (milestoneIdx < monuments.size) {
                            val mx = startX + milestoneIdx * segW
                            val isReached = completedQuests.size > milestoneIdx
                            drawCircle(
                                color = if (isReached) QuestAccent.copy(alpha = 0.3f) else Color(0xFF333333).copy(alpha = 0.2f),
                                radius = 18.dp.toPx(),
                                center = Offset(mx, pathY)
                            )
                        }
                    }
                }

                // Quest nodes
                monuments.forEachIndexed { index, quest ->
                    val segW = (trailWidth - 140.dp) / (monuments.size - 1).coerceAtLeast(1)
                    val nodeX = 70.dp + segW * index - nodeSize / 2
                    val centerY = (configuration.screenHeightDp * 0.65f / 2).dp - nodeSize / 2
                    val waveOffset = if (index % 2 == 0) (-28).dp else 28.dp
                    val nodeY = centerY + waveOffset

                    val completed = isCompleted(quest.id)
                    val available = isQuestAvailable(quest, index)
                    val isNext = index == currentQuestIndex && !completed
                    val locked = quest.isPro && !isUserPro && index > 0

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .offset(x = nodeX, y = nodeY)
                            .width(nodeSize + 20.dp)
                            .clickable {
                                when {
                                    completed -> { activeQuest = quest; showFunFact = true; selectedAnswer = null; showResult = false }
                                    locked -> showProDialog = true
                                    available -> { activeQuest = quest; selectedAnswer = null; showResult = false; showFunFact = false }
                                }
                            }
                    ) {
                        // Quest type badge
                        if (!completed && !locked && isNext) {
                            Surface(
                                color = when (quest.questType) {
                                    QuestType.EMOJI_DECODE -> Color(0xFF2C1A3E)
                                    QuestType.TRUE_FALSE -> Color(0xFF1A2E1A)
                                    QuestType.FILL_BLANK -> Color(0xFF1A2A3E)
                                    else -> QuestCardBg
                                },
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(0.5.dp, QuestAccent.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    when (quest.questType) {
                                        QuestType.EMOJI_DECODE -> "🧩"
                                        QuestType.TRUE_FALSE -> "✅❌"
                                        QuestType.FILL_BLANK -> "📝"
                                        else -> "⚔️"
                                    },
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(Modifier.height(3.dp))
                        }

                        // Shield-shaped node
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(nodeSize)
                                .then(if (isNext) Modifier.scale(pulseScale) else Modifier)
                                .then(
                                    if (isNext) Modifier.drawBehind {
                                        drawCircle(
                                            color = QuestAccent.copy(alpha = 0.12f),
                                            radius = size.minDimension / 2 + glowRadius.dp.toPx()
                                        )
                                    } else Modifier
                                )
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    when {
                                        completed -> Brush.radialGradient(listOf(Color(0xFF2A4A1E), Color(0xFF1A2E14)))
                                        locked -> Brush.radialGradient(listOf(QuestLocked, Color(0xFF151310)))
                                        isNext -> Brush.radialGradient(listOf(Color(0xFF251E10), Color(0xFF141008)))
                                        else -> Brush.radialGradient(listOf(Color(0xFF1A1810), Color(0xFF121010)))
                                    }
                                )
                                .border(
                                    width = if (isNext) 2.5.dp else 1.5.dp,
                                    color = when {
                                        completed -> QuestComplete
                                        isNext -> QuestAccent.copy(alpha = pulseAlpha)
                                        locked -> Color(0xFF2A2520)
                                        else -> QuestAccentDim.copy(alpha = 0.25f)
                                    },
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .then(if (locked) Modifier.alpha(0.4f) else Modifier)
                        ) {
                            when {
                                completed -> {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(quest.imageUrl).crossfade(true).build(),
                                        contentDescription = quest.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(14.dp)).alpha(0.5f)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.3f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("✅", fontSize = 22.sp)
                                    }
                                }
                                locked -> Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = Color(0xFF4A4035),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text("PRO", color = QuestAccent.copy(alpha = 0.5f), fontSize = 8.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                                }
                                isNext -> {
                                    Text(quest.iconEmoji, fontSize = 28.sp)
                                }
                                else -> {
                                    Text(quest.iconEmoji, fontSize = 22.sp, modifier = Modifier.alpha(0.5f))
                                }
                            }
                        }

                        Spacer(Modifier.height(5.dp))
                        Text(
                            QuestTranslations.getName(quest.id, quest.name),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = when {
                                completed -> QuestAccent
                                locked -> Color(0xFF4A4035)
                                isNext -> QuestTextPrimary
                                else -> QuestTextSecondary.copy(alpha = 0.7f)
                            },
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            lineHeight = 12.sp,
                            modifier = Modifier.width(nodeSize + 20.dp)
                        )
                        if (isNext) {
                            Spacer(Modifier.height(2.dp))
                            Text(
                                Translations.t("tap_to_play"),
                                color = QuestAccent.copy(alpha = pulseAlpha),
                                fontSize = 7.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.5.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // ══════════════════════════════
    // ── Quest Dialog (redesigned) ──
    // ══════════════════════════════
    if (activeQuest != null && !showFunFact) {
        AlertDialog(
            onDismissRequest = { activeQuest = null; selectedAnswer = null; showResult = false },
            containerColor = Color(0xFF141210),
            shape = RoundedCornerShape(24.dp),
            title = {
                Column {
                    // Monument image
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(activeQuest!!.imageUrl).crossfade(true).build(),
                        contentDescription = activeQuest!!.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                    Spacer(Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(activeQuest!!.iconEmoji, fontSize = 24.sp)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(QuestTranslations.getName(activeQuest!!.id, activeQuest!!.name), color = QuestAccent, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                        }
                        // Quest type badge
                        Surface(
                            color = QuestCardBg,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(0.5.dp, QuestAccent.copy(alpha = 0.2f))
                        ) {
                            Text(
                                when (activeQuest!!.questType) {
                                    QuestType.EMOJI_DECODE -> Translations.t("quest_type_emoji")
                                    QuestType.TRUE_FALSE -> Translations.t("quest_type_tf")
                                    QuestType.FILL_BLANK -> Translations.t("quest_type_fill")
                                    else -> Translations.t("quest_type_quiz")
                                },
                                fontSize = 9.sp,
                                color = QuestTextSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth().height(1.dp)
                            .background(Brush.horizontalGradient(listOf(Color.Transparent, QuestAccent.copy(alpha = 0.3f), Color.Transparent)))
                    )
                }
            },
            text = {
                Column {
                    Text(QuestTranslations.getQuestion(activeQuest!!.id, activeQuest!!.question), color = QuestTextSecondary, fontSize = 14.sp, lineHeight = 21.sp)
                    Spacer(Modifier.height(12.dp))

                    // ── Special Quest Type UI ──
                    when (activeQuest!!.questType) {
                        QuestType.EMOJI_DECODE -> {
                            Surface(
                                color = Color(0xFF1A1A2E),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, Color(0xFF3A3A5E)),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                            ) {
                                Text(
                                    activeQuest!!.emojiClues ?: "",
                                    fontSize = 34.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(14.dp).fillMaxWidth()
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                        QuestType.FILL_BLANK -> {
                            Surface(
                                color = Color(0xFF1A1E2E),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, Color(0xFF3A4A5E)),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("📜", fontSize = 28.sp)
                                    Spacer(Modifier.height(8.dp))
                                    val sentence = QuestTranslations.getFillBlank(activeQuest!!.id, activeQuest!!.fillBlankSentence) ?: ""
                                    val parts = sentence.split("___")
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        if (parts.isNotEmpty()) {
                                            Text(
                                                parts[0],
                                                color = QuestTextPrimary,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Surface(
                                            color = QuestAccent.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(8.dp),
                                            border = BorderStroke(2.dp, QuestAccent)
                                        ) {
                                            Text(
                                                if (showResult && selectedAnswer == activeQuest!!.correctIndex) {
                                                    activeQuest!!.options[activeQuest!!.correctIndex]
                                                } else " ? ",
                                                color = QuestAccent,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                            )
                                        }
                                        if (parts.size > 1) {
                                            Text(
                                                parts[1],
                                                color = QuestTextPrimary,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                        QuestType.TRUE_FALSE -> { /* No extra UI, styled differently via options */ }
                        else -> { /* Standard QUIZ */ }
                    }

                    Spacer(Modifier.height(4.dp))

                    // ── Answer Options ──
                    val translatedOptions = QuestTranslations.getOptions(activeQuest!!.id, activeQuest!!.options)
                    translatedOptions.forEachIndexed { i, option ->
                        val isSelected = selectedAnswer == i
                        val isCorrect = i == activeQuest!!.correctIndex
                        val borderColor = when {
                            showResult && isSelected && isCorrect -> QuestComplete
                            showResult && isSelected && !isCorrect -> QuestRedAccent
                            showResult && isCorrect -> QuestComplete.copy(alpha = 0.5f)
                            else -> Color(0xFF252220)
                        }
                        val bgColor = when {
                            showResult && isSelected && isCorrect -> QuestComplete.copy(alpha = 0.1f)
                            showResult && isSelected && !isCorrect -> QuestRedAccent.copy(alpha = 0.1f)
                            else -> Color(0xFF121010)
                        }
                        Surface(
                            color = bgColor,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, borderColor),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
                                .clickable(enabled = !showResult) {
                                    selectedAnswer = i; showResult = true
                                    if (isCorrect) completeQuest(activeQuest!!.id)
                                }
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = when (activeQuest!!.questType) {
                                        QuestType.TRUE_FALSE -> if (i == 0) Color(0xFF1B3A1B) else Color(0xFF3A1B1B)
                                        else -> Color(0xFF252220)
                                    },
                                    shape = CircleShape,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            when (activeQuest!!.questType) {
                                                QuestType.TRUE_FALSE -> if (i == 0) "✓" else "✗"
                                                else -> ('A' + i).toString()
                                            },
                                            color = when (activeQuest!!.questType) {
                                                QuestType.TRUE_FALSE -> if (i == 0) QuestComplete else QuestRedAccent
                                                else -> QuestTextSecondary
                                            },
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(Modifier.width(10.dp))
                                Text(option, color = QuestTextPrimary, fontSize = 13.sp, modifier = Modifier.weight(1f))
                                if (showResult && isSelected && isCorrect)
                                    Text("✅", fontSize = 16.sp)
                                if (showResult && isSelected && !isCorrect)
                                    Text("❌", fontSize = 16.sp)
                            }
                        }
                    }

                    // Retry button
                    if (showResult && selectedAnswer != activeQuest!!.correctIndex) {
                        Spacer(Modifier.height(14.dp))
                        OutlinedButton(
                            onClick = { selectedAnswer = null; showResult = false },
                            border = BorderStroke(1.dp, QuestAccent.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Refresh, null, tint = QuestAccent, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(Translations.t("try_again"), color = QuestAccent, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Victory: See fun fact
                    if (showResult && selectedAnswer == activeQuest!!.correctIndex) {
                        Spacer(Modifier.height(14.dp))
                        // Victory text
                        Text(
                            "🏆 ${Translations.t("victoria")} 🏆",
                            color = QuestAccent,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            letterSpacing = 2.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = { showFunFact = true },
                            colors = ButtonDefaults.buttonColors(containerColor = QuestAccent),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("📜", fontSize = 14.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(Translations.t("see_fun_fact"), color = QuestDarkBg, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    // ══════════════════════════════
    // ── Fun Fact Dialog (scroll) ──
    // ══════════════════════════════
    if (showFunFact && activeQuest != null) {
        AlertDialog(
            onDismissRequest = { activeQuest = null; showFunFact = false },
            containerColor = Color(0xFF141210),
            shape = RoundedCornerShape(24.dp),
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("📜", fontSize = 40.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(QuestTranslations.getName(activeQuest!!.id, activeQuest!!.name), color = QuestAccent, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(4.dp))
                    Text(Translations.t("historical_insight"), color = QuestTextSecondary, fontSize = 10.sp, letterSpacing = 2.sp)
                }
            },
            text = {
                Column {
                    // Roman scroll-style background
                    Surface(
                        color = Color(0xFF1A1810),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, QuestAccent.copy(alpha = 0.15f))
                    ) {
                        Text(
                            QuestTranslations.getFunFact(activeQuest!!.id, activeQuest!!.funFact),
                            color = QuestTextPrimary,
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { activeQuest = null; showFunFact = false },
                    colors = ButtonDefaults.buttonColors(containerColor = QuestAccent),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("⚔️", fontSize = 14.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(Translations.t("continue_journey"), color = QuestDarkBg, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // ══════════════════════════════
    // ── PRO Paywall Dialog ──
    // ══════════════════════════════
    if (showProDialog) {
        AlertDialog(
            onDismissRequest = { showProDialog = false },
            containerColor = Color(0xFF0D0C0A),
            shape = RoundedCornerShape(24.dp),
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("👑", fontSize = 48.sp)
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "UNLOCK ALL QUESTS",
                        color = QuestAccent,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center,
                        letterSpacing = 2.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Become a true Roman Explorer",
                        color = QuestTextSecondary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            },
            text = {
                Column {
                    val benefits = listOf(
                        "🏛️" to "20 Interactive Roman Quests",
                        "🧩" to "Emoji Decode, True/False & Fill-Blank",
                        "🏆" to "Unlock 4 Secret Hidden Places",
                        "🚫" to "Remove All Ads",
                        "🌙" to "Night Mode for Nightlife",
                        "📖" to "Unlimited Italian Phrases",
                        "🗺️" to "Offline Maps Download",
                        "👤" to "4 Premium Avatars"
                    )
                    benefits.forEach { (emoji, text) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(emoji, fontSize = 16.sp)
                            Spacer(Modifier.width(10.dp))
                            Text(text, color = Color.White, fontSize = 13.sp)
                        }
                    }
                    Spacer(Modifier.height(16.dp))

                    // Purchase button
                    Surface(
                        color = QuestAccent,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // Trigger Google Play Billing
                                (context as? android.app.Activity)?.let { activity ->
                                    com.riki.vojo.billing.BillingManager.launchPurchase(activity)
                                }
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("ONE-TIME PURCHASE", color = QuestDarkBg, fontSize = 10.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("👑", fontSize = 20.sp)
                                Spacer(Modifier.width(8.dp))
                                Text("€4.99", color = QuestDarkBg, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                            }
                            Text("No subscriptions • Forever yours", color = QuestDarkBg.copy(alpha = 0.7f), fontSize = 11.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    TextButton(onClick = { showProDialog = false }) {
                        Text(Translations.t("maybe_later"), color = QuestTextSecondary, fontSize = 13.sp)
                    }
                }
            }
        )
    }

    // ══════════════════════════════
    // ── All Quests Complete Reward ──
    // ══════════════════════════════
    LaunchedEffect(completedQuests.size) {
        if (completedQuests.size >= monuments.size && monuments.isNotEmpty()) {
            showUnlockReward = true
        }
    }

    if (showUnlockReward) {
        AlertDialog(
            onDismissRequest = { showUnlockReward = false },
            containerColor = Color(0xFF0D0C0A),
            shape = RoundedCornerShape(24.dp),
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("🏆", fontSize = 60.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "VICTORIA MAXIMA!",
                        color = QuestAccent,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        letterSpacing = 3.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "All 20 Quests Completed!",
                        color = QuestTextSecondary,
                        fontSize = 13.sp
                    )
                }
            },
            text = {
                Column {
                    Text(
                        "Congratulations, Commander! You've conquered the Via Romana!",
                        color = QuestTextPrimary,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "🔓 UNLOCKED:",
                        color = QuestAccent,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        letterSpacing = 2.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    val rewards = listOf(
                        "🗺️" to "4 Secret Hidden Places on Map",
                        "📜" to "All Historical Fun Facts",
                        "🏛️" to "True Roman Explorer Title"
                    )
                    rewards.forEach { (emoji, text) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(emoji, fontSize = 18.sp)
                            Spacer(Modifier.width(10.dp))
                            Text(text, color = Color.White, fontSize = 14.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showUnlockReward = false },
                    colors = ButtonDefaults.buttonColors(containerColor = QuestAccent),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🗺️", fontSize = 16.sp)
                    Spacer(Modifier.width(8.dp))
                    Text("Explore Secret Places", color = QuestDarkBg, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // ══════════════════════════════
    // ── Rate App Popup (after 5 quests) ──
    // ══════════════════════════════
    LaunchedEffect(completedQuests.size) {
        val alreadyAsked = prefs.getBoolean("rate_app_asked", false)
        if (completedQuests.size >= 5 && !alreadyAsked) {
            showRateDialog = true
            prefs.edit().putBoolean("rate_app_asked", true).apply()
        }
    }

    if (showRateDialog) {
        AlertDialog(
            onDismissRequest = { showRateDialog = false },
            containerColor = Color(0xFF0D0C0A),
            shape = RoundedCornerShape(24.dp),
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("⭐⭐⭐⭐⭐", fontSize = 28.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        Translations.t("rate_app_title"),
                        color = QuestAccent,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                }
            },
            text = {
                Text(
                    Translations.t("rate_app_desc"),
                    color = QuestTextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(
                        onClick = {
                            showRateDialog = false
                            try {
                                val intent = android.content.Intent(
                                    android.content.Intent.ACTION_VIEW,
                                    android.net.Uri.parse("market://details?id=com.riki.vojo")
                                )
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                val intent = android.content.Intent(
                                    android.content.Intent.ACTION_VIEW,
                                    android.net.Uri.parse("https://play.google.com/store/apps/details?id=com.riki.vojo")
                                )
                                context.startActivity(intent)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = QuestAccent),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(Translations.t("rate_now"), color = QuestDarkBg, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = { showRateDialog = false }) {
                        Text(Translations.t("not_now"), color = QuestTextSecondary, fontSize = 13.sp)
                    }
                }
            }
        )
    }
}
