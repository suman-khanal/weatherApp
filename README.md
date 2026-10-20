[README.md file](sandbox:/workspace/artifacts/dayready-feature-readme/README.md)

Since earlier downloads failed, you can also copy the following into a file named **`README.md`** in your project’s root folder.

````markdown
# DayReady

DayReady is an Android utility app for checking the weather before leaving home. It shows current conditions and a short preparation checklist, helping users decide whether to take rain gear, water or an extra layer.

The project builds on the lecturer’s CP3406 / CP5307 Compose template. It uses a single activity with two screens: Today and Settings.

## Core features and implementation

| Feature | Implementation |
|---|---|
| Current weather | `WeatherApi.kt` defines the Retrofit request to Open-Meteo. Gson maps the JSON response, and `NetworkWeatherRepository` validates the data before returning a `Weather` object. The screen displays temperature, feels-like temperature, conditions, wind and precipitation. |
| City selection | `Weather.kt` stores coordinates for Brisbane, Sydney, Melbourne, Perth and Kathmandu. Selecting a city calls `WeatherViewModel.selectCity()`, clears the previous city’s weather and starts a new request. No device-location permission is needed. |
| Preparation tips | `preparationTips()` in `PreparationTips.kt` applies rules to current conditions. It suggests extra layers below 18°C feels-like, water at 28°C or above, and wind preparation at 30 km/h or above. Weather codes and precipitation trigger additional advice. |
| Celsius/Fahrenheit | `temperatureLabel()` converts Celsius to Fahrenheit for display. Both temperature and feels-like values use the selected setting. Internal values remain Celsius, so changing units does not require another API request. |
| Tips and appearance settings | Settings callbacks update `WeatherUiState`. Users can show or hide the checklist and choose device, light or dark appearance. The Material 3 theme uses a teal palette and matching sun/cloud launcher artwork. |
| Refresh and error recovery | The ViewModel fetches weather when it is created and provides manual refresh. Loading and error states are displayed in Compose. Failed refreshes retain previous weather with a warning. |
| Request cancellation | Refresh cancels the previous request. A request identifier also prevents an older response from replacing a newer result after rapid city changes. |
| State restoration | The ViewModel retains state during rotation. `SavedStateHandle` restores settings when Android restores the session, while `rememberSaveable` restores the selected tab. |
| Responsive layout | Reusable Compose components display weather, metrics, tips and settings. Both screens scroll. Wide layouts can show weather and tips side by side; compact layouts and larger text use stacked metrics. |

Preparation tips use current conditions. They are not a daily forecast or official severe-weather advice.

## Architecture

```text
MainActivity → WeatherViewModel → WeatherRepository → WeatherApi → Open-Meteo
                       ↓
                 WeatherUiState → Compose screens
```

`DayReadyApplication` owns `AppContainer`, which creates the OkHttp client, Retrofit service and repository. `MainActivity` supplies the repository through the ViewModel factory. This is manual dependency injection.

The ViewModel manages requests and publishes screen state through `StateFlow`. Compose observes that state with `collectAsStateWithLifecycle` and sends user actions back through callbacks. Networking stays outside the screen composables.

The Kotlin source is under:

`app/src/main/java/au/edu/jcu/assessment/utilityapp/`

- `data/`: city and weather models, Retrofit API and repository.
- `di/`: dependency construction in `AppContainer`.
- `ui/`: ViewModel, navigation, screens and preparation rules.
- `ui/theme/`: colours, typography and appearance selection.

## Networking and data handling

The app requests current weather from:

`https://api.open-meteo.com/v1/forecast`

Requests include the selected coordinates, weather fields, explicit units and `timezone=auto`.

The repository rejects missing fields and invalid measurements instead of displaying fabricated zero values. OkHttp uses a 15-second connection timeout, 20-second read timeout and 30-second call timeout.

Temperature remains Celsius internally. Wind uses km/h, and precipitation uses millimetres for the current API interval rather than a daily total. Timestamps use the returned city timezone.

The interface labels observations at least 30 minutes old as previous conditions. This freshness check does not automatically download new weather.

## Build and run

The project was tested with:

- Full JDK 21, including `javac`.
- Android SDK platform 36.
- Android build-tools 35.0.0.
- Gradle 8.13.
- Android Gradle Plugin 8.13.2.
- Kotlin 2.0.21.

Java and Kotlin bytecode target version 11. The minimum supported Android version is API 26.

1. Open the folder containing `settings.gradle.kts` in Android Studio.
2. Install the SDK components and select the full JDK as the Gradle JDK.
3. Sync the project with internet access.
4. Run the `app` module on a suitable device or emulator.

Brisbane is selected initially. No account or API key is required.

```sh
# macOS or Linux
bash gradlew testDebugUnitTest lintDebug assembleDebug

# Windows
gradlew.bat testDebugUnitTest lintDebug assembleDebug

# With a connected device or running emulator
bash gradlew connectedDebugAndroidTest
```

The debug APK is generated at:

`app/build/outputs/apk/debug/app-debug.apk`

## Testing

Validation recorded on 15 October 2026 included:

- 25 passing unit and host-side Compose tests.
- Successful app and test APK builds.
- Android Lint with no errors and 14 dependency-update notices.
- A successful live Open-Meteo endpoint check.

Tests cover preparation rules, conversions, API mapping, invalid data, HTTP failures, refresh recovery, request races, saved settings, timezone freshness and Compose interactions.

Five device tests compile but were not run because the software emulator did not complete its first boot. Physical-device rotation, accessibility and network-recovery checks remain outstanding.

A GitHub Actions workflow is included to run host tests, lint and the APK build. Remote workflow results require separate verification.

## Attribution

Weather data comes from [Open-Meteo](https://open-meteo.com/) under [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/).

The app uses the [non-commercial forecast endpoint](https://open-meteo.com/en/docs). See the [API terms](https://open-meteo.com/en/terms).

The lecturer’s template supplied the starting structure and navigation concept. ChatGPT/Codex assisted with implementation, troubleshooting, testing and documentation. This assistance should be included in the assignment’s required AI declaration.

## Limitations

The app uses preset city-centre locations. It has no GPS tracking, persistent weather cache, background refresh, daily forecast or severe-weather alert service. Preferences are not permanently stored between fresh sessions.
````