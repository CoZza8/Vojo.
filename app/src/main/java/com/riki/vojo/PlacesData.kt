package com.riki.vojo

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateListOf
import com.google.android.gms.maps.model.LatLng
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// --- DATA MODELS ---

data class DetailedRating(val food: Int, val price: Int, val service: Int, val atmosphere: Int)
data class PlaceReview(val author: String, val comment: String, val rating: DetailedRating, val date: String)

data class Place(
    val name: String,
    val description: String,
    val history: String,
    val wikiUrl: String,
    val imageUrl: String,
    val coords: LatLng,
    val rating: Double,
    val type: String,
    val priceOrTag: String,
    val openingHours: String,
    val reviews: MutableList<PlaceReview> = mutableListOf(),
    val longHistory: String = "",
    val recommendation : String? = null ,
)

data class PassportStamp(
    val placeName: String,
    val image: Bitmap?,
    val date: String
)
data class QuestMonument(
    val id: String,
    val name: String,
    val imageUrl: String,
    val iconEmoji: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val funFact: String,
    val xPercent: Float,
    val isPro: Boolean,
    val questType: QuestType = QuestType.QUIZ,
    val emojiClues: String? = null,       // for EMOJI_DECODE
    val fillBlankSentence: String? = null // for FILL_BLANK
)

enum class QuestType {
    QUIZ,            // Classic multiple choice
    EMOJI_DECODE,    // Guess the monument from emoji clues
    TRUE_FALSE,      // True or False challenge
    FILL_BLANK       // Fill in the blank
}

object QuestData {
    val monuments = listOf(
        QuestMonument(
            id = "colosseum",
            name = "Colosseum",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/de/Colosseo_2020.jpg/600px-Colosseo_2020.jpg",
            iconEmoji = "🏟️",
            question = "How many spectators could the Colosseum hold at its peak?",
            options = listOf("25,000", "50,000", "80,000", "120,000"),
            correctIndex = 2,
            funFact = "The Colosseum could be flooded with water to stage mock naval battles called \"naumachiae\". Engineers built an elaborate system of channels and aqueducts beneath the arena floor!",
            xPercent = 0.5f,
            isPro = false
        ),
        // 🧩 EMOJI DECODE QUEST
        QuestMonument(
            id = "gladiator_emoji",
            name = "Arena Challenge",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/de/Colosseo_2020.jpg/600px-Colosseo_2020.jpg",
            iconEmoji = "⚔️",
            question = "Which ancient Rome fighter do these emojis represent?\n\n⚔️🛡️🏟️💪🗡️",
            options = listOf("Gladiator", "Centurion", "Legionary", "Senator"),
            correctIndex = 0,
            funFact = "Gladiators were often celebrities in ancient Rome! They had fan clubs, and women would pay large sums for their sweat, which was believed to be an aphrodisiac. Some gladiators even endorsed products!",
            xPercent = 0.55f,
            isPro = false,
            questType = QuestType.EMOJI_DECODE,
            emojiClues = "⚔️🛡️🏟️💪🗡️"
        ),
        QuestMonument(
            id = "pantheon",
            name = "Pantheon",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ef/Pantheon_Rom_1_cropped.jpg/600px-Pantheon_Rom_1_cropped.jpg",
            iconEmoji = "🏛️",
            question = "What is unique about the Pantheon's dome?",
            options = listOf("It is made of gold", "It has no supporting pillars", "It has an open hole (oculus) at the top", "It was built in one day"),
            correctIndex = 2,
            funFact = "The Pantheon's oculus is 9 meters wide and is the only source of light. When it rains, water falls through the hole but drains away through 22 almost-invisible holes in the floor!",
            xPercent = 0.25f,
            isPro = false
        ),
        // ✅❌ TRUE/FALSE QUEST
        QuestMonument(
            id = "pantheon_tf",
            name = "Fact or Fiction?",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ef/Pantheon_Rom_1_cropped.jpg/600px-Pantheon_Rom_1_cropped.jpg",
            iconEmoji = "🤔",
            question = "TRUE or FALSE: The Pantheon's concrete dome is still the largest unreinforced concrete dome in the world after 2,000 years.",
            options = listOf("TRUE ✅", "FALSE ❌"),
            correctIndex = 0,
            funFact = "It's TRUE! The Pantheon's dome (43.3m diameter) remains the world's largest unreinforced concrete dome. Modern engineers still can't fully explain how ancient Romans achieved this. The concrete gets progressively lighter towards the top, using volcanic pumice near the oculus!",
            xPercent = 0.3f,
            isPro = true,
            questType = QuestType.TRUE_FALSE
        ),
        QuestMonument(
            id = "trevi",
            name = "Trevi Fountain",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c7/Trevi_Fountain_-_Roma.jpg/600px-Trevi_Fountain_-_Roma.jpg",
            iconEmoji = "⛲",
            question = "How much money is thrown into the Trevi Fountain each year?",
            options = listOf("About €100,000", "About €500,000", "About €1.5 million", "About €5 million"),
            correctIndex = 2,
            funFact = "Approximately €1.5 million is thrown into the Trevi Fountain annually! The money is collected every night and donated to Caritas, a Catholic charity that funds a supermarket for the poor in Rome.",
            xPercent = 0.72f,
            isPro = true
        ),
        // 😀 EMOJI DECODE QUEST
        QuestMonument(
            id = "emoji_landmark",
            name = "Emoji Decode",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c7/Trevi_Fountain_-_Roma.jpg/600px-Trevi_Fountain_-_Roma.jpg",
            iconEmoji = "🧩",
            question = "Which Roman landmark do these emojis represent?\n\n💧⛲🪙🤞✨",
            options = listOf("Baths of Caracalla", "Trevi Fountain", "Navona Fountain", "Aqua Virgo"),
            correctIndex = 1,
            funFact = "The tradition of throwing coins into the Trevi says: 1 coin = return to Rome, 2 coins = find love in Rome, 3 coins = get married in Rome! The fountain is fed by a 2,000-year-old Roman aqueduct called Aqua Virgo!",
            xPercent = 0.68f,
            isPro = true,
            questType = QuestType.EMOJI_DECODE,
            emojiClues = "💧⛲🪙🤞✨"
        ),
        QuestMonument(
            id = "vatican",
            name = "St. Peter's Basilica",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f5/Basilica_di_San_Pietro_in_Vaticano_September_2015-1a.jpg/600px-Basilica_di_San_Pietro_in_Vaticano_September_2015-1a.jpg",
            iconEmoji = "⛪",
            question = "Who designed the dome of St. Peter's Basilica?",
            options = listOf("Leonardo da Vinci", "Michelangelo", "Raphael", "Bernini"),
            correctIndex = 1,
            funFact = "Michelangelo designed the dome at age 71 and never saw it completed. The dome is so large that the Statue of Liberty could fit inside it! It took 22 years to build.",
            xPercent = 0.3f,
            isPro = true
        ),
        QuestMonument(
            id = "spanish_steps",
            name = "Spanish Steps",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b8/Piazza_di_Spagna_%28Rome%29_0004.jpg/600px-Piazza_di_Spagna_%28Rome%29_0004.jpg",
            iconEmoji = "🪜",
            question = "How many steps make up the Spanish Steps?",
            options = listOf("100", "135", "150", "200"),
            correctIndex = 1,
            funFact = "The 135 steps were built in 1723-1725 to link the Spanish Embassy to the Trinità dei Monti church. Eating on the steps was banned in 2019 — you can get fined up to €400!",
            xPercent = 0.65f,
            isPro = true
        ),
        // ✅❌ TRUE/FALSE QUEST #2
        QuestMonument(
            id = "bernini_tf",
            name = "Bernini Challenge",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/08/Piazza_Navona_%28Rome%29_at_night.jpg/600px-Piazza_Navona_%28Rome%29_at_night.jpg",
            iconEmoji = "🎨",
            question = "TRUE or FALSE: Bernini designed both the colonnade of St. Peter's Square AND the Fountain of the Four Rivers in Piazza Navona.",
            options = listOf("TRUE ✅", "FALSE ❌"),
            correctIndex = 0,
            funFact = "It's TRUE! Gian Lorenzo Bernini created over 60 masterpieces in Rome! He designed the colonnade of St. Peter's Square, the Fountain of the Four Rivers in Piazza Navona, and sculpted the famous 'Ecstasy of Saint Teresa'. He started sculpting at age 8!",
            xPercent = 0.38f,
            isPro = true,
            questType = QuestType.TRUE_FALSE
        ),
        QuestMonument(
            id = "forum",
            name = "Roman Forum",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6a/Foro_Romano_Musei_Capitolini_Roma.jpg/600px-Foro_Romano_Musei_Capitolini_Roma.jpg",
            iconEmoji = "🏗️",
            question = "What was the Roman Forum primarily used for?",
            options = listOf("Military training grounds", "Public gatherings, commerce, and politics", "Religious ceremonies only", "Gladiator fights"),
            correctIndex = 1,
            funFact = "The Roman Forum was the beating heart of Roman public life for centuries. Julius Caesar was cremated here in 44 BC, and Romans threw so many offerings into his pyre that the fire brigade had to be called!",
            xPercent = 0.4f,
            isPro = true
        ),
        QuestMonument(
            id = "castel",
            name = "Castel Sant'Angelo",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/66/Castel_Sant%27_Angelo_Between_Leaves.jpg/600px-Castel_Sant%27_Angelo_Between_Leaves.jpg",
            iconEmoji = "🏰",
            question = "What was Castel Sant'Angelo originally built as?",
            options = listOf("A prison", "A mausoleum for Emperor Hadrian", "A fortress", "A church"),
            correctIndex = 1,
            funFact = "Castel Sant'Angelo has a secret escape passage called the \"Passetto di Borgo\" — a 800-meter elevated corridor connecting it to the Vatican. Pope Clement VII used it to escape during the Sack of Rome in 1527!",
            xPercent = 0.7f,
            isPro = true
        ),
        QuestMonument(
            id = "navona",
            name = "Piazza Navona",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/08/Piazza_Navona_%28Rome%29_at_night.jpg/600px-Piazza_Navona_%28Rome%29_at_night.jpg",
            iconEmoji = "🎭",
            question = "What was the original purpose of Piazza Navona's elongated shape?",
            options = listOf("It was a marketplace", "It was built on a Roman stadium", "It was designed for parades", "It was a river port"),
            correctIndex = 1,
            funFact = "Piazza Navona was built on the ruins of the Stadium of Domitian (1st century AD). Until the 19th century, the square was regularly flooded on weekends in August so that Romans could wade and play in the water to escape the summer heat!",
            xPercent = 0.35f,
            isPro = true
        ),
        // ─── NEW QUESTS (13-20) ───
        // 🧩 EMOJI DECODE: Trastevere
        QuestMonument(
            id = "trastevere_emoji",
            name = "Trastevere Decode",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/63/01_Santa_Maria_in_Trastevere_Facade.jpg/600px-01_Santa_Maria_in_Trastevere_Facade.jpg",
            iconEmoji = "🍷",
            question = "Which charming Roman neighborhood do these emojis represent?\n\n🍷🌙🎶🏘️✨",
            options = listOf("Trastevere", "Testaccio", "Monti", "Prati"),
            correctIndex = 0,
            funFact = "Trastevere means 'across the Tiber'. In ancient Rome, it was home to immigrants and sailors. Today it's Rome's most bohemian neighborhood — its narrow cobblestone streets are covered in ivy and filled with live music every evening!",
            xPercent = 0.42f,
            isPro = true,
            questType = QuestType.EMOJI_DECODE,
            emojiClues = "🍷🌙🎶🏘️✨"
        ),
        // 🏛️ QUIZ: Circus Maximus
        QuestMonument(
            id = "circus_maximus",
            name = "Circus Maximus",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3a/Circus_Maximus_in_Rome.jpg/600px-Circus_Maximus_in_Rome.jpg",
            iconEmoji = "🐎",
            question = "How many spectators could the Circus Maximus hold for chariot races?",
            options = listOf("50,000", "100,000", "150,000", "250,000"),
            correctIndex = 3,
            funFact = "The Circus Maximus could seat 250,000 spectators — over 3x the Colosseum! Chariot races were the most popular sport in ancient Rome. Drivers were celebrities, and fans organized into factions (Blues, Greens, Reds, Whites) that sometimes caused riots!",
            xPercent = 0.48f,
            isPro = true
        ),
        // ✅❌ TRUE/FALSE: Catacombs
        QuestMonument(
            id = "catacombs_tf",
            name = "Catacomb Mystery",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f1/Rome-Catacombs001.jpg/600px-Rome-Catacombs001.jpg",
            iconEmoji = "💀",
            question = "TRUE or FALSE: Rome's underground catacombs stretch over 150 kilometers — longer than the Rome Metro system.",
            options = listOf("TRUE ✅", "FALSE ❌"),
            correctIndex = 0,
            funFact = "It's TRUE! Rome's catacombs are an underground city of the dead, stretching over 150km with an estimated 750,000 burial sites! The Catacombs of San Callisto alone have 20km of tunnels on 4 levels, reaching 20 meters underground.",
            xPercent = 0.52f,
            isPro = true,
            questType = QuestType.TRUE_FALSE
        ),
        // 🏛️ QUIZ: Villa Borghese
        QuestMonument(
            id = "borghese",
            name = "Villa Borghese",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/4/4a/Galleria_Borghese_%2846755302%29.jpeg/600px-Galleria_Borghese_%2846755302%29.jpeg",
            iconEmoji = "🎨",
            question = "Which sculptor created the famous 'Apollo and Daphne' in the Borghese Gallery?",
            options = listOf("Michelangelo", "Donatello", "Bernini", "Canova"),
            correctIndex = 2,
            funFact = "Bernini sculpted 'Apollo and Daphne' when he was just 24 years old! The marble sculpture captures the exact moment of Daphne transforming into a laurel tree — her fingers turning into leaves and bark growing up her legs. It's considered one of the greatest sculptures ever made.",
            xPercent = 0.56f,
            isPro = true
        ),
        // 📝 FILL_BLANK: Via Appia
        QuestMonument(
            id = "appian_way",
            name = "Via Appia Antica",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/04/Via_Appia_Antica_Rome.jpg/600px-Via_Appia_Antica_Rome.jpg",
            iconEmoji = "🛤️",
            question = "Complete the famous saying about the Roman road system:",
            options = listOf("Rome", "Victory", "Glory", "Caesar"),
            correctIndex = 0,
            funFact = "The Via Appia (Appian Way) was built in 312 BC and called 'Regina Viarum' (Queen of Roads). It's one of the oldest and most important Roman roads, stretching 563 km from Rome to Brindisi. Parts of the original Roman stones are still walkable today — making it over 2,300 years old!",
            xPercent = 0.6f,
            isPro = true,
            questType = QuestType.FILL_BLANK,
            fillBlankSentence = "All roads lead to ___"
        ),
        // ✅❌ TRUE/FALSE: Tiber Island
        QuestMonument(
            id = "tiberina",
            name = "Tiber Island",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/73/Isola_Tiberina_-_Roma.jpg/600px-Isola_Tiberina_-_Roma.jpg",
            iconEmoji = "🏝️",
            question = "TRUE or FALSE: The Pons Fabricius connecting Tiber Island to the mainland is the oldest bridge in Rome, built in 62 BC.",
            options = listOf("TRUE ✅", "FALSE ❌"),
            correctIndex = 0,
            funFact = "It's TRUE! The Pons Fabricius (Ponte Fabricio) was built in 62 BC and is still standing — making it over 2,000 years old! Legend says Tiber Island was formed when Romans threw the tyrant king Tarquinius's wheat harvest into the river, where mud collected around it to form an island.",
            xPercent = 0.64f,
            isPro = true,
            questType = QuestType.TRUE_FALSE
        ),
        // 🧩 EMOJI DECODE: Julius Caesar
        QuestMonument(
            id = "julius_emoji",
            name = "Caesar's Legacy",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5c/Largo_di_Torre_Argentina_din%C3%A0mica.jpg/600px-Largo_di_Torre_Argentina_din%C3%A0mica.jpg",
            iconEmoji = "👑",
            question = "Which famous Roman leader do these emojis represent?\n\n🗡️👑🏛️📜⚔️",
            options = listOf("Julius Caesar", "Augustus", "Nero", "Marcus Aurelius"),
            correctIndex = 0,
            funFact = "Julius Caesar was assassinated on March 15, 44 BC (the Ides of March) by a group of senators at the Theatre of Pompey — which is now the site of Largo di Torre Argentina, where you can also see Rome's famous cat sanctuary! Caesar's last words are debated — 'Et tu, Brute?' may be Shakespeare's invention.",
            xPercent = 0.68f,
            isPro = true,
            questType = QuestType.EMOJI_DECODE,
            emojiClues = "🗡️👑🏛️📜⚔️"
        ),
        // 🏛️ QUIZ: Bocca della Verità
        QuestMonument(
            id = "mouth_truth",
            name = "Bocca della Verità",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2e/Bocca_della_Verit%C3%A0_-_Santa_Maria_in_Cosmedin.jpg/480px-Bocca_della_Verit%C3%A0_-_Santa_Maria_in_Cosmedin.jpg",
            iconEmoji = "👄",
            question = "According to legend, what happens if you put your hand in the Mouth of Truth and tell a lie?",
            options = listOf("You turn to stone", "The mouth bites your hand off", "You hear thunder", "The ground shakes"),
            correctIndex = 1,
            funFact = "The Bocca della Verità (Mouth of Truth) is actually an ancient Roman drain cover, probably depicting the face of the sea god Oceanus. The legend of the lie-detecting mouth became world-famous after the 1953 movie 'Roman Holiday' with Audrey Hepburn and Gregory Peck — tourists queue for hours just to put their hand in!",
            xPercent = 0.72f,
            isPro = true
        )
    )
}

/** Translation system for all quest content */
object QuestTranslations {
    data class QuestText(
        val name: Map<Translations.Lang, String>? = null,
        val question: Map<Translations.Lang, String>,
        val options: Map<Translations.Lang, List<String>>? = null,
        val funFact: Map<Translations.Lang, String>? = null,
        val fillBlank: Map<Translations.Lang, String>? = null
    )

