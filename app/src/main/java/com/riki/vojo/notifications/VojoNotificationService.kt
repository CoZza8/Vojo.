package com.riki.vojo.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.*
import com.riki.vojo.R
import com.riki.vojo.Translations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Smart Notification system for Vojo PRO:
 * 1. "Golden Hour" - Alerts when sunset is approaching for perfect photo lighting
 * 2. Daily Italian phrase reminder
 */
object VojoNotificationService {
    private const val TAG = "VojoNotif"
    private const val CHANNEL_ID = "vojo_smart_alerts"
    private const val CHANNEL_NAME = "Vojo Smart Alerts"
    private const val QUOTE_CHANNEL_ID = "vojo_rome_quotes"
    private const val QUOTE_CHANNEL_NAME = "Rome Inspiration"

    /** Rome-themed inspirational quotes with translations */
    data class RomeQuote(
        val en: String,
        val it: String,
        val es: String,
        val pl: String
    )

    val romeQuotes = listOf(
        RomeQuote(
            en = "We don't fall in love with a city, but the version of ourselves we become in it. Make the best out of it.",
            it = "Non ci innamoriamo di una città, ma della versione di noi stessi che diventiamo. Fanne il meglio.",
            es = "No nos enamoramos de una ciudad, sino de la versión de nosotros mismos que nos convertimos. Sácale el máximo.",
            pl = "Nie zakochujemy się w mieście, ale w wersji siebie, którą się stajemy. Wykorzystaj to jak najlepiej."
        ),
        RomeQuote(
            en = "Rome wasn't built in a day, but they were laying bricks every hour.",
            it = "Roma non fu costruita in un giorno, ma posavano mattoni ogni ora.",
            es = "Roma no se construyó en un día, pero ponían ladrillos cada hora.",
            pl = "Rzymu nie zbudowano w jeden dzień, ale kładziono cegły co godzinę."
        ),
        RomeQuote(
            en = "All roads lead to Rome, but only the brave walk them.",
            it = "Tutte le strade portano a Roma, ma solo i coraggiosi le percorrono.",
            es = "Todos los caminos llevan a Roma, pero solo los valientes los recorren.",
            pl = "Wszystkie drogi prowadzą do Rzymu, ale tylko odważni nimi idą."
        ),
        RomeQuote(
            en = "In Rome, every stone has a story. Yours is just beginning.",
            it = "A Roma, ogni pietra ha una storia. La tua sta appena iniziando.",
            es = "En Roma, cada piedra tiene una historia. La tuya apenas comienza.",
            pl = "W Rzymie każdy kamień ma swoją historię. Twoja właśnie się zaczyna."
        ),
        RomeQuote(
            en = "La dolce vita isn't a destination — it's a choice you make every morning.",
            it = "La dolce vita non è una destinazione — è una scelta che fai ogni mattina.",
            es = "La dolce vita no es un destino — es una elección que haces cada mañana.",
            pl = "La dolce vita to nie cel podróży — to wybór, którego dokonujesz każdego ranka."
        ),
        RomeQuote(
            en = "Rome will give you everything if you just slow down and look up.",
            it = "Roma ti darà tutto se solo rallenti e guardi in alto.",
            es = "Roma te dará todo si solo reduces la velocidad y miras hacia arriba.",
            pl = "Rzym da ci wszystko, jeśli tylko zwolnisz i spojrzysz w górę."
        ),
        RomeQuote(
            en = "When in Rome, don't just do as the Romans do — become one.",
            it = "Quando sei a Roma, non fare solo come i romani — diventa uno di loro.",
            es = "Cuando estés en Roma, no solo hagas como los romanos — conviértete en uno.",
            pl = "Będąc w Rzymie, nie rób tylko tego co Rzymianie — stań się jednym z nich."
        ),
        RomeQuote(
            en = "The Eternal City doesn't rush. Neither should you.",
            it = "La Città Eterna non ha fretta. Nemmeno tu dovresti.",
            es = "La Ciudad Eterna no tiene prisa. Tú tampoco deberías.",
            pl = "Wieczne Miasto się nie spieszy. Ty też nie powinieneś."
        ),
        RomeQuote(
            en = "Leave a piece of your heart in Rome — you'll always have a reason to come back.",
            it = "Lascia un pezzo del tuo cuore a Roma — avrai sempre un motivo per tornare.",
            es = "Deja un pedazo de tu corazón en Roma — siempre tendrás una razón para volver.",
            pl = "Zostaw kawałek serca w Rzymie — zawsze będziesz mieć powód, żeby wrócić."
        ),
        RomeQuote(
            en = "A sunset in Rome is worth more than a thousand photographs.",
            it = "Un tramonto a Roma vale più di mille fotografie.",
            es = "Un atardecer en Roma vale más que mil fotografías.",
            pl = "Zachód słońca w Rzymie jest wart więcej niż tysiąc zdjęć."
        ),
        RomeQuote(
            en = "Rome teaches you that beauty is not in perfection, but in the passage of time.",
            it = "Roma ti insegna che la bellezza non è nella perfezione, ma nel passaggio del tempo.",
            es = "Roma te enseña que la belleza no está en la perfección, sino en el paso del tiempo.",
            pl = "Rzym uczy, że piękno nie tkwi w doskonałości, lecz w upływie czasu."
        ),
        RomeQuote(
            en = "Every corner of Rome whispers: 'You are exactly where you're meant to be.'",
            it = "Ogni angolo di Roma sussurra: 'Sei esattamente dove dovresti essere.'",
            es = "Cada rincón de Roma susurra: 'Estás exactamente donde debes estar.'",
            pl = "Każdy zakątek Rzymu szepcze: 'Jesteś dokładnie tam, gdzie powinieneś być.'"
        )
    )

