# Rendered UI evidence

`weather-fixture.png` was rendered from the real Compose UI by `ComposeSettingsTest` with Robolectric native graphics at 393 × 852 logical pixels. Temperature, conditions, wind and precipitation are synthetic test values; this is not a live weather observation. The timestamp uses the test run's Brisbane time to demonstrate the fresh-data layout.

The test also clicks the settings controls, returns to Today and asserts that Fahrenheit and tips visibility change the main screen. A separate host test verifies saved navigation restoration. This image is a development screenshot, not evidence of physical-device testing.