    private val translations = mapOf(
        "colosseum" to QuestText(
            name = mapOf(Translations.Lang.IT to "Colosseo", Translations.Lang.ES to "Coliseo", Translations.Lang.PL to "Koloseum"),
            question = mapOf(
                Translations.Lang.IT to "Quanti spettatori poteva contenere il Colosseo al suo apice?",
                Translations.Lang.ES to "¿Cuántos espectadores podía albergar el Coliseo en su apogeo?",
                Translations.Lang.PL to "Ilu widzów mógł pomieścić Koloseum w szczytowym okresie?"
            ),
            funFact = mapOf(
                Translations.Lang.IT to "Il Colosseo poteva essere allagato per inscenare battaglie navali chiamate \"naumachiae\". Gli ingegneri costruirono un elaborato sistema di canali e acquedotti sotto il pavimento dell'arena!",
                Translations.Lang.ES to "¡El Coliseo podía inundarse para representar batallas navales llamadas \"naumaquias\"! Los ingenieros construyeron un elaborado sistema de canales y acueductos bajo el suelo de la arena.",
                Translations.Lang.PL to "Koloseum mogło być zalewane wodą do inscenizacji bitew morskich zwanych \"naumachiae\". Inżynierowie zbudowali skomplikowany system kanałów i akweduktów pod podłogą areny!"
            )
        ),
        "gladiator_emoji" to QuestText(
            question = mapOf(
                Translations.Lang.IT to "Quale combattente dell'antica Roma rappresentano questi emoji?\n\n⚔️🛡️🏟️💪🗡️",
                Translations.Lang.ES to "¿Qué luchador de la antigua Roma representan estos emojis?\n\n⚔️🛡️🏟️💪🗡️",
                Translations.Lang.PL to "Którego wojownika starożytnego Rzymu reprezentują te emoji?\n\n⚔️🛡️🏟️💪🗡️"
            ),
            options = mapOf(
                Translations.Lang.IT to listOf("Gladiatore", "Centurione", "Legionario", "Senatore"),
                Translations.Lang.ES to listOf("Gladiador", "Centurión", "Legionario", "Senador"),
                Translations.Lang.PL to listOf("Gladiator", "Centurion", "Legionista", "Senator")
            ),
            funFact = mapOf(
                Translations.Lang.IT to "I gladiatori erano spesso celebrità nell'antica Roma! Avevano fan club, e le donne pagavano grandi somme per il loro sudore, che si credeva fosse un afrodisiaco.",
                Translations.Lang.ES to "¡Los gladiadores eran celebridades en la antigua Roma! Tenían clubes de fans, y las mujeres pagaban grandes sumas por su sudor, que se creía que era un afrodisíaco.",
                Translations.Lang.PL to "Gladiatorzy byli często celebrytami w starożytnym Rzymie! Mieli kluby fanów, a kobiety płaciły duże sumy za ich pot, który uważano za afrodyzjak."
            )
        ),
        "pantheon" to QuestText(
            question = mapOf(
                Translations.Lang.IT to "Cosa ha di unico la cupola del Pantheon?",
                Translations.Lang.ES to "¿Qué tiene de único la cúpula del Panteón?",
                Translations.Lang.PL to "Co jest wyjątkowego w kopule Panteonu?"
            ),
            options = mapOf(
                Translations.Lang.IT to listOf("È fatta d'oro", "Non ha pilastri di sostegno", "Ha un foro aperto (oculo) in cima", "Fu costruita in un giorno"),
                Translations.Lang.ES to listOf("Está hecha de oro", "No tiene pilares de soporte", "Tiene un agujero abierto (óculo) en la cima", "Fue construida en un día"),
                Translations.Lang.PL to listOf("Jest zrobiona ze złota", "Nie ma podpierających filarów", "Ma otwarty otwór (okulus) na szczycie", "Została zbudowana w jeden dzień")
            ),
            funFact = mapOf(
                Translations.Lang.IT to "L'oculo del Pantheon è largo 9 metri ed è l'unica fonte di luce. Quando piove, l'acqua cade attraverso il foro ma defluisce attraverso 22 fori quasi invisibili nel pavimento!",
                Translations.Lang.ES to "El óculo del Panteón mide 9 metros de ancho y es la única fuente de luz. Cuando llueve, el agua cae por el agujero pero drena a través de 22 agujeros casi invisibles en el suelo.",
                Translations.Lang.PL to "Okulus Panteonu ma 9 metrów szerokości i jest jedynym źródłem światła. Gdy pada deszcz, woda spada przez otwór, ale odpływa przez 22 prawie niewidoczne otwory w podłodze!"
            )
        ),
        "pantheon_tf" to QuestText(
            question = mapOf(
                Translations.Lang.IT to "VERO o FALSO: La cupola in cemento del Pantheon è ancora la più grande cupola in cemento non armato del mondo dopo 2.000 anni.",
                Translations.Lang.ES to "VERDADERO o FALSO: La cúpula de hormigón del Panteón sigue siendo la cúpula de hormigón no armado más grande del mundo después de 2.000 años.",
                Translations.Lang.PL to "PRAWDA czy FAŁSZ: Betonowa kopuła Panteonu jest nadal największą niezbrojoną kopułą betonową na świecie po 2000 lat."
            ),
            options = mapOf(
                Translations.Lang.IT to listOf("VERO ✅", "FALSO ❌"),
                Translations.Lang.ES to listOf("VERDADERO ✅", "FALSO ❌"),
                Translations.Lang.PL to listOf("PRAWDA ✅", "FAŁSZ ❌")
            ),
            funFact = mapOf(
                Translations.Lang.IT to "È VERO! La cupola del Pantheon (43,3 m di diametro) resta la più grande cupola in cemento non armato del mondo. Il cemento diventa progressivamente più leggero verso l'alto, usando pomice vulcanica vicino all'oculo!",
                Translations.Lang.ES to "¡Es VERDADERO! La cúpula del Panteón (43,3 m de diámetro) sigue siendo la cúpula de hormigón no armado más grande del mundo. El hormigón se vuelve progresivamente más ligero hacia arriba, usando piedra pómez volcánica cerca del óculo.",
                Translations.Lang.PL to "To PRAWDA! Kopuła Panteonu (43,3 m średnicy) pozostaje największą niezbrojoną kopułą betonową na świecie. Beton staje się stopniowo lżejszy ku górze, z użyciem wulkanicznego pumeksu przy okulusie!"
            )
        ),
        "trevi" to QuestText(
            name = mapOf(Translations.Lang.IT to "Fontana di Trevi", Translations.Lang.ES to "Fontana de Trevi", Translations.Lang.PL to "Fontanna di Trevi"),
            question = mapOf(
                Translations.Lang.IT to "Quanti soldi vengono gettati nella Fontana di Trevi ogni anno?",
                Translations.Lang.ES to "¿Cuánto dinero se tira a la Fontana de Trevi cada año?",
                Translations.Lang.PL to "Ile pieniędzy wrzuca się do Fontanny di Trevi każdego roku?"
            ),
            options = mapOf(
                Translations.Lang.IT to listOf("Circa €100.000", "Circa €500.000", "Circa €1,5 milioni", "Circa €5 milioni"),
                Translations.Lang.ES to listOf("Unos €100.000", "Unos €500.000", "Unos €1,5 millones", "Unos €5 millones"),
                Translations.Lang.PL to listOf("Około €100.000", "Około €500.000", "Około €1,5 miliona", "Około €5 milionów")
            ),
            funFact = mapOf(
                Translations.Lang.IT to "Circa €1,5 milioni vengono gettati nella Fontana di Trevi ogni anno! Il denaro viene raccolto ogni notte e donato alla Caritas, un'organizzazione cattolica che finanzia un supermercato per i poveri a Roma.",
                Translations.Lang.ES to "¡Aproximadamente €1,5 millones se tiran a la Fontana de Trevi anualmente! El dinero se recoge cada noche y se dona a Cáritas, una organización católica que financia un supermercado para los pobres en Roma.",
                Translations.Lang.PL to "Około €1,5 miliona jest wrzucane do Fontanny di Trevi każdego roku! Pieniądze są zbierane co noc i przekazywane Caritas, katolickiej organizacji charytatywnej finansującej supermarket dla ubogich w Rzymie."
            )
        ),
        "emoji_landmark" to QuestText(
            question = mapOf(
                Translations.Lang.IT to "Quale monumento romano rappresentano questi emoji?\n\n💧⛲🪙🤞✨",
                Translations.Lang.ES to "¿Qué monumento romano representan estos emojis?\n\n💧⛲🪙🤞✨",
                Translations.Lang.PL to "Który rzymski zabytek reprezentują te emoji?\n\n💧⛲🪙🤞✨"
            ),
            options = mapOf(
                Translations.Lang.IT to listOf("Terme di Caracalla", "Fontana di Trevi", "Fontana di Navona", "Acqua Vergine"),
                Translations.Lang.ES to listOf("Termas de Caracalla", "Fontana de Trevi", "Fuente de Navona", "Aqua Virgo"),
                Translations.Lang.PL to listOf("Termy Karakalli", "Fontanna di Trevi", "Fontanna Navona", "Aqua Virgo")
            ),
            funFact = mapOf(
                Translations.Lang.IT to "La tradizione di gettare monete nella Fontana di Trevi dice: 1 moneta = tornare a Roma, 2 monete = trovare l'amore a Roma, 3 monete = sposarsi a Roma!",
                Translations.Lang.ES to "La tradición de lanzar monedas a la Fontana de Trevi dice: 1 moneda = volver a Roma, 2 monedas = encontrar el amor en Roma, 3 monedas = casarse en Roma.",
                Translations.Lang.PL to "Tradycja wrzucania monet do fontanny mówi: 1 moneta = powrót do Rzymu, 2 monety = znalezienie miłości w Rzymie, 3 monety = ślub w Rzymie!"
            )
        ),
        "vatican" to QuestText(
            name = mapOf(Translations.Lang.IT to "Basilica di San Pietro", Translations.Lang.ES to "Basílica de San Pedro", Translations.Lang.PL to "Bazylika Świętego Piotra"),
            question = mapOf(
                Translations.Lang.IT to "Chi ha progettato la cupola della Basilica di San Pietro?",
                Translations.Lang.ES to "¿Quién diseñó la cúpula de la Basílica de San Pedro?",
                Translations.Lang.PL to "Kto zaprojektował kopułę Bazyliki Świętego Piotra?"
            ),
            options = mapOf(
                Translations.Lang.IT to listOf("Leonardo da Vinci", "Michelangelo", "Raffaello", "Bernini"),
                Translations.Lang.ES to listOf("Leonardo da Vinci", "Miguel Ángel", "Rafael", "Bernini"),
                Translations.Lang.PL to listOf("Leonardo da Vinci", "Michał Anioł", "Rafael", "Bernini")
            ),
            funFact = mapOf(
                Translations.Lang.IT to "Michelangelo progettò la cupola a 71 anni e non la vide mai completata. La cupola è così grande che la Statua della Libertà potrebbe entrarci! Ci vollero 22 anni per costruirla.",
                Translations.Lang.ES to "Miguel Ángel diseñó la cúpula a los 71 años y nunca la vio terminada. ¡La cúpula es tan grande que la Estatua de la Libertad podría caber dentro! Tardó 22 años en construirse.",
                Translations.Lang.PL to "Michał Anioł zaprojektował kopułę w wieku 71 lat i nigdy nie zobaczył jej ukończonej. Kopuła jest tak duża, że Statua Wolności zmieściłaby się w środku! Budowa trwała 22 lata."
            )
        ),
        "spanish_steps" to QuestText(
            name = mapOf(Translations.Lang.IT to "Scalinata di Trinità", Translations.Lang.ES to "Escalinata Española", Translations.Lang.PL to "Schody Hiszpańskie"),
            question = mapOf(
                Translations.Lang.IT to "Quanti gradini compongono la Scalinata di Trinità dei Monti?",
                Translations.Lang.ES to "¿Cuántos escalones tiene la Escalinata Española?",
                Translations.Lang.PL to "Ile stopni ma Schodów Hiszpańskich?"
            ),
            funFact = mapOf(
                Translations.Lang.IT to "I 135 gradini furono costruiti nel 1723-1725 per collegare l'Ambasciata di Spagna alla chiesa di Trinità dei Monti. Mangiare sui gradini è vietato dal 2019 — puoi essere multato fino a €400!",
                Translations.Lang.ES to "Los 135 escalones se construyeron en 1723-1725 para conectar la Embajada de España con la iglesia Trinità dei Monti. ¡Comer en los escalones se prohibió en 2019, con multas de hasta €400!",
                Translations.Lang.PL to "135 stopni zbudowano w latach 1723-1725, aby połączyć Ambasadę Hiszpanii z kościołem Trinità dei Monti. Jedzenie na schodach zostało zakazane w 2019 — możesz dostać mandat do €400!"
            )
        ),
        "bernini_tf" to QuestText(
            question = mapOf(
                Translations.Lang.IT to "VERO o FALSO: Bernini ha progettato sia il colonnato di Piazza San Pietro che la Fontana dei Quattro Fiumi a Piazza Navona.",
                Translations.Lang.ES to "VERDADERO o FALSO: Bernini diseñó tanto la columnata de la Plaza de San Pedro como la Fuente de los Cuatro Ríos en la Piazza Navona.",
                Translations.Lang.PL to "PRAWDA czy FAŁSZ: Bernini zaprojektował zarówno kolumnadę Placu Świętego Piotra, jak i Fontannę Czterech Rzek na Piazza Navona."
            ),
            options = mapOf(
                Translations.Lang.IT to listOf("VERO ✅", "FALSO ❌"),
                Translations.Lang.ES to listOf("VERDADERO ✅", "FALSO ❌"),
                Translations.Lang.PL to listOf("PRAWDA ✅", "FAŁSZ ❌")
            ),
            funFact = mapOf(
                Translations.Lang.IT to "È VERO! Gian Lorenzo Bernini creò oltre 60 capolavori a Roma! Progettò il colonnato di Piazza San Pietro, la Fontana dei Quattro Fiumi e scolpì la famosa 'Estasi di Santa Teresa'. Iniziò a scolpire a 8 anni!",
                Translations.Lang.ES to "¡Es VERDADERO! Gian Lorenzo Bernini creó más de 60 obras maestras en Roma. Diseñó la columnata de la Plaza de San Pedro, la Fuente de los Cuatro Ríos y esculpió el famoso 'Éxtasis de Santa Teresa'. ¡Empezó a esculpir a los 8 años!",
                Translations.Lang.PL to "To PRAWDA! Gian Lorenzo Bernini stworzył ponad 60 arcydzieł w Rzymie! Zaprojektował kolumnadę Placu św. Piotra, Fontannę Czterech Rzek i wyrzeźbił słynną 'Ekstazę św. Teresy'. Zaczął rzeźbić w wieku 8 lat!"
            )
        ),
        "forum" to QuestText(
            name = mapOf(Translations.Lang.IT to "Foro Romano", Translations.Lang.ES to "Foro Romano", Translations.Lang.PL to "Forum Romanum"),
            question = mapOf(
                Translations.Lang.IT to "A cosa serviva principalmente il Foro Romano?",
                Translations.Lang.ES to "¿Para qué se usaba principalmente el Foro Romano?",
                Translations.Lang.PL to "Do czego służyło głównie Forum Romanum?"
            ),
            options = mapOf(
                Translations.Lang.IT to listOf("Campi d'addestramento", "Assemblee, commercio e politica", "Solo cerimonie religiose", "Combattimenti dei gladiatori"),
                Translations.Lang.ES to listOf("Campos de entrenamiento", "Reuniones, comercio y política", "Solo ceremonias religiosas", "Luchas de gladiadores"),
                Translations.Lang.PL to listOf("Place ćwiczeń wojskowych", "Zgromadzenia, handel i polityka", "Tylko ceremonie religijne", "Walki gladiatorów")
            ),
            funFact = mapOf(
                Translations.Lang.IT to "Il Foro Romano fu il cuore pulsante della vita pubblica per secoli. Giulio Cesare fu cremato qui nel 44 a.C., e i romani gettarono così tante offerte nel rogo che dovettero chiamare i vigili del fuoco!",
                Translations.Lang.ES to "El Foro Romano fue el corazón de la vida pública durante siglos. Julio César fue cremado aquí en el 44 a.C., ¡y los romanos lanzaron tantas ofrendas a la pira que tuvieron que llamar a los bomberos!",
                Translations.Lang.PL to "Forum Romanum było bijącym sercem życia publicznego przez wieki. Juliusz Cezar został tu skremowany w 44 r. p.n.e., a Rzymianie wrzucili tyle darów na stos, że trzeba było wezwać straż pożarną!"
            )
        ),
        "castel" to QuestText(
            question = mapOf(
                Translations.Lang.IT to "Come cosa fu originariamente costruito Castel Sant'Angelo?",
                Translations.Lang.ES to "¿Como qué fue construido originalmente el Castillo de Sant'Angelo?",
                Translations.Lang.PL to "Jako co pierwotnie zbudowano Zamek Świętego Anioła?"
            ),
            options = mapOf(
                Translations.Lang.IT to listOf("Una prigione", "Un mausoleo per l'imperatore Adriano", "Una fortezza", "Una chiesa"),
                Translations.Lang.ES to listOf("Una prisión", "Un mausoleo para el emperador Adriano", "Una fortaleza", "Una iglesia"),
                Translations.Lang.PL to listOf("Więzienie", "Mauzoleum cesarza Hadriana", "Forteca", "Kościół")
            ),
            funFact = mapOf(
                Translations.Lang.IT to "Castel Sant'Angelo ha un passaggio segreto chiamato \"Passetto di Borgo\" — un corridoio sopraelevato di 800 metri che lo collega al Vaticano. Papa Clemente VII lo usò per fuggire durante il Sacco di Roma nel 1527!",
                Translations.Lang.ES to "El Castillo de Sant'Angelo tiene un pasaje secreto llamado \"Passetto di Borgo\" — un corredor elevado de 800 metros que lo conecta con el Vaticano. ¡El Papa Clemente VII lo usó para escapar durante el Saqueo de Roma en 1527!",
                Translations.Lang.PL to "Zamek Świętego Anioła ma sekretne przejście zwane \"Passetto di Borgo\" — 800-metrowy podwyższony korytarz łączący go z Watykanem. Papież Klemens VII użył go do ucieczki podczas Złupienia Rzymu w 1527!"
            )
        ),
        "navona" to QuestText(
            question = mapOf(
                Translations.Lang.IT to "Qual era lo scopo originale della forma allungata di Piazza Navona?",
                Translations.Lang.ES to "¿Cuál era el propósito original de la forma alargada de la Piazza Navona?",
                Translations.Lang.PL to "Jaki był pierwotny cel wydłużonego kształtu Piazza Navona?"
            ),
            options = mapOf(
                Translations.Lang.IT to listOf("Era un mercato", "Fu costruita su uno stadio romano", "Fu progettata per le parate", "Era un porto fluviale"),
                Translations.Lang.ES to listOf("Era un mercado", "Fue construida sobre un estadio romano", "Fue diseñada para desfiles", "Era un puerto fluvial"),
                Translations.Lang.PL to listOf("Był to targ", "Została zbudowana na rzymskim stadionie", "Została zaprojektowana na parady", "Był to port rzeczny")
            ),
            funFact = mapOf(
                Translations.Lang.IT to "Piazza Navona fu costruita sulle rovine dello Stadio di Domiziano (I secolo d.C.). Fino al XIX secolo, la piazza veniva regolarmente allagata nei weekend di agosto così che i romani potessero guazzare nell'acqua per sfuggire al caldo estivo!",
                Translations.Lang.ES to "La Piazza Navona fue construida sobre las ruinas del Estadio de Domiciano (siglo I d.C.). ¡Hasta el siglo XIX, la plaza se inundaba regularmente los fines de semana de agosto para que los romanos pudieran chapotear para escapar del calor!",
                Translations.Lang.PL to "Piazza Navona została zbudowana na ruinach Stadionu Domicjana (I w. n.e.). Aż do XIX wieku plac był regularnie zalewany w sierpniowe weekendy, aby Rzymianie mogli brodzić w wodzie i uciec od letniego upału!"
            )
        ),
        "trastevere_emoji" to QuestText(
            question = mapOf(
                Translations.Lang.IT to "Quale affascinante quartiere romano rappresentano questi emoji?\n\n🍷🌙🎶🏘️✨",
                Translations.Lang.ES to "¿Qué encantador barrio romano representan estos emojis?\n\n🍷🌙🎶🏘️✨",
                Translations.Lang.PL to "Którą uroczą dzielnicę Rzymu reprezentują te emoji?\n\n🍷🌙🎶🏘️✨"
            ),
            funFact = mapOf(
                Translations.Lang.IT to "Trastevere significa 'oltre il Tevere'. Nell'antica Roma era casa di immigrati e marinai. Oggi è il quartiere più bohémien di Roma — le sue stradine acciottolate sono ricoperte di edera e piene di musica dal vivo ogni sera!",
                Translations.Lang.ES to "Trastevere significa 'al otro lado del Tíber'. En la Roma antigua era hogar de inmigrantes y marineros. Hoy es el barrio más bohemio de Roma — ¡sus estrechas calles empedradas están cubiertas de hiedra y llenas de música en vivo cada noche!",
                Translations.Lang.PL to "Trastevere oznacza 'za Tybrem'. W starożytnym Rzymie mieszkali tam imigranci i marynarze. Dziś to najbardziej bohemiczna dzielnica Rzymu — wąskie brukowane uliczki porośnięte bluszczem i pełne muzyki na żywo każdego wieczoru!"
            )
        ),
        "circus_maximus" to QuestText(
            question = mapOf(
                Translations.Lang.IT to "Quanti spettatori poteva contenere il Circo Massimo per le corse dei carri?",
                Translations.Lang.ES to "¿Cuántos espectadores podía albergar el Circo Máximo para las carreras de carros?",
                Translations.Lang.PL to "Ilu widzów mógł pomieścić Circus Maximus na wyścigi rydwanów?"
            ),
            funFact = mapOf(
                Translations.Lang.IT to "Il Circo Massimo poteva ospitare 250.000 spettatori — oltre 3 volte il Colosseo! Le corse dei carri erano lo sport più popolare nell'antica Roma. I piloti erano celebrità e i tifosi si organizzavano in fazioni (Blu, Verdi, Rossi, Bianchi) che a volte causavano sommosse!",
                Translations.Lang.ES to "¡El Circo Máximo podía albergar 250.000 espectadores — más de 3 veces el Coliseo! Las carreras de carros eran el deporte más popular. Los conductores eran celebridades y los fans se organizaban en facciones (Azules, Verdes, Rojos, Blancos) que a veces causaban disturbios.",
                Translations.Lang.PL to "Circus Maximus mógł pomieścić 250.000 widzów — ponad 3 razy więcej niż Koloseum! Wyścigi rydwanów były najpopularniejszym sportem w starożytnym Rzymie. Woźnice byli celebrytami, a kibice organizowali się w fakcje (Niebieskich, Zielonych, Czerwonych, Białych), które czasem wywoływały zamieszki!"
            )
        ),
        "catacombs_tf" to QuestText(
            question = mapOf(
                Translations.Lang.IT to "VERO o FALSO: Le catacombe sotterranee di Roma si estendono per oltre 150 chilometri — più della metro di Roma.",
                Translations.Lang.ES to "VERDADERO o FALSO: Las catacumbas subterráneas de Roma se extienden más de 150 kilómetros — más que el metro de Roma.",
                Translations.Lang.PL to "PRAWDA czy FAŁSZ: Podziemne katakumby Rzymu rozciągają się na ponad 150 kilometrów — dalej niż metro w Rzymie."
            ),
            options = mapOf(
                Translations.Lang.IT to listOf("VERO ✅", "FALSO ❌"),
                Translations.Lang.ES to listOf("VERDADERO ✅", "FALSO ❌"),
                Translations.Lang.PL to listOf("PRAWDA ✅", "FAŁSZ ❌")
            ),
            funFact = mapOf(
                Translations.Lang.IT to "È VERO! Le catacombe di Roma sono una città sotterranea dei morti, che si estende per oltre 150 km con circa 750.000 sepolture! Le sole Catacombe di San Callisto hanno 20 km di gallerie su 4 livelli.",
                Translations.Lang.ES to "¡Es VERDADERO! Las catacumbas de Roma son una ciudad subterránea de los muertos, que se extiende más de 150 km con unas 750.000 tumbas. ¡Solo las Catacumbas de San Calixto tienen 20 km de túneles en 4 niveles!",
                Translations.Lang.PL to "To PRAWDA! Katakumby Rzymu to podziemne miasto umarłych, rozciągające się na ponad 150 km z około 750.000 miejsc pochówku! Same Katakumby San Callisto mają 20 km tuneli na 4 poziomach."
            )
        ),
        "borghese" to QuestText(
            question = mapOf(
                Translations.Lang.IT to "Quale scultore ha creato il famoso 'Apollo e Dafne' nella Galleria Borghese?",
                Translations.Lang.ES to "¿Qué escultor creó el famoso 'Apolo y Dafne' en la Galería Borghese?",
                Translations.Lang.PL to "Który rzeźbiarz stworzył słynne 'Apollo i Dafne' w Galerii Borghese?"
            ),
            funFact = mapOf(
                Translations.Lang.IT to "Bernini scolpì 'Apollo e Dafne' quando aveva solo 24 anni! La scultura in marmo cattura l'esatto momento della trasformazione di Dafne in un albero di alloro — le dita che diventano foglie e la corteccia che cresce sulle gambe.",
                Translations.Lang.ES to "¡Bernini esculpió 'Apolo y Dafne' cuando tenía solo 24 años! La escultura de mármol captura el momento exacto de la transformación de Dafne en un laurel — sus dedos convirtiéndose en hojas y corteza creciendo por sus piernas.",
                Translations.Lang.PL to "Bernini wyrzeźbił 'Apollo i Dafne' mając zaledwie 24 lata! Marmurowa rzeźba uchwytuje dokładny moment przemiany Dafne w drzewo laurowe — palce zamieniające się w liście i kora rosnąca na nogach."
            )
        ),
        "appian_way" to QuestText(
            name = mapOf(Translations.Lang.IT to "Via Appia Antica", Translations.Lang.ES to "Vía Apia Antigua", Translations.Lang.PL to "Via Appia Antica"),
            question = mapOf(
                Translations.Lang.IT to "Completa il famoso detto sul sistema stradale romano:",
                Translations.Lang.ES to "Completa el famoso dicho sobre el sistema de carreteras romano:",
                Translations.Lang.PL to "Uzupełnij słynne powiedzenie o rzymskim systemie drogowym:"
            ),
            options = mapOf(
                Translations.Lang.IT to listOf("Roma", "Vittoria", "Gloria", "Cesare"),
                Translations.Lang.ES to listOf("Roma", "Victoria", "Gloria", "César"),
                Translations.Lang.PL to listOf("Rzymu", "Zwycięstwa", "Chwały", "Cezara")
            ),
            fillBlank = mapOf(
                Translations.Lang.IT to "Tutte le strade portano a ___",
                Translations.Lang.ES to "Todos los caminos llevan a ___",
                Translations.Lang.PL to "Wszystkie drogi prowadzą do ___"
            ),
            funFact = mapOf(
                Translations.Lang.IT to "La Via Appia fu costruita nel 312 a.C. e chiamata 'Regina Viarum' (Regina delle Strade). Si estende per 563 km da Roma a Brindisi. Parti delle pietre originali sono ancora calpestabili oggi — rendendola vecchia di oltre 2.300 anni!",
                Translations.Lang.ES to "La Vía Apia fue construida en 312 a.C. y llamada 'Regina Viarum' (Reina de los Caminos). Se extiende 563 km de Roma a Brindisi. ¡Partes de las piedras originales aún son transitables hoy, con más de 2.300 años!",
                Translations.Lang.PL to "Via Appia została zbudowana w 312 r. p.n.e. i nazywana 'Regina Viarum' (Królowa Dróg). Rozciąga się na 563 km z Rzymu do Brindisi. Części oryginalnych kamieni można nadal chodzić — mają ponad 2300 lat!"
            )
        ),
        "tiberina" to QuestText(
            name = mapOf(Translations.Lang.IT to "Isola Tiberina", Translations.Lang.ES to "Isla Tiberina", Translations.Lang.PL to "Wyspa Tyberyjska"),
            question = mapOf(
                Translations.Lang.IT to "VERO o FALSO: Il Pons Fabricius che collega l'Isola Tiberina alla terraferma è il ponte più antico di Roma, costruito nel 62 a.C.",
                Translations.Lang.ES to "VERDADERO o FALSO: El Pons Fabricius que conecta la Isla Tiberina con el continente es el puente más antiguo de Roma, construido en 62 a.C.",
                Translations.Lang.PL to "PRAWDA czy FAŁSZ: Pons Fabricius łączący Wyspę Tyberyjską ze stałym lądem jest najstarszym mostem w Rzymie, zbudowanym w 62 r. p.n.e."
            ),
            options = mapOf(
                Translations.Lang.IT to listOf("VERO ✅", "FALSO ❌"),
                Translations.Lang.ES to listOf("VERDADERO ✅", "FALSO ❌"),
                Translations.Lang.PL to listOf("PRAWDA ✅", "FAŁSZ ❌")
            ),
            funFact = mapOf(
                Translations.Lang.IT to "È VERO! Il Pons Fabricius fu costruito nel 62 a.C. ed è ancora in piedi — rendendolo vecchio di oltre 2.000 anni! La leggenda dice che l'Isola Tiberina si formò quando i romani gettarono il raccolto di grano del tiranno Tarquinio nel fiume.",
                Translations.Lang.ES to "¡Es VERDADERO! El Pons Fabricius fue construido en 62 a.C. y sigue en pie — ¡tiene más de 2.000 años! La leyenda dice que la Isla Tiberina se formó cuando los romanos arrojaron la cosecha de trigo del tirano Tarquinio al río.",
                Translations.Lang.PL to "To PRAWDA! Pons Fabricius został zbudowany w 62 r. p.n.e. i nadal stoi — ma ponad 2000 lat! Legenda głosi, że Wyspa Tyberyjska powstała, gdy Rzymianie wrzucili zbiory pszenicy tyrana Tarkwiniusza do rzeki."
            )
        ),
        "julius_emoji" to QuestText(
            question = mapOf(
                Translations.Lang.IT to "Quale famoso leader romano rappresentano questi emoji?\n\n🗡️👑🏛️📜⚔️",
                Translations.Lang.ES to "¿Qué famoso líder romano representan estos emojis?\n\n🗡️👑🏛️📜⚔️",
                Translations.Lang.PL to "Którego słynnego przywódcę rzymskiego reprezentują te emoji?\n\n🗡️👑🏛️📜⚔️"
            ),
            options = mapOf(
                Translations.Lang.IT to listOf("Giulio Cesare", "Augusto", "Nerone", "Marco Aurelio"),
                Translations.Lang.ES to listOf("Julio César", "Augusto", "Nerón", "Marco Aurelio"),
                Translations.Lang.PL to listOf("Juliusz Cezar", "August", "Neron", "Marek Aureliusz")
            ),
            funFact = mapOf(
                Translations.Lang.IT to "Giulio Cesare fu assassinato il 15 marzo 44 a.C. (le Idi di Marzo) da un gruppo di senatori al Teatro di Pompeo — che ora è il sito di Largo di Torre Argentina, dove si trova anche il famoso santuario dei gatti di Roma!",
                Translations.Lang.ES to "Julio César fue asesinado el 15 de marzo de 44 a.C. (los Idus de Marzo) por un grupo de senadores en el Teatro de Pompeyo — que ahora es el sitio de Largo di Torre Argentina, ¡donde también está el famoso santuario de gatos de Roma!",
                Translations.Lang.PL to "Juliusz Cezar został zamordowany 15 marca 44 r. p.n.e. (Idy Marcowe) przez grupę senatorów w Teatrze Pompejusza — który jest teraz Largo di Torre Argentina, gdzie znajduje się też słynne sanktuarium kotów Rzymu!"
            )
        ),
        "mouth_truth" to QuestText(
            name = mapOf(Translations.Lang.IT to "Bocca della Verità", Translations.Lang.ES to "Boca de la Verdad", Translations.Lang.PL to "Usta Prawdy"),
            question = mapOf(
                Translations.Lang.IT to "Secondo la leggenda, cosa succede se metti la mano nella Bocca della Verità e dici una bugia?",
                Translations.Lang.ES to "Según la leyenda, ¿qué pasa si pones la mano en la Boca de la Verdad y dices una mentira?",
                Translations.Lang.PL to "Według legendy, co się stanie, jeśli włożysz rękę w Usta Prawdy i powiesz kłamstwo?"
            ),
            options = mapOf(
                Translations.Lang.IT to listOf("Ti trasformi in pietra", "La bocca ti morde la mano", "Senti un tuono", "La terra trema"),
                Translations.Lang.ES to listOf("Te conviertes en piedra", "La boca te muerde la mano", "Escuchas un trueno", "La tierra tiembla"),
                Translations.Lang.PL to listOf("Zamieniasz się w kamień", "Usta odgryzają ci rękę", "Słyszysz grzmot", "Ziemia się trzęsie")
            ),
            funFact = mapOf(
                Translations.Lang.IT to "La Bocca della Verità è in realtà un antico coperchio di fogna romano, probabilmente raffigurante il dio del mare Oceano. La leggenda divenne famosa dopo il film 'Vacanze Romane' del 1953 con Audrey Hepburn e Gregory Peck — i turisti fanno ore di coda solo per metterci la mano!",
                Translations.Lang.ES to "La Boca de la Verdad es en realidad una antigua tapa de alcantarilla romana, probablemente representando al dios del mar Océano. La leyenda se hizo mundialmente famosa después de la película 'Vacaciones en Roma' de 1953 con Audrey Hepburn y Gregory Peck.",
                Translations.Lang.PL to "Usta Prawdy to tak naprawdę starożytna rzymska pokrywa ściekowa, prawdopodobnie przedstawiająca boga morza Okeanosa. Legenda stała się sławna po filmie 'Rzymskie Wakacje' z 1953 roku z Audrey Hepburn i Gregorym Peckiem — turyści stoją godzinami w kolejce, żeby włożyć rękę!"
            )
        )
    )

