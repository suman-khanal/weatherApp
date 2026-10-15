# DayReady validation

Validation date: 15 October 2026 (Australia/Brisbane). Record subsequent runs separately; do not describe a supplied test as passed merely because it exists.

## Verified in this cloud task

- `testDebugUnitTest`: **25 tests passed**, no failures, errors or skipped tests.
  - 9 conversion/preparation-rule tests, including cold/hot/wind thresholds, freezing rain, storms and unknown codes.
  - 7 ViewModel tests for loading, recovery, city changes, late cancelled responses, restored settings, no unnecessary refetches and invalid data.
  - 5 real Retrofit/Gson + MockWebServer tests for query/units mapping, missing fields, invalid measurements/timezones and HTTP 503 handling.
  - 2 freshness tests for the 30-minute boundary and Brisbane/Kathmandu timezone offsets.
  - 2 Robolectric/Compose interaction tests for unit/tips changes on the main screen and saved navigation restoration.
- `lintDebug`: **0 errors**, 14 dependency-update notices. No app source/resource lint warnings remain. Pinned compatible versions are retained; a major dependency upgrade is a separate change.
- `assembleDebug`: **passed**; debug APK generated.
- `assembleDebugAndroidTest`: **passed**; the five UI/device tests compile.
- Direct HTTPS request to Open-Meteo: **passed**; complete Brisbane conditions, Celsius, km/h and `Australia/Brisbane` returned. This is an endpoint check, separate from device networking.
- Reusable cloud setup script rerun: **passed** with full Temurin JDK 21 and installed Android SDK components.

Commands used from the repository root:

```sh
export JAVA_HOME=/workspace/toolchains/jdk-21.0.12.1+1
export GRADLE_USER_HOME=/workspace/.gradle-home
export ANDROID_HOME=/workspace/android-sdk
export ANDROID_USER_HOME=/workspace/.android-home
export JAVA_TOOL_OPTIONS=-Djavax.net.ssl.trustStore=/etc/ssl/certs/java/cacerts
bash gradlew --no-daemon testDebugUnitTest lintDebug assembleDebug assembleDebugAndroidTest
```

These paths are cloud-specific. On your own machine use the full JDK/SDK configured in Android Studio. XML results are in `app/build/test-results/testDebugUnitTest/`; rendered reports are in `app/build/reports/`.

## Device validation

Five instrumented tests are provided and compile: known-weather/unit rendering, tips visibility, offline retry, double font scale, and settings/navigation retained through Activity recreation. **These five device tests were not run**: the API 35 emulator did not complete its first boot after about 20 minutes without hardware acceleration (`/dev/kvm` is unavailable). The emulator was stopped after the bounded attempt. This is an environment limitation, not a demonstrated app failure.

The two Robolectric tests ran successfully against Android API 35 on the host, using actual Compose semantics and clicks. They verify changing Fahrenheit/tips affects the main screen and that the Settings tab restores from saved Compose state. A native-rendered test screenshot is saved by the first test at `app/build/outputs/robolectric/dayready-test-fixture.png`. Its weather values are synthetic test data, not a live observation. These host checks do not replace physical-device lifecycle or accessibility checks.

## Student manual acceptance checklist

| Check | Expected result |
|---|---|
| First launch online | Loading indication, then Brisbane current conditions; matching units/timezone |
| Settings: Sydney | Fetch Sydney, immediately clear Brisbane data |
| Rapid city switching | Last selection matches displayed data |
| Fahrenheit | Both temperatures change; wind/rain retain units |
| Hide/show tips | Checklist hides/shows immediately |
| Light/dark/device appearance | Whole UI and system-bar icon contrast follow choice |
| Rotate on each screen | Tab, city, units, tips and appearance retained |
| Back from Settings | Returns to Today |
| Offline before launch | Labelled connection error and working retry, no fabricated weather |
| Offline after success; refresh | Previous data retained with an explicit warning |
| Reconnect and retry | Error clears and fresh data appears |
| Leave weather open for 30+ minutes | Previous-conditions notice appears without another fetch |
| 200% fonts, narrow screen and landscape | Readable text; scrolling reaches every control |
| TalkBack | Labelled radio groups, switches, headings and navigation; no duplicated switch targets |
| Launcher and app header | Same teal/sun/cloud mark, correctly masked on supported launchers |
| Attribution | Opens Open-Meteo; no crash if a browser is unavailable |
| Process killed and session restored | Restored choices; weather fetched again |
| Deliberately fresh session | Default settings, automatic initial fetch |

Physical-device TalkBack, actual rotation/landscape, process-death restoration and long-running freshness checks remain student acceptance checks. Unit tests cover the saved values and freshness calculation; they are not claims of manual device execution. The GitHub Actions workflow is supplied; a successful local build does not prove that a remote Actions run has passed.
