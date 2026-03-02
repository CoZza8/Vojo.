package com.riki.vojo.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.riki.vojo.PassportData
import com.riki.vojo.Translations
import com.riki.vojo.ui.components.VojoBottomBar
import com.riki.vojo.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PassportScreen(navController: NavController) {
    val context = LocalContext.current
    var selectedStampIndex by remember { mutableIntStateOf(-1) }

    Scaffold(
        containerColor = VojoBlack,
        topBar = {
            TopAppBar(
                title = { Text(Translations.t("my_passport"), color = VojoTextWhite) },
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
                Row(
                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(Translations.t("places_visited"), color = VojoTextGrey, fontSize = 12.sp)
                        Text("${PassportData.stamps.size}", color = VojoTextWhite, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(Translations.t("swipes_earned"), color = VojoTextGrey, fontSize = 12.sp)
                        Text("+${PassportData.earnedSwipes}", color = VojoGold, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (PassportData.stamps.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CameraAlt, null, tint = Color.DarkGray, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(Translations.t("no_stamps"), color = VojoTextGrey, fontSize = 18.sp)
                        Text(
                            Translations.t("passport_empty_hint"),
                            color = Color.Gray, textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { navController.navigate("map_screen") },
                            colors = ButtonDefaults.buttonColors(containerColor = VojoGold)
                        ) {
                            Text(Translations.t("go_to_map"), color = VojoBlack)
                        }
                    }
                }
            } else {
                // Album header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(Translations.t("photo_album"), style = MaterialTheme.typography.titleLarge, color = VojoTextWhite)
                    Surface(
                        color = VojoGold.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "${PassportData.stamps.size} ${Translations.t("stamps").lowercase()}",
                            color = VojoGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(PassportData.stamps.size) { index ->
                        val stamp = PassportData.stamps[index]
                        Card(
                            colors = CardDefaults.cardColors(containerColor = VojoDarkGrey),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.clickable { selectedStampIndex = index }
                        ) {
                            Column {
                                Box(modifier = Modifier.height(160.dp).fillMaxWidth().background(Color.Black)) {
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
                                    // Photo number badge
                                    Surface(
                                        color = VojoGold,
                                        shape = RoundedCornerShape(bottomEnd = 12.dp),
                                        modifier = Modifier.align(Alignment.TopStart)
                                    ) {
                                        Text(
                                            "#${index + 1}",
                                            color = VojoBlack,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
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

    // ── Fullscreen Photo Viewer ──
    if (selectedStampIndex >= 0 && selectedStampIndex < PassportData.stamps.size) {
        val stamp = PassportData.stamps[selectedStampIndex]
        AlertDialog(
            onDismissRequest = { selectedStampIndex = -1 },
            containerColor = Color.Black,
            modifier = Modifier.fillMaxWidth(),
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(stamp.placeName, color = VojoTextWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(stamp.date, color = VojoTextGrey, fontSize = 12.sp)
                    }
                    Text("#${selectedStampIndex + 1}", color = VojoGold, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (stamp.image != null) {
                        Image(
                            bitmap = stamp.image.asImageBitmap(),
                            contentDescription = stamp.placeName,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxWidth().heightIn(min = 200.dp, max = 400.dp)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (selectedStampIndex > 0) {
                            TextButton(onClick = { selectedStampIndex-- }) {
                                Text("← ${Translations.t("previous")}", color = VojoGold)
                            }
                        } else { Spacer(Modifier.width(1.dp)) }
                        if (selectedStampIndex < PassportData.stamps.size - 1) {
                            TextButton(onClick = { selectedStampIndex++ }) {
                                Text("${Translations.t("next")} →", color = VojoGold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedStampIndex = -1 }) {
                    Text(Translations.t("close"), color = VojoGold, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

