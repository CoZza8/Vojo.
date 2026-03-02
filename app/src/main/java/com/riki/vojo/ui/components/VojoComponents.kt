package com.riki.vojo.ui.components

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import android.app.DownloadManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.riki.vojo.Translations
import com.riki.vojo.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

// ── Weather Badge ──
@Composable
fun WeatherBadge() {
    var weatherText by remember { mutableStateOf("☀️ --°C") }
    var weatherDesc by remember { mutableStateOf("Loading...") }
    var windSpeed by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            val url = java.net.URL("https://api.open-meteo.com/v1/forecast?latitude=41.89&longitude=12.49&current_weather=true")
            val result = withContext(Dispatchers.IO) { url.readText() }
            val weather = JSONObject(result).getJSONObject("current_weather")
            val temp = weather.getDouble("temperature")
            val code = weather.getInt("weathercode")
            val wind = weather.getDouble("windspeed")
            val icon = when (code) {
                0 -> "☀️"; 1, 2, 3 -> "⛅"; in 45..48 -> "🌫️"
                in 51..67 -> "🌧️"; in 71..77 -> "❄️"; in 80..82 -> "🌦️"
                in 95..99 -> "⛈️"; else -> "🌤️"
            }
            weatherText = "$icon ${temp.toInt()}°C"
            weatherDesc = Translations.t("weather")
            windSpeed = "💨 ${wind.toInt()} km/h"
        } catch (_: Exception) {
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
            if (windSpeed.isNotEmpty()) {
                Spacer(Modifier.width(8.dp))
                Text(windSpeed, color = VojoTextGrey, fontSize = 10.sp)
            }
        }
    }
}

// ── Bottom Navigation ──
@Composable
fun VojoBottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    NavigationBar(containerColor = VojoBlack) {
        val items = listOf(
            Triple(Translations.t("map"), Icons.Default.Map, "map_screen"),
            Triple(Translations.t("passport"), Icons.Default.Book, "passport_screen"),
            Triple(Translations.t("italian"), Icons.Default.School, "learning_screen"),
            Triple(Translations.t("quests"), Icons.Default.Extension, "quests_screen"),
            Triple(Translations.t("profile"), Icons.Default.Person, "profile_screen"),
        )
        items.forEach { (label, icon, route) ->
            val isSelected = currentRoute == route
            NavigationBarItem(
                selected = isSelected,
                onClick = { if (currentRoute != route) navController.navigate(route) },
                label = { Text(label, color = if (isSelected) VojoGold else VojoTextGrey) },
                icon = { Icon(icon, label, tint = if (isSelected) VojoGold else VojoTextGrey) },
                colors = NavigationBarItemDefaults.colors(indicatorColor = VojoDarkGrey)
            )
        }
    }
}

// ── Filter Chip ──
@Composable
fun DarkFilterChip(selected: Boolean, label: String, onClick: () -> Unit, isSmall: Boolean = false) {
    Surface(
        color = if (selected) VojoGold else VojoBlack,
        shape = RoundedCornerShape(50),
        border = if (selected) null else BorderStroke(1.dp, Color.Gray),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            label,
            color = if (selected) VojoBlack else VojoTextWhite,
            modifier = Modifier.padding(
                horizontal = if (isSmall) 12.dp else 16.dp,
                vertical = if (isSmall) 6.dp else 8.dp
            ),
            fontSize = if (isSmall) 12.sp else 14.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

// ── Offline Map Download ──
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

// ── Open URL via Custom Tab ──
fun openVojoArticle(context: Context, url: String) {
    if (url.isBlank()) return
    val builder = androidx.browser.customtabs.CustomTabsIntent.Builder()
    builder.setToolbarColor(android.graphics.Color.parseColor("#121212"))
    val customTabsIntent = builder.build()
    customTabsIntent.launchUrl(context, Uri.parse(url))
}

// ── Stat Card ──
@Composable
fun ProfileStatCard(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
        Text(text = label, fontSize = 10.sp, color = Color.Gray)
    }
}

// ── PRO Benefit item ──
@Composable
fun ProBenefitItem(icon: ImageVector, title: String, desc: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = VojoGold, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(16.dp))
        Column {
            Text(title, color = VojoTextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(desc, color = VojoTextGrey, fontSize = 11.sp)
        }
    }
}
