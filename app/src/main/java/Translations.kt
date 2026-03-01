package com.riki.vojo

object Translations {
    enum class Lang { EN, IT, ES, PL }

    var currentLang: Lang = Lang.EN

    private val strings = mapOf(
        "start_journey" to mapOf(Lang.EN to "START JOURNEY", Lang.IT to "INIZIA IL VIAGGIO", Lang.ES to "EMPEZAR VIAJE", Lang.PL to "ROZPOCZNIJ PODRÓŻ"),
        "explore_collect_learn" to mapOf(Lang.EN to "Explore. Collect. Learn.", Lang.IT to "Esplora. Colleziona. Impara.", Lang.ES to "Explora. Colecciona. Aprende.", Lang.PL to "Odkrywaj. Zbieraj. Ucz się."),
        "my_passport" to mapOf(Lang.EN to "My Passport 🌍", Lang.IT to "Il Mio Passaporto 🌍", Lang.ES to "Mi Pasaporte 🌍", Lang.PL to "Mój Paszport 🌍"),
        "places_visited" to mapOf(Lang.EN to "Places Visited", Lang.IT to "Luoghi Visitati", Lang.ES to "Lugares Visitados", Lang.PL to "Odwiedzone Miejsca"),
        "swipes_earned" to mapOf(Lang.EN to "Swipes Earned", Lang.IT to "Swipe Guadagnati", Lang.ES to "Swipes Ganados", Lang.PL to "Zdobyte Swipe'y"),
        "your_memories" to mapOf(Lang.EN to "Your Memories", Lang.IT to "I Tuoi Ricordi", Lang.ES to "Tus Recuerdos", Lang.PL to "Twoje Wspomnienia"),
        "no_stamps" to mapOf(Lang.EN to "No stamps yet.", Lang.IT to "Nessun timbro ancora.", Lang.ES to "Sin sellos aún.", Lang.PL to "Brak pieczątek."),
        "go_to_map" to mapOf(Lang.EN to "Go to Map", Lang.IT to "Vai alla Mappa", Lang.ES to "Ir al Mapa", Lang.PL to "Idź do Mapy"),
        "daily_words_left" to mapOf(Lang.EN to "Daily Words Left", Lang.IT to "Parole Giornaliere", Lang.ES to "Palabras del Día", Lang.PL to "Dzienne Słowa"),
        "flashcards" to mapOf(Lang.EN to "FlashCards", Lang.IT to "Carte Flash", Lang.ES to "Tarjetas", Lang.PL to "Fiszki"),
        "learned_words" to mapOf(Lang.EN to "Learned Words", Lang.IT to "Parole Imparate", Lang.ES to "Palabras Aprendidas", Lang.PL to "Nauczone Słowa"),
        "daily_limit" to mapOf(Lang.EN to "Daily Limit Reached!", Lang.IT to "Limite Giornaliero!", Lang.ES to "¡Límite Diario!", Lang.PL to "Limit Dzienny!"),
        "search" to mapOf(Lang.EN to "Search...", Lang.IT to "Cerca...", Lang.ES to "Buscar...", Lang.PL to "Szukaj..."),
        "profile" to mapOf(Lang.EN to "Profile", Lang.IT to "Profilo", Lang.ES to "Perfil", Lang.PL to "Profil"),
        "commander_center" to mapOf(Lang.EN to "COMMANDER CENTER", Lang.IT to "CENTRO COMANDO", Lang.ES to "CENTRO DE MANDO", Lang.PL to "CENTRUM DOWODZENIA"),
        "achievements" to mapOf(Lang.EN to "ACHIEVEMENTS", Lang.IT to "OBIETTIVI", Lang.ES to "LOGROS", Lang.PL to "OSIĄGNIĘCIA"),
        "upgrade_pro" to mapOf(Lang.EN to "UPGRADE TO VOJO PRO", Lang.IT to "PASSA A VOJO PRO", Lang.ES to "MEJORA A VOJO PRO", Lang.PL to "ULEPSZ DO VOJO PRO"),
        "edit_profile" to mapOf(Lang.EN to "EDIT PROFILE", Lang.IT to "MODIFICA PROFILO", Lang.ES to "EDITAR PERFIL", Lang.PL to "EDYTUJ PROFIL"),
        "select_language" to mapOf(Lang.EN to "Select Language", Lang.IT to "Seleziona Lingua", Lang.ES to "Seleccionar Idioma", Lang.PL to "Wybierz Język"),
        "weather" to mapOf(Lang.EN to "Rome Weather", Lang.IT to "Meteo Roma", Lang.ES to "Clima Roma", Lang.PL to "Pogoda Rzym"),
        "night_mode" to mapOf(Lang.EN to "Night Mode", Lang.IT to "Modalità Notte", Lang.ES to "Modo Noche", Lang.PL to "Tryb Nocny"),
        "day_mode" to mapOf(Lang.EN to "Day Mode", Lang.IT to "Modalità Giorno", Lang.ES to "Modo Día", Lang.PL to "Tryb Dzienny"),
        "map" to mapOf(Lang.EN to "Map", Lang.IT to "Mappa", Lang.ES to "Mapa", Lang.PL to "Mapa"),
        "passport" to mapOf(Lang.EN to "Passport", Lang.IT to "Passaporto", Lang.ES to "Pasaporte", Lang.PL to "Paszport"),
        "italian" to mapOf(Lang.EN to "Italian", Lang.IT to "Italiano", Lang.ES to "Italiano", Lang.PL to "Włoski"),
        "quests" to mapOf(Lang.EN to "Quests", Lang.IT to "Missioni", Lang.ES to "Misiones", Lang.PL to "Misje"),
        "stamps" to mapOf(Lang.EN to "STAMPS", Lang.IT to "TIMBRI", Lang.ES to "SELLOS", Lang.PL to "PIECZĄTKI"),
        "phrases" to mapOf(Lang.EN to "PHRASES", Lang.IT to "FRASI", Lang.ES to "FRASES", Lang.PL to "FRAZY"),
        "power" to mapOf(Lang.EN to "POWER", Lang.IT to "POTENZA", Lang.ES to "PODER", Lang.PL to "MOC"),
        "no_words_yet" to mapOf(Lang.EN to "No words learned yet.", Lang.IT to "Nessuna parola imparata.", Lang.ES to "Sin palabras aprendidas.", Lang.PL to "Nie nauczono jeszcze słów."),
        "all_phrases_learned" to mapOf(Lang.EN to "All phrases learned!", Lang.IT to "Tutte le frasi imparate!", Lang.ES to "¡Todas las frases aprendidas!", Lang.PL to "Wszystkie frazy nauczone!"),
        "review_learned" to mapOf(Lang.EN to "Review Learned Words", Lang.IT to "Rivedi Parole", Lang.ES to "Repasar Palabras", Lang.PL to "Powtórz Słowa"),
        "navigate_maps" to mapOf(Lang.EN to "Navigate with Google Maps", Lang.IT to "Naviga con Google Maps", Lang.ES to "Navegar con Google Maps", Lang.PL to "Nawiguj z Google Maps"),
        "last_discovery" to mapOf(Lang.EN to "LAST DISCOVERY", Lang.IT to "ULTIMA SCOPERTA", Lang.ES to "ÚLTIMO DESCUBRIMIENTO", Lang.PL to "OSTATNIE ODKRYCIE"),
        "via_romana" to mapOf(Lang.EN to "Via Romana", Lang.IT to "Via Romana", Lang.ES to "Vía Romana", Lang.PL to "Via Romana"),
        "continue_journey" to mapOf(Lang.EN to "Continue Journey", Lang.IT to "Continua il Viaggio", Lang.ES to "Continuar Viaje", Lang.PL to "Kontynuuj Podróż"),
        "try_again" to mapOf(Lang.EN to "Try Again", Lang.IT to "Riprova", Lang.ES to "Intentar de Nuevo", Lang.PL to "Spróbuj Ponownie"),
        "get_pro" to mapOf(Lang.EN to "GET PRO - 4.99€", Lang.IT to "ACQUISTA PRO - 4.99€", Lang.ES to "OBTENER PRO - 4.99€", Lang.PL to "KUP PRO - 4.99€"),
        "bonus_active" to mapOf(Lang.EN to "BONUS ACTIVE!", Lang.IT to "BONUS ATTIVO!", Lang.ES to "¡BONUS ACTIVO!", Lang.PL to "BONUS AKTYWNY!"),
        "tap_listen_swipe" to mapOf(Lang.EN to "Tap to Listen • Swipe to Save", Lang.IT to "Tocca per Ascoltare • Scorri per Salvare", Lang.ES to "Toca para Escuchar • Desliza para Guardar", Lang.PL to "Dotknij, aby słuchać • Przesuń, aby zapisać"),
        "commander_profile" to mapOf(Lang.EN to "COMMANDER PROFILE", Lang.IT to "PROFILO COMANDANTE", Lang.ES to "PERFIL COMANDANTE", Lang.PL to "PROFIL DOWÓDCY"),
        "select_avatar" to mapOf(Lang.EN to "Select Your Avatar:", Lang.IT to "Seleziona Avatar:", Lang.ES to "Selecciona Avatar:", Lang.PL to "Wybierz Awatar:"),
        "save" to mapOf(Lang.EN to "SAVE", Lang.IT to "SALVA", Lang.ES to "GUARDAR", Lang.PL to "ZAPISZ"),
        "nickname" to mapOf(Lang.EN to "Nickname", Lang.IT to "Soprannome", Lang.ES to "Apodo", Lang.PL to "Pseudonim"),
    )

    fun t(key: String): String = strings[key]?.get(currentLang) ?: strings[key]?.get(Lang.EN) ?: key
}