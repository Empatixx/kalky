package cz.krokviak.kalky.core.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import cz.krokviak.kalky.core.common.AppLanguage
import cz.krokviak.kalky.core.common.AppPreferences
import cz.krokviak.kalky.core.di.koinInject

data class AppStrings(
    val nav: NavStrings,
    val home: HomeStrings,
    val settings: SettingsStrings,
    val profile: ProfileStrings,
    val camera: CameraStrings,
    val barcode: BarcodeStrings,
    val analytics: AnalyticsStrings,
    val nutrientEdit: NutrientEditStrings,
    val detail: DetailStrings,
    val onboarding: OnboardingStrings,
    val common: CommonStrings,
    val customFood: CustomFoodStrings,
    val notifications: NotificationStrings,
    val legal: LegalStrings,
    val auth: AuthStrings,
    val date: DateStrings
)

data class DateStrings(
    val daysShort: List<String>,
    val months: List<String>
)

data class LegalSection(
    val header: String,
    val body: String
)

data class LegalStrings(
    val sectionTitle: String,
    val termsTitle: String,
    val privacyTitle: String,
    val privacySections: List<LegalSection>,
    val termsSections: List<LegalSection>
)

data class AuthStrings(
    val loginTitle: String,
    val loginSubtitle: String,
    val continueWithGoogle: String,
    val continueWithApple: String,
    val continueWithEmail: String,
    val email: String,
    val password: String,
    val signIn: String,
    val createAccount: String,
    val noAccount: String,
    val haveAccount: String,
    val signOut: String,
    val or: String
)

data class NotificationStrings(
    val reminders: String,
    val enableReminders: String,
    val noFoodTitle: String,
    val noFoodBody: String,
    val behindTitle: String,
    val behindBody: String,
    val channelName: String
)

data class CustomFoodStrings(
    val title: String,
    val searchPlaceholder: String,
    val addNew: String,
    val myFoods: String,
    val all: String,
    val history: String,
    val addManually: String,
    val addIngredient: String,
    val ingredients: String,
    val recentlyUsed: String,
    val onlineResults: String,
    val per100g: String,
    val portionSize: String,
    val grams: String,
    val foodName: String,
    val noResults: String,
    val manualEntryTitle: String,
    val save: String
)

data class NavStrings(
    val home: String,
    val analytics: String,
    val profile: String,
    val settings: String
)

data class HomeStrings(
    val addedToday: String,
    val emptyTitle: String,
    val emptySubtitle: String,
    val streakDay: String,
    val streakDays: String,
    val selected: String,
    val deleteSelected: String,
    val saveAsCustom: String,
    val unknownFood: String,
    val computingMacros: String,
    val today: String,
)

data class SettingsStrings(
    val title: String,
    val appearance: String,
    val language: String,
    val units: String,
    val account: String,
    val appVersion: String,
    val themeSystem: String,
    val themeLight: String,
    val themeDark: String,
    val metric: String,
    val imperial: String
)

data class ProfileStrings(
    val title: String,
    val personalInfo: String,
    val weight: String,
    val height: String,
    val age: String,
    val gender: String,
    val male: String,
    val female: String,
    val activityLevel: String,
    val sedentary: String,
    val light: String,
    val active: String,
    val veryActive: String,
    val bmiUnderweight: String,
    val bmiNormal: String,
    val bmiOverweight: String,
    val bmiObese: String,
)

data class CameraStrings(
    val photoMode: String,
    val qrMode: String,
    val permissionDenied: String
)

data class BarcodeStrings(
    val scanBarcode: String,
    val searchingProduct: String,
    val productNotFound: String,
    val quantityGrams: String,
    val error: String
)

data class AnalyticsStrings(
    val avgDailyIntake: String,
    val avgProtein: String,
    val avgCarbs: String,
    val avgFat: String,
    val noCaloriesTitle: String,
    val noCaloriesSubtitle: String,
    val dateStart: String,
    val dateEnd: String,
    val weightEmptyTitle: String,
    val weightEmptySubtitle: String,
    val weightCurrent: String,
    val weightAverage: String
)