    fun getQuestion(questId: String, original: String): String {
        val lang = Translations.currentLang
        if (lang == Translations.Lang.EN) return original
        return translations[questId]?.question?.get(lang) ?: original
    }

    fun getOptions(questId: String, original: List<String>): List<String> {
        val lang = Translations.currentLang
        if (lang == Translations.Lang.EN) return original
        return translations[questId]?.options?.get(lang) ?: original
    }

    fun getFunFact(questId: String, original: String): String {
        val lang = Translations.currentLang
        if (lang == Translations.Lang.EN) return original
        return translations[questId]?.funFact?.get(lang) ?: original
    }

    fun getFillBlank(questId: String, original: String?): String? {
        if (original == null) return null
        val lang = Translations.currentLang
        if (lang == Translations.Lang.EN) return original
        return translations[questId]?.fillBlank?.get(lang) ?: original
    }

    fun getName(questId: String, original: String): String {
        val lang = Translations.currentLang
        if (lang == Translations.Lang.EN) return original
        return translations[questId]?.name?.get(lang) ?: original
    }
}

// --- APP DATA ---

object PassportData {
    val stamps = mutableStateListOf<PassportStamp>()
    var earnedSwipes: Int = 0

    fun addStamp(stamp: PassportStamp, context: Context) {
        stamps.add(0, stamp)
        earnedSwipes += 1
        LearningManager.addBonusSwipes(context, 1)
    }
}

object LearningData {
    data class Phrase(val original: String, val translated: String, val pronunciation: String?, val category: String)

    val learnedPhrases = mutableStateListOf<Phrase>()

