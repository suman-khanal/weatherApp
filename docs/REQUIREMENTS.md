# Assignment and feedback mapping

Source: the supplied **CP3406 / CP5307 Assessment 1: Utility App**, four-page PDF, plus the photographed feedback checklist. The rubric weights are 20% each for code quality, purpose, Android APIs and Compose; 10% each for GitHub and reflection. This is an evidence map, not a prediction of a grade.

| Criterion | Implementation / evidence | Student review still needed |
|---|---|---|
| Code quality (20%) | Separate `data`, `di`, `ui` and `ui/theme` packages; descriptive domain types; explicit imports in new screens; small UI components; comments explaining validation, units and request races. Dependencies centralized in the version catalog. | Explain the request flow and justify the rules in your own words; inspect IDE warnings. |
| Utility purpose (20%) | One immediate check before heading out: large temperature/condition, two supporting metrics and a short packing checklist. No complex navigation, account or permission flow. | Try your daily routine; record whether the most relevant information is visible quickly. |
| Android APIs (20%) | Retrofit/Gson + timed OkHttp client, repository interface, manual DI, ViewModel, coroutine cancellation, StateFlow, lifecycle-aware collection and SavedStateHandle. MockWebServer exercises actual Retrofit serialization and HTTP errors. Host-side Compose tests verify settings interactions and saved tab restoration. | Demonstrate live refresh and recovery on your own device/network. |
| Compose (20%) | Material 3 scaffold and two tabs; modular hero/metrics/checklist/settings components; matching vector logo and palette; light/dark/device themes; semantic radio groups/switches/headings; scrollable and responsive layout. | Review portrait, landscape, large text, TalkBack, contrast and launcher masking on a physical device. |
| GitHub (10%) | Clear README, file guide, automated build workflow and genuine commits for the improvements. Initial 50 commits came from a single ZIP snapshot. | Share access with lecturer/coordinator; make real commits while you learn and revise. A snapshot split does not demonstrate weeks of continuous development. |
| Reflection (10%) | Gibbs prompts and AI declaration guidance, with no invented personal experiences. | Write and export your own 500–1000 word, 1–2 page reflection PDF and complete the official AI declaration. |

## Feedback-specific checks

- **Settings affect the main screen:** units change both displayed temperatures; city changes trigger a new request; tips visibility changes the checklist; theme changes the entire app.
- **Rotation handled:** ViewModel state and the selected tab use lifecycle-aware restoration; saved settings and tab restoration have passing host tests. The compiled device test for Activity recreation is unrun; actual device rotation still needs acceptance testing.
- **Logo and colour consistency:** `ic_launcher_foreground.xml` is reused in `BrandHeader`; its teal background and gold sun match the Material palette. Adaptive icons cover all supported Android versions (minSdk 26).
- **Background versus foreground and space:** contrasting Material containers distinguish headline conditions, secondary metrics, errors and tips. Width is constrained on tablets; metrics stack at compact widths or large fonts. Both screens scroll.
- **Minimal interactions:** automatic first fetch, a manual refresh button, two navigation tabs and labelled settings controls. Back from Settings returns to Today.
- **Language/music examples:** the assignment requires settings controlling some content/functionality, not these particular controls. DayReady provides directly relevant weather and appearance settings instead.

## Honest boundaries

Passing automated checks cannot establish an HD result. Usability, the student's understanding, continuous personal development and a thoughtful reflection need direct student evidence. Tips are based on current conditions and do not predict the full day or establish that going outside is safe.
