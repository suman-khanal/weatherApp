# DayReady — Weather Preparation App

An Android utility app built from the CP3406 / CP5307 starter template using Kotlin, Jetpack Compose and Material Design 3. It uses a single activity with navigation between the Today and Settings screens.

## Features

- Current weather for Brisbane, Sydney, Melbourne, Perth and Kathmandu, with city selection and no location permission required.
- Temperature, feels-like temperature, conditions, wind and precipitation readings.
- A short preparation checklist (extra layer, water, wind advice, rain gear) based on current conditions.
- Celsius/Fahrenheit display, an optional checklist, and device, light or dark appearance in Settings.
- Manual refresh, loading feedback, and retry after errors. A failed refresh keeps the previous weather with a warning.
- Rotation and session restoration, and layouts that adapt to wide screens and larger text.

## Run the project

1. Open the project root (the folder containing `settings.gradle.kts`) in Android Studio.
2. Install Android SDK platform 36 and build-tools 35.0.0, and select a full JDK 21 (including `javac`) as the Gradle JDK.
3. Sync Gradle, then run on an emulator or device running Android 8.0 / API 26 or later.

The Gradle wrapper is included. From the project root, use:

```bash
./gradlew assembleDebug
./gradlew testDebugUnitTest
./gradlew lintDebug
./gradlew connectedDebugAndroidTest   # requires a device or emulator
```

On Windows, use `gradlew.bat` instead of `./gradlew`. The debug APK is generated at `app/build/outputs/apk/debug/app-debug.apk`.

Internet access is required for dependency downloads and weather requests. The current API setup requires no API key. Brisbane is selected on first launch.

## Code structure

Source root: `app/src/main/java/au/edu/jcu/assessment/utilityapp/`

| Location | Responsibility |
| --- | --- |
| `DayReadyApplication` | Application class that owns `AppContainer` |
| `MainActivity` | Activity setup and ViewModel factory |
| `di/AppContainer` | Manual dependency injection: OkHttp client, Retrofit service and repository |
| `data/Weather.kt` | Weather model and preset city coordinates |
| `data/WeatherApi.kt` | Retrofit interface and Gson response mapping for Open-Meteo |
| `data/NetworkWeatherRepository` | Fetching, response validation and error handling |
| `ui/WeatherViewModel` | Request lifecycle, city selection, refresh and `StateFlow` state |
| `ui/WeatherUiState` | Screen state: weather, loading/error status, city, units and settings |
| `ui/PreparationTips.kt` | `preparationTips()` checklist rules and `temperatureLabel()` unit conversion |
| `ui/` screens and navigation | Today and Settings screens built from reusable Compose components |
| `ui/theme/` | Teal Material 3 palette, typography and appearance selection |

## Data and behaviour

Data comes from the [Open-Meteo forecast API](https://open-meteo.com/en/docs) (`https://api.open-meteo.com/v1/forecast`) and represents current conditions, not a daily forecast. Requests include the selected coordinates, weather fields, explicit units and `timezone=auto`. Wind is in km/h and precipitation in millimetres for the current API interval.

Temperature is held in Celsius internally, so changing units converts for display without a new request. Missing fields or invalid measurements are rejected and shown as an error rather than displayed as zero. Timestamps use the returned city timezone, and observations at least 30 minutes old are labelled as previous conditions; this does not trigger an automatic refresh.

Selecting another city or refreshing cancels the previous request, and a request identifier prevents an obsolete response from replacing a newer result. OkHttp uses a 15-second connection timeout, 20-second read timeout and 30-second call timeout.

Settings are restored with `SavedStateHandle` when Android restores the session, but they are not persisted across a fresh app launch. There is no GPS, weather cache, background refresh, daily forecast or severe-weather alert service. Preparation tips are general guidance and not official advice.

## Verification

Validation recorded on 15 October 2026 included 25 passing unit and host-side Compose tests, successful app and test APK builds, Android Lint with no errors (14 dependency-update notices), and a successful live Open-Meteo endpoint check. The tests cover preparation rules, conversions, API mapping, invalid data, HTTP failures, refresh recovery, request races, saved settings, timezone freshness and Compose interactions.

Five device tests compile but were not run because the software emulator did not complete its first boot. Physical-device rotation, accessibility and network-recovery checks are still outstanding. A GitHub Actions workflow runs host tests, lint and the APK build, but its remote results still need to be verified.

## Attribution

Weather data is provided by [Open-Meteo](https://open-meteo.com/) under [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/), using the non-commercial endpoint (see the [API terms](https://open-meteo.com/en/terms)).

Based on the MobileTech Utility App educational starter supplied for CP3406 / CP5307, which provided the starting structure and navigation concept. ChatGPT/Codex assisted with implementation, troubleshooting, testing and documentation, and should be included in the assignment's AI declaration.