data class NutrientEditStrings(
    val title: String,
    val macronutrients: String,
    val dailyCalorieTarget: String
)

data class DetailStrings(
    val fix: String,
    val done: String,
    val share: String,
    val delete: String,
    val healthQuality: String
)

data class OnboardingStrings(
    val chooseLanguage: String,
    val chooseUnits: String,
    val chooseAppearance: String,
    val chooseGender: String,
    val howMuchWeigh: String,
    val howTall: String,
    val howOld: String,
    val howActive: String,
    val whatsYourGoal: String,
    val yourDailyTargets: String,
    val macrosIndicativeNote: String,
    val promoCode: String,
    val havePromoCode: String,
    val promoCodeOptional: String,
    val loseWeight: String,
    val maintain: String,
    val gainWeight: String
)

data class CommonStrings(
    val calories: String,
    val protein: String,
    val fat: String,
    val carbs: String,
    val back: String,
    val add: String,
    val retry: String,
    val again: String,
    val unknownProduct: String,
    val close: String,
    val done: String,
    val continueText: String,
    val years: String,
    val cdBack: String,
    val cdMore: String,
    val cdExit: String,
    val cdFoodImage: String,
    val cdIncrease: String,
    val cdDecrease: String,
    val cdHeart: String,
    val kcal: String,
    val unitPieces: String,
    val errorGeneric: String,
    val errorNetwork: String,
    val errorPhotoAnalysis: String,
    val errorProductSearch: String
)

