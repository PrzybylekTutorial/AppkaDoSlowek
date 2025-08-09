# WordWise - Instrukcje Testowania

## ⚠️ Wymagania wstępne - Android SDK

**UWAGA:** Do uruchomienia testów wymagany jest Android SDK. W aktualnym środowisku nie jest on skonfigurowany.

### Konfiguracja Android SDK:

1. **Zainstaluj Android Studio** lub Android Command Line Tools
2. **Ustaw zmienną środowiskową ANDROID_HOME:**
   ```bash
   export ANDROID_HOME=$HOME/Android/Sdk
   export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
   ```
3. **Lub utwórz plik local.properties:**
   ```bash
   echo "sdk.dir=$HOME/Android/Sdk" > local.properties
   ```

### Alternatywnie - tylko sprawdzenie składni:
```bash
# Sprawdzenie czy kod się kompiluje (bez SDK)
./gradlew :app:compileDebugKotlin
```

---

## Przegląd

Aplikacja WordWise ma skonfigurowane różne typy testów:
- **Testy jednostkowe** - testują logikę biznesową (SRS, ViewModels)
- **Testy instrumentacyjne** - testują UI z Jetpack Compose
- **Testy manualne** - testowanie aplikacji na urządzeniu/emulatorze

## Wymagania do testowania

### Środowisko
- JDK 17+ ✅ (obecnie: OpenJDK 21)
- Android Studio / Android SDK (compileSdk 35, minSdk 26) ❌ **WYMAGANE**
- Gradle 8.7+ ✅ (skonfigurowane)
- Emulator Android lub fizyczne urządzenie (dla testów instrumentacyjnych)

### Zależności testowe
Projekt zawiera następujące biblioteki testowe:
- JUnit 4.13.2 (testy jednostkowe)
- Kotlinx Coroutines Test (testowanie coroutines)
- Arch Core Testing (testowanie LiveData/StateFlow)
- MockK (mockowanie obiektów)
- Compose UI Test (testowanie UI Compose)
- Espresso (dodatkowe narzędzia do testów UI)

## Komendy do uruchamiania testów

### 1. Testy jednostkowe (Unit Tests)

```bash
# Uruchomienie wszystkich testów jednostkowych
./gradlew test

# Uruchomienie testów jednostkowych tylko dla modułu app
./gradlew :app:testDebugUnitTest

# Uruchomienie konkretnej klasy testowej
./gradlew :app:testDebugUnitTest --tests "com.example.wordwise.domain.srs.SrsSchedulerTest"

# Uruchomienie konkretnego testu
./gradlew :app:testDebugUnitTest --tests "com.example.wordwise.viewmodel.WordViewModelTest.upsert calls repository with correct entity for new word"
```

### 2. Testy instrumentacyjne (UI Tests)

**UWAGA:** Wymagany emulator Android lub podłączone urządzenie!

```bash
# Uruchomienie wszystkich testów instrumentacyjnych
./gradlew connectedAndroidTest

# Uruchomienie testów instrumentacyjnych dla modułu app
./gradlew :app:connectedDebugAndroidTest

# Uruchomienie konkretnej klasy testowej
./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.wordwise.ui.screens.list.WordListScreenTest
```

### 3. Wszystkie testy naraz

```bash
# Uruchomienie wszystkich testów (jednostkowych + instrumentacyjnych)
./gradlew check connectedAndroidTest
```

### 4. Testy bez Android SDK (tylko składnia)

```bash
# Sprawdzenie składni kodu (kompilacja)
./gradlew :app:compileDebugKotlin

# Sprawdzenie buildowalności projektu (bez testów)
./gradlew :app:assembleDebug --dry-run
```

## Raporty testów

Po uruchomieniu testów, raporty będą dostępne w:

### Testy jednostkowe:
```
app/build/reports/tests/testDebugUnitTest/index.html
```

### Testy instrumentacyjne:
```
app/build/reports/androidTests/connected/debug/index.html
```

## Struktura testów

### Testy jednostkowe (`app/src/test/`)
```
app/src/test/java/com/example/wordwise/
├── domain/srs/
│   └── SrsSchedulerTest.kt        # Testuje logikę SRS
└── viewmodel/
    └── WordViewModelTest.kt       # Testuje ViewModels z mockami
```

### Testy instrumentacyjne (`app/src/androidTest/`)
```
app/src/androidTest/java/com/example/wordwise/
└── ui/screens/list/
    └── WordListScreenTest.kt      # Testuje UI Compose
```

## Tworzenie nowych testów

### Test jednostkowy
1. Utwórz plik w `app/src/test/java/` w odpowiednim pakiecie
2. Dodaj adnotację `@Test` do metod testowych
3. Użyj MockK do mockowania zależności
4. Dla coroutines użyj `runTest` i `UnconfinedTestDispatcher`

