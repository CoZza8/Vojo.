package com.riki.vojo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.riki.vojo.ui.theme.VojoTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt
import androidx.compose.ui.draw.alpha
import android.content.Context
import androidx.compose.ui.graphics.vector.ImageVector
import coil.request.ImageRequest
import android.app.DownloadManager
import android.os.Environment
import android.content.BroadcastReceiver
import android.content.IntentFilter
import androidx.compose.animation.core.animateDpAsState
import java.io.File
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.platform.LocalConfiguration
import io.github.sceneview.SceneView
import io.github.sceneview.node.ModelNode
import io.github.sceneview.math.Scale
import io.github.sceneview.math.Position
import coil.ImageLoader
import coil.ImageLoaderFactory
import okhttp3.OkHttpClient
import androidx.compose.foundation.horizontalScroll
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import org.json.JSONObject








val VojoBlack = Color(0xFF121212)
val VojoDarkGrey = Color(0xFF1E1E1E)
val VojoGold = Color(0xFFFFD700)
val VojoTextWhite = Color(0xFFEEEEEE)
val VojoTextGrey = Color(0xFFAAAAAA)

enum class SortOption { NONE, RATING, NEAREST, CHEAPEST }
var isNightMode by mutableStateOf(false)
class MainActivity : ComponentActivity() {