    val allPhrases = listOf(
        Phrase("Hello / Bye", "Ciao", "chow", "Basics"),
        Phrase("Good morning", "Buongiorno", "bwon-jor-no", "Basics"),
        Phrase("Good evening", "Buonasera", "bwo-na-se-ra", "Basics"),
        Phrase("Good night", "Buonanotte", "bwo-na-not-te", "Basics"),
        Phrase("See you later", "Ci vediamo", "chee ve-dya-mo", "Basics"),
        Phrase("Have a nice day", "Buona giornata", "bwo-na jor-na-ta", "Basics"),
        Phrase("Please", "Per favore", "pear fa-vo-reh", "Basics"),
        Phrase("Thank you", "Grazie", "grat-see-eh", "Basics"),
        Phrase("Thank you very much", "Grazie mille", "grat-see-eh meel-leh", "Basics"),
        Phrase("You're welcome", "Prego", "preh-go", "Basics"),
        Phrase("Yes", "Sì", "see", "Basics"),
        Phrase("No", "No", "noh", "Basics"),
        Phrase("Excuse me (attention)", "Scusa", "skoo-za", "Basics"),
        Phrase("Excuse me (passing)", "Permesso", "per-mes-so", "Basics"),
        Phrase("I'm sorry", "Mi dispiace", "mee dee-spya-che", "Basics"),
        Phrase("I don't understand", "Non capisco", "non ka-pee-sko", "Basics"),
        Phrase("I don't speak Italian", "Non parlo italiano", "non par-lo ee-tal-ya-no", "Basics"),
        Phrase("Do you speak English?", "Parla inglese?", "par-la een-gle-ze", "Basics"),
        Phrase("Help!", "Aiuto!", "ah-yoo-toh", "Basics"),
        Phrase("OK / Good", "Va bene", "va beh-neh", "Basics"),
        Phrase("Of course", "Certo", "cher-to", "Basics"),
        Phrase("What?", "Cosa?", "ko-za", "Basics"),
        Phrase("Who?", "Chi?", "kee", "Basics"),
        Phrase("Where?", "Dove?", "doh-veh", "Basics"),
        Phrase("When?", "Quando?", "kwan-do", "Basics"),
        Phrase("Why?", "Perché?", "per-keh", "Basics"),
        Phrase("How?", "Come?", "ko-meh", "Basics"),
        Phrase("Where is...?", "Dov'è...?", "doh-veh", "Directions"),
        Phrase("Where are we?", "Dove siamo?", "doh-veh sya-mo", "Directions"),
        Phrase("I'm lost", "Mi sono perso", "mee so-no per-so", "Directions"),
        Phrase("The bathroom", "Il bagno", "eel ban-yo", "Directions"),
        Phrase("The station", "La stazione", "la sta-tsyo-ne", "Directions"),
        Phrase("The airport", "L'aeroporto", "l-a-ero-por-to", "Directions"),
        Phrase("The center", "Il centro", "eel chen-tro", "Directions"),
        Phrase("Entrance", "Entrata", "en-tra-ta", "Directions"),
        Phrase("Exit", "Uscita", "oo-she-ta", "Directions"),
        Phrase("Left", "Sinistra", "see-nee-stra", "Directions"),
        Phrase("Right", "Destra", "de-stra", "Directions"),
        Phrase("Straight ahead", "Dritto", "dreet-to", "Directions"),
        Phrase("Turn", "Gira", "jee-ra", "Directions"),
        Phrase("Stop here", "Fermati qui", "fer-ma-tee kwee", "Directions"),
        Phrase("Map", "Mappa", "map-pa", "Directions"),
        Phrase("Ticket", "Biglietto", "beel-yet-to", "Directions"),
        Phrase("Bus", "Autobus", "ow-to-boos", "Directions"),
        Phrase("Train", "Treno", "treh-no", "Directions"),
        Phrase("Metro", "Metropolitana", "meh-tro-po-lee-ta-na", "Directions"),
        Phrase("Taxi", "Tassì", "tas-see", "Directions"),
        Phrase("One ticket please", "Un biglietto per favore", "oon beel-yet-to", "Directions"),
        Phrase("Is it far?", "È lontano?", "eh lon-ta-no", "Directions"),
        Phrase("Is it near?", "È vicino?", "eh vee-chee-no", "Directions"),
        Phrase("I am hungry", "Ho fame", "oh fa-meh", "Food"),
        Phrase("I am thirsty", "Ho sete", "oh seh-teh", "Food"),
        Phrase("The menu, please", "Il menù, per favore", "eel meh-noo", "Food"),
        Phrase("The bill, please", "Il conto, per favore", "eel con-toh", "Food"),
        Phrase("Water", "Acqua", "ak-wa", "Food"),
        Phrase("Sparkling water", "Acqua frizzante", "ak-wa free-zan-te", "Food"),
        Phrase("Still water", "Acqua naturale", "ak-wa na-too-ra-le", "Food"),
        Phrase("Wine", "Vino", "vee-no", "Food"),
        Phrase("Red / White", "Rosso / Bianco", "ros-so / byan-ko", "Food"),
        Phrase("Beer", "Birra", "beer-ra", "Food"),
        Phrase("Coffee", "Caffè", "kaf-feh", "Food"),
        Phrase("With milk", "Con latte", "kon lat-te", "Food"),
        Phrase("Without sugar", "Senza zucchero", "sen-za zoo-kero", "Food"),
        Phrase("Breakfast", "Colazione", "ko-la-tsyo-ne", "Food"),
        Phrase("Lunch", "Pranzo", "pran-zo", "Food"),
        Phrase("Dinner", "Cena", "che-na", "Food"),
        Phrase("A table for two", "Un tavolo per due", "oon ta-vo-lo per doo-eh", "Food"),
        Phrase("It is delicious", "È delizioso", "eh de-lee-zyo-zo", "Food"),
        Phrase("I am vegetarian", "Sono vegetariano", "so-no ve-je-ta-rya-no", "Food"),
        Phrase("I have allergies", "Ho delle allergie", "oh del-leh al-ler-jee-eh", "Food"),
        Phrase("Gluten free", "Senza glutine", "sen-za gloo-tee-ne", "Food"),
        Phrase("Spicy", "Piccante", "peek-kan-te", "Food"),
        Phrase("Bread", "Pane", "pa-neh", "Food"),
        Phrase("Cheese", "Formaggio", "for-mad-jo", "Food"),
        Phrase("Meat", "Carne", "kar-neh", "Food"),
        Phrase("Fish", "Pesce", "peh-she", "Food"),
        Phrase("Chicken", "Pollo", "pol-lo", "Food"),
        Phrase("Vegetables", "Verdure", "ver-doo-reh", "Food"),
        Phrase("Fruit", "Frutta", "froot-ta", "Food"),
        Phrase("Dessert", "Dolce", "dol-che", "Food"),
        Phrase("Ice cream", "Gelato", "je-la-to", "Food"),
        Phrase("Aperitivo time!", "Ora dell'aperitivo", "ora del a-pe-ri-tee-vo", "Food"),
        Phrase("One Spritz please", "Uno Spritz per favore", "oo-no spritz", "Food"),
        Phrase("Cheers!", "Cin cin!", "chin chin", "Food"),
        Phrase("Bon appetit", "Buon appetito", "bwon ap-peh-tee-to", "Food"),
        Phrase("How much is it?", "Quanto costa?", "kwan-toh cos-ta", "Shopping"),
        Phrase("Can I pay by card?", "Posso pagare con carta?", "pos-so pa-ga-re con car-ta", "Shopping"),
        Phrase("Cash only", "Solo contanti", "so-lo con-tan-tee", "Shopping"),
        Phrase("Too expensive", "Troppo caro", "trop-po ka-ro", "Shopping"),
        Phrase("Discount", "Sconto", "skon-to", "Shopping"),
        Phrase("I'll take it", "Lo prendo", "lo pren-do", "Shopping"),
        Phrase("I'm just looking", "Sto solo guardando", "sto so-lo gwar-dan-do", "Shopping"),
        Phrase("Open", "Aperto", "ah-pear-toh", "Shopping"),
        Phrase("Closed", "Chiuso", "kyoo-zoh", "Shopping"),
        Phrase("Size", "Taglia", "tal-ya", "Shopping"),
        Phrase("Bag", "Busta", "boo-sta", "Shopping"),
        Phrase("Receipt", "Scontrino", "skon-tree-no", "Shopping"),
        Phrase("Zero", "Zero", "dze-ro", "Numbers"),
        Phrase("One", "Uno", "oo-no", "Numbers"),
        Phrase("Two", "Due", "doo-eh", "Numbers"),
        Phrase("Three", "Tre", "treh", "Numbers"),
        Phrase("Four", "Quattro", "kwat-tro", "Numbers"),
        Phrase("Five", "Cinque", "cheen-kweh", "Numbers"),
        Phrase("Six", "Sei", "say", "Numbers"),
        Phrase("Seven", "Sette", "set-te", "Numbers"),
        Phrase("Eight", "Otto", "ot-to", "Numbers"),
        Phrase("Nine", "Nove", "no-ve", "Numbers"),
        Phrase("Ten", "Dieci", "dye-chee", "Numbers"),
        Phrase("Twenty", "Venti", "ven-tee", "Numbers"),
        Phrase("Fifty", "Cinquanta", "cheen-kwan-ta", "Numbers"),
        Phrase("One hundred", "Cento", "chen-to", "Numbers"),
        Phrase("Thousand", "Mille", "meel-leh", "Numbers"),
        Phrase("What time is it?", "Che ore sono?", "keh o-re so-no", "Time"),
        Phrase("Now", "Adesso", "ah-des-so", "Time"),
        Phrase("Later", "Dopo", "doh-po", "Time"),
        Phrase("Today", "Oggi", "od-jee", "Time"),
        Phrase("Tomorrow", "Domani", "do-ma-nee", "Time"),
        Phrase("Yesterday", "Ieri", "yeh-ree", "Time"),
        Phrase("Morning", "Mattina", "mat-tee-na", "Time"),
        Phrase("Afternoon", "Pomeriggio", "po-meh-reed-jo", "Time"),
        Phrase("Night", "Notte", "not-te", "Time"),
        Phrase("Monday", "Lunedì", "loo-ne-dee", "Time"),
        Phrase("Tuesday", "Martedì", "mar-te-dee", "Time"),
        Phrase("Wednesday", "Mercoledì", "mer-ko-le-dee", "Time"),
        Phrase("Thursday", "Giovedì", "jo-ve-dee", "Time"),
        Phrase("Friday", "Venerdì", "ve-ner-dee", "Time"),
        Phrase("Saturday", "Sabato", "sa-ba-to", "Time"),
        Phrase("Sunday", "Domenica", "do-me-nee-ka", "Time"),
        Phrase("I have a reservation", "Ho una prenotazione", "oh oo-na preh-no-ta-tsyo-ne", "Hotel"),
        Phrase("Passport", "Passaporto", "pas-sa-por-to", "Hotel"),
        Phrase("Room", "Camera", "ka-me-ra", "Hotel"),
        Phrase("Key", "Chiave", "kya-ve", "Hotel"),
        Phrase("Wifi password", "Password del wifi", "pas-sword del wi-fi", "Hotel"),
        Phrase("Towels", "Asciugamani", "a-shoo-ga-ma-nee", "Hotel"),
        Phrase("Air conditioning", "Aria condizionata", "a-rya con-dee-tsyo-na-ta", "Hotel"),
        Phrase("Elevator", "Ascensore", "a-shen-so-re", "Hotel"),
        Phrase("Luggage", "Bagagli", "ba-gal-yee", "Hotel"),
        Phrase("Check out", "Check out", "check out", "Hotel"),
        Phrase("I feel sick", "Mi sento male", "mee sen-to ma-le", "Health"),
        Phrase("Doctor", "Medico", "meh-dee-co", "Health"),
        Phrase("Pharmacy", "Farmacia", "far-ma-chee-a", "Health"),
        Phrase("Hospital", "Ospedale", "os-peh-da-le", "Health"),
        Phrase("Police", "Polizia", "po-lee-tsee-a", "Health"),
        Phrase("Ambulance", "Ambulanza", "am-boo-lan-tsa", "Health"),
        Phrase("Headache", "Mal di testa", "mal dee tes-ta", "Health"),
        Phrase("Stomach ache", "Mal di pancia", "mal dee pan-cha", "Health"),
        Phrase("Fever", "Febbre", "feb-bre", "Health"),
        Phrase("Medicine", "Medicina", "meh-dee-chee-na", "Health"),
        Phrase("My name is...", "Mi chiamo...", "mee kya-mo", "Social"),
        Phrase("Nice to meet you", "Piacere", "pya-che-re", "Social"),
        Phrase("How are you?", "Come stai?", "ko-meh sty", "Social"),
        Phrase("I'm fine", "Sto bene", "sto beh-neh", "Social"),
        Phrase("And you?", "E tu?", "eh too", "Social"),
        Phrase("Where are you from?", "Di dove sei?", "dee do-ve say", "Social"),
        Phrase("I am a tourist", "Sono un turista", "so-no oon too-ree-sta", "Social"),
        Phrase("I love Rome", "Amo Roma", "ah-mo ro-ma", "Social"),
        Phrase("It is beautiful", "È bellissimo", "eh bel-lees-see-mo", "Social"),
        Phrase("You are beautiful", "Sei bellissimo/a", "say bel-lees-see-mo", "Social"),
        Phrase("Can I have your number?", "Posso avere il tuo numero?", "pos-so a-ve-re...", "Social"),
        Phrase("Let's go!", "Andiamo!", "an-dya-mo", "Social"),
        Phrase("Really?", "Davvero?", "dav-ve-ro", "Social"),
        Phrase("Maybe", "Forse", "for-seh", "Social"),
        Phrase("I don't know", "Non lo so", "non lo so", "Social"),
        Phrase("Good luck", "Buona fortuna", "bwo-na for-too-na", "Social"),
        Phrase("See you soon", "A presto", "ah pres-to", "Social"),
        Phrase("I love you", "Ti amo", "tee ah-mo", "Social"),
        Phrase("Sun", "Sole", "so-le", "Nature"),
        Phrase("Moon", "Luna", "loo-na", "Nature"),
        Phrase("Sea", "Mare", "ma-re", "Nature"),
        Phrase("Mountain", "Montagna", "mon-ta-nya", "Nature"),
        Phrase("City", "Città", "cheet-ta", "Nature"),
        Phrase("Street", "Strada", "stra-da", "Nature"),
        Phrase("House", "Casa", "ka-za", "Objects"),
        Phrase("Book", "Libro", "lee-bro", "Objects"),
        Phrase("Phone", "Telefono", "te-le-fo-no", "Objects"),
        Phrase("Money", "Soldi", "sol-dee", "Objects"),
        Phrase("Work", "Lavoro", "la-vo-ro", "Social"),
        Phrase("Friend", "Amico", "a-mee-ko", "Social"),
        Phrase("Family", "Famiglia", "fa-mee-lya", "Social"),
        Phrase("Child", "Bambino", "bam-bee-no", "Social"),
        Phrase("Man", "Uomo", "wo-mo", "Social"),
        Phrase("Woman", "Donna", "don-na", "Social"),
        Phrase("Boy", "Ragazzo", "ra-gat-tso", "Social"),
        Phrase("Girl", "Ragazza", "ra-gat-tsa", "Social"),
        Phrase("School", "Scuola", "skwo-la", "Social"),
        Phrase("Church", "Chiesa", "kye-za", "Social"),
        Phrase("Square", "Piazza", "pyat-tsa", "Social"),
        Phrase("Bridge", "Ponte", "pon-te", "Social"),
        Phrase("Flower", "Fiore", "fyo-re", "Nature"),
        Phrase("Tree", "Albero", "al-be-ro", "Nature"),
        Phrase("Dog", "Cane", "ka-ne", "Animals"),
        Phrase("Cat", "Gatto", "gat-to", "Animals"),
        Phrase("Bird", "Uccello", "oot-chel-lo", "Animals"),
        Phrase("Fish", "Pesce", "pe-she", "Animals"),
        Phrase("Red", "Rosso", "ros-so", "Colors"),
        Phrase("Blue", "Blu", "bloo", "Colors"),
        Phrase("Green", "Verde", "ver-de", "Colors"),
        Phrase("Yellow", "Giallo", "jal-lo", "Colors"),
        Phrase("Black", "Nero", "ne-ro", "Colors"),
        Phrase("White", "Bianco", "byan-ko", "Colors"),
        Phrase("Hot", "Caldo", "kal-do", "Weather"),
        Phrase("Cold", "Freddo", "fred-do", "Weather"),
        Phrase("Rain", "Pioggia", "pyod-ja", "Weather"),
        Phrase("Snow", "Neve", "ne-ve", "Weather"),
        Phrase("Wind", "Vento", "ven-to", "Weather"),
        Phrase("Fast", "Veloce", "ve-lo-che", "Adjectives"),
        Phrase("Slow", "Lento", "len-to", "Adjectives"),
        Phrase("Big", "Grande", "gran-de", "Adjectives"),
        Phrase("Small", "Piccolo", "peek-ko-lo", "Adjectives"),
        Phrase("New", "Nuovo", "nwo-vo", "Adjectives"),
        Phrase("Old", "Vecchio", "vek-kyo", "Adjectives"),
        Phrase("Beautiful", "Bello", "bel-lo", "Adjectives"),
        Phrase("Ugly", "Brutto", "broot-to", "Adjectives"),
        Phrase("Rich", "Ricco", "reek-ko", "Adjectives"),
        Phrase("Poor", "Povero", "po-ve-ro", "Adjectives"),
        Phrase("Strong", "Forte", "for-te", "Adjectives"),
        Phrase("Weak", "Debole", "de-bo-le", "Adjectives"),
        Phrase("Happy", "Felice", "fe-lee-che", "Adjectives"),
        Phrase("Sad", "Triste", "tree-ste", "Adjectives"),
        Phrase("Angry", "Arrabbiato", "ar-rab-bya-to", "Adjectives"),
        Phrase("Tired", "Stanco", "stan-ko", "Adjectives"),
        Phrase("Bored", "Annoiato", "an-noy-ya-to", "Adjectives"),
        Phrase("Surprised", "Sorpreso", "sor-pre-zo", "Adjectives"),
        Phrase("Scared", "Spaventato", "spa-ven-ta-to", "Adjectives"),
        Phrase("True", "Vero", "ve-ro", "Adjectives"),
        Phrase("False", "Falso", "fal-so", "Adjectives"),
        Phrase("Easy", "Facile", "fa-chee-le", "Adjectives"),
        Phrase("Difficult", "Difficile", "deef-fee-chee-le", "Adjectives"),
        Phrase("Important", "Importante", "eem-por-tan-te", "Adjectives"),
        Phrase("Interesting", "Interessante", "een-te-res-san-te", "Adjectives"),
        Phrase("Boring", "Noioso", "noy-yo-zo", "Adjectives"),
        Phrase("Funny", "Divertente", "dee-ver-ten-te", "Adjectives"),
        Phrase("Serious", "Serio", "se-ryo", "Adjectives"),
        Phrase("Quiet", "Silenzioso", "see-len-tsyo-zo", "Adjectives"),
        Phrase("Loud", "Rumoroso", "roo-mo-ro-zo", "Adjectives"),
        Phrase("Clean", "Pulito", "poo-lee-to", "Adjectives"),
        Phrase("Dirty", "Sporco", "spor-ko", "Adjectives"),
        Phrase("Open", "Aperto", "a-per-toh", "Adjectives"),
        Phrase("Closed", "Chiuso", "kyoo-zoh", "Adjectives"),
        Phrase("Full", "Pieno", "pye-no", "Adjectives"),
        Phrase("Empty", "Vuoto", "vwo-to", "Adjectives"),
        Phrase("Light", "Luce", "loo-che", "Objects"),
        Phrase("Dark", "Buio", "buy-yo", "Weather"),
        Phrase("Early", "Presto", "pre-sto", "Time"),
        Phrase("Late", "Tardi", "tar-dee", "Time"),
        Phrase("Always", "Sempre", "sem-pre", "Time"),
        Phrase("Never", "Mai", "my", "Time"),
        Phrase("Sometimes", "A volte", "a vol-te", "Time"),
        Phrase("Often", "Spesso", "spes-so", "Time"),
        Phrase("Rarely", "Raramente", "ra-ra-men-te", "Time"),
        Phrase("Already", "Già", "ja", "Time"),
        Phrase("Not yet", "Non ancora", "non an-ko-ra", "Time"),
        Phrase("Wait", "Aspetta", "a-spet-ta", "Verbs"),
        Phrase("Listen", "Ascolta", "a-skol-ta", "Verbs"),
        Phrase("Look", "Guarda", "gwar-da", "Verbs"),
        Phrase("Speak", "Parla", "par-la", "Verbs"),
        Phrase("Read", "Leggi", "led-jee", "Verbs"),
        Phrase("Write", "Scrivi", "skree-vee", "Verbs"),
        Phrase("Eat", "Mangia", "man-ja", "Verbs"),
        Phrase("Drink", "Bevi", "be-vee", "Verbs"),
        Phrase("Sleep", "Dormi", "dor-mee", "Verbs"),
        Phrase("Go", "Vai", "vy", "Verbs"),
        Phrase("Come", "Vieni", "vye-nee", "Verbs"),
        Phrase("Buy", "Compra", "kom-pra", "Verbs"),
        Phrase("Sell", "Vendi", "ven-dee", "Verbs"),
        Phrase("Pay", "Paga", "pa-ga", "Verbs"),
        Phrase("Give", "Dai", "dy", "Verbs"),
        Phrase("Take", "Prendi", "pren-dee", "Verbs"),
        Phrase("Help", "Aiuta", "a-yu-ta", "Verbs"),
        Phrase("Understand", "Capisci", "ka-pee-shee", "Verbs"),
        Phrase("Know", "Sai", "sy", "Verbs"),
        Phrase("Think", "Pensi", "pen-see", "Verbs"),
        Phrase("Believe", "Credi", "kre-dee", "Verbs"),
        Phrase("Love", "Ami", "a-mee", "Verbs"),
        Phrase("Like", "Piace", "pya-che", "Verbs"),
        Phrase("Hate", "Odio", "o-dyo", "Verbs"),
        Phrase("Want", "Vuoi", "vwoy", "Verbs"),
        Phrase("Need", "Serve", "ser-ve", "Verbs"),
        Phrase("Can", "Puoi", "pwoy", "Verbs"),
        Phrase("Should", "Dovresti", "do-vres-tee", "Verbs"),
        Phrase("Will", "Farai", "fa-ra-ee", "Verbs"),
        Phrase("North", "Nord", "nord", "Directions"),
        Phrase("South", "Sud", "sood", "Directions"),
        Phrase("East", "Est", "est", "Directions"),
        Phrase("West", "Ovest", "o-vest", "Directions"),
        Phrase("Up", "Su", "soo", "Directions"),
        Phrase("Down", "Giù", "joo", "Directions"),
        Phrase("Left", "Sinistra", "see-nee-stra", "Directions"),
        Phrase("Right", "Destra", "de-stra", "Directions"),
        Phrase("Front", "Davanti", "da-van-tee", "Directions"),
        Phrase("Back", "Dietro", "dye-tro", "Directions"),
        Phrase("Inside", "Dentro", "den-tro", "Directions"),
        Phrase("Outside", "Fuori", "fwo-ree", "Directions"),
        Phrase("Near", "Vicino", "vee-chee-no", "Directions"),
        Phrase("Far", "Lontano", "lon-ta-no", "Directions"),
        Phrase("Between", "Tra", "tra", "Directions"),
        Phrase("Under", "Sotto", "sot-to", "Directions"),
        Phrase("Over", "Sopra", "so-pra", "Directions"),
        Phrase("Table", "Tavolo", "ta-vo-lo", "Furniture"),
        Phrase("Chair", "Sedia", "se-dya", "Furniture"),
        Phrase("Bed", "Letto", "let-to", "Furniture"),
        Phrase("Door", "Porta", "por-ta", "Objects"),
        Phrase("Window", "Finestra", "fee-nes-tra", "Objects"),
        Phrase("Wall", "Muro", "moo-ro", "Objects"),
        Phrase("Floor", "Pavimento", "pa-vee-men-to", "Objects"),
        Phrase("Kitchen", "Cucina", "koo-chee-na", "Rooms"),
        Phrase("Bathroom", "Bagno", "ban-yo", "Rooms"),
        Phrase("Bedroom", "Camera", "ka-me-ra", "Rooms"),
        Phrase("Computer", "Computer", "kom-pyu-ter", "Tech"),
        Phrase("Internet", "Internet", "een-ter-net", "Tech"),
        Phrase("Email", "E-mail", "ee-me-yl", "Tech"),
        Phrase("Message", "Messaggio", "mes-sad-jo", "Tech"),
        Phrase("Call", "Chiamata", "kya-ma-ta", "Tech"),
        Phrase("Music", "Musica", "moo-zee-ka", "Social"),
        Phrase("Art", "Arte", "ar-te", "Social"),
        Phrase("Movie", "Film", "feelm", "Social"),
        Phrase("Game", "Gioco", "jo-ko", "Social"),
        Phrase("Party", "Festa", "fes-ta", "Social"),
        Phrase("Birthday", "Compleanno", "kom-ple-an-no", "Social"),
        Phrase("Wedding", "Matrimonio", "ma-tree-mo-nyo", "Social"),
        Phrase("Holiday", "Vacanze", "va-kan-tse", "Social"),
        Phrase("Work", "Lavoro", "la-vo-ro", "Social"),
        Phrase("Success", "Successo", "soot-ches-so", "Social"),
        Phrase("Problem", "Problema", "pro-ble-ma", "Social"),
        Phrase("Solution", "Soluzione", "so-loo-tsyo-ne", "Social"),
        Phrase("Question", "Domanda", "do-man-da", "Social"),
        Phrase("Answer", "Risposta", "ree-spo-sta", "Social"),
        Phrase("True", "Vero", "ve-ro", "Social"),
        Phrase("False", "Falso", "fal-so", "Social"),
        Phrase("Paper", "Carta", "kar-ta", "Objects"),
        Phrase("Pen", "Penna", "pen-na", "Objects"),
        Phrase("Key", "Chiave", "kya-ve", "Objects"),
        Phrase("Bag", "Borsa", "bor-sa", "Objects"),
        Phrase("Clothes", "Vestiti", "ve-stee-tee", "Objects"),
        Phrase("Shoes", "Scarpe", "skar-pe", "Objects"),
        Phrase("Hat", "Cappello", "kap-pel-lo", "Objects"),
        Phrase("Clock", "Orologio", "o-ro-lo-jo", "Objects"),
        Phrase("Bottle", "Bottiglia", "bot-tee-lya", "Objects"),
        Phrase("Glass", "Bicchiere", "beek-kye-re", "Objects"),
        Phrase("Cup", "Tazza", "tat-tsa", "Objects"),
        Phrase("Fork", "Forchetta", "for-ket-ta", "Objects"),
        Phrase("Knife", "Coltello", "kol-tel-lo", "Objects"),
        Phrase("Spoon", "Cucchiaio", "kook-kya-yo", "Objects"),
        Phrase("Plate", "Piatto", "pyat-to", "Objects"),
        Phrase("Fruit", "Frutta", "froot-ta", "Food"),
        Phrase("Apple", "Mela", "me-la", "Food"),
        Phrase("Banana", "Banana", "ba-na-na", "Food"),
        Phrase("Orange", "Arancia", "a-ran-cha", "Food"),
        Phrase("Bread", "Pane", "pa-ne", "Food"),
        Phrase("Cheese", "Formaggio", "for-mad-jo", "Food"),
        Phrase("Meat", "Carne", "kar-ne", "Food"),
        Phrase("Egg", "Uovo", "wo-vo", "Food"),
        Phrase("Sugar", "Zucchero", "dzook-ke-ro", "Food"),
        Phrase("Salt", "Sale", "sa-le", "Food"),
        Phrase("Milk", "Latte", "lat-te", "Food"),
        Phrase("Coffee", "Caffè", "kaf-fe", "Food"),
        Phrase("Tea", "Tè", "te", "Food"),
        Phrase("Juice", "Succo", "sook-ko", "Food"),
        Phrase("Wine", "Vino", "vee-no", "Food"),
        Phrase("Beer", "Birra", "be-ra", "Food"),
        Phrase("Breakfast", "Colazione", "ko-la-tsyo-ne", "Food"),
        Phrase("Lunch", "Pranzo", "pran-zo", "Food"),
        Phrase("Dinner", "Cena", "che-na", "Food"),
        Phrase("Bill", "Conto", "kon-to", "Food"),
        Phrase("Tip", "Mancia", "man-cha", "Food"),
        Phrase("Police", "Polizia", "po-lee-tsya", "Emergency"),
        Phrase("Fire", "Fuoco", "fwo-ko", "Emergency"),
        Phrase("Danger", "Pericolo", "pe-ree-ko-lo", "Emergency"),
        Phrase("Doctor", "Medico", "me-dee-ko", "Emergency"),
        Phrase("Help", "Aiuto", "a-yu-to", "Emergency"),
        Phrase("I'm lost", "Mi sono perso", "mee so-no per-so", "Emergency"),
        Phrase("Sick", "Malato", "ma-la-to", "Emergency"),
        Phrase("Stop", "Fermati", "fer-ma-tee", "Emergency"),
        Phrase("Go", "Vai", "vy", "Emergency"),
        Phrase("Run", "Corri", "kor-ree", "Emergency"),
        Phrase("Slow down", "Rallenta", "ral-len-ta", "Emergency"),
        Phrase("Wait", "Aspetta", "a-spet-ta", "Emergency"),
        Phrase("Safe", "Sicuro", "see-koo-ro", "Emergency"),
        Phrase("Careful", "Attento", "at-ten-to", "Emergency"),
        Phrase("Attention", "Attenzione", "at-ten-tsyo-ne", "Emergency"),
        Phrase("Danger", "Pericolo", "pe-ree-ko-lo", "Emergency"),
        Phrase("Forbidden", "Vietato", "vye-ta-to", "Emergency"),
        Phrase("Exit", "Uscita", "oo-shee-ta", "Emergency"),
        Phrase("Entrance", "Entrata", "en-tra-ta", "Emergency"),
        Phrase("Push", "Spingere", "speen-je-re", "Emergency"),
        Phrase("Pull", "Tirare", "tee-ra-re", "Emergency"),
        Phrase("Toilet", "Bagno", "ba-nyo", "Emergency"),
        Phrase("Men", "Uomini", "wo-mee-nee", "Emergency"),
        Phrase("Women", "Donne", "don-ne", "Emergency"),
        Phrase("Reserved", "Riservato", "ree-ser-va-to", "Emergency"),
        Phrase("Free", "Libero", "lee-be-ro", "Emergency"),
        Phrase("Busy", "Occupato", "ok-koo-pa-to", "Emergency"),
        Phrase("Open", "Aperto", "a-per-to", "Emergency"),
        Phrase("Closed", "Chiuso", "kyoo-zo", "Emergency"),
        Phrase("Broken", "Rotto", "rot-to", "Emergency"),
        Phrase("Working", "Funziona", "foon-tsyo-na", "Emergency"),
        Phrase("Help me", "Aiutami", "a-yu-ta-mee", "Emergency"),
        Phrase("I am lost", "Mi sono perso", "mee so-no per-so", "Emergency"),
        Phrase("I need a doctor", "Ho bisogno di un medico", "o bee-zo-nyo dee oon me-dee-ko", "Emergency"),
        Phrase("Call the police", "Chiama la polizia", "kya-ma la po-lee-tsya", "Emergency"),
        Phrase("Call an ambulance", "Chiama un'ambulanza", "kya-ma oon am-boo-lan-tsa", "Emergency"),
        Phrase("There is a fire", "C'è un incendio", "che oon een-chen-dyo", "Emergency"),
        Phrase("Where is the hospital?", "Dov'è l'ospedale?", "do-ve l-os-pe-da-le", "Emergency"),
        Phrase("I feel ill", "Mi sento male", "mee sen-to ma-le", "Emergency"),
        Phrase("I have been robbed", "Mi hanno rubato", "mee an-no roo-ba-to", "Emergency"),
        Phrase("It's an emergency", "È un'emergenza", "e oon e-mer-jen-tsa", "Emergency"),
        Phrase("Hurry up", "Fai presto", "fy pre-sto", "Emergency"),
        Phrase("Calm down", "Calmati", "kal-ma-tee", "Emergency"),
        Phrase("Don't touch me", "Non mi toccare", "non mee tok-ka-re", "Emergency"),
        Phrase("I will call the police", "Chiamo la polizia", "kya-mo la po-lee-tsya", "Emergency"),
        Phrase("Stop thief!", "Al ladro!", "al la-dro", "Emergency"),
        Phrase("I am diabetic", "Sono diabetico", "so-no dya-be-tee-ko", "Emergency"),
        Phrase("I am allergic to...", "Sono allergico a...", "so-no al-ler-jee-ko a", "Emergency"),
        Phrase("I need medicine", "Ho bisogno di medicine", "o bee-zo-nyo dee me-dee-chee-ne", "Emergency"),

        // === EXPANDED: Nature ===
        Phrase("Sky", "Cielo", "chye-lo", "Nature"),
        Phrase("Cloud", "Nuvola", "noo-vo-la", "Nature"),
        Phrase("Star", "Stella", "stel-la", "Nature"),
        Phrase("River", "Fiume", "fyoo-me", "Nature"),
        Phrase("Lake", "Lago", "la-go", "Nature"),
        Phrase("Forest", "Foresta", "fo-res-ta", "Nature"),
        Phrase("Field", "Campo", "kam-po", "Nature"),
        Phrase("Garden", "Giardino", "jar-dee-no", "Nature"),

        // === EXPANDED: Colors ===
        Phrase("Orange (color)", "Arancione", "a-ran-cho-ne", "Colors"),
        Phrase("Pink", "Rosa", "ro-za", "Colors"),
        Phrase("Purple", "Viola", "vyo-la", "Colors"),
        Phrase("Brown", "Marrone", "mar-ro-ne", "Colors"),
        Phrase("Grey", "Grigio", "gree-jo", "Colors"),
        Phrase("Gold", "Oro", "o-ro", "Colors"),
        Phrase("Silver", "Argento", "ar-jen-to", "Colors"),
        Phrase("Light blue", "Azzurro", "ad-dzoor-ro", "Colors"),

        // === EXPANDED: Weather ===
        Phrase("Sunny", "Soleggiato", "so-led-ja-to", "Weather"),
        Phrase("Cloudy", "Nuvoloso", "noo-vo-lo-zo", "Weather"),
        Phrase("Storm", "Temporale", "tem-po-ra-le", "Weather"),
        Phrase("Fog", "Nebbia", "neb-bya", "Weather"),
        Phrase("Temperature", "Temperatura", "tem-pe-ra-too-ra", "Weather"),
        Phrase("Degrees", "Gradi", "gra-dee", "Weather"),
        Phrase("Humid", "Umido", "oo-mee-do", "Weather"),
        Phrase("Weather forecast", "Previsioni del tempo", "pre-vee-zyo-nee del tem-po", "Weather"),
        Phrase("It's nice weather", "Fa bel tempo", "fa bel tem-po", "Weather"),

        // === EXPANDED: Furniture ===
        Phrase("Sofa", "Divano", "dee-va-no", "Furniture"),
        Phrase("Wardrobe", "Armadio", "ar-ma-dyo", "Furniture"),
        Phrase("Desk", "Scrivania", "skree-va-nya", "Furniture"),
        Phrase("Shelf", "Scaffale", "skaf-fa-le", "Furniture"),
        Phrase("Lamp", "Lampada", "lam-pa-da", "Furniture"),
        Phrase("Mirror", "Specchio", "spek-kyo", "Furniture"),
        Phrase("Carpet", "Tappeto", "tap-pe-to", "Furniture"),

        // === EXPANDED: Rooms ===
        Phrase("Living room", "Soggiorno", "sod-jor-no", "Rooms"),
        Phrase("Dining room", "Sala da pranzo", "sa-la da pran-zo", "Rooms"),
        Phrase("Balcony", "Balcone", "bal-ko-ne", "Rooms"),
        Phrase("Garage", "Garage", "ga-razh", "Rooms"),
        Phrase("Garden", "Giardino", "jar-dee-no", "Rooms"),
        Phrase("Roof", "Tetto", "tet-to", "Rooms"),
        Phrase("Stairs", "Scale", "ska-le", "Rooms"),

        // === EXPANDED: Tech ===
        Phrase("Battery", "Batteria", "bat-te-ree-a", "Tech"),
        Phrase("Charger", "Caricatore", "ka-ree-ka-to-re", "Tech"),
        Phrase("Photo", "Foto", "fo-to", "Tech"),
        Phrase("Video", "Video", "vee-de-o", "Tech"),
        Phrase("App", "App", "app", "Tech"),
        Phrase("Password", "Password", "pas-sword", "Tech"),
        Phrase("Download", "Scaricare", "ska-ree-ka-re", "Tech"),
        Phrase("Upload", "Caricare", "ka-ree-ka-re", "Tech"),
        Phrase("Screen", "Schermo", "sker-mo", "Tech"),

        // === EXPANDED: Animals ===
        Phrase("Horse", "Cavallo", "ka-val-lo", "Animals"),
        Phrase("Cow", "Mucca", "mook-ka", "Animals"),
        Phrase("Pig", "Maiale", "ma-ya-le", "Animals"),
        Phrase("Rabbit", "Coniglio", "ko-neel-yo", "Animals"),
        Phrase("Mouse", "Topo", "to-po", "Animals"),
        Phrase("Duck", "Anatra", "a-na-tra", "Animals"),
        Phrase("Butterfly", "Farfalla", "far-fal-la", "Animals"),
        Phrase("Bee", "Ape", "a-pe", "Animals"),
        Phrase("Wolf", "Lupo", "loo-po", "Animals"),
        Phrase("Bear", "Orso", "or-so", "Animals"),

        // === NEW: Culture & Sightseeing ===
        Phrase("Museum", "Museo", "moo-ze-o", "Culture"),
        Phrase("Exhibition", "Mostra", "mos-tra", "Culture"),
        Phrase("Painting", "Quadro", "kwa-dro", "Culture"),
        Phrase("Sculpture", "Scultura", "skool-too-ra", "Culture"),
        Phrase("Fountain", "Fontana", "fon-ta-na", "Culture"),
        Phrase("Ruins", "Rovine", "ro-vee-ne", "Culture"),
        Phrase("Castle", "Castello", "kas-tel-lo", "Culture"),
        Phrase("Tower", "Torre", "tor-re", "Culture"),
        Phrase("Monument", "Monumento", "mo-noo-men-to", "Culture"),
        Phrase("Ancient", "Antico", "an-tee-ko", "Culture"),
        Phrase("Medieval", "Medievale", "me-dye-va-le", "Culture"),
        Phrase("Renaissance", "Rinascimento", "ree-na-shee-men-to", "Culture"),
        Phrase("Can I take a photo?", "Posso fare una foto?", "pos-so fa-re oo-na fo-to", "Culture"),
        Phrase("Where is the entrance?", "Dov'è l'entrata?", "do-ve l-en-tra-ta", "Culture"),
        Phrase("How much is the ticket?", "Quanto costa il biglietto?", "kwan-to kos-ta eel beel-yet-to", "Culture"),
        Phrase("Guide", "Guida", "gwee-da", "Culture"),
        Phrase("Audio guide", "Audioguida", "ow-dyo-gwee-da", "Culture"),
        Phrase("Souvenir", "Souvenir", "soo-ve-neer", "Culture")
    )
}

object PlacesData {
    const val GENERIC_HISTORY = "Visit this place to unlock its history."

    const val IMG_PIZZA = "https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=400&h=300&fit=crop"
    const val IMG_PASTA = "https://images.unsplash.com/photo-1621996346565-e3dbc646d9a9?w=400&h=300&fit=crop"
    const val IMG_GELATO = "https://images.unsplash.com/photo-1501443762994-82bd5dace89a?w=400&h=300&fit=crop"
    const val IMG_COFFEE = "https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=400&h=300&fit=crop"
    const val IMG_APERITIVO = "https://images.unsplash.com/photo-1551024709-8f23befc6f87?w=400&h=300&fit=crop"
    const val IMG_MALL = "https://images.unsplash.com/photo-1555529669-e69e7aa0ba9a?w=400&h=300&fit=crop"
    const val IMG_WATER = "https://images.unsplash.com/photo-1548839140-29a749e1cf4d?w=400&h=300&fit=crop"
    const val IMG_WC = "https://images.unsplash.com/photo-1585320806297-9794b3e4eeae?w=400&h=300&fit=crop"
    const val IMG_PARK = "https://images.unsplash.com/photo-1585320806297-9794b3e4eeae?w=400&h=300&fit=crop"
    const val IMG_NIGHTLIFE = "https://images.unsplash.com/photo-1566417713940-fe7c737a9ef2?w=400&h=300&fit=crop"

