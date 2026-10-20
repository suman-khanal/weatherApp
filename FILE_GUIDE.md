# Beginner-friendly file guide

Read MainActivity, UtilityScreens, WeatherViewModel, WeatherRepository, WeatherApi and AppContainer in that order.

## Kotlin source files

| File (under app/src/main/java/au/edu/jcu/assessment/utilityapp) | Purpose |
|---|---|
| `MainActivity.kt` | Android entry point. Creates/retrieves the ViewModel with injected repository and collects its state for Compose. |
| `DayReadyApplication.kt` | Application-wide owner of the dependency container, created lazily once per process. |
| `data/WeatherApi.kt` | Retrofit endpoint and JSON data classes. @Query creates URL parameters; @SerializedName maps API snake_case names. |
| `data/WeatherRepository.kt` | City presets, validated Weather model, repository interface and network implementation. Rejects missing API fields. |
| `di/AppContainer.kt` | Manual DI wiring: constructs timed HTTP client, Retrofit service and repository. |
| `ui/WeatherViewModel.kt` | Owns selected city, units, tips visibility, loading/error/weather state. Cancels superseded requests and preserves lifecycle state. |
| `ui/PreparationTips.kt` | Pure functions for temperature formatting, WMO condition names and packing suggestions. Celsius stays the internal unit. |
| `ui/UtilityScreens.kt` | UtilityApp scaffold, utility/settings screens, preference switches and offline preview. UI receives state and callbacks. |
| `ui/theme/Color.kt` | Lecturer skeleton theme colour definitions. |
| `ui/theme/Theme.kt` | Lecturer skeleton Material theme, including supported light/dark/dynamic colour behaviour. |
| `ui/theme/Type.kt` | Lecturer skeleton typography choices. |

## Configuration and resources

| File / family | Purpose |
|---|---|
| `app/src/main/AndroidManifest.xml` | Registers the Application and activity; grants INTERNET permission. |
| `app/src/main/res/values/strings.xml` | Launcher app name: DayReady. |
| `app/src/main/res/values/themes.xml` | Android window theme used before Compose starts. |
| `app/src/main/res/xml/*.xml` | Skeleton backup/data extraction rules. |
| `app/src/main/res/drawable/*` and `mipmap-*/*` | Skeleton launcher artwork and icons at different resolutions. |
| `build.gradle.kts` | Root plugin declarations. |
| `app/build.gradle.kts` | SDK 36, minimum SDK 26, bytecode targets, Compose and dependency declarations. |
| `settings.gradle.kts` | Project name, included app module and dependency repositories. |
| `gradle/libs.versions.toml` | Skeleton dependency/version aliases. New weather dependencies are explicit in app build file. |
| `gradle.properties` | Skeleton Gradle/Android project preferences. |
| `gradle/wrapper/gradle-wrapper.properties` | Pins the Gradle download to 8.13. |
| `gradle/wrapper/gradle-wrapper.jar` | Binary wrapper bootstrap; do not edit. |
| `gradlew` / `gradlew.bat` | Gradle launch scripts for Unix/Windows. |
| `app/proguard-rules.pro` | Skeleton rules for optional code shrinking; release shrinking is disabled. |
| `.gitignore` files | Keep generated outputs and machine-specific files out of Git. |
| `app/src/test/.../PreparationTipsTest.kt` | Added JUnit tests for conversion and preparation-rule boundaries. |
| `app/src/test/.../ExampleUnitTest.kt` | Original starter arithmetic example; not evidence of app correctness. |
| `app/src/androidTest/.../ExampleInstrumentedTest.kt` | Original starter device package-name test. |
| `README.md` | Setup, features, architecture, references and validation status. |
| `TESTING_CHECKLIST.md` | Local verification steps and expected outcomes. |
| `REFLECTION_AND_AI_GUIDANCE.md` | Gibbs prompts and an editable AI declaration draft. |
| `FILE_GUIDE.md` | This explanation and complete file inventory. |

## How data moves