    private val downloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id != -1L) {
                Toast.makeText(context, "Vojo: Rome Map Ready! 🏛️", Toast.LENGTH_LONG).show()
            }
        }
    }

    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(downloadReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_NOT_EXPORTED)
            } else {
                registerReceiver(downloadReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.ITALIAN
            }
        }

        setContent {
            VojoTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "welcome_screen") {
                    composable("welcome_screen") { WelcomeScreen(navController) }
                    composable("map_screen") { MapScreen(navController, tts) }
                    composable("passport_screen") { PassportScreen(navController) }
                    composable("learning_screen") { LearningScreen(navController, tts) }
                    composable("profile_screen") { ProfileScreen(navController) }
                    composable("quests_screen") {
                        val context = LocalContext.current
                        val prefManager = remember { PrefManager(context) }
                        QuestMapScreen(isUserPro = prefManager.isPro())
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        try {
            unregisterReceiver(downloadReceiver)
        } catch (e: Exception) {}
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}
@Composable
fun WeatherBadge() {
    var weatherText by remember { mutableStateOf("☀️ --°C") }
    var weatherDesc by remember { mutableStateOf("Loading...") }

    LaunchedEffect(Unit) {
        try {
            val url = java.net.URL("https://api.open-meteo.com/v1/forecast?latitude=41.89&longitude=12.49&current_weather=true")
            val result = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                url.readText()
            }
            val temp = org.json.JSONObject(result)
                .getJSONObject("current_weather")
                .getDouble("temperature")
            val code = org.json.JSONObject(result)
                .getJSONObject("current_weather")
                .getInt("weathercode")
            val icon = when (code) {
                0 -> "☀️"; 1, 2, 3 -> "⛅"; in 45..48 -> "🌫️"
                in 51..67 -> "🌧️"; in 71..77 -> "❄️"; in 80..82 -> "🌦️"
                in 95..99 -> "⛈️"; else -> "🌤️"
            }
            weatherText = "$icon ${temp.toInt()}°C"
            weatherDesc = "Rome now"
        } catch (e: Exception) {
            weatherText = "☀️ --°C"
            weatherDesc = "Offline"
        }
    }

    Surface(
        color = VojoDarkGrey.copy(alpha = 0.9f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, VojoGold.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(weatherText, color = VojoTextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(6.dp))
            Text(weatherDesc, color = VojoTextGrey, fontSize = 10.sp)
        }
    }
}
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


            Text("Explore. Collect. Learn.", fontSize = 18.sp, color = VojoTextGrey)

            Spacer(modifier = Modifier.height(32.dp))

            Text("Select Language", color = VojoTextGrey, fontSize = 14.sp)
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
                                opt.name,
                                fontSize = 10.sp,
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
                    navController.navigate("map_screen")
                },
                colors = ButtonDefaults.buttonColors(containerColor = VojoGold),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("START JOURNEY", color = VojoBlack, fontWeight = FontWeight.Bold)
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PassportScreen(navController: NavController) {
    val context = LocalContext.current
    Scaffold(
        containerColor = VojoBlack,
        topBar = {
            TopAppBar(
                title = { Text(Translations.t("My Passport 🌍"), color = VojoTextWhite) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VojoBlack),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = VojoTextWhite)
                    }
                }
            )
        },
        bottomBar = { VojoBottomBar(navController) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Card(colors = CardDefaults.cardColors(containerColor = VojoDarkGrey), modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(20.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text((Translations.t("Places Visited")), color = VojoTextGrey, fontSize = 12.sp)
                        Text("${PassportData.stamps.size}", color = VojoTextWhite, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text((Translations.t("Swipes Earned")), color = VojoTextGrey, fontSize = 12.sp)
                        Text("+${PassportData.earnedSwipes}", color = VojoGold, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text((Translations.t("Your Memories")), style = MaterialTheme.typography.titleLarge, color = VojoTextWhite)
            Spacer(modifier = Modifier.height(12.dp))

            if (PassportData.stamps.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CameraAlt, null, tint = Color.DarkGray, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text((Translations.t("No stamps yet.")), color = VojoTextGrey, fontSize = 18.sp)
                        Text((Translations.t("Visit a MONUMENT and take a photo\nto unlock more Italian phrases!")), color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 32.dp))
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = { navController.navigate("map_screen") }, colors = ButtonDefaults.buttonColors(containerColor = VojoGold)) {
                            Text((Translations.t("Go to Map")), color = VojoBlack)
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(PassportData.stamps) { stamp ->
                        Card(colors = CardDefaults.cardColors(containerColor = VojoDarkGrey), shape = RoundedCornerShape(12.dp)) {
                            Column {
                                Box(modifier = Modifier.height(140.dp).fillMaxWidth().background(Color.Black)) {
                                    if (stamp.image != null) {
                                        Image(
                                            bitmap = stamp.image.asImageBitmap(),
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        Box(Modifier.fillMaxSize(), Alignment.Center) {
                                            Icon(Icons.Default.Image, null, tint = Color.Gray)
                                        }
                                    }
                                }
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(stamp.placeName, color = VojoTextWhite, fontWeight = FontWeight.Bold, maxLines = 1, fontSize = 14.sp)
                                    Text(stamp.date, color = VojoTextGrey, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningScreen(navController: NavController, tts: TextToSpeech?) {
    val context = LocalContext.current


    var swipesAvailable by remember { mutableIntStateOf(LearningManager.refreshAndGetTotal(context)) }
    var learnedCount by remember { mutableIntStateOf(LearningManager.getTotalLearnedCount(context)) }
    var currentIndex by remember { mutableIntStateOf(learnedCount) }
    var selectedTab by remember { mutableIntStateOf(0) }

    val currentPhrase = if (currentIndex < LearningData.allPhrases.size) LearningData.allPhrases[currentIndex] else null

    Scaffold(
        containerColor = VojoBlack,
        topBar = {
            Column {
                TopAppBar(
                    title = { Text((Translations.t("Italian")), color = VojoTextWhite) },
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
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text((Translations.t("FlashCards")), fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text((Translations.t("Learned Words ($learnedCount)")), fontWeight = FontWeight.Bold) }
                    )
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {

            if (selectedTab == 0) {
                Card(colors = CardDefaults.cardColors(containerColor = VojoDarkGrey), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text((Translations.t("Daily Words Left")), color = VojoTextGrey, fontSize = 12.sp)
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text("$swipesAvailable", color = VojoGold, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                                Text(" / 10", color = VojoTextGrey, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp))
                            }
                        }
                        if (swipesAvailable > 10) {
                            Text("BONUS ACTIVE! 🚀", color = Color.Green, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                if (swipesAvailable > 0 && currentPhrase != null) {
                    Text((Translations.t("Tap to Listen • Swipe to Save")), color = VojoTextGrey, fontSize = 14.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(16.dp))

                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().weight(1f)) {
                        SwipeableFlashcard(
                            phrase = currentPhrase,
                            onSwipeNext = {
                                if (!LearningData.learnedPhrases.contains(currentPhrase)) {
                                    LearningData.learnedPhrases.add(0, currentPhrase)
                                }
                                LearningManager.consumeSwipe(context)
                                swipesAvailable = LearningManager.refreshAndGetTotal(context)
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
                        Text("Daily Limit Reached!", color = VojoTextWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Come back tomorrow for +5 words\nor visit Monuments to get Bonuses!", color = VojoTextGrey, textAlign = TextAlign.Center, lineHeight = 24.sp)
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(onClick = { selectedTab = 1 }, colors = ButtonDefaults.buttonColors(containerColor = VojoDarkGrey), modifier = Modifier.fillMaxWidth().height(50.dp)) {
                            Text("Review Learned Words", color = VojoTextWhite)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { navController.navigate("map_screen") }, colors = ButtonDefaults.buttonColors(containerColor = VojoGold), modifier = Modifier.fillMaxWidth().height(50.dp)) {
                            Text("Go to Map & Earn Bonuses", color = VojoBlack, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CheckCircle, null, tint = VojoGold, modifier = Modifier.size(64.dp))
                        Text(text = Translations.t("All phrases learned!"), color = VojoTextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }

            } else {
                if (LearningData.learnedPhrases.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No words learned yet.\nGo to 'FlashCards' and start swiping!", color = VojoTextGrey, textAlign = TextAlign.Center)
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
                                        if (phrase.pronunciation != null) Text("/${phrase.pronunciation}/", color = VojoGold, fontSize = 12.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
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
            if (phrase.pronunciation != null) Text("/${phrase.pronunciation}/", color = Color.Gray, fontSize = 16.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            Spacer(modifier = Modifier.height(32.dp))
            Text(phrase.original, color = VojoTextGrey, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(48.dp))
            Icon(Icons.AutoMirrored.Filled.VolumeUp, null, tint = VojoGold, modifier = Modifier.size(32.dp))
            Text("Tap to listen", color = Color.DarkGray, fontSize = 10.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun MapScreen(navController: NavController, tts: TextToSpeech?) {
    var searchQuery by remember { mutableStateOf("") }
    var downloadProgress by remember { mutableStateOf(0f) }
    var isDownloading by remember { mutableStateOf(false) }
    var selectedPlace by remember { mutableStateOf<Place?>(null) }
    var mainFilter by remember { mutableStateOf("All") }
    var subFilter by remember { mutableStateOf("All") }
    var activeSort by remember { mutableStateOf(SortOption.NONE) }
    var isPanelExpanded by remember { mutableStateOf(true) }
    var userLocation by remember { mutableStateOf<LatLng?>(null) }

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) userLocation = LatLng(location.latitude, location.longitude)
        }
    }
    LaunchedEffect(Unit) { launcher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) }

    val allPlaces = PlacesData.list
    val displayedPlaces = remember(mainFilter, subFilter, searchQuery, activeSort, userLocation) {
        val filtered = allPlaces.filter { place ->
            val matchesMain = when (mainFilter) {
                "Monuments" -> place.type == "monuments"
                "Food & Drink" -> place.type == "food" || place.type == "aperitivo" || place.type == "coffee"
                "Nasoni" -> place.type == "nasoni"
                "Toilets" -> place.type == "wc"
                else -> if (isNightMode) true else place.type != "aperitivo" || mainFilter == "All"
            }

            val matchesSub = if (subFilter == "All") true else {
                if (mainFilter == "Food & Drink") {
                    if (subFilter == "Coffee") place.type == "coffee" || place.priceOrTag.contains("Coffee", true)
                    else if (subFilter == "Aperitivo") place.type == "aperitivo" || place.priceOrTag.contains("Aperitivo", true)
                    else place.priceOrTag.contains(subFilter, true)
                } else {
                    place.priceOrTag.contains(subFilter, true) || place.description.contains(subFilter, true)
                }
            }

            val matchesSearch = if (searchQuery.isEmpty()) true else place.name.contains(searchQuery, true) || place.description.contains(searchQuery, true) || place.priceOrTag.contains(searchQuery, true)
            matchesMain && matchesSub && matchesSearch
        }


        when (activeSort) {
            SortOption.RATING -> filtered.sortedByDescending { it.rating }
            SortOption.CHEAPEST -> filtered.sortedBy {
                when {
                    it.priceOrTag.contains("Free", true) -> 0
                    it.priceOrTag.contains("Cheap", true) -> 1
                    else -> it.priceOrTag.count { c -> c == '€' }
                }
            }
            SortOption.NEAREST -> {
                val referenceLoc = userLocation ?: LatLng(41.8902, 12.4922)
                filtered.sortedBy {
                    val r = FloatArray(1)
                    Location.distanceBetween(referenceLoc.latitude, referenceLoc.longitude, it.coords.latitude, it.coords.longitude, r)
                    r[0]
                }
            }
            else -> filtered.sortedByDescending { !it.recommendation.isNullOrEmpty() }
        }
    }

    val mapWeight by animateFloatAsState(if (isPanelExpanded) 0.35f else 0.85f, label = "map")
    val panelWeight by animateFloatAsState(if (isPanelExpanded) 0.65f else 0.15f, label = "panel")

    Scaffold(containerColor = VojoBlack, bottomBar = { VojoBottomBar(navController) }) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize().background(VojoBlack)) {
            Box(modifier = Modifier.weight(mapWeight)) {
                Box(modifier = Modifier.padding(12.dp).align(Alignment.TopStart)) {
                    WeatherBadge()
                }
                VojoMapView(
                    places = displayedPlaces,
                    onPlaceClick = { place ->
                        selectedPlace = place
                        isPanelExpanded = false
                    }
                )
            }
            Column(modifier = Modifier.weight(panelWeight).clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)).background(VojoDarkGrey)) {
                Box(modifier = Modifier.fillMaxWidth().clickable { isPanelExpanded = !isPanelExpanded }.padding(12.dp), contentAlignment = Alignment.Center) {
                    Icon(if (isPanelExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp, contentDescription = "Toggle", tint = VojoTextGrey)
                }

                if (isPanelExpanded) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(if (isNightMode) "🌙" else "☀️", fontSize = 20.sp)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    if (isNightMode) "Night Mode" else "Day Mode",
                                    color = VojoTextWhite,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Switch(
                                checked = isNightMode,
                                onCheckedChange = { isNightMode = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = VojoGold,
                                    checkedTrackColor = Color(0xFF2D1B69),
                                    uncheckedThumbColor = Color(0xFFFF9800),
                                    uncheckedTrackColor = Color(0xFF4CAF50).copy(alpha = 0.3f)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search...", color = VojoTextGrey) },
                            leadingIcon = { Icon(Icons.Default.Search, null, tint = VojoGold) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = VojoTextWhite, unfocusedTextColor = VojoTextWhite, cursorColor = VojoGold, focusedBorderColor = VojoGold, unfocusedBorderColor = Color.Gray, focusedContainerColor = VojoBlack, unfocusedContainerColor = VojoBlack)
                        )

                        Spacer(modifier = Modifier.height(12.dp))


                        val prefManager = remember { PrefManager(context) }
                        val isUserPro = prefManager.isPro()

                        Button(
                            onClick = {
                                if (isUserPro) {
                                    isDownloading = true
                                    downloadOfflineMap(context)
                                } else {
                                    Toast.makeText(context, "Unlock PRO to download maps! 🗺️", Toast.LENGTH_LONG).show()
                                    navController.navigate("profile_screen")
                                }
                            },

                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (downloadProgress == 1f) Color(0xFF1B5E20) else VojoDarkGrey
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, VojoGold)
                        ) {
                            if (isDownloading && downloadProgress < 1f) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = VojoGold,
                                    strokeWidth = 2.dp
                                )
                                Spacer(Modifier.width(12.dp))
                                Text("DOWNLOADING ROME... 112MB", color = VojoGold, fontSize = 12.sp)
                            } else {
                                Icon(Icons.Default.DownloadForOffline, null, tint = VojoGold)
                                Spacer(Modifier.width(8.dp))
                                Text("DOWNLOAD OFFLINE MAP (PRO)", color = VojoGold, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            item { DarkFilterChip(mainFilter == "All", "All", { mainFilter="All"; subFilter="All"; activeSort=SortOption.NONE }) }
                            item { DarkFilterChip(mainFilter == "Monuments", "🏛️ Monuments", { mainFilter="Monuments"; subFilter="All"; activeSort=SortOption.NONE }) }
                            item { DarkFilterChip(mainFilter == "Food & Drink", "🍔 Food & Drink", { mainFilter="Food & Drink"; subFilter="All"; activeSort=SortOption.NONE }) }
                            if (isNightMode) {
                                item { DarkFilterChip(mainFilter == "Nightlife", "🍸 Bars & Clubs", { mainFilter="Nightlife"; subFilter="All"; activeSort=SortOption.NONE }) }
                            }
                            item { DarkFilterChip(mainFilter == "Nasoni", "🚰 Free Water", { mainFilter="Nasoni"; subFilter="All"; activeSort=SortOption.NONE }) }
                            item { DarkFilterChip(mainFilter == "Toilets", "🚽 WC", { mainFilter="Toilets"; subFilter="All"; activeSort=SortOption.NONE }) }
                        }

                        AnimatedVisibility(visible = mainFilter == "Food & Drink", enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
                            Column {
                                Spacer(modifier = Modifier.height(12.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf("All", "Pizza", "Pasta", "Gelato", "Coffee", "Aperitivo").forEach { tag ->
                                        item { DarkFilterChip(subFilter == tag, tag, { subFilter = tag }, isSmall = true) }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))


                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                            if (mainFilter != "Nasoni" && mainFilter != "Toilets") {
                                DarkFilterChip(activeSort == SortOption.RATING, "⭐ Top Rated", { activeSort = if (activeSort == SortOption.RATING) SortOption.NONE else SortOption.RATING }, isSmall = true)
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            if (mainFilter == "Food & Drink" || mainFilter == "Malls") {
                                DarkFilterChip(activeSort == SortOption.CHEAPEST, "💰 Cheapest", { activeSort = if (activeSort == SortOption.CHEAPEST) SortOption.NONE else SortOption.CHEAPEST }, isSmall = true)
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            DarkFilterChip(activeSort == SortOption.NEAREST, "📍 Nearest", { activeSort = if (activeSort == SortOption.NEAREST) SortOption.NONE else SortOption.NEAREST }, isSmall = true)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(displayedPlaces) { place ->
                            GlovoDarkCard(place) { selectedPlace = place }
                        }
                        if (displayedPlaces.isEmpty()) item { Text("No places found.", color = VojoTextGrey, modifier = Modifier.padding(16.dp)) }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                        Text("Explore ${displayedPlaces.size} places nearby", color = VojoTextGrey, fontSize = 14.sp)
                    }
                }
            }
        }

        if (selectedPlace != null) {
            ModalBottomSheet(onDismissRequest = { selectedPlace = null }, containerColor = VojoDarkGrey) {
                PlaceDetailsContent(selectedPlace!!)
            }
        }
    }
}
@Composable
fun VojoMapView(places: List<Place>, onPlaceClick: (Place) -> Unit) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(41.8902, 12.4922), 12f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = true)
    ) {
        places.forEach { place ->
            val hue = when(place.type) {
                "food", "aperitivo", "coffee" -> BitmapDescriptorFactory.HUE_ORANGE
                "monuments" -> BitmapDescriptorFactory.HUE_VIOLET
                "nasoni" -> BitmapDescriptorFactory.HUE_AZURE
                "parks" -> BitmapDescriptorFactory.HUE_GREEN
                "malls" -> BitmapDescriptorFactory.HUE_ROSE
                "wc" -> BitmapDescriptorFactory.HUE_YELLOW
                else -> BitmapDescriptorFactory.HUE_RED
            }

            Marker(
                state = MarkerState(place.coords),
                title = place.name,
                snippet = place.openingHours,
                icon = BitmapDescriptorFactory.defaultMarker(hue),
                onInfoWindowClick = { onPlaceClick(place) }
            )
        }
    }
}

@Composable
fun PlaceDetailsContent(place: Place) {
    var tabIndex by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    val isMonument = place.type == "monuments"

    val isAlreadyVisited = remember(PassportData.stamps.size, place.name) {
        PassportData.stamps.any { it.placeName.equals(place.name, ignoreCase = true) }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            val alreadyIn = PassportData.stamps.any { it.placeName.equals(place.name, ignoreCase = true) }
            if (!alreadyIn) {
                val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
                PassportData.addStamp(PassportStamp(place.name, bitmap, date), context)
                Toast.makeText(context, "Victory! Photo added to Passport! 🎉", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Commander, this site is already documented!", Toast.LENGTH_LONG).show()
            }
        }
    }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        hasCameraPermission = isGranted
        if (isGranted) cameraLauncher.launch(null) else Toast.makeText(context, "Camera permission needed", Toast.LENGTH_SHORT).show()
    }

    Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
        Text(place.name, style = MaterialTheme.typography.headlineMedium, color = VojoTextWhite, fontWeight = FontWeight.Bold)
        if (place.imageUrl.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                .data(place.imageUrl)
                .crossfade(true)
                .build(),
                contentDescription = place.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (isMonument) {
            TabRow(selectedTabIndex = tabIndex, containerColor = VojoBlack, contentColor = VojoGold) {
                Tab(selected = tabIndex == 0, onClick = { tabIndex = 0 }, text = { Text("Info") })
                Tab(selected = tabIndex == 1, onClick = { tabIndex = 1 }, text = { Text("📖 History") })
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (!isMonument || tabIndex == 0) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = VojoBlack, shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, VojoGold)) {
                    Text(place.priceOrTag, modifier = Modifier.padding(10.dp, 6.dp), color = VojoTextWhite, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Icon(Icons.Default.AccessTime, null, tint = VojoTextGrey, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(place.openingHours, color = VojoTextGrey, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(place.description, color = VojoTextWhite)
            Spacer(modifier = Modifier.height(24.dp))

            if (isMonument) {
                Button(
                    onClick = {
                        if (isAlreadyVisited) {
                            Toast.makeText(context, "This monument is already in your Passport!", Toast.LENGTH_SHORT).show()
                        } else {
                            if (hasCameraPermission) cameraLauncher.launch(null) else permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isAlreadyVisited) Color.DarkGray else VojoGold),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = if (isAlreadyVisited) Icons.Default.CheckCircle else Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = if (isAlreadyVisited) Color.Green else VojoBlack
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAlreadyVisited) "ALREADY COLLECTED" else "Add to Passport (+3 Bonus Words)",
                        color = if (isAlreadyVisited) Color.White else VojoBlack,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Button(onClick = {
                val gmmIntentUri = Uri.parse("google.navigation:q=${place.coords.latitude},${place.coords.longitude}")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                try { context.startActivity(mapIntent) } catch (e: Exception) { context.startActivity(Intent(Intent.ACTION_VIEW, gmmIntentUri)) }
            }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = VojoDarkGrey), shape = RoundedCornerShape(12.dp)) {
                Icon(Icons.Default.Place, null, tint = VojoTextWhite)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Navigate with Google Maps", color = VojoTextWhite)
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.HistoryEdu, null, tint = VojoGold, modifier = Modifier.size(100.dp))
                Spacer(modifier = Modifier.height(24.dp))
                Text("HISTORICAL DOSSIER", color = VojoGold, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                Text("Access architectural secrets and legends for ${place.name}.", color = VojoTextGrey, textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp))
                Button(onClick = { openVojoArticle(context, place.wikiUrl) }, colors = ButtonDefaults.buttonColors(VojoGold)) {
                    Text("READ FULL HISTORY", color = VojoBlack)
                }
            }
        }
    }
}
@Composable
fun VojoBottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    NavigationBar(containerColor = VojoBlack) {
        val items = listOf(
            Triple("Map", Icons.Default.Map, "map_screen"),
            Triple("Passport", Icons.Default.Book, "passport_screen"),
            Triple(" Italian", Icons.Default.School, "learning_screen"),
            Triple(first ="Quests" , second = Icons.Default.Extension, third = "quests_screen"),
            Triple("Profile", Icons.Default.Person, "profile_screen"),


        )
        items.forEach { (label, icon, route) ->
            val isSelected = currentRoute == route
            NavigationBarItem(
                selected = isSelected,
                onClick = { if (currentRoute != route) navController.navigate(route) },
                label = { Text(label, color = if(isSelected) VojoGold else VojoTextGrey) },
                icon = { Icon(icon, label, tint = if(isSelected) VojoGold else VojoTextGrey) },
                colors = NavigationBarItemDefaults.colors(indicatorColor = VojoDarkGrey)
            )
        }
    }
}

@Composable
fun DarkFilterChip(selected: Boolean, label: String, onClick: () -> Unit, isSmall: Boolean = false) {
    Surface(color = if (selected) VojoGold else VojoBlack, shape = RoundedCornerShape(50), border = if (selected) null else BorderStroke(1.dp, Color.Gray), modifier = Modifier.clickable(onClick = onClick)) {
        Text(label, color = if (selected) VojoBlack else VojoTextWhite, modifier = Modifier.padding(horizontal = if(isSmall) 12.dp else 16.dp, vertical = if(isSmall) 6.dp else 8.dp), fontSize = if(isSmall) 12.sp else 14.sp, fontWeight = if(selected) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
fun GlovoDarkCard(place: Place, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = VojoBlack)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1A1A1A)),
                contentAlignment = Alignment.Center
            ) {
                if (place.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(place.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = place.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        error = painterResource(android.R.drawable.ic_menu_gallery),
                        placeholder = painterResource(android.R.drawable.ic_menu_gallery)
                    )
                } else {
                    Icon(
                        imageVector = when (place.type) {
                            "monuments" -> Icons.Default.AccountBalance
                            "nasoni" -> Icons.Default.WaterDrop
                            "parks" -> Icons.Default.Park
                            "malls" -> Icons.Default.ShoppingBag
                            "wc" -> Icons.Default.Wc
                            "aperitivo" -> Icons.Default.LocalBar
                            "coffee" -> Icons.Default.Coffee
                            else -> Icons.Default.Restaurant
                        },
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                if (!place.recommendation.isNullOrEmpty()) {
                    Surface(
                        color = VojoGold.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, VojoGold.copy(alpha = 0.5f)),
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Stars, null, tint = VojoGold, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(place.recommendation.uppercase(), color = VojoGold, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                        }
                    }
                }

                Text(place.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = VojoTextWhite)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⭐ ${place.rating}", style = MaterialTheme.typography.bodySmall, color = VojoGold)
                    Text(" • ${place.priceOrTag}", style = MaterialTheme.typography.bodySmall, color = VojoTextGrey)
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: com.riki.vojo.presentation.ProViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = com.riki.vojo.presentation.ProViewModelFactory(LocalContext.current)
    )
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("vojo_prefs", Context.MODE_PRIVATE) }

    // Używamy nowego ViewModelu do sprawdzania statusu PRO i awatarów
    val isUserPro by viewModel.isProUser.collectAsState()
    val selectedAvatarIndex by viewModel.selectedAvatarIndex.collectAsState()
    val availableAvatars = viewModel.availableAvatars

    // Szukamy wybranego awatara po indeksie (domyślnie pierwszy)
    val currentAvatarResId = availableAvatars.find { it.id == selectedAvatarIndex }?.resId ?: R.drawable.colosseum

    val totalLearned = LearningManager.getTotalLearnedCount(context)
    val swipes = LearningManager.refreshAndGetTotal(context)
    val stampsCount = PassportData.stamps.size
    val lastStamp = PassportData.stamps.firstOrNull()

    var userName by remember { mutableStateOf(prefs.getString("user_nick", "Vojo Explorer") ?: "Vojo Explorer") }
    var showGoProDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf(userName) }

    val explorerLevel = (stampsCount * 2) + (totalLearned / 5)

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            containerColor = VojoDarkGrey,
            title = { Text("COMMANDER PROFILE", color = VojoGold, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { if (it.length <= 15) tempName = it },
                        label = { Text("Nickname", color = VojoGold) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = VojoGold
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(20.dp))
                    Text("Select Your Avatar:", color = VojoTextGrey, fontSize = 12.sp)
                    Spacer(Modifier.height(12.dp))

                    // Ładujemy awatary z ViewModelu, a nie ze starej listy
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(availableAvatars) { avatar ->
                            AvatarSelectionItem(
                                resId = avatar.resId,
                                isSelected = (selectedAvatarIndex == avatar.id)
                            ) {
                                viewModel.selectAvatar(avatar.id)
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
                    Text(text = Translations.t("Save"), color = VojoBlack, fontWeight = FontWeight.ExtraBold)
                }
            }
        )
    }

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
            Text(text= Translations.t("COMMANDER CENTER"), fontSize = 14.sp, color = VojoGold, letterSpacing = 3.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(24.dp))

            Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.clickable { showEditDialog = true }) {
                // Wyświetlamy aktualny awatar pobrany z ViewModelu
                Image(
                    painter = painterResource(id = currentAvatarResId),
                    contentDescription = null,
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(VojoDarkGrey)
                        .border(2.dp, VojoGold, CircleShape),
                    contentScale = ContentScale.Crop
                )
                Surface(color = VojoGold, shape = CircleShape, modifier = Modifier.size(32.dp).border(2.dp, VojoBlack, CircleShape)) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(explorerLevel.toString(), color = VojoBlack, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Text(userName, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = VojoTextWhite, modifier = Modifier.padding(top = 16.dp))
            Text("Rome Scout • Level $explorerLevel", color = VojoGold, fontSize = 12.sp)

            Spacer(Modifier.height(32.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                ProfileStatCard((Translations.t("STAMPS")), stampsCount.toString(), VojoGold)
                ProfileStatCard(Translations.t("PHRASES"), totalLearned.toString(), Color(0xFF4CAF50))
                ProfileStatCard(Translations.t("POWER"), swipes.toString(), Color(0xFF2196F3))
            }

            Spacer(Modifier.height(32.dp))

            Text(Translations.t("ACHIEVEMENTS"), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start, color = VojoTextGrey, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AchievementIcon(
                    icon = Icons.Default.LocalPizza,
                    name = "Foodie",
                    unlocked = stampsCount >= 5,
                    hint = "Collect 5 food stamps"
                )
                AchievementIcon(
                    icon = Icons.Default.HistoryEdu,
                    name = "Scholar",
                    unlocked = totalLearned >= 20,
                    hint = "Learn 20 new words!"
                )
                AchievementIcon(
                    icon = Icons.Default.Camera,
                    name = "Paparazzi",
                    unlocked = stampsCount >= 10,
                    hint = "Document 10 historic sites"
                )
                AchievementIcon(
                    icon = Icons.Default.Star,
                    name = "Elite",
                    unlocked = explorerLevel >= 15,
                    hint = "Reach Scout Level 15"
                )
            }

            Spacer(Modifier.height(32.dp))

            if (lastStamp != null) {
                Text((Translations.t("LAST DISCOVERY")), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start, color = VojoTextGrey, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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

            // Ukrywamy przycisk zakupu PRO, jeśli użytkownik już je ma
            if (!isUserPro) {
                Button(
                    onClick = { showGoProDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VojoGold)
                ) {
                    Icon(Icons.Default.Stars, null, tint = VojoBlack)
                    Spacer(Modifier.width(12.dp))
                    Text("UPGRADE TO VOJO PRO", color = VojoBlack, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                }
                Spacer(Modifier.height(12.dp))
            }

            Button(
                onClick = { showEditDialog = true },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VojoDarkGrey),
                border = BorderStroke(1.dp, VojoGold.copy(alpha = 0.5f))
            ) {
                Icon(Icons.Default.Edit, null, tint = VojoGold, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(12.dp))
                Text((Translations.t("EDIT PROFILE")), color = VojoTextWhite, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showGoProDialog) {
        AlertDialog(
            onDismissRequest = { showGoProDialog = false },
            containerColor = VojoDarkGrey,
            title = { Text("VOJO PRO", color = VojoGold, fontWeight = FontWeight.ExtraBold) },
            text = {
                Column {
                    Text(
                        "Unlock premium avatars, remove ads, and explore all interactive quests!",
                        color = VojoTextGrey,
                        fontSize = 14.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Używamy nowej funkcji z ViewModelu do odblokowania PRO
                        viewModel.unlockPro()
                        showGoProDialog = false
                        Toast.makeText(context, "Welcome to PRO! 👑", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VojoGold)
                ) {
                    Text((Translations.t("GET PRO - 4.99€")), color = VojoBlack, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}



@Composable
fun AvatarSelectionItem(resId: Int, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(if (isSelected) VojoGold.copy(alpha = 0.3f) else Color.Transparent)
            .border(2.dp, if (isSelected) VojoGold else Color.Transparent, CircleShape)
            .clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = resId),
            contentDescription = null,
            modifier = Modifier.fillMaxSize().clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun ProfileStatCard(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
        Text(text = label, fontSize = 10.sp, color = Color.Gray)
    }
}

@Composable
fun AchievementIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    name: String,
    unlocked: Boolean,
    hint: String
) {
    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable {

                val message = if (unlocked) Translations.t("Unlocked: $name!") else "${Translations.t("How to achieve")}: $hint"
                android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
            }
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(if (unlocked) VojoGold.copy(alpha = 0.2f) else Color.DarkGray.copy(alpha = 0.1f), CircleShape)
                .border(1.dp, if (unlocked) VojoGold else Color.Transparent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = name,
                tint = if (unlocked) VojoGold else Color.DarkGray,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(name, fontSize = 10.sp, color = if (unlocked) Color.White else Color.DarkGray)
    }
}
fun openVojoArticle(context: Context, url: String) {
    val builder = androidx.browser.customtabs.CustomTabsIntent.Builder()
    builder.setToolbarColor(android.graphics.Color.parseColor("#121212"))
    val customTabsIntent = builder.build()
    customTabsIntent.launchUrl(context, Uri.parse(url))
}
@Composable
fun OfflineDownloadButton() {
    var isDownloaded by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }
    val animatedProgress by animateFloatAsState(targetValue = progress)

    Button(
        onClick = {
            progress = 1f
            isDownloaded = true
        },
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isDownloaded) Color(0xFF1B5E20) else VojoGold
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isDownloaded) Icons.Default.CheckCircle else Icons.Default.DownloadForOffline,
                contentDescription = null,
                tint = VojoBlack
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = if (isDownloaded) "ROME OFFLINE READY" else "DOWNLOAD ROME MAP (OFFLINE)",
                color = VojoBlack,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp
            )
        }
    }
}
fun downloadOfflineMap(context: Context) {
    val url = "https://github.com/stamen/terrain-classic/archive/refs/heads/master.zip"

    try {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("Vojo Rome Map Data")
            .setDescription("Downloading high-precision tiles...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, "rome_map.zip")
            .setAllowedOverMetered(true)

        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)

        Toast.makeText(context, "DOWNLOAD STARTED! Check your notifications.", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(context, "ERROR: ${e.message}", Toast.LENGTH_LONG).show()
    }
}
@Composable
fun ProBenefitItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, desc: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = VojoGold, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(16.dp))
        Column {
            Text(title, color = VojoTextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(desc, color = VojoTextGrey, fontSize = 11.sp)
        }
    }
}
class PrefManager(context: Context) {
    private val prefs = context.getSharedPreferences("vojo_prefs", Context.MODE_PRIVATE)

    fun setPro(isPro: Boolean) {
        prefs.edit().putBoolean("is_pro_user", isPro).apply()
    }

    fun isPro(): Boolean {
        return prefs.getBoolean("is_pro_user", false)
    }
}
data class VojoQuest(
    val id: Int,
    val title: String,
    val mapIcon: Int,
    val positionY: Float,
    val isCompleted: Boolean = false,
    val isLocked: Boolean = true
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestMapScreen(isUserPro: Boolean, navController: NavController? = null) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    val prefManager = remember { PrefManager(context) }

    val prefs = remember { context.getSharedPreferences("vojo_quest_prefs", Context.MODE_PRIVATE) }
    var completedQuests by remember {
        mutableStateOf(prefs.getStringSet("completed_quests", emptySet())?.toSet() ?: emptySet())
    }
    var currentQuestIndex by remember {
        mutableIntStateOf(prefs.getInt("current_quest_index", 0))
    }

    var activeQuest by remember { mutableStateOf<QuestMonument?>(null) }
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    var showResult by remember { mutableStateOf(false) }
    var showFunFact by remember { mutableStateOf(false) }
    var showProDialog by remember { mutableStateOf(false) }

    val monuments = QuestData.monuments
    val nodeSize = 90.dp
    val trailWidth = (monuments.size * 200).dp

    fun completeQuest(questId: String) {
        val updated = completedQuests + questId
        completedQuests = updated
        val nextIndex = (currentQuestIndex + 1).coerceAtMost(monuments.size - 1)
        currentQuestIndex = nextIndex
        prefs.edit()
            .putStringSet("completed_quests", updated)
            .putInt("current_quest_index", nextIndex)
            .apply()
    }

    fun isQuestAvailable(quest: QuestMonument, index: Int): Boolean {
        if (index == 0) return true
        if (quest.isPro && !isUserPro) return false
        return index <= currentQuestIndex
    }

    fun isCompleted(questId: String) = completedQuests.contains(questId)

    val romeGreen = Color(0xFF2D5016)
    val romeSand = Color(0xFFC4A265)
    val romeStone = Color(0xFF8B7355)

    Scaffold(
        containerColor = Color(0xFF0D1B0E),
        bottomBar = { if (navController != null) VojoBottomBar(navController) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0D1B0E))
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("🏛️", fontSize = 22.sp)
                    Text("Via Romana", color = romeSand, fontSize = 22.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
                Surface(
                    color = Color(0xFF1A2E1A),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, romeSand.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("🏆", fontSize = 14.sp)
                        Text("${completedQuests.size}/${monuments.size}", color = romeSand, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }


            val scrollState = rememberScrollState()
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(scrollState)
            ) {

                Box(
                    modifier = Modifier
                        .width(trailWidth)
                        .fillMaxHeight()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF1A3320),
                                    Color(0xFF0D1B0E),
                                    Color(0xFF162B1A),
                                    Color(0xFF0D1B0E)
                                )
                            )
                        )
                )


                Canvas(modifier = Modifier.width(trailWidth).fillMaxHeight()) {
                    val pathY = size.height * 0.5f
                    val segmentW = (size.width - 200.dp.toPx()) / (monuments.size - 1).coerceAtLeast(1)


                    drawLine(
                        color = Color(0xFF3D2E1F).copy(alpha = 0.6f),
                        start = androidx.compose.ui.geometry.Offset(80.dp.toPx(), pathY),
                        end = androidx.compose.ui.geometry.Offset(size.width - 80.dp.toPx(), pathY),
                        strokeWidth = 40.dp.toPx(),
                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                    drawLine(
                        color = Color(0xFF5C4A3A).copy(alpha = 0.4f),
                        start = androidx.compose.ui.geometry.Offset(80.dp.toPx(), pathY),
                        end = androidx.compose.ui.geometry.Offset(size.width - 80.dp.toPx(), pathY),
                        strokeWidth = 36.dp.toPx(),
                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                    )


                    for (i in 0 until monuments.size - 1) {
                        val fromX = 100.dp.toPx() + i * segmentW
                        val toX = 100.dp.toPx() + (i + 1) * segmentW
                        val pathCompleted = isCompleted(monuments[i].id)
                        drawLine(
                            color = if (pathCompleted) romeSand.copy(alpha = 0.8f) else Color(0xFF2C2C2E).copy(alpha = 0.3f),
                            start = androidx.compose.ui.geometry.Offset(fromX, pathY),
                            end = androidx.compose.ui.geometry.Offset(toX, pathY),
                            strokeWidth = 4.dp.toPx(),
                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                    }
                }


                val treeEmojis = listOf("🌿", "🌳", "🏺", "⛲", "🌿", "🌳", "🪴", "🏺")
                treeEmojis.forEachIndexed { i, emoji ->
                    val x = (i * 180 + 50).dp
                    val y = if (i % 2 == 0) 40.dp else (configuration.screenHeightDp - 120).dp
                    Text(
                        emoji,
                        fontSize = 28.sp,
                        modifier = Modifier.offset(x = x, y = y).alpha(0.5f)
                    )
                }


                monuments.forEachIndexed { index, quest ->
                    val segmentW = (trailWidth - 200.dp) / (monuments.size - 1).coerceAtLeast(1)
                    val nodeX = 100.dp + segmentW * index - nodeSize / 2
                    val centerY = (configuration.screenHeightDp / 2).dp - nodeSize - 40.dp
                    val waveOffset = if (index % 2 == 0) (-30).dp else 30.dp
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
                                if (completed) {
                                    activeQuest = quest; showFunFact = true; selectedAnswer = null; showResult = false
                                } else if (locked) {
                                    showProDialog = true
                                } else if (available) {
                                    activeQuest = quest; selectedAnswer = null; showResult = false; showFunFact = false
                                }
                            }
                    ) {

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(nodeSize)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        completed -> romeSand
                                        locked -> Color(0xFF1A1A1A)
                                        isNext -> Color(0xFF1A2E1A)
                                        else -> Color(0xFF1E1E1E)
                                    }
                                )
                                .border(
                                    width = if (isNext) 4.dp else 3.dp,
                                    color = when {
                                        completed -> Color(0xFFD4A84B)
                                        isNext -> romeSand
                                        locked -> Color(0xFF333333)
                                        else -> romeStone.copy(alpha = 0.5f)
                                    },
                                    shape = CircleShape
                                )
                                .then(if (locked) Modifier.alpha(0.5f) else Modifier)
                        ) {
                            if (completed) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(quest.imageUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = quest.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize().clip(CircleShape).alpha(0.7f)
                                )
                                Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(32.dp))
                            } else if (locked) {
                                Icon(Icons.Default.Lock, null, tint = VojoTextGrey, modifier = Modifier.size(24.dp))
                            } else {
                                Text(quest.iconEmoji, fontSize = 36.sp)
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        Text(
                            quest.name,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                completed -> romeSand
                                locked -> Color.DarkGray
                                else -> VojoTextWhite
                            },
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            modifier = Modifier.width(nodeSize + 20.dp)
                        )

                        if (isNext) {
                            Spacer(Modifier.height(4.dp))
                            Text("⚔️", fontSize = 18.sp)
                        }
                    }
                }
            }
        }
    }



    if (activeQuest != null && !showFunFact) {
        AlertDialog(
            onDismissRequest = { activeQuest = null; selectedAnswer = null; showResult = false },
            containerColor = VojoDarkGrey,
            title = {
                Column {
                    Text(activeQuest!!.iconEmoji, fontSize = 28.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(activeQuest!!.name, color = VojoTextWhite, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                }
            },
            text = {
                Column {
                    Text(activeQuest!!.question, color = VojoTextGrey, fontSize = 16.sp, lineHeight = 24.sp)
                    Spacer(Modifier.height(20.dp))
                    activeQuest!!.options.forEachIndexed { i, option ->
                        val isSelected = selectedAnswer == i
                        val isCorrect = i == activeQuest!!.correctIndex
                        val borderColor = when {
                            showResult && isSelected && isCorrect -> Color(0xFF4CAF50)
                            showResult && isSelected && !isCorrect -> Color(0xFFF44336)
                            showResult && isCorrect -> Color(0xFF4CAF50).copy(alpha = 0.5f)
                            else -> Color(0xFF3A3A3C)
                        }
                        val bgColor = when {
                            showResult && isSelected && isCorrect -> Color(0xFF4CAF50).copy(alpha = 0.15f)
                            showResult && isSelected && !isCorrect -> Color(0xFFF44336).copy(alpha = 0.15f)
                            else -> VojoBlack
                        }
                        Surface(
                            color = bgColor, shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.5.dp, borderColor),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)
                                .clickable(enabled = !showResult) {
                                    selectedAnswer = i; showResult = true
                                    if (isCorrect) completeQuest(activeQuest!!.id)
                                }
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Surface(color = Color(0xFF3A3A3C), shape = CircleShape, modifier = Modifier.size(28.dp)) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(('A' + i).toString(), color = VojoTextGrey, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(Modifier.width(12.dp))
                                Text(option, color = VojoTextWhite, fontSize = 15.sp, modifier = Modifier.weight(1f))
                                if (showResult && isSelected && isCorrect) Icon(Icons.Default.Check, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(18.dp))
                                if (showResult && isSelected && !isCorrect) Icon(Icons.Default.Close, null, tint = Color(0xFFF44336), modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                    if (showResult && selectedAnswer != activeQuest!!.correctIndex) {
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { selectedAnswer = null; showResult = false }, colors = ButtonDefaults.buttonColors(containerColor = VojoDarkGrey), border = BorderStroke(1.dp, VojoGold.copy(alpha = 0.5f))) {
                            Text("Try Again", color = VojoGold, fontWeight = FontWeight.Bold)
                        }
                    }
                    if (showResult && selectedAnswer == activeQuest!!.correctIndex) {
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { showFunFact = true }, colors = ButtonDefaults.buttonColors(containerColor = VojoGold), modifier = Modifier.fillMaxWidth()) {
                            Text("See Fun Fact! ✨", color = VojoBlack, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }


    if (showProDialog) {
        AlertDialog(
            onDismissRequest = { showProDialog = false },
            containerColor = VojoDarkGrey,
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Stars, null, tint = VojoGold, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("Unlock All Quests", color = VojoTextWhite, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
                }
            },
            text = {
                Column {
                    Text(
                        "The first quest is free! Upgrade to VOJO PRO to explore all 8 Roman landmarks and unlock hidden historical secrets.",
                        color = VojoTextGrey,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                    Spacer(Modifier.height(20.dp))
                    listOf("8 Interactive Roman Quests", "Historical Fun Facts", "Unlimited Italian Phrases", "Offline Maps").forEach { benefit ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Check, null, tint = VojoGold, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(12.dp))
                            Text(benefit, color = VojoTextWhite, fontSize = 15.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                            prefManager.setPro(true)
                            showProDialog = false
                            Toast.makeText(context, "Welcome to PRO! 👑", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VojoGold),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("GET PRO — €4.99", color = VojoBlack, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
                    }
                    TextButton(onClick = { showProDialog = false }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Text("Maybe later", color = VojoTextGrey)
                    }
                }
            }
        )
    }
}



data class QuestNodeData(val title: String, val xPercent: Float, val yPos: Float, val isPro: Boolean, val model: String)






