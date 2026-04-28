# Email – stav projektu Kalky (PV239)

**Předmět:** PV239 – Kalky – stav projektu a konzultace

Dobrý den,

posíláme krátký stav našeho semestrálního projektu **Kalky** – aplikace pro sledování jídla a výživy pro Android a iOS (Kotlin Multiplatform + Compose Multiplatform).

Autoři: **Jan Herold (učo 550508)**, **Jiří Krokviak (učo 582916)**

**Repozitář:** https://github.com/Empatixx/kalai

Repozitář je **privátní** – máme v něm napojené CI/CD přímo na produkci včetně secrets (Firebase service account, OpenAI API key, admin klíč). Pokud potřebujete přístup ke kódu, rádi nahrajeme zrcadlenou verzi (bez secrets) na **GitLab FI** a přidáme vás s rolí **Reporter**.

---

## Co máme hotové

**Architektura a infrastruktura**
- Kotlin Multiplatform projekt (shared module pro UI, VM, DB, síť; Android app jako tenký shell; iOS přes `ComposeUIViewController`)
- DI (Koin), SQLDelight, Ktor, kotlinx-serialization/datetime, multiplatform-settings
- CI pro Android, iOS a backend (GitHub Actions)
- Backend: Bun + SQLite server (`/cal` analýza fotky přes OpenAI Vision, `/api/barcode/:code`, `/api/search`, `/api/auth/me`)
- Firebase Auth (Google + Apple Sign-In) pro Android i iOS, ID token automaticky připojovaný do requestů

**Implementované obrazovky (funkční na Androidu, renderují se i na iOS)**
- Onboarding (jazyk, jednotky, téma, pohlaví, váha, výška, věk, aktivita, cíl, makra, promo kód)
- Domovská obrazovka (denní přehled kalorií a makroživin, week date-picker, streak, seznam jídel)
- Přidání jídla 4 způsoby: foto + AI analýza, scan čárového kódu, textové vyhledávání, manuální zadání
- Detail jídla (editace, re-analýza fotky, share, delete)
- Úprava denních cílů makroživin
- Vlastní (custom) jídla + kompozitní jídla z ingrediencí
- Analytika (váhový trend, bar charts nutrice)
- Profil (BMI, aktivita), nastavení (téma, jazyk CZ/EN, jednotky, notifikace)
- Meal reminder notifikace (WorkManager)
- Streak systém

## Co chceme stihnout do 11. týdne (druhá konzultace)

- Dokončit iOS paritu: kamera (AVFoundation) a barcode scanner plně funkční, iOS Firebase notifikace
- Nahradit `StubAuthViewModel` na iOS reálnou Firebase implementací
- Doladit iOS UI (iOS-inspired design je cíl i pro Android)
- Rozšířit backend databázi produktů (scraper pro Kaufland už je v repu, doplnit další zdroje)
- Testování na reálných zařízeních, oprava chyb, polishing
- Příprava finálního buildu a krátké prezentace/videa

## Konzultace

Preferujeme **[DOPLNIT: během semináře / po semináři / online, den a čas]**. Jsme flexibilní, stačí dát vědět termín, který vám vyhovuje.

V 8. týdnu jsme připraveni projekt ukázat živě (Android emulátor + iOS simulátor).

S pozdravem,
Jan Herold, Jiří Krokviak
