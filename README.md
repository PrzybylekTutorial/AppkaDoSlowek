# WordWise (Android, Kotlin, Jetpack Compose)

Aplikacja do nauki słówek z funkcjami: dodawanie/edycja, lista, tryb nauki (flashcards), powtórki SRS, import/eksport CSV, lokalna baza Room, motywy jasny/ciemny i widget na ekran główny (Glance).

## Funkcje
- Dodawanie/edycja/usuwanie słówek (term, translation, example)
- Lista słówek z filtrem alfabetycznym
- Tryb nauki (flashcards): sprawdzanie tłumaczenia, ocena poprawności, aktualizacja SRS
- Prosty SRS: odstępy 1/3/7/14/30/60/120 dni w zależności od streaku
- Room (SQLite) + Flow
- DataStore: preferencje motywu (System/LIGHT/DARK)
- Import/eksport CSV przez Storage Access Framework
- Widget Glance AppWidget: wyświetla losowe/kolejne słówko, możliwość przewijania

## Struktura
- `app/src/main/java/com/example/wordwise/data`: Room (Entity/Dao/Db), Repo, DataStore
- `app/src/main/java/com/example/wordwise/domain`: SRS
- `app/src/main/java/com/example/wordwise/ui`: Compose (nawigacja, ekrany, motyw)
- `app/src/main/java/com/example/wordwise/viewmodel`: ViewModel-e (Flow/Coroutines)
- `app/src/main/java/com/example/wordwise/widget`: Glance widget

## Build
Wymagania:
- JDK 17
- Android Studio / Android SDK (compileSdk 35, minSdk 26)
- Gradle 8.7+ (AGP 8.5.2)

Kroki (pierwsze uruchomienie):
1) W katalogu projektu uruchom wrapper:
```bash
gradle wrapper --gradle-version 8.7
./gradlew tasks
```
2) Zbuduj aplikację:
```bash
./gradlew :app:assembleDebug
```
3) Uruchom na emulatorze/urządzeniu z Android Studio.

## Import/eksport CSV
- Ekran listy: przyciski Import/Eksport
- CSV format: nagłówki `term,translation,example`, pola w cudzysłowie; przykład:
```csv
term,translation,example
"house","dom","This is my house."
"cat","kot","The cat sleeps."
```

## Widget
- Dodaj widżet „WordWise” na ekran główny
- Strzałki „<” i ">" przewijają słówka zapisując indeks w stanie Glance (Preferences)

## Uwaga
- Repozytorium CSV jest uproszczone (bez transakcji/importu wsadowego)
- Migracje Room są destrukcyjne (fallbackToDestructiveMigration) w wersji 1