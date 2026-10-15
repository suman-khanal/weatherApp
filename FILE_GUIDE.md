# Source guide

Start with `MainActivity`, then `UtilityScreens`, `WeatherViewModel`, `WeatherRepository`, `WeatherApi` and `AppContainer`.

Paths below are relative to `app/src/main/java/au/edu/jcu/assessment/utilityapp/`.

| File | Responsibility |
|---|---|
| `MainActivity.kt` | Activity entry, factory injection, lifecycle-aware state collection and system-bar contrast for the chosen theme. |
| `DayReadyApplication.kt` | Owns the application-scoped dependency container. |
| `data/Weather.kt` | City presets and immutable weather domain values in SI units. |
| `data/WeatherApi.kt` | Retrofit endpoint, explicit units, query parameters and nullable response DTOs. |
| `data/WeatherRepository.kt` | Repository interface and validated network-to-domain mapping. |
| `di/AppContainer.kt` | Constructs HTTP timeouts, Retrofit/Gson, API and repository once per process. |
| `ui/WeatherViewModel.kt` | Immutable UI state, saved settings, refresh, cancellation, race protection and distinct failure types. |
| `ui/UtilityScreens.kt` | Two-tab scaffold, Back behavior, saved navigation state and previews. |
| `ui/WeatherScreen.kt` | Current conditions, loading/retry/previous-data states, adaptive weather/checklist arrangement and timestamp freshness. |
| `ui/WeatherComponents.kt` | Shared brand header, section headings and metric tiles. |
| `ui/SettingsScreen.kt` | Accessible location, units, tips and theme controls. |
| `ui/PreparationTips.kt` | Pure formatting, WMO condition labels and explicit preparation rules. |
| `ui/theme/ThemeMode.kt` | Device/light/dark preference values. |
| `ui/theme/Color.kt`, `Theme.kt`, `Type.kt` | DayReady palette, theme selection and typography. |

Resources: `values/strings.xml` contains screen chrome and accessibility text; condition labels and rule text live with the pure presentation functions. `drawable/ic_launcher_*` contains the vector logo; `mipmap-anydpi` wires it into adaptive launchers. `values/themes.xml` is the Android window theme. The manifest registers the Application/activity and requests only Internet access.

Tests: `src/test` contains conversion/rule boundaries, ViewModel scenarios, MockWebServer/Retrofit integration, timezone freshness and host-side Robolectric/Compose interaction tests. `src/androidTest` checks rendered weather, hidden tips, retry, large text and settings retained through Activity recreation.

Build: `gradle/libs.versions.toml` centralizes dependencies; `app/build.gradle.kts` configures SDK 36, Compose and tests. The supplied Gradle 8.13 wrapper is retained. Select a full JDK 21 through Android Studio or `JAVA_HOME`; there is no vendor-specific daemon download.

Documentation: `docs/REQUIREMENTS.md` maps the rubric; `TESTING_CHECKLIST.md` records validation and remaining manual checks; `REFLECTION_AND_AI_GUIDANCE.md` provides prompts for the student's own reflection and declaration.