### Test instrumentacyjny
1. Utwórz plik w `app/src/androidTest/java/` w odpowiednim pakiecie
2. Dodaj `@RunWith(AndroidJUnit4::class)` do klasy
3. Użyj `createComposeRule()` do testowania Compose UI
4. Używaj `composeTestRule.onNodeWithText()`, `performClick()` itp.

## Debugging testów

### Uruchomienie testów z dodatkowym logowaniem:
```bash
./gradlew test --info
./gradlew connectedAndroidTest --info
```

### Debugowanie konkretnego testu:
```bash
./gradlew :app:testDebugUnitTest --tests "NazwaKlasy.nazwa_testu" --debug-jvm
```

## Przykłady użycia

### Szybkie sprawdzenie czy wszystko działa:
```bash
# 1. Uruchom testy jednostkowe (szybkie)
./gradlew :app:testDebugUnitTest

# 2. Jeśli przeszły, uruchom testy UI (wolniejsze)
./gradlew :app:connectedDebugAndroidTest
```

### Development workflow:
```bash
# Podczas rozwoju funkcji, uruchamiaj tylko testy jednostkowe
./gradlew :app:testDebugUnitTest --tests "*WordViewModel*"

# Przed commitem, uruchom wszystkie testy
./gradlew check connectedAndroidTest
```

## Pokrycie kodu

Do sprawdzenia pokrycia kodu można dodać JaCoCo:
```bash
# W build.gradle.kts dodaj plugin:
# id("jacoco")

# Następnie uruchom:
./gradlew jacocoTestReport
```

## Częste problemy

1. **Testy instrumentacyjne nie działają** - Upewnij się, że emulator jest uruchomiony i odblokowny
2. **MockK nie działa** - Sprawdź czy używasz odpowiedniej wersji (mockk vs mockk-android)
3. **Testy Compose timeout** - Zwiększ timeout w `composeTestRule.waitForIdle()`
4. **Coroutines testy zawieszone** - Użyj `runTest` zamiast `runBlocking`

## Continuous Integration

Dla CI/CD pipeline, użyj:
```yaml
# GitHub Actions przykład
- name: Setup Android SDK
  uses: android-actions/setup-android@v3
  
- name: Run Unit Tests
  run: ./gradlew :app:testDebugUnitTest

- name: Run UI Tests
  run: ./gradlew :app:connectedDebugAndroidTest
  env:
    API_LEVEL: 29
```

---

## ✅ Gotowe komponenty testowe

W projekcie zostały utworzone następujące testy:

### Testy jednostkowe:
1. **`SrsSchedulerTest.kt`** - Testuje logikę SRS (Spaced Repetition System)
   - ✅ Weryfikuje prawidłowe interwały dla różnych streakow
   - ✅ Testuje graniczne przypadki (negatywne i wysokie streak)
   - ✅ Sprawdza maksymalne limity interwałów

2. **`WordViewModelTest.kt`** - Testuje logikę ViewModelu
   - ✅ Mockuje repository z użyciem MockK
   - ✅ Testuje operacje CRUD (upsert, delete)
   - ✅ Weryfikuje flow StateFlow i coroutines
   - ✅ Testuje funkcje import/export CSV

### Testy instrumentacyjne:
1. **`WordListScreenTest.kt`** - Testuje UI Compose
   - ✅ Testuje wyświetlanie listy słówek
   - ✅ Weryfikuje interakcje użytkownika (kliknięcia)
   - ✅ Sprawdza nawigację i callbacks
   - ✅ Testuje stan pustej listy

### Konfiguracja testowa:
- ✅ Struktura katalogów testowych
- ✅ Zależności testowe w build.gradle.kts
- ✅ Gradle wrapper skonfigurowany
- ✅ Kompletna dokumentacja testowania

## 🎯 Następne kroki

Po skonfigurowaniu Android SDK, możesz:

1. **Uruchomić testy jednostkowe:**
   ```bash
   ./gradlew :app:testDebugUnitTest
   ```

2. **Uruchomić testy UI (z emulatorem):**
   ```bash
   ./gradlew :app:connectedDebugAndroidTest
   ```

3. **Dodać więcej testów** dla innych ekranów:
   - LearnScreen (tryb nauki)
   - EditScreen (dodawanie/edycja słówek)
   - SettingsScreen (ustawienia motywu)

4. **Skonfigurować pokrycie kodu** z JaCoCo

5. **Dodać testy wydajnościowe** dla dużych zbiorów danych

## 📚 Przydatne linki

- [Android Testing Documentation](https://developer.android.com/training/testing)
- [Jetpack Compose Testing](https://developer.android.com/jetpack/compose/testing)
- [MockK Documentation](https://mockk.io/)
- [Coroutines Testing](https://kotlinlang.org/docs/coroutines-testing.html)