val CzechStrings = AppStrings(
    nav = NavStrings(
        home = "Domov",
        analytics = "Anal\u00FDza",
        profile = "Profil",
        settings = "Nastaven\u00ED"
    ),
    home = HomeStrings(
        addedToday = "P\u0159id\u00E1no dnes",
        emptyTitle = "Dneska jsi je\u0161t\u011B nic nep\u0159idal/a",
        emptySubtitle = "Klikni na tla\u010D\u00EDtko dole a p\u0159idej si prvn\u00ED j\u00EDdlo",
        streakDay = "1 den v \u0159ad\u011B",
        streakDays = "%d dn\u00ED v \u0159ad\u011B",
        selected = "Vybr\u00E1no",
        deleteSelected = "Smazat",
        saveAsCustom = "Ulo\u017Eit jako j\u00EDdlo",
        unknownFood = "Nezn\u00E1m\u00E9 j\u00EDdlo",
        computingMacros = "Po\u010D\u00EDt\u00E1m makro\u017Eiviny...",
        today = "Dnes",
    ),
    settings = SettingsStrings(
        title = "Nastaven\u00ED",
        appearance = "Vzhled",
        language = "Jazyk",
        units = "Jednotky",
        account = "\u00DA\u010Det",
        appVersion = "Verze aplikace 1.0",
        themeSystem = "Syst\u00E9m",
        themeLight = "Sv\u011Btl\u00FD",
        themeDark = "Tmav\u00FD",
        metric = "Metrick\u00E9",
        imperial = "Imperi\u00E1ln\u00ED"
    ),
    profile = ProfileStrings(
        title = "Profil",
        personalInfo = "Osobn\u00ED \u00FAdaje",
        weight = "V\u00E1ha",
        height = "V\u00FD\u0161ka",
        age = "V\u011Bk",
        gender = "Pohlav\u00ED",
        male = "Mu\u017E",
        female = "\u017Dena",
        activityLevel = "\u00DArove\u0148 aktivity",
        sedentary = "Sedav\u00FD",
        light = "M\u00EDrn\u00FD",
        active = "Aktivn\u00ED",
        veryActive = "Velmi aktivn\u00ED",
        bmiUnderweight = "Podv\u00E1ha",
        bmiNormal = "Norm\u00E1ln\u00ED",
        bmiOverweight = "Nadv\u00E1ha",
        bmiObese = "Obezita",
    ),
    camera = CameraStrings(
        photoMode = "Re\u017Eim foto",
        qrMode = "Re\u017Eim QR",
        permissionDenied = "P\u0159\u00EDstup ke kame\u0159e zam\u00EDtnut"
    ),
    barcode = BarcodeStrings(
        scanBarcode = "Naskenujte \u010D\u00E1rov\u00FD k\u00F3d",
        searchingProduct = "Hled\u00E1m produkt...",
        productNotFound = "Produkt nenalezen",
        quantityGrams = "Mno\u017Estv\u00ED (g)",
        error = "Chyba"
    ),
    analytics = AnalyticsStrings(
        avgDailyIntake = "Pr\u016Fm\u011Brn\u00FD denn\u00ED p\u0159\u00EDjem",
        avgProtein = "Pr\u016Fm\u011Br b\u00EDlkovin",
        avgCarbs = "Pr\u016Fm\u011Br sacharid\u016F",
        avgFat = "Pr\u016Fm\u011Br tuk\u016F",
        noCaloriesTitle = "Chyb\u00ED informace o va\u0161\u00EDch kalori\u00EDch",
        noCaloriesSubtitle = "P\u0159idejte sv\u00E9 kalorie v \u00FAvodu",
        dateStart = "Za\u010D\u00E1tek",
        dateEnd = "Konec",
        weightEmptyTitle = "Zat\u00EDm nem\u00E1me \u00FAdaje o va\u0161\u00ED v\u00E1ze",
        weightEmptySubtitle = "P\u0159idejte je v nastaven\u00ED.",
        weightCurrent = "Aktu\u00E1ln\u00ED v\u00E1ha",
        weightAverage = "Pr\u016Fm\u011Br"
    ),
    nutrientEdit = NutrientEditStrings(
        title = "\u00DAprava makro\u017Eivin",
        macronutrients = "Makro\u017Eiviny",
        dailyCalorieTarget = "Denn\u00ED c\u00EDl kalori\u00ED"
    ),
    detail = DetailStrings(
        fix = "Opravit",
        done = "Dokon\u010Dit",
        share = "Sd\u00EDlet",
        delete = "Smazat",
        healthQuality = "Zdravotn\u00ED kvalita"
    ),
    onboarding = OnboardingStrings(
        chooseLanguage = "Vyber jazyk",
        chooseUnits = "Vyber jednotky",
        chooseAppearance = "Vyber vzhled",
        chooseGender = "Vyber pohlav\u00ED",
        howMuchWeigh = "Kolik v\u00E1\u017E\u00ED\u0161?",
        howTall = "Jak jsi vysok\u00FD/\u00E1?",
        howOld = "Kolik je ti let?",
        howActive = "Jak aktivn\u00ED jsi?",
        whatsYourGoal = "Jak\u00FD m\u00E1\u0161 c\u00EDl?",
        yourDailyTargets = "Tvoje denn\u00ED c\u00EDle",
        macrosIndicativeNote = "Hodnoty jsou orienta\u010Dn\u00ED a vypo\u010D\u00EDtan\u00E9 na z\u00E1klad\u011B tvych \u00FAdaj\u016F. M\u016F\u017Ee\u0161 je kdykoliv upravit.",
        promoCode = "Promo k\u00F3d",
        havePromoCode = "M\u00E1\u0161 promo k\u00F3d?",
        promoCodeOptional = "Promo k\u00F3d (voliteln\u00E9)",
        loseWeight = "Chci zhubnout",
        maintain = "Chci udr\u017Eet",
        gainWeight = "Chci nabrat"
    ),
    common = CommonStrings(
        calories = "Kalorie",
        protein = "B\u00EDlkoviny",
        fat = "Tuky",
        carbs = "Sacharidy",
        back = "Zp\u011Bt",
        add = "P\u0159idat",
        retry = "Zkusit znovu",
        again = "Znovu",
        unknownProduct = "Nezn\u00E1m\u00FD produkt",
        close = "Zav\u0159\u00EDt",
        done = "Dokon\u010Dit",
        continueText = "Pokra\u010Dovat",
        years = "let",
        cdBack = "Zp\u011Bt",
        cdMore = "V\u00EDce",
        cdExit = "Zav\u0159\u00EDt",
        cdFoodImage = "Obr\u00E1zek j\u00EDdla",
        cdIncrease = "P\u0159idat",
        cdDecrease = "Odebrat",
        cdHeart = "Zdrav\u00ED",
        kcal = "kcal",
        unitPieces = "ks",
        errorGeneric = "N\u011Bco se nepoda\u0159ilo",
        errorNetwork = "Chyba s\u00EDt\u011B, zkontroluj p\u0159ipojen\u00ED",
        errorPhotoAnalysis = "Nepoda\u0159ilo se analyzovat fotku",
        errorProductSearch = "Vyhled\u00E1v\u00E1n\u00ED produkt\u016F selhalo"
    ),
    customFood = CustomFoodStrings(
        title = "P\u0159idat j\u00EDdlo",
        searchPlaceholder = "Hledat j\u00EDdlo...",
        addNew = "P\u0159idat novou polo\u017Eku",
        myFoods = "Moje j\u00EDdla",
        all = "V\u0161e",
        history = "Historie",
        addManually = "P\u0159idat ru\u010Dn\u011B",
        addIngredient = "P\u0159idat ingredienci",
        ingredients = "Ingredience",
        recentlyUsed = "Ned\u00E1vno pou\u017Eit\u00E9",
        onlineResults = "V\u00FDsledky z datab\u00E1ze",
        per100g = "na 100 g",
        portionSize = "Velikost porce",
        grams = "g",
        foodName = "N\u00E1zev j\u00EDdla",
        noResults = "\u017D\u00E1dn\u00E9 v\u00FDsledky",
        manualEntryTitle = "Nov\u00E9 j\u00EDdlo",
        save = "Ulo\u017Eit"
    ),
    legal = LegalStrings(
        sectionTitle = "Pr\u00E1vn\u00ED informace",
        termsTitle = "Obchodn\u00ED podm\u00EDnky",
        privacyTitle = "Ochrana osobn\u00EDch \u00FAdaj\u016F",
        privacySections = listOf(
            LegalSection("1. Spr\u00E1vce \u00FAdaj\u016F", "Spr\u00E1vcem osobn\u00EDch \u00FAdaj\u016F zpracov\u00E1van\u00FDch prost\u0159ednictv\u00EDm aplikace Kalky je jej\u00ED provozovatel."),
            LegalSection("2. Jak\u00E9 \u00FAdaje shroma\u017E\u010Fujeme", "Aplikace shroma\u017E\u010Fuje \u00FAdaje, kter\u00E9 zad\u00E1te: v\u00FD\u0161ka, v\u00E1ha, v\u011Bk, pohlav\u00ED, \u00FArove\u0148 aktivity a z\u00E1znamy o stravov\u00E1n\u00ED. Tyto \u00FAdaje jsou ukl\u00E1d\u00E1ny v\u00FDhradn\u011B lok\u00E1ln\u011B na va\u0161em za\u0159\u00EDzen\u00ED."),
            LegalSection("3. \u00DA\u010Del zpracov\u00E1n\u00ED", "Va\u0161e \u00FAdaje jsou zpracov\u00E1v\u00E1ny za \u00FA\u010Delem v\u00FDpo\u010Dtu doporu\u010Den\u00E9ho denn\u00EDho p\u0159\u00EDjmu kalori\u00ED a makro\u017Eivin a zobrazov\u00E1n\u00ED statistik o va\u0161em stravov\u00E1n\u00ED."),
            LegalSection("4. Sd\u00EDlen\u00ED \u00FAdaj\u016F", "Va\u0161e osobn\u00ED \u00FAdaje nejsou sd\u00EDleny s t\u0159et\u00EDmi stranami. Fotografie j\u00EDdel mohou b\u00FDt odesl\u00E1ny na server pro anal\u00FDzu nutri\u010Dn\u00EDch hodnot, ale nejsou trvale ukl\u00E1d\u00E1ny."),
            LegalSection("5. Va\u0161e pr\u00E1va", "M\u00E1te pr\u00E1vo na p\u0159\u00EDstup k sv\u00FDm \u00FAdaj\u016Fm, jejich opravu nebo vymaz\u00E1n\u00ED. Ve\u0161ker\u00E1 data m\u016F\u017Eete smazat odinstalov\u00E1n\u00EDm aplikace.")
        ),
        termsSections = listOf(
            LegalSection("1. \u00DAvodn\u00ED ustanoven\u00ED", "Tyto obchodn\u00ED podm\u00EDnky upravuj\u00ED pr\u00E1va a povinnosti u\u017Eivatel\u016F mobiln\u00ED aplikace Kalky (d\u00E1le jen \u201EAplikace\u201C). Pou\u017E\u00EDv\u00E1n\u00EDm Aplikace souhlas\u00EDte s t\u011Bmito podm\u00EDnkami."),
            LegalSection("2. Popis slu\u017Eby", "Aplikace Kalky slou\u017E\u00ED k sledov\u00E1n\u00ED p\u0159\u00EDjmu potravin a nutri\u010Dn\u00EDch hodnot. Aplikace umo\u017E\u0148uje zaznamen\u00E1vat j\u00EDdla pomoc\u00ED fotografi\u00ED, \u010D\u00E1rov\u00FDch k\u00F3d\u016F nebo ru\u010Dn\u00EDho zad\u00E1n\u00ED."),
            LegalSection("3. U\u017Eivatelsk\u00FD \u00FA\u010Det", "Pro pou\u017E\u00EDv\u00E1n\u00ED Aplikace nen\u00ED vy\u017Eadov\u00E1na registrace. Ve\u0161ker\u00E1 data jsou ukl\u00E1d\u00E1na lok\u00E1ln\u011B na za\u0159\u00EDzen\u00ED u\u017Eivatele."),
            LegalSection("4. Omezen\u00ED odpov\u011Bdnosti", "Nutri\u010Dn\u00ED hodnoty zobrazen\u00E9 v Aplikaci maj\u00ED informativn\u00ED charakter a nenahrazuj\u00ED odborn\u00E9 poradenstv\u00ED. Provozovatel nenese odpov\u011Bdnost za p\u0159esnost \u00FAdaj\u016F z\u00EDskan\u00FDch z extern\u00EDch datab\u00E1z\u00ED."),
            LegalSection("5. Zm\u011Bny podm\u00EDnek", "Provozovatel si vyhrazuje pr\u00E1vo tyto podm\u00EDnky kdykoli zm\u011Bnit. O zm\u011Bn\u00E1ch bude u\u017Eivatel informov\u00E1n prost\u0159ednictv\u00EDm aktualizace Aplikace.")
        )
    ),
    notifications = NotificationStrings(
        reminders = "Upozorn\u011Bn\u00ED",
        enableReminders = "P\u0159ipom\u00EDnky j\u00EDdel",
        noFoodTitle = "Zapomn\u011Bl/a jsi j\u00EDst?",
        noFoodBody = "Dnes jsi je\u0161t\u011B nic nezaznamenal/a. P\u0159idej si j\u00EDdlo!",
        behindTitle = "Jsi pozadu s j\u00EDdlem",
        behindBody = "M\u00E1\u0161 za sebou jen %d%% kalori\u00ED. Nezapome\u0148 j\u00EDst!",
        channelName = "P\u0159ipom\u00EDnky j\u00EDdel"
    ),
    auth = AuthStrings(
        loginTitle = "V\u00EDtejte v Kalky",
        loginSubtitle = "P\u0159ihla\u0161te se pro pokra\u010Dov\u00E1n\u00ED",
        continueWithGoogle = "Pokra\u010Dovat p\u0159es Google",
        continueWithApple = "Pokra\u010Dovat p\u0159es Apple",
        continueWithEmail = "Pokra\u010Dovat p\u0159es e-mail",
        email = "E-mail",
        password = "Heslo",
        signIn = "P\u0159ihl\u00E1sit se",
        createAccount = "Vytvo\u0159it \u00FA\u010Det",
        noAccount = "Nem\u00E1te \u00FA\u010Det?",
        haveAccount = "M\u00E1te \u00FA\u010Det?",
        signOut = "Odhl\u00E1sit se",
        or = "nebo"
    ),
    date = DateStrings(
        daysShort = listOf("Po", "\u00DAt", "St", "\u010Ct", "P\u00E1", "So", "Ne"),
        months = listOf(
            "Leden", "\u00DAnor", "B\u0159ezen", "Duben", "Kv\u011Bten", "\u010Cerven",
            "\u010Cervenec", "Srpen", "Z\u00E1\u0159\u00ED", "\u0158\u00EDjen", "Listopad", "Prosinec"
        )
    )
)