1. AppContainer builds WeatherApi and injects it into NetworkWeatherRepository.
2. MainActivity injects that repository into WeatherViewModel using Factory.
3. On launch, refresh or a location change, the ViewModel launches a coroutine.
4. The repository asks Retrofit to download JSON and validates required fields.
5. The ViewModel updates StateFlow; lifecycle-aware Compose collection receives it.
6. UtilityScreen renders loading/error/weather. Settings callbacks change ViewModel state.

A Composable describes the screen. A ViewModel owns its state. A repository separates data access from screen logic. Dependency injection means passing dependencies into classes rather than constructing networking in the UI.

## Complete packaged file inventory

- `.gitignore`
- `README.md`
- `REFLECTION_AND_AI_GUIDANCE.md`
- `TESTING_CHECKLIST.md`
- `app/.gitignore`
- `app/build.gradle.kts`
- `app/proguard-rules.pro`
- `app/src/androidTest/java/au/edu/jcu/assessment/utilityapp/ExampleInstrumentedTest.kt`
- `app/src/main/AndroidManifest.xml`
- `app/src/main/java/au/edu/jcu/assessment/utilityapp/DayReadyApplication.kt`
- `app/src/main/java/au/edu/jcu/assessment/utilityapp/MainActivity.kt`
- `app/src/main/java/au/edu/jcu/assessment/utilityapp/data/WeatherApi.kt`
- `app/src/main/java/au/edu/jcu/assessment/utilityapp/data/WeatherRepository.kt`
- `app/src/main/java/au/edu/jcu/assessment/utilityapp/di/AppContainer.kt`
- `app/src/main/java/au/edu/jcu/assessment/utilityapp/ui/PreparationTips.kt`
- `app/src/main/java/au/edu/jcu/assessment/utilityapp/ui/UtilityScreens.kt`
- `app/src/main/java/au/edu/jcu/assessment/utilityapp/ui/WeatherViewModel.kt`
- `app/src/main/java/au/edu/jcu/assessment/utilityapp/ui/theme/Color.kt`
- `app/src/main/java/au/edu/jcu/assessment/utilityapp/ui/theme/Theme.kt`
- `app/src/main/java/au/edu/jcu/assessment/utilityapp/ui/theme/Type.kt`
- `app/src/main/res/drawable/ic_launcher_background.xml`
- `app/src/main/res/drawable/ic_launcher_foreground.xml`
- `app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml`
- `app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml`
- `app/src/main/res/mipmap-hdpi/ic_launcher.webp`
- `app/src/main/res/mipmap-hdpi/ic_launcher_round.webp`
- `app/src/main/res/mipmap-mdpi/ic_launcher.webp`
- `app/src/main/res/mipmap-mdpi/ic_launcher_round.webp`
- `app/src/main/res/mipmap-xhdpi/ic_launcher.webp`
- `app/src/main/res/mipmap-xhdpi/ic_launcher_round.webp`
- `app/src/main/res/mipmap-xxhdpi/ic_launcher.webp`
- `app/src/main/res/mipmap-xxhdpi/ic_launcher_round.webp`
- `app/src/main/res/mipmap-xxxhdpi/ic_launcher.webp`
- `app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.webp`
- `app/src/main/res/values/colors.xml`
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values/themes.xml`
- `app/src/main/res/xml/backup_rules.xml`
- `app/src/main/res/xml/data_extraction_rules.xml`
- `app/src/test/java/au/edu/jcu/assessment/utilityapp/ExampleUnitTest.kt`
- `app/src/test/java/au/edu/jcu/assessment/utilityapp/PreparationTipsTest.kt`
- `build.gradle.kts`
- `gradle/gradle-daemon-jvm.properties`
- `gradle/libs.versions.toml`
- `gradle/wrapper/gradle-wrapper.jar`
- `gradle/wrapper/gradle-wrapper.properties`
- `gradle.properties`
- `gradlew`
- `gradlew.bat`
- `settings.gradle.kts`
