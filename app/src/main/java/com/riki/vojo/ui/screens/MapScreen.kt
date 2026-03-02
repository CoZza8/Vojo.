package com.riki.vojo.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.riki.vojo.*
import com.riki.vojo.ui.components.*
import com.riki.vojo.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun MapScreen(navController: NavController, tts: TextToSpeech?, isProUser: Boolean) {
    var searchQuery by remember { mutableStateOf("") }
    var downloadProgress by remember { mutableStateOf(0f) }
    var isDownloading by remember { mutableStateOf(false) }
    var selectedPlace by remember { mutableStateOf<Place?>(null) }
    var mainFilter by remember { mutableStateOf("All") }
    var subFilter by remember { mutableStateOf("All") }
    var activeSort by remember { mutableStateOf(SortOption.NONE) }
    // Panel: 0=collapsed(map big), 1=half, 2=full(map hidden)
    var panelState by remember { mutableIntStateOf(1) }
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var hasLocationPermission by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        hasLocationPermission = permissions.values.any { it }
        if (hasLocationPermission) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) userLocation = LatLng(location.latitude, location.longitude)
            }
        }
    }
    LaunchedEffect(Unit) {
        hasLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!hasLocationPermission) {
            launcher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) userLocation = LatLng(location.latitude, location.longitude)
            }
        }
    }

    // Check if all quests are completed for secret places
    val questPrefs = remember { context.getSharedPreferences("vojo_quest_prefs", android.content.Context.MODE_PRIVATE) }
    val completedQuests = remember { questPrefs.getStringSet("completed_quests", emptySet())?.toSet() ?: emptySet() }
    val allQuestsCompleted = completedQuests.size >= QuestData.monuments.size

    val allPlaces = PlacesData.list
    val displayedPlaces = remember(mainFilter, subFilter, searchQuery, activeSort, userLocation, isNightMode, allQuestsCompleted, isProUser) {
        val filtered = allPlaces.filter { place ->
            // Hide secret places unless all quests done
            if (place.type == "secret" && !allQuestsCompleted) return@filter false
            // Hide nightlife clubs for non-PRO users
            if (place.type == "nightlife" && !isProUser) return@filter false

            val matchesMain = when (mainFilter) {
                "Monuments" -> place.type == "monuments"
                "Food & Drink" -> place.type == "food" || place.type == "aperitivo" || place.type == "coffee"
                "Nightlife" -> place.type == "aperitivo" || place.type == "nightlife"
                "Nasoni" -> place.type == "nasoni"
                "Toilets" -> place.type == "wc"
                "🏆 Secrets" -> place.type == "secret"
                // Night mode "All": show nightlife, aperitivo, nasoni, wc; Day mode "All": show everything except secret, nightlife, and aperitivo
                else -> if (isNightMode) (place.type == "nasoni" || place.type == "wc" || place.type == "aperitivo" || place.type == "nightlife") else (place.type != "secret" && place.type != "nightlife" && place.type != "aperitivo")
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
                val ref = userLocation ?: LatLng(41.8902, 12.4922)
                filtered.sortedBy {
                    val r = FloatArray(1)
                    Location.distanceBetween(ref.latitude, ref.longitude, it.coords.latitude, it.coords.longitude, r)
                    r[0]
                }
            }
            else -> filtered.sortedByDescending { !it.recommendation.isNullOrEmpty() }
        }
    }

    val mapWeight by animateFloatAsState(
        when (panelState) { 0 -> 0.85f; 1 -> 0.35f; else -> 0.001f }, label = "map"
    )
    val panelWeight by animateFloatAsState(
        when (panelState) { 0 -> 0.15f; 1 -> 0.65f; else -> 0.999f }, label = "panel"
    )

    Scaffold(containerColor = VojoBlack, bottomBar = {
        Column {
            // Ad Banner for free users
            if (!isProUser) {
                androidx.compose.ui.viewinterop.AndroidView(
                    factory = { ctx -> com.riki.vojo.ads.AdManager.createBannerAdView(ctx) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            VojoBottomBar(navController)
        }
    }) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize().background(VojoBlack)) {
            if (panelState < 2) {
                Box(modifier = Modifier.weight(mapWeight)) {
                    VojoMapView(places = displayedPlaces, onPlaceClick = { place -> selectedPlace = place; panelState = 0 }, hasLocationPermission = hasLocationPermission)
                    Box(modifier = Modifier.padding(12.dp).align(Alignment.TopStart)) {
                        WeatherBadge()
                    }
                }
            }
            Column(
                modifier = Modifier
                    .weight(if (panelState == 2) 1f else panelWeight)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(VojoDarkGrey)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .clickable { panelState = (panelState + 1) % 3 }
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(
                            when (panelState) { 0 -> Icons.Default.KeyboardArrowUp; 2 -> Icons.Default.KeyboardArrowDown; else -> Icons.Default.KeyboardArrowUp },
                            contentDescription = "Toggle", tint = VojoTextGrey
                        )
                        if (panelState == 0) Text("Show list", color = VojoTextGrey, fontSize = 10.sp)
                        if (panelState == 1) Text("Full list", color = VojoTextGrey, fontSize = 10.sp)
                        if (panelState == 2) Text("Show map", color = VojoTextGrey, fontSize = 10.sp)
                    }
                }
                if (panelState >= 1) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        // Night mode toggle
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(if (isNightMode) "🌙" else "☀️", fontSize = 20.sp)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    Translations.t(if (isNightMode) "night_mode" else "day_mode"),
                                    color = VojoTextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold
                                )
                            }
                            Switch(
                                checked = isNightMode,
                                onCheckedChange = {
                                    if (isProUser) {
                                        isNightMode = it
                                    } else {
                                        Toast.makeText(context, "🌙 Night Mode is a PRO feature! Upgrade to unlock nightlife spots.", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = VojoGold,
                                    checkedTrackColor = Color(0xFF2D1B69),
                                    uncheckedThumbColor = Color(0xFFFF9800),
                                    uncheckedTrackColor = Color(0xFF4CAF50).copy(alpha = 0.3f)
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        // Search
                        OutlinedTextField(
                            value = searchQuery, onValueChange = { searchQuery = it },
                            placeholder = { Text(Translations.t("search"), color = VojoTextGrey) },
                            leadingIcon = { Icon(Icons.Default.Search, null, tint = VojoGold) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp), singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = VojoTextWhite, unfocusedTextColor = VojoTextWhite,
                                cursorColor = VojoGold, focusedBorderColor = VojoGold, unfocusedBorderColor = Color.Gray,
                                focusedContainerColor = VojoBlack, unfocusedContainerColor = VojoBlack
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Offline download (PRO only)
                        Button(
                            onClick = {
                                if (isProUser) { isDownloading = true; downloadOfflineMap(context) }
                                else { Toast.makeText(context, "Unlock PRO to download maps! 🗺️", Toast.LENGTH_LONG).show(); navController.navigate("profile_screen") }
                            },
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = if (downloadProgress == 1f) Color(0xFF1B5E20) else VojoDarkGrey),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, VojoGold)
                        ) {
                            if (isDownloading && downloadProgress < 1f) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = VojoGold, strokeWidth = 2.dp)
                                Spacer(Modifier.width(12.dp))
                                Text("DOWNLOADING ROME... 112MB", color = VojoGold, fontSize = 12.sp)
                            } else {
                                Icon(Icons.Default.DownloadForOffline, null, tint = VojoGold)
                                Spacer(Modifier.width(8.dp))
                                Text("DOWNLOAD OFFLINE MAP (PRO)", color = VojoGold, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        // Category filters
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            item { DarkFilterChip(mainFilter == "All", "All", { mainFilter = "All"; subFilter = "All"; activeSort = SortOption.NONE }) }
                            item { DarkFilterChip(mainFilter == "Monuments", "🏛️ Monuments", { mainFilter = "Monuments"; subFilter = "All"; activeSort = SortOption.NONE }) }
                            item { DarkFilterChip(mainFilter == "Food & Drink", "🍔 Food & Drink", { mainFilter = "Food & Drink"; subFilter = "All"; activeSort = SortOption.NONE }) }
                            if (isNightMode) {
                                item { DarkFilterChip(mainFilter == "Nightlife", "🍸 Bars & Clubs", { mainFilter = "Nightlife"; subFilter = "All"; activeSort = SortOption.NONE }) }
                            }
                            item { DarkFilterChip(mainFilter == "Nasoni", "🚰 Free Water", { mainFilter = "Nasoni"; subFilter = "All"; activeSort = SortOption.NONE }) }
                            item { DarkFilterChip(mainFilter == "Toilets", "🚽 WC", { mainFilter = "Toilets"; subFilter = "All"; activeSort = SortOption.NONE }) }
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

                        // Sort filters
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
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(displayedPlaces) { place -> GlovoDarkCard(place) { selectedPlace = place } }
                        if (displayedPlaces.isEmpty()) item { Text(Translations.t("no_places_found"), color = VojoTextGrey, modifier = Modifier.padding(16.dp)) }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                        Text("Explore ${displayedPlaces.size} places nearby", color = VojoTextGrey, fontSize = 14.sp)
                    }
                }
            }

            // ── AdMob Banner for non-PRO ──
            if (!isProUser) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(50.dp).background(VojoBlack),
                    contentAlignment = Alignment.Center
                ) {
                    // AdMob BannerAdView placeholder - will be replaced with actual AdView
                    Text("Ad Banner", color = VojoTextGrey, fontSize = 10.sp)
                }
            }
        }

        if (selectedPlace != null) {
            ModalBottomSheet(onDismissRequest = { selectedPlace = null }, containerColor = VojoDarkGrey) {
                PlaceDetailsContent(selectedPlace!!, userLocation)
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun VojoMapView(places: List<Place>, onPlaceClick: (Place) -> Unit, hasLocationPermission: Boolean = false) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(41.8902, 12.4922), 12f)
    }
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = hasLocationPermission)
    ) {
        places.forEach { place ->
            val hue = when (place.type) {
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
fun PlaceDetailsContent(place: Place, userLocation: LatLng? = null) {
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
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
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
                model = ImageRequest.Builder(LocalContext.current).data(place.imageUrl).crossfade(true).build(),
                contentDescription = place.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(12.dp))
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
                            // 500m geofence check
                            val distance = if (userLocation != null) {
                                val results = FloatArray(1)
                                android.location.Location.distanceBetween(
                                    userLocation.latitude, userLocation.longitude,
                                    place.coords.latitude, place.coords.longitude,
                                    results
                                )
                                results[0]
                            } else null

                            if (distance != null && distance > 500f) {
                                Toast.makeText(context, Translations.t("too_far_away"), Toast.LENGTH_LONG).show()
                            } else {
                                if (hasCameraPermission) cameraLauncher.launch(null) else permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
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
                        text = if (isAlreadyVisited) "ALREADY COLLECTED" else "Add to Passport (+1 Bonus Swipe)",
                        color = if (isAlreadyVisited) Color.White else VojoBlack,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Button(
                onClick = {
                    val gmmIntentUri = Uri.parse("google.navigation:q=${place.coords.latitude},${place.coords.longitude}")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    try { context.startActivity(mapIntent) } catch (_: Exception) { context.startActivity(Intent(Intent.ACTION_VIEW, gmmIntentUri)) }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VojoDarkGrey),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Place, null, tint = VojoTextWhite)
                Spacer(modifier = Modifier.width(8.dp))
                Text(Translations.t("navigate_maps"), color = VojoTextWhite)
            }
        } else {
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
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
fun GlovoDarkCard(place: Place, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = VojoBlack)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF1A1A1A)),
                contentAlignment = Alignment.Center
            ) {
                if (place.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).data(place.imageUrl).crossfade(true).build(),
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
                        contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp)
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
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)) {
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