val EnglishStrings = AppStrings(
    nav = NavStrings(
        home = "Home",
        analytics = "Analytics",
        profile = "Profile",
        settings = "Settings"
    ),
    home = HomeStrings(
        addedToday = "Added today",
        emptyTitle = "You haven't added anything yet",
        emptySubtitle = "Tap the button below to add your first food",
        streakDay = "1 day streak",
        streakDays = "%d day streak",
        selected = "Selected",
        deleteSelected = "Delete",
        saveAsCustom = "Save as food",
        unknownFood = "Unknown food",
        computingMacros = "Calculating macros...",
        today = "Today",
    ),
    settings = SettingsStrings(
        title = "Settings",
        appearance = "Appearance",
        language = "Language",
        units = "Units",
        account = "Account",
        appVersion = "App version 1.0",
        themeSystem = "System",
        themeLight = "Light",
        themeDark = "Dark",
        metric = "Metric",
        imperial = "Imperial"
    ),
    profile = ProfileStrings(
        title = "Profile",
        personalInfo = "Personal info",
        weight = "Weight",
        height = "Height",
        age = "Age",
        gender = "Gender",
        male = "Male",
        female = "Female",
        activityLevel = "Activity level",
        sedentary = "Sedentary",
        light = "Light",
        active = "Active",
        veryActive = "Very active",
        bmiUnderweight = "Underweight",
        bmiNormal = "Normal",
        bmiOverweight = "Overweight",
        bmiObese = "Obese",
    ),
    camera = CameraStrings(
        photoMode = "Photo mode",
        qrMode = "QR mode",
        permissionDenied = "Camera permission denied"
    ),
    barcode = BarcodeStrings(
        scanBarcode = "Scan a barcode",
        searchingProduct = "Searching product...",
        productNotFound = "Product not found",
        quantityGrams = "Quantity (g)",
        error = "Error"
    ),
    analytics = AnalyticsStrings(
        avgDailyIntake = "Average daily intake",
        avgProtein = "Average protein",
        avgCarbs = "Average carbs",
        avgFat = "Average fat",
        noCaloriesTitle = "No calorie data available",
        noCaloriesSubtitle = "Add your calories on the home screen",
        dateStart = "Start",
        dateEnd = "End",
        weightEmptyTitle = "No weight data yet",
        weightEmptySubtitle = "Add it in settings.",
        weightCurrent = "Current weight",
        weightAverage = "Average"
    ),
    nutrientEdit = NutrientEditStrings(
        title = "Edit macronutrients",
        macronutrients = "Macronutrients",
        dailyCalorieTarget = "Daily calorie target"
    ),
    detail = DetailStrings(
        fix = "Fix",
        done = "Done",
        share = "Share",
        delete = "Delete",
        healthQuality = "Health quality"
    ),
    onboarding = OnboardingStrings(
        chooseLanguage = "Choose language",
        chooseUnits = "Choose units",
        chooseAppearance = "Choose appearance",
        chooseGender = "Choose gender",
        howMuchWeigh = "How much do you weigh?",
        howTall = "How tall are you?",
        howOld = "How old are you?",
        howActive = "How active are you?",
        whatsYourGoal = "What's your goal?",
        yourDailyTargets = "Your daily targets",
        macrosIndicativeNote = "These values are indicative, calculated from your data. You can adjust them anytime.",
        promoCode = "Promo code",
        havePromoCode = "Got a promo code?",
        promoCodeOptional = "Promo code (optional)",
        loseWeight = "Lose weight",
        maintain = "Maintain weight",
        gainWeight = "Gain weight"
    ),
    common = CommonStrings(
        calories = "Calories",
        protein = "Protein",
        fat = "Fat",
        carbs = "Carbs",
        back = "Back",
        add = "Add",
        retry = "Try again",
        again = "Again",
        unknownProduct = "Unknown product",
        close = "Close",
        done = "Done",
        continueText = "Continue",
        years = "yrs",
        cdBack = "Back",
        cdMore = "More",
        cdExit = "Exit",
        cdFoodImage = "Food image",
        cdIncrease = "Increase",
        cdDecrease = "Decrease",
        cdHeart = "Health",
        kcal = "kcal",
        unitPieces = "pcs",
        errorGeneric = "Something went wrong",
        errorNetwork = "Network error, check your connection",
        errorPhotoAnalysis = "Photo analysis failed",
        errorProductSearch = "Product search failed"
    ),
    customFood = CustomFoodStrings(
        title = "Add food",
        searchPlaceholder = "Search food...",
        addNew = "Add new item",
        myFoods = "My foods",
        all = "All",
        history = "History",
        addManually = "Add manually",
        addIngredient = "Add ingredient",
        ingredients = "Ingredients",
        recentlyUsed = "Recently used",
        onlineResults = "Database results",
        per100g = "per 100 g",
        portionSize = "Portion size",
        grams = "g",
        foodName = "Food name",
        noResults = "No results",
        manualEntryTitle = "New food",
        save = "Save"
    ),
    legal = LegalStrings(
        sectionTitle = "Legal",
        termsTitle = "Terms & Conditions",
        privacyTitle = "Privacy Policy",
        privacySections = listOf(
            LegalSection("1. Data Controller", "The controller of personal data processed through the Kalky application is its operator."),
            LegalSection("2. What Data We Collect", "The application collects data you enter: height, weight, age, gender, activity level and meal records. This data is stored exclusively locally on your device."),
            LegalSection("3. Purpose of Processing", "Your data is processed to calculate your recommended daily calorie and macronutrient intake and to display statistics about your diet."),
            LegalSection("4. Data Sharing", "Your personal data is not shared with third parties. Meal photos may be sent to a server for nutritional analysis, but are not stored permanently."),
            LegalSection("5. Your Rights", "You have the right to access, correct or delete your data. You can delete all data by uninstalling the application.")
        ),
        termsSections = listOf(
            LegalSection("1. Introductory Provisions", "These terms and conditions govern the rights and obligations of users of the Kalky mobile application (the \u201CApplication\u201D). By using the Application you agree to these terms."),
            LegalSection("2. Service Description", "The Kalky application is used for tracking food intake and nutritional values. The Application allows recording meals using photos, barcodes or manual entry."),
            LegalSection("3. User Account", "No registration is required to use the Application. All data is stored locally on the user's device."),
            LegalSection("4. Limitation of Liability", "Nutritional values shown in the Application are informative and do not replace professional advice. The operator is not responsible for the accuracy of data obtained from external databases."),
            LegalSection("5. Changes to Terms", "The operator reserves the right to change these terms at any time. Users will be informed of changes through an Application update.")
        )
    ),
    notifications = NotificationStrings(
        reminders = "Notifications",
        enableReminders = "Meal reminders",
        noFoodTitle = "Forgot to eat?",
        noFoodBody = "You haven't logged any food today. Add something!",
        behindTitle = "You're behind on food",
        behindBody = "You've only logged %d%% of your calories. Don't forget to eat!",
        channelName = "Meal reminders"
    ),
    auth = AuthStrings(
        loginTitle = "Welcome to Kalky",
        loginSubtitle = "Sign in to continue",
        continueWithGoogle = "Continue with Google",
        continueWithApple = "Continue with Apple",
        continueWithEmail = "Continue with Email",
        email = "Email",
        password = "Password",
        signIn = "Sign in",
        createAccount = "Create account",
        noAccount = "Don't have an account?",
        haveAccount = "Already have an account?",
        signOut = "Sign out",
        or = "or"
    ),
    date = DateStrings(
        daysShort = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
        months = listOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
    )
)

val LocalStrings = staticCompositionLocalOf { CzechStrings }

fun stringsFor(language: AppLanguage): AppStrings = when (language) {
    AppLanguage.CS -> CzechStrings
    AppLanguage.EN -> EnglishStrings
}

@Composable
fun rememberStrings(): AppStrings {
    val appPreferences: AppPreferences = koinInject()
    val language by appPreferences.language.collectAsState()
    return stringsFor(language)
}