    val list = listOf(
        // === MONUMENTY & HISTORIA (70) ===
        Place("Galleria Spada", "Optical Illusion.", "Famous 9-meter corridor that looks 35 meters long.", "https://en.wikipedia.org/wiki/Palazzo_Spada", "https://upload.wikimedia.org/wikipedia/commons/thumb/d/dc/Palazzo_Spada_-_Rome%2C_Italy_-_DSC09752.jpg/600px-Palazzo_Spada_-_Rome%2C_Italy_-_DSC09752.jpg", LatLng(41.8939, 12.4712), 4.6, "monuments", "5€", "08:30-19:30"),
        Place("Teatro di Marcello", "The Mini-Colosseum.", "Ancient open-air theatre built by Augustus.", "https://en.wikipedia.org/wiki/Theatre_of_Marcellus", "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a2/Teatro_di_Marcello_intero.jpg/600px-Teatro_di_Marcello_intero.jpg", LatLng(41.8919, 12.4795), 4.6, "monuments", "Free View", "24h"),
        Place("Santa Maria del Popolo", "Caravaggio Inside.", "Home to two of Caravaggio's greatest masterpieces.", "https://en.wikipedia.org/wiki/Santa_Maria_del_Popolo", "https://upload.wikimedia.org/wikipedia/commons/thumb/3/36/20140803_Basilica_of_Santa_Maria_del_Popolo_Rome_0191.jpg/600px-20140803_Basilica_of_Santa_Maria_del_Popolo_Rome_0191.jpg", LatLng(41.9114, 12.4764), 4.7, "monuments", "Free", "07:30-19:00", recommendation = "Must see"),
        Place("San Luigi dei Francesi", "More Caravaggio.", "The Matthew cycle paintings in the Contarelli Chapel.", "https://en.wikipedia.org/wiki/San_Luigi_dei_Francesi", "https://upload.wikimedia.org/wikipedia/commons/thumb/9/94/%C3%89glise_San_Luigi_Francesi_-_Rome_%28IT62%29_-_2021-08-28_-_2.jpg/600px-%C3%89glise_San_Luigi_Francesi_-_Rome_%28IT62%29_-_2021-08-28_-_2.jpg", LatLng(41.8996, 12.4748), 4.7, "monuments", "Free", "10:00-18:50"),
        Place("Bramante Cloister", "Renaissance Peace.", "Hidden courtyard museum near Piazza Navona.", "https://en.wikipedia.org/wiki/Cloister_of_Bramante", "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b4/Tempietto_del_Bramante_nel_Chiostro_di_S._Pietro_in_Montorio_di_roma.jpg/600px-Tempietto_del_Bramante_nel_Chiostro_di_S._Pietro_in_Montorio_di_roma.jpg", LatLng(41.9001, 12.4716), 4.6, "parks", "Free Entry", "10:00-19:00"),
        Place("Doria Pamphilj Gallery", "Private Palace.", "Stunning gallery with over 400 Renaissance artworks.", "https://en.wikipedia.org/wiki/Doria_Pamphilj_Gallery", "https://upload.wikimedia.org/wikipedia/commons/thumb/6/64/Palazzo_Doria_Pamphilj.jpg/600px-Palazzo_Doria_Pamphilj.jpg", LatLng(41.8978, 12.4815), 4.7, "monuments", "15€", "09:00-19:00"),
        Place("Church of Sant'Ignazio", "The Fake Dome.", "Famous 3D fresco creating an illusion of a massive dome.", "https://en.wikipedia.org/wiki/Sant%27Ignazio,_Rome", "https://upload.wikimedia.org/wikipedia/commons/thumb/5/57/Triumph_of_St._Ignatius_of_Loyola%2C_ceiling_fresco_by_Andrea_Pozzo.jpg/600px-Triumph_of_St._Ignatius_of_Loyola%2C_ceiling_fresco_by_Andrea_Pozzo.jpg", LatLng(41.8992, 12.4795), 4.8, "monuments", "Free", "09:00-23:30"),
        Place("Palazzo Altemps", "Sculpture Heaven.", "Quiet Renaissance palace housing elite Roman sculptures.", "https://en.wikipedia.org/wiki/Palazzo_Altemps", "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ed/Palazzo_altemps%2C_cortile_07.JPG/600px-Palazzo_altemps%2C_cortile_07.JPG", LatLng(41.9008, 12.4728), 4.7, "monuments", "10€", "09:00-19:45"),
        Place("Villa Farnesina", "Raphael's Art.", "Renaissance villa with stunning mythological frescoes.", "https://en.wikipedia.org/wiki/Villa_Farnesina", "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0a/La_villa_Farnesina_%28Rome%29_%2834029492720%29.jpg/600px-La_villa_Farnesina_%28Rome%29_%2834029492720%29.jpg", LatLng(41.8935, 12.4675), 4.8, "monuments", "10€", "09:00-14:00"),
        Place("Crypta Balbi", "City Layers.", "Museum showing how the city changed over 2,000 years.", "https://en.wikipedia.org/wiki/Crypta_Balbi", "https://upload.wikimedia.org/wikipedia/commons/thumb/7/72/Pigna-s_Angelo_-_via_delle_Botteghe_oscure_-_crypta_Balbi_00738.JPG/600px-Pigna-s_Angelo_-_via_delle_Botteghe_oscure_-_crypta_Balbi_00738.JPG", LatLng(41.8938, 12.4785), 4.5, "monuments", "8€", "09:00-19:45"),
        Place("Temple of Hadrian", "Roman Columns.", "11 massive ancient columns integrated into the Stock Exchange.", "https://en.wikipedia.org/wiki/Temple_of_Hadrian", "https://upload.wikimedia.org/wikipedia/commons/thumb/6/68/Tempio_di_Adriano_-_esterno.jpg/600px-Tempio_di_Adriano_-_esterno.jpg", LatLng(41.8998, 12.4788), 4.6, "monuments", "Free View", "24h"),
        Place("Arch of Janus", "Four-Way Arch.", "The only quadrifrons triumphal arch preserved in Rome.", "https://en.wikipedia.org/wiki/Arch_of_Janus", "https://upload.wikimedia.org/wikipedia/commons/thumb/6/66/Arch_of_Janus.jpg/600px-Arch_of_Janus.jpg", LatLng(41.8892, 12.4828), 4.4, "monuments", "Free View", "24h"),
        Place("Mausoleum of Augustus", "Emperor's Tomb.", "Recently reopened massive circular tomb of the first emperor.", "https://en.wikipedia.org/wiki/Mausoleum_of_Augustus", "https://upload.wikimedia.org/wikipedia/commons/thumb/c/cf/Photographs_of_the_Mausoleum_of_Augustus_14_%28cropped%29.jpg/600px-Photographs_of_the_Mausoleum_of_Augustus_14_%28cropped%29.jpg", LatLng(41.9061, 12.4765), 4.5, "monuments", "5€", "09:00-19:00"),
        Place("Pasquino Statue", "Talking Statue.", "Ancient fragment where people post anonymous satirical poems.", "https://en.wikipedia.org/wiki/Pasquino", "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3d/Pasquino_02.jpg/600px-Pasquino_02.jpg", LatLng(41.8978, 12.4722), 4.3, "monuments", "Free", "24h"),
        Place("San Stefano Rotondo", "Circular Church.", "Stunning 5th-century round church with intense frescoes.", "https://en.wikipedia.org/wiki/Santo_Stefano_Rotondo", "https://upload.wikimedia.org/wikipedia/commons/c/c8/SantoStefanoRotondoByRoeslerFranz.jpg", LatLng(41.8848, 12.4965), 4.7, "monuments", "Free", "09:30-12:30, 15:30-18:30"),
        Place("Largo di Torre Argentina", "Sacred Area.", "Site of Caesar's assassination and a famous cat sanctuary.", "https://en.wikipedia.org/wiki/Largo_di_Torre_Argentina", "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b0/Curia_of_Pompey.jpg/600px-Curia_of_Pompey.jpg", LatLng(41.8961, 12.4768), 4.5, "monuments", "5€ (Walkway)", "09:30-19:00"),
        Place("Quirinal Palace", "Presidential Home.", "Official residence of the President of Italy on the highest hill.", "https://en.wikipedia.org/wiki/Quirinal_Palace", "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e4/Quirinale_palazzo_e_obelisco_con_dioscuri_Roma.jpg/600px-Quirinale_palazzo_e_obelisco_con_dioscuri_Roma.jpg", LatLng(41.9002, 12.4868), 4.7, "monuments", "10€ (Booking)", "09:30-16:00"),

        Place("National Gallery", "Modern Art Hub.", "Grand museum of 19th and 20th century Italian art.", "https://en.wikipedia.org/wiki/Galleria_Nazionale_d%27Arte_Moderna", "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3e/Galleria_Nazionale_di_Arte_Moderna_-_Roma%2C_Italia.jpg/600px-Galleria_Nazionale_di_Arte_Moderna_-_Roma%2C_Italia.jpg", LatLng(41.9168, 12.4822), 4.6, "monuments", "10€", "09:00-19:00"),
        Place("Santa Maria in Trastevere", "Golden Mosaics.", "One of the oldest churches in Rome.", "https://en.wikipedia.org/wiki/Santa_Maria_in_Trastevere", "https://upload.wikimedia.org/wikipedia/commons/thumb/6/63/01_Santa_Maria_in_Trastevere_Facade.jpg/600px-01_Santa_Maria_in_Trastevere_Facade.jpg", LatLng(41.8895, 12.4705), 4.8, "monuments", "Free", "07:30-21:00"),
        Place("San Giovanni in Laterano", "Rome's Cathedral.", "The official ecclesiastical seat of the Pope.", "https://en.wikipedia.org/wiki/Archbasilica_of_Saint_John_Lateran", "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5f/San_Giovanni_in_Laterano_2021.jpg/600px-San_Giovanni_in_Laterano_2021.jpg", LatLng(41.8859, 12.5057), 4.8, "monuments", "Free", "07:00-18:30"),
        Place("San Pietro in Vincoli", "Michelangelo's Moses.", "Famous for the horns of Moses statue.", "https://en.wikipedia.org/wiki/San_Pietro_in_Vincoli", "https://upload.wikimedia.org/wikipedia/commons/thumb/7/74/San_Pietro_in_Vincoli_-_esterno.jpg/600px-San_Pietro_in_Vincoli_-_esterno.jpg", LatLng(41.8938, 12.4931), 4.7, "monuments", "Free", "08:00-18:50"),
        Place("Santa Sabina", "Early Christian.", "Beautiful wooden doors from 432 AD on Aventine.", "https://en.wikipedia.org/wiki/Santa_Sabina", "https://upload.wikimedia.org/wikipedia/commons/thumb/8/80/Santa_Sabina_%28Rome%29_-_Esterno.jpg/600px-Santa_Sabina_%28Rome%29_-_Esterno.jpg", LatLng(41.8845, 12.4795), 4.8, "monuments", "Free", "07:30-19:00"),
        Place("Bramante's Tempietto", "Renaissance Jewel.", "Small circular temple in San Pietro in Montorio.", "https://en.wikipedia.org/wiki/Tempietto_of_Bramante", "https://upload.wikimedia.org/wikipedia/commons/thumb/0/01/Tempietto_di_San_Pietro_in_Montorio.jpg/600px-Tempietto_di_San_Pietro_in_Montorio.jpg", LatLng(41.8892, 12.4655), 4.7, "monuments", "Free", "09:00-18:00"),
        Place("Colosseum", "Iconic amphitheater.", "Ancient heart of Rome.", "https://en.m.wikipedia.org/wiki/Colosseum", "https://upload.wikimedia.org/wikipedia/commons/thumb/d/de/Colosseo_2020.jpg/600px-Colosseo_2020.jpg", LatLng(41.8902, 12.4922), 4.9, "monuments", "Ticket", "08:30-19:00", recommendation = "Must see"),
        Place("Pantheon", "Ancient temple.", "Best preserved Roman building.", "https://en.m.wikipedia.org/wiki/Pantheon,_Rome", "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ef/Pantheon_Rom_1_cropped.jpg/600px-Pantheon_Rom_1_cropped.jpg", LatLng(41.8986, 12.4769), 4.9, "monuments", "5€", "09:00-19:00", recommendation = "Must see"),
        Place("Trevi Fountain", "Baroque masterpiece.", "Legendary coin-tossing spot.", "https://en.m.wikipedia.org/wiki/Trevi_Fountain", "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c7/Trevi_Fountain_-_Roma.jpg/600px-Trevi_Fountain_-_Roma.jpg", LatLng(41.9009, 12.4833), 4.8, "monuments", "Free", "24h", recommendation = "Must see"),
        Place("Vatican Museums", "Papal Art.", "Home to Sistine Chapel.", "https://en.m.wikipedia.org/wiki/Vatican_Museums", "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f5/Basilica_di_San_Pietro_in_Vaticano_September_2015-1a.jpg/600px-Basilica_di_San_Pietro_in_Vaticano_September_2015-1a.jpg", LatLng(41.9065, 12.4536), 4.8, "monuments", "20€", "08:00-19:00"),
        Place("Basilica di San Pietro", "Vatican Heart.", "Largest church globally.", "https://en.m.wikipedia.org/wiki/St._Peter%27s_Basilica", "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f5/Basilica_di_San_Pietro_in_Vaticano_September_2015-1a.jpg/600px-Basilica_di_San_Pietro_in_Vaticano_September_2015-1a.jpg", LatLng(41.9022, 12.4539), 4.9, "monuments", "Free", "07:00-19:10", recommendation = "Must see"),
        Place("Foro Romano", "Ancient Center.", "Empire ruins.", "https://en.m.wikipedia.org/wiki/Roman_Forum", "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6a/Foro_Romano_Musei_Capitolini_Roma.jpg/600px-Foro_Romano_Musei_Capitolini_Roma.jpg", LatLng(41.8925, 12.4853), 4.8, "monuments", "Ticket", "09:00-19:15", recommendation = "Must see"),
        Place("Castel Sant'Angelo", "Hadrian's Tomb.", "Papal fortress.", "https://en.m.wikipedia.org/wiki/Castel_Sant%27Angelo", "https://upload.wikimedia.org/wikipedia/commons/thumb/6/66/Castel_Sant%27_Angelo_Between_Leaves.jpg/600px-Castel_Sant%27_Angelo_Between_Leaves.jpg", LatLng(41.9031, 12.4663), 4.7, "monuments", "13€", "09:00-19:30"),
        Place("Spanish Steps", "Famous stairs.", "Piazza di Spagna.", "https://en.m.wikipedia.org/wiki/Spanish_Steps", "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b8/Piazza_di_Spagna_%28Rome%29_0004.jpg/600px-Piazza_di_Spagna_%28Rome%29_0004.jpg", LatLng(41.9057, 12.4823), 4.6, "monuments", "Free", "24h", recommendation = "Must see"),
        Place("Altare della Patria", "White Monument.", "Victor Emmanuel II.", "https://en.m.wikipedia.org/wiki/Victor_Emmanuel_II_National_Monument", "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a2/Piazza_Venezia_-_Il_Vittoriano.jpg/600px-Piazza_Venezia_-_Il_Vittoriano.jpg", LatLng(41.8946, 12.4831), 4.6, "monuments", "Free", "09:30-19:30"),
        Place("Piazza Navona", "Baroque Square.", "Bernini fountains.", "https://en.m.wikipedia.org/wiki/Piazza_Navona", "https://upload.wikimedia.org/wikipedia/commons/thumb/0/08/Piazza_Navona_%28Rome%29_at_night.jpg/600px-Piazza_Navona_%28Rome%29_at_night.jpg", LatLng(41.8992, 12.4731), 4.7, "monuments", "Free", "24h", recommendation = "Must see"),
        Place("Baths of Caracalla", "Imperial Spa.", "Massive ruins.", "https://en.m.wikipedia.org/wiki/Baths_of_Caracalla", "https://upload.wikimedia.org/wikipedia/commons/thumb/5/53/Baths_of_Caracalla%2C_facing_Caldarium.jpg/600px-Baths_of_Caracalla%2C_facing_Caldarium.jpg", LatLng(41.8788, 12.4927), 4.7, "parks", "8€", "09:00-19:00"),
        Place("Janiculum Hill", "City View.", "Sunset lookout.", "https://en.wikipedia.org/wiki/Janiculum", "https://upload.wikimedia.org/wikipedia/commons/thumb/6/66/Janiculum.jpg/600px-Janiculum.jpg", LatLng(41.8913, 12.4616), 4.8, "parks", "Free", "24h"),
        Place("Orange Garden", "Aventine View.", "Perfect park.", "https://en.wikipedia.org/wiki/Giardino_degli_Aranci", "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8f/Dal_giardino_degli_aranci_-_tutta_roma.JPG/600px-Dal_giardino_degli_aranci_-_tutta_roma.JPG", LatLng(41.8845, 12.4795), 4.9, "parks", "Free", "07:00-18:00"),
        Place("Keyhole of Malta", "Secret View.", "Dome through a hole.", "https://en.wikipedia.org/wiki/Piazza_dei_Cavalieri_di_Malta", "https://upload.wikimedia.org/wikipedia/commons/thumb/7/74/Il_buco_della_serratura_-_Priorato_dei_Cavalieri_di_Malta_%2825525267346%29.jpg/600px-Il_buco_della_serratura_-_Priorato_dei_Cavalieri_di_Malta_%2825525267346%29.jpg", LatLng(41.8824, 12.4785), 4.7, "monuments", "Free", "24h", recommendation = "Must see"),
        Place("Capuchin Crypt", "Bone Church.", "Bone decorations.", "https://en.wikipedia.org/wiki/Capuchin_Crypt", "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ec/Rom%2C_Santa_Maria_Immacolata_a_Via_Veneto%2C_Krypta_der_Kapuziner_1.jpg/600px-Rom%2C_Santa_Maria_Immacolata_a_Via_Veneto%2C_Krypta_der_Kapuziner_1.jpg", LatLng(41.9048, 12.4885), 4.6, "monuments", "9€", "10:00-19:00"),
        Place("Galleria Borghese", "Art Museum.", "Bernini statues.", "https://en.wikipedia.org/wiki/Galleria_Borghese", "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f9/Ingresso_monumentale_di_Villa_Borghese_a_Roma_su_piazzale_Flaminio_2018-02.jpg/600px-Ingresso_monumentale_di_Villa_Borghese_a_Roma_su_piazzale_Flaminio_2018-02.jpg", LatLng(41.9142, 12.4921), 4.8, "monuments", "15€", "09:00-19:00"),
        Place("Largo Argentina", "Caesar's End.", "Cat sanctuary.", "https://en.wikipedia.org/wiki/Largo_di_Torre_Argentina", "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b0/Curia_of_Pompey.jpg/600px-Curia_of_Pompey.jpg", LatLng(41.8960, 12.4769), 4.5, "monuments", "Free", "24h"),
        Place("MAXXI Museum", "Modern Art.", "Zaha Hadid design.", "https://en.wikipedia.org/wiki/MAXXI", "https://upload.wikimedia.org/wikipedia/en/thumb/9/9b/MAXXI_%2827483747665%29.jpg/600px-MAXXI_%2827483747665%29.jpg", LatLng(41.9282, 12.4665), 4.5, "monuments", "12€", "11:00-19:00"),
        Place("Galleria Sciarra", "Hidden Art.", "Courtyard frescoes.", "", "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ae/Sciarra_Gallery_Rome_%2814348543%29.jpeg/600px-Sciarra_Gallery_Rome_%2814348543%29.jpeg", LatLng(41.8995, 12.4820), 4.7, "monuments", "Free", "09:00-20:00"),
        // ── HIDDEN GEM MUST-SEE MONUMENTS ──
        Place("Terrazza delle Quadrighe", "Golden Rooftop.", "Secret panoramic terrace atop Palazzo delle Esposizioni crowned by magnificent golden quadriga statues. One of Rome's most spectacular hidden viewpoints — the golden chariots gleam at sunset over the entire city skyline.", "https://en.wikipedia.org/wiki/Palazzo_delle_Esposizioni", "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5e/Palazzo_delle_Esposizioni_Roma.jpg/600px-Palazzo_delle_Esposizioni_Roma.jpg", LatLng(41.8988, 12.4893), 4.9, "monuments", "Free View", "10:00-20:00", recommendation = "Must see"),
        Place("Basilica di San Clemente", "Three Layers Deep.", "Mind-blowing archaeological lasagna: a 12th-century basilica built over a 4th-century church, built over a 1st-century Roman house and ancient Mithraeum temple. You literally descend through 2,000 years of history in one building.", "https://en.wikipedia.org/wiki/Basilica_of_San_Clemente", "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3e/San_Clemente_al_Laterano_exterior.jpg/600px-San_Clemente_al_Laterano_exterior.jpg", LatLng(41.8892, 12.4972), 4.9, "monuments", "10€", "09:00-12:30, 15:00-18:00", recommendation = "Must see"),

        // === PASTA / PIZZA(food) (60) ===
        Place("Checco er Carettiere", "Trastevere Tradition.", "Family-run institution since 1935.", "", IMG_PASTA, LatLng(41.8922, 12.4695), 4.4, "food", "€€€ • Pasta", "12:30-23:00"),
        Place("La Carbonara", "Monti Classic.", "Legendary location on Via Panisperna.", "", IMG_PASTA, LatLng(41.8955, 12.4912), 4.4, "food", "€€ • Pasta", "12:30-23:00"),
        Place("Perilli a Testaccio", "Authentic Temple.", "Unchanged recipes since the 1950s.", "", IMG_PASTA, LatLng(41.8770, 12.4785), 4.6, "food", "€€ • Pasta", "12:30-22:30"),
        Place("Da Gino", "Parliament Pasta.", "Where senators eat classic Roman dishes.", "", IMG_PASTA, LatLng(41.9015, 12.4782), 4.4, "food", "€€ • Pasta", "13:00-21:30"),
        Place("L'Arcangelo", "Gourmet Gnocchi.", "The best potato gnocchi in the Prati district.", "", IMG_PASTA, LatLng(41.9055, 12.4655), 4.5, "food", "€€€ • Pasta", "19:00-23:00"),
        Place("Hostaria Romana", "Graffiti Walls.", "Massive portions and energetic vibe near Barberini.", "", IMG_PASTA, LatLng(41.9030, 12.4880), 4.3, "food", "€€ • Pasta", "12:30-23:00"),
        Place("Pasta e Vino", "Live Shaping.", "Fresh pasta made right in front of you.", "", IMG_PASTA, LatLng(41.8899, 12.4688), 4.4, "food", "€€ • Pasta", "12:00-23:00"),
        Place("Borghiciana Pastificio", "Handmade Vatican.", "Tiny shop with elite fresh pasta varieties.", "", IMG_PASTA, LatLng(41.9028, 12.4595), 4.6, "food", "€ • Pasta", "12:00-22:00"),
        Place("Antica Pesa", "Celebrity Trastevere.", "Refined dining in a historic setting.", "", IMG_PASTA, LatLng(41.8915, 12.4665), 4.6, "food", "€€€€ • Pasta", "19:30-23:30"),
        Place("Colline Emiliane", "Egg Pasta King.", "The best Northern Italian style pasta in Rome.", "", IMG_PASTA, LatLng(41.9038, 12.4885), 4.7, "food", "€€€ • Pasta", "12:45-22:45"),
        Place("Hostaria da Nerone", "Colosseum Terrace.", "Dine overlooking the ruins with classic flavors.", "", IMG_PASTA, LatLng(41.8915, 12.4925), 4.3, "food", "€€ • Pasta", "12:30-23:00"),
        Place("Grappolo d'Oro", "Slow Food Gem.", "High quality seasonal Roman cooking near Campo.", "", IMG_PASTA, LatLng(41.8965, 12.4725), 4.6, "food", "€€ • Pasta", "12:30-23:00"),
        Place("Osteria Fratelli Mori", "Ostiense Soul.", "Hearty portions and a great Gricia.", "", IMG_PASTA, LatLng(41.8745, 12.4825), 4.6, "food", "€€ • Pasta", "12:30-23:00"),
        Place("Enoteca Corsi", "Wine Shop Lunch.", "Lunch-only spot where you eat among wine crates.", "", IMG_PASTA, LatLng(41.8955, 12.4795), 4.4, "food", "€€ • Pasta", "12:00-15:30"),
        Place("Ba'Ghetto", "Kosher Roman.", "Fried artichokes and Roman-Jewish classics.", "", IMG_PASTA, LatLng(41.8925, 12.4775), 4.3, "food", "€€€ • Pasta", "12:00-23:00"),
        Place("La Tavernola", "Testaccio Value.", "Authentic working-class lunch spot.", "", IMG_PASTA, LatLng(41.8795, 12.4745), 4.5, "food", "€ • Pasta", "12:00-15:00"),
        Place("I Quintili", "Award Winning Napoli.", "Contemporary soft borders and premium toppings.", "", IMG_PIZZA, LatLng(41.8625, 12.5655), 4.7, "food", "€€ • Pizza", "19:00-23:30"),
        Place("Alle Carrette", "Hidden Monti.", "Tucked in an alley, classic Roman thin crust.", "", IMG_PIZZA, LatLng(41.8945, 12.4895), 4.3, "food", "€ • Pizza", "19:00-00:00"),
        Place("La Pratolina", "Pinsa Excellence.", "Famous for highly digestible Pinsa Romana.", "", IMG_PIZZA, LatLng(41.9105, 12.4585), 4.6, "food", "€€ • Pizza", "19:00-23:45"),
        Place("Magnifica", "Upper Rome Quality.", "Exceptional ingredients and long leavening.", "", IMG_PIZZA, LatLng(41.9285, 12.4485), 4.6, "food", "€€ • Pizza", "19:00-23:30"),
        Place("Zazà Pizza", "Pantheon Slices.", "Organic flour and creative seasonal toppings.", "", IMG_PIZZA, LatLng(41.8985, 12.4755), 4.5, "food", "€ • Pizza", "10:00-22:00"),
        Place("Pizza Zizza", "Vatican Hospitality.", "Gourmet tasting slices near St. Peter's.", "", IMG_PIZZA, LatLng(41.9015, 12.4565), 4.8, "food", "€ • Pizza", "11:00-21:00"),
        Place("L'Archetto", "Hidden Courtyard.", "Thin pizza with over 100 varieties near Trevi.", "", IMG_PIZZA, LatLng(41.8992, 12.4835), 4.3, "food", "€€ • Pizza", "12:00-00:00"),
        Place("Pizzeria Nerone", "Prati Local.", "Famous for their wood-fired oven and fritti.", "", IMG_PIZZA, LatLng(41.9075, 12.4635), 4.4, "food", "€€ • Pizza", "19:00-23:30"),
        Place("Giulietta", "Double Style.", "High-end Neapolitan and Roman styles in Testaccio.", "", IMG_PIZZA, LatLng(41.8755, 12.4815), 4.5, "food", "€€ • Pizza", "19:30-23:30"),
        Place("Pizzeria San Marino", "Big & Crispy.", "Huge Roman pizzas loved by students.", "", IMG_PIZZA, LatLng(41.9185, 12.5185), 4.3, "food", "€ • Pizza", "19:00-00:00"),
        Place("Graspo de Ua", "Quiet Trastevere.", "Authentic thin pizza away from the crowds.", "", IMG_PIZZA, LatLng(41.8895, 12.4725), 4.2, "food", "€ • Pizza", "18:00-00:00"),
        Place("In Fucina", "Pizza Tasting.", "Served in slices like a high-end restaurant.", "", IMG_PIZZA, LatLng(41.8595, 12.4585), 4.7, "food", "€€€ • Pizza", "19:30-23:00"),
        Place("Peppo al Cosimato", "Sourdough Fish.", "Seafood-focused pizza in Trastevere.", "", IMG_PIZZA, LatLng(41.8885, 12.4715), 4.5, "food", "€€ • Pizza", "12:00-23:30"),
        Place("Moma Pizza", "Hip Monti.", "Modern Roman vibe and very thin crust.", "", IMG_PIZZA, LatLng(41.8955, 12.4905), 4.4, "food", "€€ • Pizza", "19:00-00:00"),
        Place("La Montecarlo", "Navona Speed.", "Fast service, paper tablecloths, pure Rome.", "", IMG_PIZZA, LatLng(41.8995, 12.4705), 4.4, "food", "€ • Pizza", "12:00-01:00"),
        Place("Baffetto 2", "Legendary Expansion.", "The famous thin pizza with more seating.", "", IMG_PIZZA, LatLng(41.8945, 12.4725), 4.0, "food", "€€ • Pizza", "18:00-00:30"),
        Place("Spiazzo", "Gourmet Lab.", "The 'Pizza-Burger' and innovative toppings.", "", IMG_PIZZA, LatLng(41.8685, 12.4725), 4.5, "food", "€€ • Pizza", "19:30-23:30"),
        Place("Alice Pizza ", "The Cut King.", "The original flagship for gourmet Roman slices.", "", IMG_PIZZA, LatLng(41.9075, 12.4475), 4.4, "food", "€ • Pizza", "10:00-21:30"),
        Place("Pizzeria Ostiense", "Industrial Style.", "Thin Roman pizza in the trendy Gasometer area.", "", IMG_PIZZA, LatLng(41.8715, 12.4825), 4.5, "food", "€ • Pizza", "18:30-23:30"),
        Place("Gia Sotto L'Arco", "Vatican Classic.", "Old school thin pizza and local vibe.", "", IMG_PIZZA, LatLng(41.9032, 12.4552), 4.3, "food", "€€ • Pizza", "19:00-00:00"),
        Place("A Taglio", "Modern Slice.", "High hydration dough and premium toppings.", "", IMG_PIZZA, LatLng(41.8892, 12.4695), 4.6, "food", "€ • Pizza", "11:00-23:00"),
        Place("Pizzeria Da Simone", "Testaccio Secret.", "Neighborhood spot for classic Roman scrocchiarella.", "", IMG_PIZZA, LatLng(41.8765, 12.4748), 4.4, "food", "€ • Pizza", "19:00-23:30"),
        Place("La Pariolina", "Parioli's Best.", "Thin Roman or Neapolitan, you choose.", "", IMG_PIZZA, LatLng(41.9245, 12.4925), 4.5, "food", "€€ • Pizza", "19:00-00:00"),
        Place("L'Angolo Cottura", "Light & Airy.", "Ancient grains used for a very light base.", "", IMG_PIZZA, LatLng(41.9155, 12.4555), 4.4, "food", "€€ • Pizza", "19:00-23:00"),
        Place("I Belcastro", "Northern Excellence.", "Award-winning pizza in the Flaminio area.", "", IMG_PIZZA, LatLng(41.9215, 12.4885), 4.5, "food", "€€ • Pizza", "19:30-23:30"),
        Place("Pizzeria Loffredo", "Napoli Soul.", "True fluffy Neapolitan heart in the suburbs.", "", IMG_PIZZA, LatLng(41.8555, 12.5585), 4.7, "food", "€€ • Pizza", "19:00-23:30"),
        Place("Pizzeria L'Osteria", "Testaccio Thin.", "Locals' favorite for the thinnest pizza.", "", IMG_PIZZA, LatLng(41.8785, 12.4755), 4.4, "food", "€€ • Pizza", "19:00-23:30"),
        Place("Zazà Pantheon", "Quick Quality.", "Sustainable ingredients near the Pantheon.", "", IMG_PIZZA, LatLng(41.8985, 12.4755), 4.5, "food", "€ • Pizza", "10:00-22:00"),
        Place("Pizza Florida", "The Healthy Slice.", "Long fermentation dough near Largo Argentina.", "", IMG_PIZZA, LatLng(41.8955, 12.4775), 4.6, "food", "€ • Pizza", "10:00-22:00"),
        Place("Antico Forno Roscioli", "Pizza Bianca King.", "Legendary crispy pizza with mortadella.", "", IMG_PIZZA, LatLng(41.8943, 12.4731), 4.8, "food", "€ • Pizza", "07:30-20:00"),
        Place("Sforno Tuscolana", "Thick Crust King.", "Cacio e Pepe pizza with black pepper dough.", "", IMG_PIZZA, LatLng(41.8596, 12.5539), 4.6, "food", "€€ • Pizza", "19:30-23:30"),
        Place("Hostaria da Corrado", "Trastevere Local.", "Paper tablecloths, very cheap house wine.", "", IMG_PASTA, LatLng(41.8898, 12.4685), 4.4, "food", "€ • Pasta", "12:30-23:00"),
        Place("Pro Loco Dol", "Centocelle gem.", "Only local ingredients from Lazio.", "", IMG_PASTA, LatLng(41.8795, 12.5625), 4.6, "food", "€€ • Pasta", "19:00-23:30"),
        Place("Sbanco Pizza", "Appia Nuova.", "Massive fried starters and craft beer selection.", "", IMG_PIZZA, LatLng(41.8745, 12.5155), 4.5, "food", "€€ • Pizza", "19:00-00:00"),
        Place("Santi Sebastiano e Valentino", "Bakery & More.", "Gourmet bread and elegant dishes near Salario.", "", IMG_PASTA, LatLng(41.9165, 12.4985), 4.7, "food", "€€€ • Pasta", "08:30-23:00"),
        Place("Seu Pizza Ostiense", "Modern Art.", "Creative crusts in the old industrial zone.", "", IMG_PIZZA, LatLng(41.8725, 12.4785), 4.7, "food", "€€€ • Pizza", "19:00-23:30"),
        Place("Tazza d'Oro Shop", "Coffee Beans.", "Buy the beans to take home near Pantheon.", "", IMG_COFFEE, LatLng(41.8998, 12.4765), 4.6, "coffee", "€€", "08:00-20:00"),
        Place("La Montecarlo", "Roman Thin.", "Fast service, noisy, real.", "", IMG_PIZZA, LatLng(41.8995, 12.4705), 4.4, "food", "€ • Pizza", "12:00-01:00"),
        Place("Pizzeria Baffetto", "Legendary.", "Queues are long but worth it.", "", IMG_PIZZA, LatLng(41.9001, 12.4702), 4.3, "food", "€€ • Pizza", "18:00-01:00"),
        Place("Pizzarium Bonci", "By the cut.", "Bonci's original spot.", "", IMG_PIZZA, LatLng(41.9077, 12.4468), 4.8, "food", "€€ • Pizza", "11:00-22:00"),
        Place("Moma Pizza", "Testaccio local.", "Very crispy Roman style.", "", IMG_PIZZA, LatLng(41.8785, 12.4782), 4.5, "food", "€ • Pizza", "19:00-00:00"),
        Place("Zizzi Pizza", "Vatican area.", "Great variety of toppings.", "", IMG_PIZZA, LatLng(41.9012, 12.4562), 4.7, "food", "€ • Pizza", "11:00-21:00"),
        Place("L'Angolo Divino", "Wine & Pasta.", "Near Campo de' Fiori.", "", IMG_PASTA, LatLng(41.8942, 12.4728), 4.6, "food", "€€ • Pasta", "11:30-23:00"),
        Place("Osteria del Pegno", "Hidden Gem.", "Best Carbonara near Navona.", "", IMG_PASTA, LatLng(41.8995, 12.4712), 4.7, "food", "€€ • Pasta", "12:30-23:00"),
        Place("Salumeria Roscioli", "World Famous.", "Reservation mandatory.", "", IMG_PASTA, LatLng(41.8936, 12.4735), 4.8, "food", "€€€ • Pasta", "12:30-00:00"),
        Place("Old Bridge Pasta", "Vatican Takeaway.", "Quick and fresh.", "", IMG_PASTA, LatLng(41.9072, 12.4545), 4.4, "food", "€ • Pasta", "11:00-22:00"),
        Place("Pastasciutta", "Cheap & Fast.", "Steps from Vatican museums.", "", IMG_PASTA, LatLng(41.9062, 12.4542), 4.5, "food", "€ • Pasta", "10:30-21:00"),
        Place("Osteria Maracuja", "Colosseum View.", "Romantic seafood pasta.", "", IMG_PASTA, LatLng(41.8925, 12.4905), 4.3, "food", "€€€ • Pasta", "12:30-23:30"),
        Place("Osteria dell'Angelo", "Prati rustic.", "Local vibe.", "", IMG_PASTA, LatLng(41.9065, 12.4515), 4.5, "food", "€€ • Pasta", "12:30-23:00"),
        Place("Trattoria della Stampa", "Near Trevi.", "Classic menu.", "", IMG_PASTA, LatLng(41.9005, 12.4825), 4.3, "food", "€€ • Pasta", "12:00-23:00"),
        Place("Da Oio a Casa Mia", "Testaccio heart.", "Offal expert.", "", IMG_PASTA, LatLng(41.8775, 12.4765), 4.5, "food", "€€ • Pasta", "12:30-23:00"),
        Place("Rocco Ristorante", "Monti soul.", "Hip yet traditional.", "", IMG_PASTA, LatLng(41.8955, 12.4945), 4.5, "food", "€€ • Pasta", "12:30-23:00"),
        Place("Hostaria da Pietro", "Spanish Steps.", "Family run.", "", IMG_PASTA, LatLng(41.9085, 12.4785), 4.6, "food", "€€ • Pasta", "12:30-23:00"),
        Place("Poldo e Gianna", "Near Pantheon.", "Refined Roman.", "", IMG_PASTA, LatLng(41.9015, 12.4775), 4.4, "food", "€€€ • Pasta", "12:30-23:00"),
        Place("Matricianella", "Fried Artichokes.", "Old school center.", "", IMG_PASTA, LatLng(41.9025, 12.4785), 4.5, "food", "€€€ • Pasta", "12:30-23:00"),
        Place("L'Archeologia", "Appia Antica.", "Historic garden.", "", IMG_PASTA, LatLng(41.8545, 12.5195), 4.6, "food", "€€€€ • Pasta", "12:30-23:00"),
        Place("Da Teo", "Piazza Ponziani.", "Trastevere gem.", "", IMG_PASTA, LatLng(41.8875, 12.4735), 4.5, "food", "€€ • Pasta", "12:30-23:00"),
        Place("Trattoria Da Danilo", "Carbonara legend.", "Esquilino area.", "", IMG_PASTA, LatLng(41.8966, 12.5028), 4.6, "food", "€€€ • Pasta", "19:45-23:00"),
        Place("Trattoria da Enzo", "Trastevere King.", "Legendary carbonara.", "", IMG_PASTA, LatLng(41.8885, 12.4776), 4.8, "food", "€€ • Pasta", "12:15-23:00", recommendation = "Highly recommended by locals"),
        Place("Tonnarello", "Trastevere Icon.", "Pasta in iron pans.", "", IMG_PASTA, LatLng(41.8894, 12.4691), 4.7, "food", "€€ • Pasta", "11:30-23:30", recommendation = "Highly recommended by locals"),
        Place("Felice a Testaccio", "Cacio e Pepe.", "Mixed at table.", "", IMG_PASTA, LatLng(41.8798, 12.4765), 4.6, "food", "€€€ • Pasta", "12:30-23:30"),
        Place("Roscioli", "Deli & Pasta.", "Top carbonara.", "", IMG_PASTA, LatLng(41.8936, 12.4735), 4.6, "food", "€€€ • Pasta", "12:30-00:00"),
        Place("Osteria da Fortunata", "Handmade.", "Watch them roll dough.", "", IMG_PASTA, LatLng(41.8955, 12.4722), 4.4, "food", "€€ • Pasta", "12:00-01:00"),
        Place("Flavio Velavevodetto", "Inside hill.", "Built into pottery mountain.", "", IMG_PASTA, LatLng(41.8767, 12.4753), 4.5, "food", "€€ • Pasta", "12:30-23:00"),
        Place("Roma Sparita", "Cheese bowl.", "Bourdain's favorite.", "", IMG_PASTA, LatLng(41.8892, 12.4735), 4.5, "food", "€€ • Pasta", "12:30-23:00"),
        Place("Cantina e Cucina", "Vibrant.", "Fun near Navona.", "", IMG_PASTA, LatLng(41.8988, 12.4715), 4.6, "food", "€€ • Pasta", "11:00-01:00", recommendation = "Highly recommended by locals"),
        Place("Da Danilo", "Creamy King.", "Elite carbonara.", "", IMG_PASTA, LatLng(41.8966, 12.5028), 4.6, "food", "€€€ • Pasta", "19:45-23:00"),
        Place("Hostaria Romana", "Graffiti Walls.", "Massive portions.", "", IMG_PASTA, LatLng(41.9030, 12.4880), 4.3, "food", "€€ • Pasta", "12:30-23:00"),
        Place("Pasta e Vino", "Choice of Shape.", "Fresh in Trastevere.", "", IMG_PASTA, LatLng(41.8899, 12.4688), 4.4, "food", "€€ • Pasta", "12:00-23:00"),
        Place("L'Arcangelo", "Gourmet Prati.", "High-end carbonara.", "", IMG_PASTA, LatLng(41.9055, 12.4655), 4.5, "food", "€€€ • Pasta", "19:00-23:00"),
        Place("Pierluigi", "Seafood elite.", "Celebrity spot.", "", IMG_PASTA, LatLng(41.8946, 12.4692), 4.5, "food", "€€€€ • Pasta", "12:00-00:00"),
        Place("Trattoria Monti", "Marche Roots.", "Giant egg tortello.", "", IMG_PASTA, LatLng(41.8965, 12.5012), 4.7, "food", "€€€ • Pasta", "19:30-22:30"),
        Place("Osteria Bonelli", "Authentic Local.", "Far but worth it.", "", IMG_PASTA, LatLng(41.8785, 12.5385), 4.7, "food", "€ • Pasta", "12:30-23:00"),
        Place("Pizzarium Bonci", "Pizza Lab.", "Gabriele Bonci.", "", IMG_PIZZA, LatLng(41.9077, 12.4468), 4.7, "food", "€€ • Pizza", "11:00-22:00", recommendation = "Highly recommended by locals"),
        Place("Pizzeria Da Remo", "Thin Roman.", "Testaccio classic.", "", IMG_PIZZA, LatLng(41.8797, 12.4779), 4.6, "food", "€ • Pizza", "18:30-00:00"),
        Place("Emma Pizza", "Gourmet.", "Roscioli dough.", "", IMG_PIZZA, LatLng(41.8948, 12.4746), 4.5, "food", "€€ • Pizza", "12:30-23:30", recommendation = "Highly recommended by locals"),
        Place("Seu Pizza Illuminati", "Modern Napoli.", "Award winning.", "", IMG_PIZZA, LatLng(41.8824, 12.4705), 4.7, "food", "€€€ • Pizza", "19:00-23:30"),
        Place("Ai Marmi", "The Slab.", "Marble tables.", "", IMG_PIZZA, LatLng(41.8888, 12.4755), 4.4, "food", "€ • Pizza", "18:30-02:00"),
        Place("L'Elementare", "Craft Vibe.", "Trastevere beer & pizza.", "", IMG_PIZZA, LatLng(41.8885, 12.4705), 4.6, "food", "€€ • Pizza", "12:00-01:00"),
        Place("Pinsere", "Original Pinsa.", "Ancient oval style.", "", IMG_PIZZA, LatLng(41.9078, 12.4934), 4.8, "food", "€ • Pizza", "09:00-21:00"),
        Place("180g Pizzeria", "Super Thin.", "Reservation only.", "", IMG_PIZZA, LatLng(41.8623, 12.5689), 4.8, "food", "€€ • Pizza", "19:30-23:30"),
        Place("Sbanco", "Beer & Fried.", "Appia gem.", "", IMG_PIZZA, LatLng(41.8745, 12.5155), 4.5, "food", "€€ • Pizza", "19:00-00:00"),
        Place("Pizzeria Ostiense", "Local Soul.", "Industrial vibe.", "", IMG_PIZZA, LatLng(41.8715, 12.4825), 4.5, "food", "€ • Pizza", "18:30-23:30"),
        Place("La Gatta Mangiona", "Dough Masters.", "Experimental.", "", IMG_PIZZA, LatLng(41.8705, 12.4505), 4.5, "food", "€€ • Pizza", "19:00-23:30"),
        Place("Sforno", "Cacio e Pepe Pizza.", "Thick airy crust.", "", IMG_PIZZA, LatLng(41.8596, 12.5539), 4.6, "food", "€€ • Pizza", "19:30-23:30"),
        // ── HIDDEN GEM RESTAURANTS ──
        Place("Trattoria Da Cesare al Casaletto", "Legendary Local.", "The most authentic cacio e pepe in Rome, hidden in the residential Monteverde neighborhood. Zero tourists, only Romans who've been coming for decades. Their amatriciana is poetry. Reservation essential — they don't care about Instagram.", "", IMG_PASTA, LatLng(41.8718, 12.4512), 4.8, "food", "€€ • Pasta", "12:30-15:00, 19:30-23:00", recommendation = "Hidden Gem"),
        Place("Osteria Fernanda", "Michelin Secret.", "Tucked in a quiet Trastevere alley, this tiny 30-seat osteria serves modern Roman cuisine that earned a Michelin star. Chef Davide Del Duca reinvents carbonara with egg yolk cream and guanciale dust. Pure genius.", "", IMG_PASTA, LatLng(41.8845, 12.4655), 4.9, "food", "€€€ • Pasta", "19:30-23:00", recommendation = "Hidden Gem"),

        // === GELATO (food) (50) ===\
        Place("Gelateria del Viale", "Smallest Shop.", "Tiny spot in Trastevere with huge flavors.", "", IMG_GELATO, LatLng(41.8895, 12.4705), 4.8, "food", "€ • Gelato", "11:00-22:00"),
        Place("Gunther Gelato", "Spiced Art.", "Using South Tyrolean milk and unique spices.", "", IMG_GELATO, LatLng(41.8965, 12.4725), 4.7, "food", "€€ • Gelato", "11:00-23:30"),
        Place("La Strega Nocciola", "Nut Master.", "Best hazelnut and chocolate varieties.", "", IMG_GELATO, LatLng(41.9055, 12.4815), 4.8, "food", "€€ • Gelato", "11:00-22:00"),
        Place("Gelato San Lorenzo", "The Lab.", "No artificial ingredients, experimental flavors.", "", IMG_GELATO, LatLng(41.8985, 12.5125), 4.6, "food", "€€ • Gelato", "11:00-00:00"),
        Place("Said 1923", "Chocolate Factory.", "Legendary chocolate-focused gelato in San Lorenzo.", "", IMG_GELATO, LatLng(41.8955, 12.5115), 4.4, "food", "€€€ • Gelato", "10:00-00:00"),
        Place("Il Gelato di San Crispino", "Gourmet Cup.", "Famous honey and ginger flavors.", "", IMG_GELATO, LatLng(41.9005, 12.4842), 4.5, "food", "€€ • Gelato", "11:00-00:00"),
        Place("Neve di Latte", "Milk Purity.", "Focus on the highest quality milk and eggs.", "", IMG_GELATO, LatLng(41.9258, 12.4645), 4.7, "food", "€€€ • Gelato", "11:00-23:00"),
        Place("Punto Gelato", "Gunther's Lab.", "Focus on seasonal fruits and spices.", "", IMG_GELATO, LatLng(41.8965, 12.4725), 4.7, "food", "€€ • Gelato", "11:00-23:30"),
        Place("Grezzo Raw Chocolate", "Vegan Organic.", "Raw, plant-based chocolate treats.", "", IMG_GELATO, LatLng(41.8955, 12.4915), 4.5, "food", "€€€ • Gelato", "11:00-22:00"),
        Place("Gelateria La Romana", "Chocolate Tap.", "Liquid chocolate at the bottom of the cone.", "", IMG_GELATO, LatLng(41.9095, 12.4905), 4.6, "food", "€€ • Gelato", "12:00-00:00"),
        Place("Gelateria Dei Gracchi", "Nut Paradise.", "The best toasted pistachio in Rome.", "", IMG_GELATO, LatLng(41.9075, 12.4615), 4.7, "food", "€€ • Gelato", "12:00-00:00"),
        Place("Fatamorgana Monti", "Creative Botanical.", "Infusions like tobacco or basil.", "", IMG_GELATO, LatLng(41.8948, 12.4895), 4.7, "food", "€€ • Gelato", "13:00-00:00"),
        Place("Pasticceria Regoli", "Maritozzo King.", "Iconic cream-filled buns since 1916.", "", IMG_COFFEE, LatLng(41.8965, 12.5035), 4.8, "food", "€€ • Dessert", "06:30-19:20"),
        Place("Caffè Giolitti", "Historic Ice.", "Since 1900, a landmark for Rome.", "", IMG_GELATO, LatLng(41.9011, 12.4773), 4.5, "food", "€€ • Gelato", "07:00-01:00"),
        Place("Frigidarium", "Shell Dipped.", "Choose dark or white chocolate shell.", "", IMG_GELATO, LatLng(41.8978, 12.4716), 4.8, "food", "€ • Gelato", "10:30-01:00", recommendation = "Highly recommended by locals"),
        Place("Two Sizes", "Tiramisu Go.", "Perfect takeaway tiramisu near Navona.", "", IMG_GELATO, LatLng(41.8985, 12.4725), 4.8, "food", "€ • Dessert", "11:00-23:00", recommendation = "Highly recommended by locals"),
        Place("Come il Latte", "Super Creamy.", "Incredibly rich texture, many toppings.", "", IMG_GELATO, LatLng(41.9068, 12.4965), 4.8, "food", "€ • Gelato", "11:00-23:00"),
        Place("Pompi Tiramisù", "King of Cakes.", "Rome's most famous tiramisu shop.", "", IMG_GELATO, LatLng(41.9062, 12.4815), 4.4, "food", "€ • Dessert", "10:00-22:00"),
        Place("Gelatist", "Modern Scoops.", "High quality and fast in the center.", "", IMG_GELATO, LatLng(41.9015, 12.4825), 4.1, "food", "€ • Gelato", "10:00-00:00"),
        Place("Fiocco di Neve", "Zabaglione Master.", "Traditional flavors near the Pantheon.", "", IMG_GELATO, LatLng(41.8992, 12.4762), 4.3, "food", "€ • Gelato", "10:00-23:00"),
        Place("Gelateria Valentino", "Trevi Gem.", "Highest quality near the fountain.", "", IMG_GELATO, LatLng(41.9015, 12.4845), 4.9, "food", "€ • Gelato", "10:00-23:00"),
        Place("Point G", "Gourmet Style.", "Innovative creams and crunchy nuts.", "", IMG_GELATO, LatLng(41.8935, 12.4905), 4.7, "food", "€€ • Gelato", "12:00-00:00"),
        Place("Lemongrass Prati", "Local Favorite.", "Intense fruit flavors and sorbets.", "", IMG_GELATO, LatLng(41.9075, 12.4555), 4.6, "food", "€ • Gelato", "11:00-00:00"),
        Place("Gelateria I Caruso", "Old School Prati.", "Known for the best whipped cream (panna).", "", IMG_GELATO, LatLng(41.9055, 12.4635), 4.6, "food", "€ • Gelato", "11:00-00:00"),
        Place("Gelato San Crispino", "Airport Stop.", "Last chance for honey gelato in Terminal 1.", "", IMG_GELATO, LatLng(41.7955, 12.2515), 4.3, "food", "€€ • Gelato", "06:00-21:00"),
        Place("Pasticceria Bompiani", "Art Desserts.", "Cakes that look like modern art sculptures.", "", IMG_GELATO, LatLng(41.8655, 12.4985), 4.8, "food", "€€€ • Dessert", "07:00-20:00"),
        Place("La Romana", "Melted Choco.", "Liquid gold tap.", "", IMG_GELATO, LatLng(41.9084, 12.5020), 4.7, "food", "€ • Gelato", "11:00-00:00"),
        Place("Come il Latte", "Rich texture.", "Super creamy.", "", IMG_GELATO, LatLng(41.9068, 12.4965), 4.8, "food", "€ • Gelato", "11:00-23:00"),
        Place("Stefano Ferrara", "The Lab.", "Contemporary style.", "", IMG_GELATO, LatLng(41.8615, 12.4605), 4.8, "food", "€ • Gelato", "12:00-22:00"),
        Place("Hedera", "Borgo Pio.", "Vatican greenery.", "", IMG_GELATO, LatLng(41.9042, 12.4602), 4.5, "food", "€€ • Gelato", "11:00-22:00"),
        Place("San Crispino", "Honey flavor.", "Trevi area.", "", IMG_GELATO, LatLng(41.9005, 12.4842), 4.5, "food", "€€ • Gelato", "11:00-00:00"),
        Place("Punto Gelato", "Gunther's art.", "Near Navona.", "", IMG_GELATO, LatLng(41.8965, 12.4725), 4.7, "food", "€€ • Gelato", "11:00-23:30"),
        Place("Giolitti", "Oldest Temple.", "Since 1900.", "", IMG_GELATO, LatLng(41.9011, 12.4773), 4.5, "food", "€€ • Gelato", "07:00-01:00"),
        Place("Gelateria Teatro", "Artisan Lab.", "Natural flavors.", "", IMG_GELATO, LatLng(41.8998, 12.4692), 4.7, "food", "€€ • Gelato", "11:00-23:00"),
        Place("Otaleg", "Gelato Lab.", "Savory options.", "", IMG_GELATO, LatLng(41.8878, 12.4705), 4.8, "food", "€€ • Gelato", "12:00-23:00"),
        // === COFFEE (coffee) (50) ===
        Place("Wisdomless Club", "Cocktails & Ink.", "A tattoo studio and high-end coffee bar.", "", IMG_COFFEE, LatLng(41.8985, 12.4725), 4.6, "coffee", "€€€ • Coffee", "18:00-02:00"),
        Place("Sciascia Caffè 1919", "Chocolate Espresso.", "Coffee served in chocolate-coated cups.", "", IMG_COFFEE, LatLng(41.9082, 12.4619), 4.6, "coffee", "€ • Coffee", "07:00-21:00"),
        Place("Caffè Peru", "Historic Local.", "Classic morning stop since 1933.", "", IMG_COFFEE, LatLng(41.8968, 12.4725), 4.5, "coffee", "€ • Coffee", "07:00-21:00"),
        Place("Barnum Roma", "Nomad Spot.", "Best brunch and specialty coffee near Campo.", "", IMG_COFFEE, LatLng(41.8955, 12.4715), 4.5, "coffee", "€€ • Coffee", "08:00-15:30"),
        Place("Roscioli Caffè", "Elite Pastry.", "Tiny but high-end coffee and maritozzi.", "", IMG_COFFEE, LatLng(41.8940, 12.4738), 4.7, "coffee", "€ • Coffee", "07:00-18:00"),
        Place("Canova Tadolini", "Sculptor's Studio.", "Drink among massive marble monuments.", "", IMG_COFFEE, LatLng(41.9075, 12.4795), 4.5, "coffee", "€€ • Coffee", "08:00-20:00"),
        Place("Pergamino Caffè", "Specialty Vatican.", "V60, Aeropress and elite beans.", "", IMG_COFFEE, LatLng(41.9025, 12.4555), 4.6, "coffee", "€€ • Coffee", "08:00-19:00"),
        Place("Marigold", "Artisan Bakery.", "Sourdough and Nordic style specialty coffee.", "", IMG_COFFEE, LatLng(41.8715, 12.4825), 4.7, "coffee", "€€ • Coffee", "09:00-15:00"),
        Place("La Licata", "Monti Morning.", "The heart of breakfast in the Monti district.", "", IMG_COFFEE, LatLng(41.8935, 12.4905), 4.5, "coffee", "€ • Coffee", "06:00-21:00"),
        Place("Tram Depot", "Vintage Kiosk.", "Garden coffee served in a green tram car.", "", IMG_COFFEE, LatLng(41.8755, 12.4835), 4.6, "coffee", "€ • Coffee", "08:00-01:00"),
        Place("Caffè de la Paix", "Chic Prati.", "Elegant corner for a high-end morning shot.", "", IMG_COFFEE, LatLng(41.9005, 12.4715), 4.2, "coffee", "€€ • Coffee", "08:00-21:00"),
        Place("Ex Circus", "Relaxed Work.", "Couches and international vibe near Navona.", "", IMG_COFFEE, LatLng(41.8998, 12.4715), 4.4, "coffee", "€ • Coffee", "10:00-20:00"),
        Place("Coromandel", "Vintage Brunch.", "Most beautiful interior for a long breakfast.", "", IMG_COFFEE, LatLng(41.8998, 12.4708), 4.4, "coffee", "€€€ • Coffee", "08:30-15:00"),
        Place("Caffè Valentini", "Traditional Family.", "Unchanged since the 1950s, pure local vibe.", "", IMG_COFFEE, LatLng(41.8915, 12.4805), 4.3, "coffee", "€ • Coffee", "06:30-19:00"),
        Place("Babingtons", "Tea & History.", "Elegant tea room at the Spanish Steps.", "", IMG_COFFEE, LatLng(41.9058, 12.4821), 4.4, "coffee", "€€€ • Coffee", "10:00-21:00"),
        Place("L'Emporio alla Pace", "Bookish Vibe.", "Quiet spot for coffee and reading near Navona.", "", IMG_COFFEE, LatLng(41.8995, 12.4705), 4.5, "coffee", "€€ • Coffee", "09:00-20:00"),
        Place("Caffè Camerino", "Argentine Shot.", "Fastest espresso in the city, high quality.", "", IMG_COFFEE, LatLng(41.8952, 12.4758), 4.4, "coffee", "€ • Coffee", "07:00-20:00"),
        Place("Homebaked", "American Corner.", "Pancakes and filter coffee for a change.", "", IMG_COFFEE, LatLng(41.9062, 12.4545), 4.4, "coffee", "€€ • Coffee", "08:00-16:00"),
        Place("Sant'Eustachio Shop", "Gift Shop.", "Buy the famous yellow cans to go.", "", IMG_COFFEE, LatLng(41.8984, 12.4755), 4.4, "coffee", "€€ • Coffee", "09:30-20:00"),
        Place("Tazza d'Oro Shop", "Roastery Store.", "The freshest beans near the Pantheon.", "", IMG_COFFEE, LatLng(41.8998, 12.4765), 4.6, "coffee", "€€ • Coffee", "08:00-20:00"),
        Place("Caffè Castroni", "Global Goods.", "Coffee roastery and gourmet foods store.", "", IMG_COFFEE, LatLng(41.9038, 12.4617), 4.6, "coffee", "€ • Coffee", "08:30-20:00"),
        Place("Antico Caffè Greco", "Historic Art.", "Rome's oldest bar since 1760.", "", IMG_COFFEE, LatLng(41.9056, 12.4818), 4.3, "coffee", "€€€€ • Coffee", "09:00-21:00"),
        Place("Faro Coffee", "Specialty Master.", "Third wave coffee and artisan pastries.", "", IMG_COFFEE, LatLng(41.9105, 12.4988), 4.8, "coffee", "€€ • Coffee", "08:00-16:00"),
        Place("Barnum Roma", "Nomad hub.", "Brunch expert.", "", IMG_COFFEE, LatLng(41.8955, 12.4715), 4.5, "coffee", "€€ • Brunch", "08:00-15:30"),
        Place("Coromandel", "Beautiful decor.", "Fancy breakfast.", "", IMG_COFFEE, LatLng(41.8998, 12.4708), 4.4, "coffee", "€€€ • Brunch", "08:30-15:00"),
        Place("Roscioli Caffè", "Pastry perfection.", "Standing bar.", "", IMG_COFFEE, LatLng(41.8940, 12.4738), 4.7, "coffee", "€ • Coffee", "07:00-18:00"),
        Place("Pergamino Caffè", "Specialty beans.", "Vatican side.", "", IMG_COFFEE, LatLng(41.9025, 12.4555), 4.6, "coffee", "€€ • Coffee", "08:00-19:00"),
        Place("Castroni Ottaviano", "Gourmet shop.", "Elite espresso.", "", IMG_COFFEE, LatLng(41.9038, 12.4617), 4.6, "coffee", "€ • Coffee", "08:30-20:00"),
        Place("Sant'Eustachio", "Secret Foam.", "Iconic bar.", "", IMG_COFFEE, LatLng(41.8983, 12.4754), 4.4, "coffee", "€ • Coffee", "07:30-01:00"),
        Place("Tazza d'Oro", "Granita King.", "Pantheon classic.", "", IMG_COFFEE, LatLng(41.8996, 12.4768), 4.7, "coffee", "€ • Coffee", "07:00-20:00"),
        Place("Faro Coffee", "Specialty.", "Modern brew.", "", IMG_COFFEE, LatLng(41.9105, 12.4988), 4.8, "coffee", "€€ • Coffee", "08:00-16:00"),
        Place("Sciascia 1919", "Choco Cup.", "Prati legend.", "", IMG_COFFEE, LatLng(41.9082, 12.4619), 4.6, "coffee", "€ • Coffee", "07:00-21:00"),
        Place("Antico Caffè Greco", "Elite luxury.", "Since 1760.", "", IMG_COFFEE, LatLng(41.9056, 12.4818), 4.3, "coffee", "€€€€ • Coffee", "09:00-21:00"),
        Place("Caffè Peru", "Local Soul.", "Since 1933.", "", IMG_COFFEE, LatLng(41.8968, 12.4725), 4.5, "coffee", "€ • Coffee", "07:00-21:00"),

        // === APERITIVO (aperitivo) (40) ===
        Place("Jerry Thomas", "Secret Bar.", "Password required, world-class mixology.", "", IMG_APERITIVO, LatLng(41.8968, 12.4718), 4.8, "aperitivo", "€€€ • Aperitivo", "21:00-04:00"),
        Place("The Court", "Arena View.", "Luxury cocktails right in front of the Colosseum.", "", IMG_APERITIVO, LatLng(41.8905, 12.4935), 4.6, "aperitivo", "€€€€ • Aperitivo", "18:00-01:00"),
        Place("Blackmarket Hall", "Jazz & Gin.", "Cozy vintage rooms in the Monti district.", "", IMG_APERITIVO, LatLng(41.8953, 12.4912), 4.6, "aperitivo", "€€ • Aperitivo", "18:00-02:00"),
        Place("Argot", "Secret Piano Bar.", "Intimate basement near Campo de' Fiori.", "", IMG_APERITIVO, LatLng(41.8965, 12.4725), 4.7, "aperitivo", "€€€ • Aperitivo", "20:00-03:00"),
        Place("Enoteca il Terzo", "Natural Wine.", "Sophisticated wine bar in quiet Trastevere.", "", IMG_APERITIVO, LatLng(41.8885, 12.4725), 4.6, "aperitivo", "€€ • Aperitivo", "17:00-01:00"),
        Place("Bukowski's Bar", "Poetic Lounge.", "Artistic vibe near Castel Sant'Angelo.", "", IMG_APERITIVO, LatLng(41.9035, 12.4655), 4.5, "aperitivo", "€€ • Aperitivo", "18:00-02:00"),
        Place("Sacripante", "Pharmacy Art.", "Cocktails inside a former 19th-century shop.", "", IMG_APERITIVO, LatLng(41.8945, 12.4925), 4.6, "aperitivo", "€€€ • Aperitivo", "18:00-02:00"),
        Place("Freni e Frizioni", "Trastevere Icon.", "Best social vibe and big buffet.", "", IMG_APERITIVO, LatLng(41.8918, 12.4693), 4.6, "aperitivo", "€€ • Aperitivo", "18:30-02:00"),
        Place("Drink Kong", "Neon Future.", "Top 50 bars in the world list member.", "", IMG_APERITIVO, LatLng(41.8952, 12.4925), 4.7, "aperitivo", "€€€ • Aperitivo", "18:30-02:00"),
        Place("Salotto 42", "Temple Side.", "Chic lounge by the Temple of Hadrian.", "", IMG_APERITIVO, LatLng(41.8997, 12.4789), 4.4, "aperitivo", "€€€ • Aperitivo", "10:30-02:00"),
        Place("Bar San Calisto", "Old School Soul.", "Cheap beer and local legends.", "", IMG_APERITIVO, LatLng(41.8883, 12.4704), 4.5, "aperitivo", "€ • Aperitivo", "06:00-02:00"),
        Place("Terrazza Borromini", "Navona View.", "Elite rooftop spritz experience.", "", IMG_APERITIVO, LatLng(41.8988, 12.4732), 4.3, "aperitivo", "€€€€ • Aperitivo", "12:00-00:00"),
        Place("Mimì e Cocò", "Navona Wine.", "Friendly corner with great platters.", "", IMG_APERITIVO, LatLng(41.8981, 12.4718), 4.5, "aperitivo", "€€ • Aperitivo", "10:00-01:00"),
        Place("Ma Che Siete", "Beer Temple.", "The best craft selection in Trastevere.", "", IMG_APERITIVO, LatLng(41.8895, 12.4695), 4.7, "aperitivo", "€€ • Aperitivo", "11:00-02:00"),
        Place("Hotel de Russie", "Stravinskij Bar.", "Most elite garden bar.", "", IMG_APERITIVO, LatLng(41.9108, 12.4772), 4.7, "aperitivo", "€€€€ • Luxury", "11:00-01:00"),
        Place("Blackmarket Hall", "Monti Jazz.", "Great food and cocktails.", "", IMG_APERITIVO, LatLng(41.8962, 12.4935), 4.6, "aperitivo", "€€ • Music", "18:00-02:00"),
        Place("Coropuna", "Tiki Bar.", "Fusion drinks and sushi.", "", IMG_APERITIVO, LatLng(41.9215, 12.4715), 4.4, "aperitivo", "€€€ • Tiki", "19:30-02:00"),
        Place("Ai Tre Scalini", "Bottiglieria.", "Classic Monti wine bar.", "", IMG_APERITIVO, LatLng(41.8955, 12.4908), 4.5, "aperitivo", "€€ • Wine", "12:00-01:00"),
        Place("Ma Che Siete Venuti a Fa", "Craft Beer.", "Trastevere icon.", "", IMG_APERITIVO, LatLng(41.8895, 12.4695), 4.7, "aperitivo", "€€ • Craft Beer", "11:00-02:00"),
        Place("Mimì e Cocò", "Wine bar vibe.", "Navona area.", "", IMG_APERITIVO, LatLng(41.8981, 12.4718), 4.5, "aperitivo", "€€ • Wine", "10:00-01:00"),
        Place("Terrazza Borromini", "High luxury.", "Navona rooftop.", "", IMG_APERITIVO, LatLng(41.8988, 12.4732), 4.3, "aperitivo", "€€€€ • Rooftop", "12:00-00:00"),
        Place("Bukowski's Bar", "Artistic lounge.", "Near Castel.", "", IMG_APERITIVO, LatLng(41.9035, 12.4655), 4.5, "aperitivo", "€€ • Drinks", "18:00-02:00"),
        Place("Sacripante Gallery", "Art & Gin.", "Monti pharmacy.", "", IMG_APERITIVO, LatLng(41.8945, 12.4925), 4.6, "aperitivo", "€€€ • Cocktails", "18:00-02:00"),
        Place("Litro", "Natural Wine.", "Gianicolo hill.", "", IMG_APERITIVO, LatLng(41.8875, 12.4615), 4.6, "aperitivo", "€€ • Wine", "18:00-00:00"),
        Place("The Barber Shop", "Speakeasy.", "Underground club.", "", IMG_APERITIVO, LatLng(41.8925, 12.4925), 4.7, "aperitivo", "€€€ • Speakeasy", "21:00-04:00"),
        Place("Freni e Frizioni", "Garage Buffet.", "Cool vibes.", "", IMG_APERITIVO, LatLng(41.8918, 12.4693), 4.6, "aperitivo", "€€ • Aperitivo", "18:30-02:00"),
        Place("Drink Kong", "Neon Future.", "Top 50 Bar.", "", IMG_APERITIVO, LatLng(41.8952, 12.4925), 4.7, "aperitivo", "€€€ • Cocktails", "18:30-02:00"),
        Place("Salotto 42", "Hadrian View.", "Chic lounge.", "", IMG_APERITIVO, LatLng(41.8997, 12.4789), 4.4, "aperitivo", "€€€ • Aperitivo", "10:30-02:00"),
        Place("Bar San Calisto", "Real Soul.", "Cheap beer.", "", IMG_APERITIVO, LatLng(41.8883, 12.4704), 4.5, "aperitivo", "€ • Aperitivo", "06:00-02:00"),
        Place("The Court", "Colosseum Front.", "Luxury view.", "", IMG_APERITIVO, LatLng(41.8905, 12.4935), 4.6, "aperitivo", "€€€€ • Rooftop", "18:00-01:00"),
        // ── HIDDEN GEM DRINKS ──
        Place("Bartoló", "Hidden Wine Cave.", "Underground natural wine bar in Ostiense where Roman sommeliers drink after their shifts. No sign outside — just a small door next to a vintage shop. Ask for their orange wine flights and house-cured meats. Pure local magic.", "", IMG_APERITIVO, LatLng(41.8695, 12.4785), 4.8, "aperitivo", "€€ • Natural Wine", "18:00-01:00", recommendation = "Hidden Gem"),
        Place("Il Barretto", "Secret Garden Bar.", "Tiny courtyard cocktail bar hidden behind an unmarked door on Via del Garofano. Only 20 seats surrounded by jasmine and fairy lights. Their Negroni Sbagliato with prosecco from Veneto is legendary among Roman bartenders.", "", IMG_APERITIVO, LatLng(41.9065, 12.4795), 4.7, "aperitivo", "€€€ • Cocktails", "19:00-02:00", recommendation = "Hidden Gem"),

        // === NIGHTLIFE CLUBS & BARS (nightlife) ===
        Place("Goa Club", "Techno Temple.", "Rome's legendary electronic music club in Ostiense.", "", IMG_NIGHTLIFE, LatLng(41.8677, 12.4811), 4.5, "nightlife", "€€€ • Club", "23:00-05:00"),
        Place("Lanificio 159", "Industrial Rave.", "Massive warehouse club with live music and DJs in Pietralata.", "", IMG_NIGHTLIFE, LatLng(41.9195, 12.5385), 4.4, "nightlife", "€€ • Club", "22:00-04:00"),
        Place("Alibi Club", "Testaccio Icon.", "Gay-friendly club, open to everyone, great rooftop.", "", IMG_NIGHTLIFE, LatLng(41.8765, 12.4755), 4.3, "nightlife", "€€ • Club", "23:00-05:00"),
        Place("Vinile", "Retro Party.", "60s/70s/80s vibes with live music and dinner-show.", "", IMG_NIGHTLIFE, LatLng(41.8695, 12.4795), 4.6, "nightlife", "€€€ • Club", "20:00-04:00"),
        Place("Qube Club", "Mega Venue.", "Multi-floor electronic music megaclub near Tiburtina.", "", IMG_NIGHTLIFE, LatLng(41.9108, 12.5288), 4.2, "nightlife", "€€ • Club", "23:00-05:00"),
        Place("Rashomon", "Underground.", "Alternative music and experimental DJ sets in Pigneto.", "", IMG_NIGHTLIFE, LatLng(41.8892, 12.5245), 4.5, "nightlife", "€€ • Club", "22:00-04:00"),
        Place("Circolo degli Illuminati", "Elite Club.", "Top international DJs, premium techno in Ostiense.", "", IMG_NIGHTLIFE, LatLng(41.8682, 12.4818), 4.4, "nightlife", "€€€ • Club", "23:00-06:00"),
        Place("Salotto 42", "Chic Lounge.", "Design cocktail bar next to the Temple of Hadrian.", "", IMG_NIGHTLIFE, LatLng(41.8997, 12.4789), 4.4, "nightlife", "€€€ • Lounge", "10:30-02:00"),
        Place("Ice Club Roma", "Frozen Bar.", "Everything is made of ice — walls, glasses, bar!", "", IMG_NIGHTLIFE, LatLng(41.9028, 12.4780), 4.2, "nightlife", "€€€ • Experience", "17:00-02:00"),
        Place("Piper Club", "Historic Venue.", "Open since 1965 — Rome's oldest nightclub still running.", "", IMG_NIGHTLIFE, LatLng(41.9255, 12.4935), 4.3, "nightlife", "€€€ • Club", "23:00-05:00"),
        Place("Shari Vari Playhouse", "Cocktail Club.", "Live DJ sets and creative cocktails near Via Veneto.", "", IMG_NIGHTLIFE, LatLng(41.9045, 12.4868), 4.5, "nightlife", "€€€ • Club", "22:00-04:00"),
        Place("Santos Social Club", "Rooftop Party.", "Open-air dance floor with stunning Rome panorama.", "", IMG_NIGHTLIFE, LatLng(41.8972, 12.5145), 4.5, "nightlife", "€€€ • Rooftop", "21:00-04:00"),

        // === SECRET QUEST REWARD PLACES (secret) ===
        // Only visible after completing all quests — 2 hidden gem sights + 2 amazing food spots
        Place("Aventine Keyhole", "🔑 Secret Gem.", "Peer through the Knights of Malta keyhole for a perfectly framed view of St. Peter's dome through a garden tunnel.", "", "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0d/Piazza_dei_Cavalieri_di_Malta.jpg/600px-Piazza_dei_Cavalieri_di_Malta.jpg", LatLng(41.8826, 12.4796), 4.9, "secret", "Free", "24h", recommendation = "🏆 Quest Reward"),
        Place("Quartiere Coppedè", "🔑 Secret Gem.", "A fairy-tale Art Nouveau neighborhood hidden in plain sight. Unreal arches, frescoes, and a spider chandelier!", "", "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a4/Quartiere_Coppede_Roma.jpg/600px-Quartiere_Coppede_Roma.jpg", LatLng(41.9198, 12.5065), 4.8, "secret", "Free", "24h", recommendation = "🏆 Quest Reward"),
        Place("Supplizio", "🍴 Secret Eats.", "The world's best supplì (Roman fried rice balls) in a tiny hidden shop near Navona. Life-changing street food!", "", "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=400&h=300&fit=crop", LatLng(41.8972, 12.4725), 4.9, "secret", "€ • Street Food", "12:00-22:00", recommendation = "🏆 Quest Reward"),
        Place("Roscioli Bakery", "🍴 Secret Eats.", "Not the famous restaurant — the secret bakery next door! Best pizza bianca and pastries in Rome at dawn.", "", "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=400&h=300&fit=crop", LatLng(41.8935, 12.4738), 4.8, "secret", "€ • Bakery", "06:00-20:00", recommendation = "🏆 Quest Reward"),

        // === WATER & WC (70) ===
        Place("WC: Barberini Metro", "Underground.", "Inside the Metro A entrance hall.", "", IMG_WC, LatLng(41.9035, 12.4885), 3.0, "wc", "1.00€", "06:00-23:00"),
        Place("WC: Lepanto", "Prati Shop Area.", "Next to the Metro exit on Via Marcantonio.", "", IMG_WC, LatLng(41.9115, 12.4665), 3.0, "wc", "1.00€", "06:00-23:00"),
        Place("WC: Castel S. Angelo", "Park Perimeter.", "Facility inside the castle gardens.", "", IMG_WC, LatLng(41.9045, 12.4685), 3.0, "wc", "1.00€", "09:00-19:00"),
        Place("WC: Villa Pamphili", "Info Point.", "Main building in the center of the park.", "", IMG_WC, LatLng(41.8875, 12.4535), 3.0, "wc", "0.50€", "09:00-18:00"),
        Place("WC: Tiburtina", "Main Station.", "Modern automatic toilets upstairs.", "", IMG_WC, LatLng(41.9105, 12.5305), 3.0, "wc", "1.00€", "06:00-23:00"),
        Place("WC: Pigneto Metro", "Metro C Hall.", "Basement level of the station.", "", IMG_WC, LatLng(41.8885, 12.5285), 3.0, "wc", "1.00€", "06:00-23:00"),
        Place("WC: Flaminio Gate", "Popolo Entrance.", "Clean unit near the park entry.", "", IMG_WC, LatLng(41.9125, 12.4765), 3.5, "wc", "1.00€", "06:00-23:00"),
        Place("WC: Euroma2 Mall", "Shopping Floors.", "Very clean, located in the food court.", "", IMG_WC, LatLng(41.8055, 12.4665), 4.5, "wc", "Free", "10:00-21:00"),
        Place("WC: Roma Est Mall", "Mall Hub.", "Massive free facilities throughout.", "", IMG_WC, LatLng(41.9155, 12.6655), 4.5, "wc", "Free", "10:00-22:00"),
        Place("WC: Porta di Roma", "IKEA Floor.", "Excellent family-friendly facilities.", "", IMG_WC, LatLng(41.9715, 12.5355), 4.4, "wc", "Free", "10:00-22:00"),
        Place("WC: St. Peter's Square", "Colonnade.", "Underneath the Bernini columns.", "", IMG_WC, LatLng(41.9015, 12.4575), 3.5, "wc", "1.00€", "08:00-18:00"),
        Place("WC: Termini Station", "Underground.", "Downstairs near the Metro B line.", "", IMG_WC, LatLng(41.9010, 12.5014), 3.0, "wc", "1.00€", "06:00-23:00"),
        Place("WC: Colosseum Metro", "Exit Hall.", "Convenient stop right at the arena.", "", IMG_WC, LatLng(41.8912, 12.4935), 3.0, "wc", "1.00€", "09:00-18:00"),
        Place("WC: Piazza Navona", "Center Hub.", "Underground facility in the middle.", "", IMG_WC, LatLng(41.8995, 12.4735), 3.5, "wc", "1.00€", "10:00-20:00"),
        Place("WC: Villa Borghese", "Zoo Area.", "Public unit near the Bioparco entry.", "", IMG_WC, LatLng(41.9135, 12.4915), 2.5, "wc", "0.50€", "09:00-18:00"),
        Place("WC: Trastevere Sisto", "Bridge Corner.", "Next to the fountain at the bridge.", "", IMG_WC, LatLng(41.8905, 12.4705), 3.0, "wc", "1.00€", "10:00-00:00"),
        Place("WC: San Giovanni", "Basilica Side.", "Next to the Holy Stairs building.", "", IMG_WC, LatLng(41.8855, 12.5095), 3.0, "wc", "1.00€", "06:00-23:00"),
        Place("WC: Piramide", "Metro B Side.", "Just outside the turnstiles.", "", IMG_WC, LatLng(41.8755, 12.4815), 2.5, "wc", "1.00€", "06:00-23:00"),
        Place("WC: Cipro Vatican", "Metro Exit.", "Best stop before the Vatican wall.", "", IMG_WC, LatLng(41.9075, 12.4475), 3.0, "wc", "1.00€", "06:00-23:00"),
        Place("WC: Repubblica", "Metro A Hall.", "Clean pay-unit near the station hub.", "", IMG_WC, LatLng(41.9025, 12.4955), 3.0, "wc", "1.00€", "06:00-23:00"),
        Place("WC: Barberini Hub", "Metro Center.", "Central pay-toilet unit.", "", IMG_WC, LatLng(41.9035, 12.4885), 3.0, "wc", "1.00€", "06:00-23:00"),
        Place("WC: Lepanto Shop", "Prati Area.", "Shopping district stop.", "", IMG_WC, LatLng(41.9115, 12.4665), 3.0, "wc", "1.00€", "06:00-23:00"),
        Place("WC: Ostiense Stop", "Train Floor.", "Basement unit near the ticket office.", "", IMG_WC, LatLng(41.8725, 12.4825), 3.0, "wc", "1.00€", "06:00-22:00"),
        Place("WC: Trastevere Sisto", "Bridge Corner.", "Bridge-side hydration.", "", IMG_WC, LatLng(41.8905, 12.4705), 3.0, "wc", "1.00€", "10:00-00:00"),
        Place("WC: Popolo Gate", "Park Entrance.", "Next to the Flaminio station.", "", IMG_WC, LatLng(41.9105, 12.4765), 3.5, "wc", "1.00€", "09:00-20:00"),
        Place("WC: Piramide Hub", "Metro B.", "Pyramid side station stop.", "", IMG_WC, LatLng(41.8755, 12.4815), 2.5, "wc", "1.00€", "06:00-23:00"),
        Place("WC: Cipro Entry", "Museum Side.", "Exit of Metro A.", "", IMG_WC, LatLng(41.9075, 12.4475), 3.0, "wc", "1.00€", "06:00-23:00"),
        Place("WC: Barberini A", "Metro Unit.", "Inside the main ticket hall.", "", IMG_WC, LatLng(41.9035, 12.4885), 3.0, "wc", "1.00€", "06:00-23:00"),
        Place("WC: Repubblica A", "Metro Fountain.", "Below the piazza hub.", "", IMG_WC, LatLng(41.9025, 12.4955), 3.0, "wc", "1.00€", "06:00-23:00"),
        Place("WC: San Giovanni Hub", "Metro A/C.", "Basilica side hub unit.", "", IMG_WC, LatLng(41.8855, 12.5095), 3.0, "wc", "1.00€", "06:00-23:00"),
        Place("WC: Piramide B", "Metro Station.", "Below the white pyramid.", "", IMG_WC, LatLng(41.8755, 12.4815), 2.5, "wc", "1.00€", "06:00-23:00"),
        Place("WC: Cipro Hub", "Vatican Museum.", "Last unit before the walls.", "", IMG_WC, LatLng(41.9075, 12.4475), 3.0, "wc", "1.00€", "06:00-23:00"),
        Place("WC: Lepanto Shop", "Prati Station.", "Prati shopping hub.", "", IMG_WC, LatLng(41.9115, 12.4665), 3.0, "wc", "1.00€", "06:00-23:00"),
        Place("WC: Tiburtina Main", "Main Station.", "Automatic pay toilets.", "", IMG_WC, LatLng(41.9105, 12.5305), 3.0, "wc", "1.00€", "06:00-23:00"),
        Place("WC: San Lorenzo Market", "Public Market.", "Inside the local stalls hall.", "", IMG_WC, LatLng(41.8975, 12.5155), 2.8, "wc", "Free/Tip", "07:00-14:00"),
        Place("WC: Testaccio Market", "New Market.", "Clean, free for market shoppers.", "", IMG_WC, LatLng(41.8755, 12.4745), 4.0, "wc", "Free", "08:00-15:00"),
        Place("WC: Campo de' Fiori", "Underground.", "Corner of the square unit.", "", IMG_WC, LatLng(41.8950, 12.4715), 3.0, "wc", "1.00€", "09:00-20:00"),
        Place("WC: Barberini Metro", "Underground.", "Automatic pay station.", "", IMG_WC, LatLng(41.9035, 12.4885), 3.0, "wc", "1.00€", "06:00-22:00"),
        Place("WC: Repubblica Metro", "Republic Side.", "Basement level unit.", "", IMG_WC, LatLng(41.9028, 12.4948), 3.0, "wc", "1.00€", "06:00-22:00"),
        Place("WC: San Giovanni Laterano", "Basilica Side.", "Next to the entry gates.", "", IMG_WC, LatLng(41.8855, 12.5095), 3.0, "wc", "1.00€", "06:00-22:00"),
        Place("WC: Piramide Metro", "Pyramid Stop.", "Main station ticket hall.", "", IMG_WC, LatLng(41.8755, 12.4815), 2.5, "wc", "1.00€", "06:00-22:00"),
        Place("WC: Cipro Vatican", "Vatican Museum side.", "Emergency stop near the wall.", "", IMG_WC, LatLng(41.9075, 12.4475), 3.0, "wc", "1.00€", "06:00-22:00"),
        Place("WC: Lepanto Prati", "Cola di Rienzo.", "Shopping hub stop.", "", IMG_WC, LatLng(41.9115, 12.4665), 3.0, "wc", "1.00€", "06:00-22:00"),
        Place("WC: Flaminio Popolo", "Park side.", "Clean unit near the gate.", "", IMG_WC, LatLng(41.9125, 12.4765), 3.5, "wc", "1.00€", "06:00-22:00"),
        Place("WC: Castel Sant'Angelo", "Fortress Park.", "Inside the garden perimeter.", "", IMG_WC, LatLng(41.9045, 12.4685), 3.0, "wc", "1.00€", "09:00-19:00"),
        Place("WC: Villa Pamphili North", "Entry Point.", "Park gate facilities.", "", IMG_WC, LatLng(41.8895, 12.4525), 3.0, "wc", "0.50€", "09:00-18:00"),
        Place("WC: Tiburtina Station", "Main Hub.", "Basement station toilets.", "", IMG_WC, LatLng(41.9105, 12.5305), 3.0, "wc", "1.00€", "06:00-22:00"),
        Place("WC: Pigneto Hub", "Metro C.", "Station hall basement.", "", IMG_WC, LatLng(41.8885, 12.5285), 3.0, "wc", "1.00€", "06:00-22:00"),
        Place("WC: Flaminio Hub", "Popolo Gate.", "Gate entrance facilities.", "", IMG_WC, LatLng(41.9125, 12.4765), 3.5, "wc", "1.00€", "06:00-22:00"),
        Place("WC: Euroma2 Food", "Mall Court.", "Clean food court toilets.", "", IMG_WC, LatLng(41.8055, 12.4665), 4.5, "wc", "Free", "10:00-21:00"),
        Place("WC: Barberini Metro Hall", "Entrance Hall.", "Basement level hub stop.", "", IMG_WC, LatLng(41.9035, 12.4885), 3.0, "wc", "1.00€", "06:00-23:00"),
        Place("WC: St. Peter's Square Colonnade", "Vatican Side.", "Under the Bernini arches.", "", IMG_WC, LatLng(41.9015, 12.4575), 3.5, "wc", "1.00€", "08:00-18:00"),
        Place("WC: Termini Station Underground", "Hub Unit.", "Down near the Metro B line.", "", IMG_WC, LatLng(41.9010, 12.5014), 3.0, "wc", "1.00€", "06:00-23:00"),
        Place("Nasoni: Piazza Navona", "Plaza Center.", "Stay hydrated near the fountains.", "", IMG_WATER, LatLng(41.8989, 12.4731), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Vatican Side", "Bernini Column.", "Water next to the massive colonnade.", "", IMG_WATER, LatLng(41.9035, 12.4565), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Monti Heart", "Piazza Madonna.", "Central water point for Monti locals.", "", IMG_WATER, LatLng(41.8955, 12.4915), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Campo Ghetto", "Turtle Fountain.", "Near the famous turtle fountain.", "", IMG_WATER, LatLng(41.8925, 12.4785), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Tiber Island", "Hospital Side.", "Clean water on the historic island.", "", IMG_WATER, LatLng(41.8905, 12.4775), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Ara Pacis", "Museum Wall.", "Next to the modern Meier building.", "", IMG_WATER, LatLng(41.9065, 12.4728), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Popolo South", "Via del Babuino.", "Water point near the southern entrance.", "", IMG_WATER, LatLng(41.9095, 12.4775), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Gianicolo Top", "Lighthouse Side.", "Fresh water at the panoramic summit.", "", IMG_WATER, LatLng(41.8912, 12.4618), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Campidoglio", "Senator Steps.", "Water near the Michelangelo square.", "", IMG_WATER, LatLng(41.8932, 12.4828), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Termini Front", "Station Entry.", "Hydrate before entering the hub.", "", IMG_WATER, LatLng(41.9025, 12.5015), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Corso Side", "Shopping Tap.", "Hidden in a side street off Via del Corso.", "", IMG_WATER, LatLng(41.9035, 12.4805), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Circus Track", "Arena Water.", "Water at the southern end of the stadium.", "", IMG_WATER, LatLng(41.8855, 12.4875), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Appia Start", "Historical Stones.", "Near the beginning of the old road.", "", IMG_WATER, LatLng(41.8525, 12.5125), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Castel Gate", "Castle Moat.", "Water near the entrance of Hadrian's fortress.", "", IMG_WATER, LatLng(41.9035, 12.4665), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Villa Entry", "Pincio Base.", "Water point near the garden climb.", "", IMG_WATER, LatLng(41.9125, 12.4765), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Aventine View", "Keyhole Side.", "Water for the queue at the secret keyhole.", "", IMG_WATER, LatLng(41.8825, 12.4785), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Cipro Metro", "Vatican Exit.", "Hydration near the Metro A stop.", "", IMG_WATER, LatLng(41.9075, 12.4475), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: San Lorenzo", "Uni District.", "Popular student hydration point.", "", IMG_WATER, LatLng(41.8985, 12.5125), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Testaccio Hill", "Market Side.", "Water at the base of the pottery hill.", "", IMG_WATER, LatLng(41.8765, 12.4745), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Pigneto", "Alt District.", "Water point in Rome's artistic quarter.", "", IMG_WATER, LatLng(41.8885, 12.5295), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Ostiense", "Pyramid Side.", "Water near the ancient white pyramid.", "", IMG_WATER, LatLng(41.8755, 12.4805), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Celio Park", "Colosseum Top.", "Water overlooking the arena.", "", IMG_WATER, LatLng(41.8885, 12.4935), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Barberini", "Bernini Tritone.", "Water near the famous Triton fountain.", "", IMG_WATER, LatLng(41.9038, 12.4882), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Quirinale", "Palace Steps.", "Hydrate near the Presidential residence.", "", IMG_WATER, LatLng(41.8995, 12.4865), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Laterano", "Obelisk Side.", "Water near the oldest basilica.", "", IMG_WATER, LatLng(41.8865, 12.5052), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Re di Roma", "Circular Plaza.", "Central hub hydration point.", "", IMG_WATER, LatLng(41.8818, 12.5115), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Garbatella", "Carlotta Fountain.", "The most poetic nasone in Rome.", "", IMG_WATER, LatLng(41.8615, 12.4885), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Ponte Milvio", "Bridge Entry.", "Water at the historic lock bridge.", "", IMG_WATER, LatLng(41.9325, 12.4665), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Parioli", "Luxury Tap.", "Hydration in Rome's chicest district.", "", IMG_WATER, LatLng(41.9255, 12.4915), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Prati Central", "Cola di Rienzo.", "Shopping break water point.", "", IMG_WATER, LatLng(41.9088, 12.4655), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Trastevere South", "Mastai Side.", "Water in lower Trastevere.", "", IMG_WATER, LatLng(41.8875, 12.4745), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Esquiline", "Near Termini.", "Water near the station hub.", "", IMG_WATER, LatLng(41.8980, 12.5005), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Portico", "Ottavia Side.", "Ancient water ruins.", "", IMG_WATER, LatLng(41.8928, 12.4782), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Farnese", "Bathtubs.", "Giant granite tub water.", "", IMG_WATER, LatLng(41.8945, 12.4710), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Borgo Vittorio", "Secret Wall.", "Vatican escape wall water.", "", IMG_WATER, LatLng(41.9040, 12.4590), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Gianicolo Sunset", "Light Side.", "Lighthouse panoramic water.", "", IMG_WATER, LatLng(41.8912, 12.4618), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Campidoglio Steps", "Capitol Tap.", "Michelangelo square hydration.", "", IMG_WATER, LatLng(41.8932, 12.4828), 5.0, "nasoni", "Free", "24h"),
        Place("WC: Vatican Walls", "Museum Entry.", "Behind the main entrance.", "", IMG_WC, LatLng(41.9065, 12.4535), 3.0, "wc", "1€", "08:00-18:00"),
        Place("WC: Villa Pamphili North", "Park Entry.", "Facilities near the visitor center.", "", IMG_WC, LatLng(41.8895, 12.4525), 3.0, "wc", "0.50€", "09:00-18:00"),
        Place("WC: Ostiense Station", "Train Stop.", "Near the main platforms.", "", IMG_WC, LatLng(41.8725, 12.4825), 2.8, "wc", "1€", "06:00-23:00"),
        Place("Nasoni: Piazza Cavour", "Prati Water.", "Near the supreme court building.", "", IMG_WATER, LatLng(41.9055, 12.4695), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Via Appia", "History Water.", "Along the path of ancient stones.", "", IMG_WATER, LatLng(41.8655, 12.5085), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Aventino", "Orange Garden Hill.", "Next to Santa Sabina entry.", "", IMG_WATER, LatLng(41.8842, 12.4795), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Termini Side", "Marsala Exit.", "Hydrate before the train.", "", IMG_WATER, LatLng(41.9025, 12.5025), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Piazza Fiume", "Market Water.", "Corner of the square.", "", IMG_WATER, LatLng(41.9105, 12.4975), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Testaccio", "Market Square.", "Near the big hill.", "", IMG_WATER, LatLng(41.8782, 12.4765), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Campo de Fiori", "Market Tap.", "Behind the statue.", "", IMG_WATER, LatLng(41.8955, 12.4722), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Via Giulia", "Historic road.", "Clean tap.", "", IMG_WATER, LatLng(41.8940, 12.4680), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Ghetto", "Portico Water.", "Ancient ruins.", "", IMG_WATER, LatLng(41.8928, 12.4782), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Borgo", "Secret Passage.", "Hydration point.", "", IMG_WATER, LatLng(41.9040, 12.4590), 5.0, "nasoni", "Free", "24h"),
        Place("WC: Colosseo", "Metro Station.", "Paid unit.", "", IMG_WC, LatLng(41.8912, 12.4935), 3.0, "wc", "1€", "09:00-18:00"),
        Place("WC: Navona", "Underground.", "Central center.", "", IMG_WC, LatLng(41.8995, 12.4735), 3.5, "wc", "1€", "10:00-20:00"),
        Place("Nasoni: Colosseum", "Hydration.", "Metro exit.", "", IMG_WATER, LatLng(41.8906, 12.4905), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Pantheon", "Ancient Tap.", "Temple side.", "", IMG_WATER, LatLng(41.8985, 12.4765), 5.0, "nasoni", "Free", "24h"),
        Place("Nasoni: Spagna", "Step Water.", "Base of steps.", "", IMG_WATER, LatLng(41.9062, 12.4820), 5.0, "nasoni", "Free", "24h"),
        Place("WC: Termini", "Station Toilet.", "Modern pay unit.", "", IMG_WC, LatLng(41.9010, 12.5014), 3.0, "wc", "1€", "06:00-23:00"),
        Place("WC: Spagna", "Automated.", "Metro side.", "", IMG_WC, LatLng(41.9060, 12.4825), 3.5, "wc", "1€", "09:00-19:00")
    )
}


object LearningManager {
    private const val PREFS_NAME = "VojoLearningPrefs"
    private const val KEY_LAST_DATE = "last_study_date"
    private const val KEY_BASE_SWIPES = "base_swipes_left"
    private const val KEY_BONUS_SWIPES = "bonus_swipes_left"
    private const val KEY_TOTAL_LEARNED = "total_words_learned"

    fun refreshAndGetTotal(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastDate = prefs.getString(KEY_LAST_DATE, "")
        val today = SimpleDateFormat("dd-MM-", Locale.getDefault()).format(Date())

        var base = prefs.getInt(KEY_BASE_SWIPES, 10)
        val bonus = prefs.getInt(KEY_BONUS_SWIPES, 0)

        if (lastDate != today) {
            base += 5
            if (base > 10) base = 10

            prefs.edit()
                .putString(KEY_LAST_DATE, today)
                .putInt(KEY_BASE_SWIPES, base)
                .apply()
        }

        return base + bonus
    }

    fun consumeSwipe(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        var base = prefs.getInt(KEY_BASE_SWIPES, 0)
        var bonus = prefs.getInt(KEY_BONUS_SWIPES, 0)

        if (base > 0) {
            base--
        } else if (bonus > 0) {
            bonus--
        }

        val learned = prefs.getInt(KEY_TOTAL_LEARNED, 0) + 1

        prefs.edit()
            .putInt(KEY_BASE_SWIPES, base)
            .putInt(KEY_BONUS_SWIPES, bonus)
            .putInt(KEY_TOTAL_LEARNED, learned)
            .apply()
    }

    fun addBonusSwipes(context: Context, amount: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentBonus = prefs.getInt(KEY_BONUS_SWIPES, 0)
        prefs.edit().putInt(KEY_BONUS_SWIPES, currentBonus + amount).apply()
    }

    fun getTotalLearnedCount(context: Context): Int {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getInt(KEY_TOTAL_LEARNED, 0)
    }
}
