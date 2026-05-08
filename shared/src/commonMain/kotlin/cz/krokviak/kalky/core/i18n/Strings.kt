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

/**
 * [daysShort]: Mon..Sun (size 7).
 * [months]: Jan..Dec (size 12).
 */
data class DateStrings(
    val daysShort: List<String>,
    val months: List<String>
)

data class LegalStrings(
    val sectionTitle: String,
    val termsTitle: String,
    val privacyTitle: String
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
    val veryActive: String
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
    val dateEnd: String
)

data class NutrientEditStrings(
    val title: String,
    val macronutrients: String
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
    val unitPieces: String
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
        veryActive = "Velmi aktivn\u00ED"
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
        dateEnd = "Konec"
    ),
    nutrientEdit = NutrientEditStrings(
        title = "\u00DAprava makro\u017Eivin",
        macronutrients = "Makro\u017Eiviny"
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
        unitPieces = "ks"
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
        privacyTitle = "Ochrana osobn\u00EDch \u00FAdaj\u016F"
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
        veryActive = "Very active"
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
        dateEnd = "End"
    ),
    nutrientEdit = NutrientEditStrings(
        title = "Edit macronutrients",
        macronutrients = "Macronutrients"
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
        unitPieces = "pcs"
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
        privacyTitle = "Privacy Policy"
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

@Composable
fun rememberStrings(): AppStrings {
    val appPreferences: AppPreferences = koinInject()
    val language by appPreferences.language.collectAsState()
    return when (language) {
        AppLanguage.CS -> CzechStrings
        AppLanguage.EN -> EnglishStrings
    }
}