    /** Get a translated quote based on current app language */
    fun getTranslatedQuote(quote: RomeQuote): String {
        return when (Translations.currentLang) {
            Translations.Lang.IT -> quote.it
            Translations.Lang.ES -> quote.es
            Translations.Lang.PL -> quote.pl
            else -> quote.en
        }
    }

    /** Create notification channels (required for Android 8+) */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val smartChannel = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Smart travel alerts: Golden Hour and daily Italian phrases"
            }
            val quoteChannel = NotificationChannel(
                QUOTE_CHANNEL_ID, QUOTE_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Inspirational Rome quotes and travel wisdom"
            }
            manager.createNotificationChannel(smartChannel)
            manager.createNotificationChannel(quoteChannel)
        }
    }

    /** Schedule recurring smart notification checks */
    fun scheduleSmartAlerts(context: Context) {
        createNotificationChannel(context)

        // Schedule 3 daily checks: morning (9:00), afternoon (16:00), evening (18:00)
        val workRequest = PeriodicWorkRequestBuilder<SmartAlertWorker>(4, TimeUnit.HOURS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "vojo_smart_alerts",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
        Log.d(TAG, "Smart alerts scheduled")
    }

    /** Send a notification */
    fun sendNotification(context: Context, title: String, message: String, notificationId: Int, channelId: String = CHANNEL_ID) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "Notification permission not granted")
                return
            }
        }

        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.colosseum)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }

    /** Send an inspirational Rome quote notification */
    fun sendQuoteNotification(context: Context) {
        val quote = romeQuotes.random()
        val translatedQuote = getTranslatedQuote(quote)
        sendNotification(
            context,
            "✨ Rome Inspiration",
            "\"$translatedQuote\"",
            1004,
            QUOTE_CHANNEL_ID
        )
    }
}

/**
 * WorkManager worker that checks weather data and sends smart alerts.
 */
class SmartAlertWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        try {
            val prefs = applicationContext.getSharedPreferences("vojo_prefs", Context.MODE_PRIVATE)
            val isProUser = prefs.getBoolean("is_pro_user", false)

            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)

            // Fetch weather data from Open-Meteo
            val weatherData = fetchWeatherData()

            when {
                // Morning alert (8-10): Daily Italian phrase + weather tip + Rome quote
                hour in 8..10 -> {
                    // 50% chance: Italian phrase, 50% chance: inspirational quote
                    if (Math.random() < 0.5) {
                        val phrases = listOf(
                            "\"Buongiorno!\" means Good Morning 🌅",
                            "\"Quanto costa?\" means How much? 💰",
                            "\"Scusa, dov'è...?\" means Excuse me, where is...? 🗺️",
                            "\"Un caffè, per favore\" means A coffee, please ☕",
                            "\"Il conto, per favore\" means The bill, please 💳",
                            "\"Grazie mille!\" means Thank you very much! 🙏"
                        )
                        val phrase = phrases.random()
                        VojoNotificationService.sendNotification(
                            applicationContext,
                            "🇮🇹 Buongiorno! Today's Italian",
                            "$phrase ${if (weatherData != null) "\n☀️ Rome: ${weatherData.temp}°C" else ""}",
                            1001
                        )
                    } else {
                        VojoNotificationService.sendQuoteNotification(applicationContext)
                    }
                }

                // Midday quote (12-14): Inspirational Rome quotes
                hour in 12..14 -> {
                    VojoNotificationService.sendQuoteNotification(applicationContext)
                }

                // Golden Hour alert (16-18): Perfect photo timing
                hour in 16..18 -> {
                    if (weatherData != null && weatherData.isGoodForPhotos) {
                        val monument = listOf(
                            "Colosseum" to "The golden light hits the arches perfectly right now!",
                            "Trevi Fountain" to "The fountain sparkles in golden hour light 📸",
                            "Spanish Steps" to "Perfect warm light for a Piazza di Spagna photo!",
                            "Pantheon" to "The oculus light beam is magical at this time!",
                            "St. Peter's Basilica" to "The dome glows golden right now 🏛️"
                        ).random()

                        VojoNotificationService.sendNotification(
                            applicationContext,
                            "✨ Golden Hour at ${monument.first}!",
                            "${monument.second}\nTemperature: ${weatherData.temp}°C, ${weatherData.conditions}.",
                            1002
                        )
                    }
                }

                // Evening alert (19-21): Night exploration tips + Rome quote
                hour in 19..21 -> {
                    if (Math.random() < 0.5) {
                        VojoNotificationService.sendNotification(
                            applicationContext,
                            "🌙 Evening in Rome — Time to Explore!",
                            "Rome's monuments are beautifully illuminated! Perfect time to visit Piazza Navona or Castel Sant'Angelo by night 🏰✨",
                            1003
                        )
                    } else {
                        VojoNotificationService.sendQuoteNotification(applicationContext)
                    }
                }
            }

            return Result.success()
        } catch (e: Exception) {
            Log.e("SmartAlertWorker", "Error: ${e.message}")
            return Result.retry()
        }
    }

    private suspend fun fetchWeatherData(): WeatherInfo? = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://api.open-meteo.com/v1/forecast?latitude=41.89&longitude=12.49&current_weather=true")
            val response = url.readText()
            val json = JSONObject(response)
            val current = json.getJSONObject("current_weather")

            val temp = current.getDouble("temperature")
            val weatherCode = current.getInt("weathercode")
            val windSpeed = current.getDouble("windspeed")

            WeatherInfo(
                temp = temp.toInt(),
                conditions = describeWeather(weatherCode),
                isGoodForPhotos = weatherCode in listOf(0, 1, 2, 3) && windSpeed < 30,
                windSpeed = windSpeed
            )
        } catch (e: Exception) {
            Log.e("SmartAlertWorker", "Weather fetch failed: ${e.message}")
            null
        }
    }

    private fun describeWeather(code: Int): String = when (code) {
        0 -> "Clear sky ☀️"
        1 -> "Mainly clear 🌤️"
        2 -> "Partly cloudy ⛅"
        3 -> "Overcast ☁️"
        in 45..48 -> "Foggy 🌫️"
        in 51..55 -> "Light drizzle 🌦️"
        in 61..65 -> "Rain 🌧️"
        in 71..77 -> "Snow ❄️"
        in 80..82 -> "Rain showers 🌦️"
        in 95..99 -> "Thunderstorm ⛈️"
        else -> "Variable"
    }


}

data class WeatherInfo(
    val temp: Int,
    val conditions: String,
    val isGoodForPhotos: Boolean,
    val windSpeed: Double
)
