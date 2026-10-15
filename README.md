# DayReady — Utility App

A small Android utility for checking current weather before leaving home. Based on the lecturer's CP3406 / CP5307 single-activity Compose skeleton. Original package and theme names are retained.

## Features
- At-a-glance temperature, apparent temperature, condition, wind and precipitation.
- Preparation tips from transparent rules using current weather, not a full-day prediction.
- Two screens: UtilityScreen and SettingsScreen, inside UtilityApp bottom navigation.
- Five preset city-centre locations; Celsius/Fahrenheit and tips visibility settings.
- Automatic initial fetch; refresh, loading indicator, friendly errors and retry.
- Old weather retained on failed refresh with an explicit out-of-date warning. Location changes clear old-city data.
- ViewModel + StateFlow, lifecycle-aware collection, repository interface and manual dependency injection.
- Retrofit/Gson networking with timeouts and cancellable coroutines. No API key or device-location permission.

## Open and run
1. Extract this ZIP and open this folder (the one containing settings.gradle.kts) in Android Studio.
2. Install Android SDK platform 36. Set Gradle JDK to JDK 17 in Android Studio settings.
3. Keep the supplied Gradle 8.13 wrapper and AGP 8.13.2 version catalog. Do not replace it with Gradle 9.6 from an earlier pasted example.
4. Sync Gradle while connected to the internet, then run on an emulator or device with API 26 or newer.
5. Allow internet access on the emulator/device. Switch location or tap Refresh to request weather.

Java/Kotlin bytecode targets remain 11 from the skeleton; the JDK running Gradle is 17.

## Architecture
MainActivity obtains the repository from DayReadyApplication's AppContainer and injects it into WeatherViewModel through a factory. The ViewModel calls WeatherRepository, which calls WeatherApi through Retrofit. JSON is mapped into validated Weather values. The ViewModel publishes UI state, and Compose reads that state with collectAsStateWithLifecycle. User actions call ViewModel methods. Settings survive rotation in the ViewModel but are not stored permanently.

## API attribution
Weather data by [Open-Meteo](https://open-meteo.com/), licensed under [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/). Current conditions are weather-model data, not a personal weather-station observation. This educational app uses the non-commercial endpoint.
- [Forecast API documentation](https://open-meteo.com/en/docs)
- [API terms](https://open-meteo.com/en/terms)
- [Retrofit](https://square.github.io/retrofit/)
- [Android architecture guidance](https://developer.android.com/topic/architecture)
- [AGP compatibility](https://developer.android.com/build/releases/agp-8-13-0-release-notes)

Requests use https://api.open-meteo.com/v1/forecast with latitude, longitude, current and timezone=auto. Temperature values stay Celsius internally; Fahrenheit is a display conversion. Wind is km/h; precipitation is mm for the API's current interval, not a daily total. The time displayed is the API weather timestamp in the returned city timezone.

## Validation status
Source/configuration checks and ZIP integrity checks completed in the preparation environment. A Gradle test/build command was attempted but stopped downloading Gradle with `Network is unreachable`. No successful compilation, Android Lint, automated test run, emulator run or live API integration test is claimed. Run these locally:

```sh
# macOS/Linux
bash gradlew testDebugUnitTest lintDebug assembleDebug
# Windows
# gradlew.bat testDebugUnitTest lintDebug assembleDebug
```

See TESTING_CHECKLIST.md and FILE_GUIDE.md. Unit test source covers conversions and meaningful preparation-rule boundaries.

## Assignment submission
- Develop and review the app yourself; verify it against the rubric and understand the code.
- Create your GitHub repository and make genuine, descriptive commits as you develop. No fabricated development history is included.
- Share the repository with your lecturer and coordinator, and include its URL in your submission.
- Write a 500–1000 word, 1–2 page Gibbs self-reflection about your actual experiences, then export it as PDF.
- Complete the required Declaration of AI-Generated Material. See REFLECTION_AND_AI_GUIDANCE.md.
- After local testing, use Android Studio File > Export > Export to ZIP for the final submission.

## Limitations
Preset locations only; no saved preferences, offline disk cache, automatic periodic updates or daily forecast. The Android launcher graphics and Material theme originate from the lecturer's skeleton. String localisation is a possible future improvement. Preparation tips are simple rules, not official severe-weather advice.
