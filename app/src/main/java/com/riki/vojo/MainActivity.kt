package com.riki.vojo

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.riki.vojo.presentation.ProViewModel
import com.riki.vojo.presentation.ProViewModelFactory
import com.riki.vojo.ui.screens.*
import com.riki.vojo.ui.theme.VojoTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize TTS
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.ITALIAN
            } else {
                Log.e("VojoTTS", "TTS initialization failed")
            }
        }

        // Initialize AdMob
        com.riki.vojo.ads.AdManager.initialize(this)

        // Initialize Google Play Billing
        com.riki.vojo.billing.BillingManager.initialize(this)

        // Schedule smart notifications (Golden Hour, Daily Italian)
        com.riki.vojo.notifications.VojoNotificationService.scheduleSmartAlerts(this)

        setContent {
            VojoTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    VojoNavigation(tts = tts)
                }
            }
        }
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}

@Composable
fun VojoNavigation(tts: TextToSpeech?) {
    val navController = rememberNavController()
    val context = LocalContext.current

    val viewModel: ProViewModel = viewModel(
        factory = ProViewModelFactory(context)
    )
    val isProUser by viewModel.isProUser.collectAsState()

    NavHost(navController = navController, startDestination = "welcome_screen") {
        composable("welcome_screen") {
            WelcomeScreen(navController)
        }
        composable("onboarding_screen") {
            OnboardingScreen(navController)
        }
        composable("map_screen") {
            MapScreen(navController, tts, isProUser)
        }
        composable("passport_screen") {
            PassportScreen(navController)
        }
        composable("learning_screen") {
            LearningScreen(navController, tts, isProUser)
        }
        composable("quests_screen") {
            QuestMapScreen(isUserPro = isProUser, navController = navController)
        }
        composable("profile_screen") {
            ProfileScreen(navController, viewModel)
        }
        composable("settings_screen") {
            SettingsScreen(navController)
        }
    }
}
