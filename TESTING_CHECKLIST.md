# DayReady testing checklist

Status: manual checks below are pending on your machine. Record real outcomes and screenshots.

| Check | Expected result |
|---|---|
| Gradle sync | Dependencies resolve with supplied wrapper and JDK 17 |
| testDebugUnitTest | Preparation rule and conversion tests pass |
| lintDebug / assembleDebug | Review lint output; debug APK builds |
| First launch online | Loading indication, then Brisbane current weather |
| Settings: Sydney | Fetch Sydney, clear Brisbane data while loading |
| Rapid location switching | Final selected city matches displayed data |
| Fahrenheit toggle | Temperature and feels-like change; wind/rain retain units |
| Hide/show tips | Preparation cards hide/show immediately |
| Settings → home → settings | Preferences retained during session |
| Rotate in both screens | Selected tab, city and settings retained |
| Back from settings | Returns to utility screen |
| Disable internet before initial launch | Friendly error and retry; no fabricated weather |
| Disable internet after success; refresh | Previous data plus stale warning |
| Re-enable internet; retry | Error clears and data updates |
| Font scale 200%, narrow screen, landscape | Scroll works and controls remain reachable |
| TalkBack | Navigation and switch labels are announced |
| Dark theme | Text and cards remain readable |
| Attribution link | Opens Open-Meteo in browser |
| New app session | Default settings restored; initial fetch runs |

## Preparation-environment checks
XML and version catalog parsing, file/reference review, and archive integrity checks passed. Gradle execution was blocked at distribution download by unavailable network access. Tests are supplied but not executed. APK is not included.
