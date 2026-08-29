# Jak spustit Kalky

Tento návod popisuje spuštění aplikace pro Android, lokálního backendu a iOS verze.

## Požadavky

- Android Studio s JDK 21 (JDK 25 není s použitou verzí Android Gradle Pluginu kompatibilní)
- Android emulátor nebo zařízení s Androidem 11 / API 30 a novějším
- [Bun](https://bun.sh/) 1.0+ pro lokální backend
- Pro iOS: macOS, Xcode a iOS Simulator

## Android

1. Otevřete kořenovou složku projektu v Android Studiu.
2. V nastavení Gradle nastavte JDK 21 (v Android Studiu obvykle stačí jeho vestavěné JDK).
3. Počkejte na dokončení Gradle synchronizace.
4. Vyberte konfiguraci `app`, zvolte emulátor nebo připojené zařízení a spusťte ji tlačítkem Run.

Z příkazové řádky lze sestavit a nainstalovat debug verzi takto:

```bash
./gradlew :app:installDebug
```

Aplikace používá Firebase konfiguraci uloženou v repozitáři a jako výchozí hodnotu používá nasazený backend. Pro běžné spuštění Android aplikace proto není nutné spouštět backend lokálně.

## Lokální backend

Lokální backend je potřeba jen při jeho vývoji nebo pro testování vlastních změn na API.

```bash
cd backend
bun install
cp .env.example .env
# Do .env doplňte OPENAI_API_KEY, ADMIN_KEY a cestu k Firebase service-account JSON.
bun run dev
```

Server poběží na `http://localhost:3000`; funkčnost ověříte na adrese `http://localhost:3000/health`.

Proměnná `GOOGLE_APPLICATION_CREDENTIALS` musí ukazovat na Firebase service-account JSON. Je nutná pro přihlášení, App Check i všechny endpointy používané aplikací.

Chcete-li Android aplikaci připojit na lokální server, nastavte ve Firebase Remote Config hodnotu `backend_base_url`:

- Android emulátor: `http://10.0.2.2:3000`
- fyzické zařízení: `http://<IP-adresa-vašeho-počítače>:3000`

Zařízení i počítač musí být ve stejné síti. Po změně Remote Config aplikaci znovu spusťte, aby si hodnotu načetla.

## iOS

1. V kořeni projektu vytvořte framework pro iOS Simulator:

   ```bash
   ./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
   ```

2. Otevřete `iosApp/Kalky.xcodeproj` v Xcode.
3. Vyberte schéma `Kalky` a iOS Simulator, potom aplikaci spusťte (`⌘R`).

Při prvním otevření Xcode stáhne Swift Package závislosti. Detailní informace o testování iOS verze jsou v [iosApp/TESTING.md](../iosApp/TESTING.md).

## Rychlá kontrola

Po spuštění ověřte, že se zobrazí onboarding, a projděte přihlášení. Kamera, analýza fotky a čárové kódy vyžadují oprávnění zařízení a dostupný backend.
