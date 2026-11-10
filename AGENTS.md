# Yuno Android SDK - Example App

Demo application showcasing all integration patterns for the Yuno Payments Android SDK (`com.yuno.payments:android-sdk`).

This file is the single source of instructions for engineers and coding agents (any model). `CLAUDE.md` only imports it.

## Tech Stack
- **Language:** Kotlin 1.8.0 (Compose compiler extension 1.4.0)
- **Platform:** Android (compileSdk 35, minSdk 23, targetSdk 35), Java 17 toolchain
- **UI:** Jetpack Compose with Material 3
- **Architecture:** MVVM (Activity + ViewModel + Compose Screen)
- **Build System:** Gradle wrapper 8.10, Android Gradle Plugin 8.4.0 (Groovy DSL)
- **Modules:** a single Gradle module, `:app` (see `settings.gradle`)
- **SDK Version:** `com.yuno.payments:android-sdk:2.29.0` (pinned in `app/build.gradle`)
- **SDK artifact repository:** `https://yunopayments.jfrog.io/artifactory/snapshots-libs-release` (declared in `settings.gradle`)

Requirements to build: JDK 17 and an Android SDK with platform 35, located through `ANDROID_HOME` or `sdk.dir` in `local.properties`.

## Quick Commands

| Action | Command |
|--------|---------|
| Build (assemble + unit tests + lint) | `./gradlew build` |
| Assemble debug APK | `./gradlew :app:assembleDebug` |
| Test (unit, all variants) | `./gradlew test` |
| Test (unit, debug only) | `./gradlew :app:testDebugUnitTest` |
| Test (instrumented, needs device/emulator) | `./gradlew connectedAndroidTest` |
| Lint | `./gradlew :app:lint` |
| Clean | `./gradlew clean` |
| Install debug APK | `./gradlew installDebug` |

## How to Test

- **Unit tests** (JVM, no device): live in `app/src/test/java/com/yuno/payments/example/`, JUnit 4 (`junit:junit:4.13.2`).
  - All: `./gradlew test`
  - Single class: `./gradlew :app:testDebugUnitTest --tests "com.yuno.payments.example.ExampleUnitTest"`
  - Single method: `./gradlew :app:testDebugUnitTest --tests "com.yuno.payments.example.ExampleUnitTest.addition_isCorrect"`
  - Reports: `app/build/reports/tests/testDebugUnitTest/index.html`
- **Instrumented tests** (device or emulator required): live in `app/src/androidTest/java/com/yuno/payments/example/`, run with `AndroidJUnitRunner`.
  - All: `./gradlew connectedAndroidTest`
  - Single class: `./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.yuno.payments.example.ExampleInstrumentedTest`
- **What CI runs:** every PR head is checked by a Bitrise app that posts the `ci/bitrise/38f6d3044457e072/pr` status. Its pipeline is configured in the Bitrise dashboard, not in this tree (there is no pipeline file in the repository). Before opening a PR, also run `./gradlew build` locally (it runs assemble, unit tests and lint) and, when you changed UI or SDK wiring, `./gradlew connectedAndroidTest` on an emulator. Report in the PR what you ran.
- **Test rules:**
  - Put new logic (ViewModel state transitions, validation, formatting helpers) where it can be unit tested on the JVM, and add the test with the change.
  - A bug fix comes with a regression test that fails without the fix.
  - Test names describe behaviour, not ticket ids.
  - Never delete or weaken a failing test to get green.
  - Tests never use real credentials; use fakes or empty `BuildConfig` defaults.

## Project Structure
```
app/src/main/java/com/yuno/payments/example/
  CustomApplication.kt              # SDK initialization (Yuno.initialize)
  extensions/
    ClipboardExtensions.kt          # Clipboard utility
    StatusMessageExtensions.kt      # StatusMessage?.toLogString() for logging FAIL/INTERNAL_ERROR outcomes
  features/
    enrollment/
      activities/
        EnrollmentLiteActivity.kt   # Lite enrollment flow (SDK-managed UI)
        EnrollmentRenderActivity.kt # Render enrollment (merchant-controlled UI)
      ui/
        EnrollmentLiteScreen.kt     # Compose screen for lite enrollment
        EnrollmentRenderScreen.kt   # Compose screen for render enrollment
      viewmodel/
        EnrollmentRenderViewModel.kt
    payment/
      activities/
        CheckoutCompleteActivity.kt # Full checkout with SDK payment list
        CheckoutLiteActivity.kt     # Lite payment (merchant-controlled selection)
        PaymentRenderActivity.kt    # Render payment form (merchant-controlled layout)
      ui/
        CheckoutCompleteScreen.kt
        CheckoutLiteScreen.kt
        PaymentRenderScreen.kt
      viewmodel/
        CheckoutCompleteViewModel.kt
        CheckoutLiteViewModel.kt
        PaymentRenderViewModel.kt
  ui/
    HomeActivity.kt                 # Entry point - navigation hub
    HomeScreen.kt                   # Home screen with flow cards
    components/
      YunoComponents.kt            # Shared Compose components (TextField, Button, etc.)
    theme/
      YunoTheme.kt                 # Material 3 theme with light/dark support
app/src/test/                       # JVM unit tests
app/src/androidTest/                # Instrumented tests
docs/                               # architecture/, technical/api.md, vocabulary.md
```

## SDK Integration Patterns

This app demonstrates 5 integration patterns:

| Flow | Activity | SDK Entry Point | Description |
|------|----------|-----------------|-------------|
| Checkout Lite | `CheckoutLiteActivity` | `startPaymentLite()` | Merchant controls payment method selection |
| Checkout Complete | `CheckoutCompleteActivity` | `startPayment()` | SDK provides payment method list UI |
| Payment Render | `PaymentRenderActivity` | `startPaymentRender()` | SDK renders payment form as Fragment in merchant layout |
| Enrollment Lite | `EnrollmentLiteActivity` | `startEnrollment()` | SDK manages enrollment UI |
| Enrollment Render | `EnrollmentRenderActivity` | `startEnrollmentRender()` | SDK renders enrollment form as Fragment in merchant layout |

## Key Patterns & Conventions

- **SDK initialization:** `Yuno.initialize()` must be called in `Application.onCreate()` (see `CustomApplication.kt`)
- **startCheckout() in onCreate:** For payment flows, `startCheckout()` MUST be called in the Activity's `onCreate()` before any UI setup. Calling it later causes "Object not injected" crashes
- **initEnrollment() in onCreate:** For enrollment flows, `initEnrollment()` MUST be called in `onCreate()` before `startEnrollment()`
- **AppCompatActivity for Render flows:** Activities using `startPaymentRender()` or `startEnrollmentRender()` must extend `AppCompatActivity` (not `ComponentActivity`) because the SDK renders Fragments via `supportFragmentManager`
- **Always-present FrameLayout:** In Render flows, the `AndroidView` FrameLayout container must ALWAYS be in the Compose hierarchy. Placing it inside a conditional block causes fragment manager crashes
- **Sealed interface UI states:** Each flow uses a sealed interface for state management (e.g., `Config -> PaymentEntry -> OttResult`)
- **Loading state preservation:** ViewModels track `preLoadingState` to restore the correct state when SDK loading finishes
- **OTT flow:** SDK returns a One-Time Token (OTT) -> merchant creates payment on backend -> calls `continuePayment()` for final status
- **Status callbacks (SDK 2.22.0+):** payment state callbacks receive `(state, subState, statusMessage)`, enrollment state callbacks `(state, statusMessage)`, and render listeners' `returnStatus` gets a `StatusMessage?`. It is non-null only on `FAIL` / `INTERNAL_ERROR`; format it with `toLogString()`

## Architecture Rules

- **Module boundaries:** everything lives in `:app`. Add a new Gradle module only through its own PR and work item; never reach into another feature's package internals, share through `ui/components`, `ui/theme` or `extensions`.
- **Layering:** presentation (Activity, Compose `*Screen`, ViewModel) → domain → data. Dependencies point only in that direction. Today the app has no domain or data layer because the SDK owns networking and tokenization; if you add one (for example a merchant-backend client), put it under `features/<feature>/domain` and `features/<feature>/data` and keep the arrow direction.
- **Activities are thin coordinators:** they call SDK entry points and wire callbacks into the ViewModel. UI goes in `*Screen.kt`; Screens are stateless and receive state and callbacks as parameters.
- **ViewModels** extend `ViewModel`, hold no `Context`/`Activity`/`View`, expose `StateFlow` (private `_name`, public `name` via `asStateFlow()`).
- **No Android framework types in domain code:** no `Context`, `Activity`, `Bundle`, `View` or `android.*` imports, so it stays JVM unit testable.
- **Coroutines:** inject `CoroutineDispatcher`s (constructor parameter with a default) instead of hard-coding `Dispatchers.IO`/`Main`, and use `viewModelScope` or the scope passed to the SDK; never `GlobalScope`.
- **Naming:** `<Feature><Pattern>Activity`, `<Feature><Pattern>ViewModel`, `<Feature><Pattern>Screen`, `<Feature><Pattern>UiState`.

## Clean Code

- Names state intent; no abbreviations-only or numbered names (`data2`).
- Small, single-purpose functions; split a function that becomes hard to test.
- No dead code, commented-out code or unused imports/resources.
- No comments that restate the code. The existing `// CRITICAL:` / `// IMPORTANT:` markers document SDK ordering constraints and stay.
- Follow `kotlin.code.style=official` (set in `gradle.properties`) and the repository's existing style over personal preference.

## SDK and Security Rules

- **Public API stability:** this repo is the reference integration merchants copy. Keep calls aligned with the public SDK API of the pinned version; never rely on SDK internals, reflection or undocumented parameters.
- **Binary compatibility:** an SDK version bump is its own PR (`Project | Update SDK Version to X.Y.Z`). Adapt every call site to signature changes and update the version in `app/build.gradle`, `README.md` and this file in the same PR. `CHANGELOG.md` is unmaintained (it stops at 1.17.0); do not update it in bump PRs.
- **No PAN/CVV in logs:** never log, print, persist, copy to clipboard or put in analytics card numbers, CVV/CVC, expiry dates or full tokens. Log only `StatusMessage` via `toLogString()` and status strings.
- **Card data only through the secure path:** card data is captured by the SDK UI (Lite, Complete, Render) or, in headless mode, passed directly to the SDK's headless API (documented in `README.md`; this app implements no headless flow). The app never stores or transmits raw card data itself.
- **Credentials:** never commit API keys, sessions or JFrog credentials. They go in the git-ignored `local.properties` (see below).
- **Dependencies:** pin exact versions in `app/build.gradle`; justify any new dependency in the PR.

## Local Configuration

Test credentials are read from `local.properties` (git-ignored):
```properties
test.api_key="your-api-key"
test.checkout_session="your-checkout-session"
test.customer_session="your-customer-session"
test.countryCode="CO"
```

These are exposed as `BuildConfig.YUNO_TEST_*` fields in debug builds. Without them the fields default to empty strings and the app still builds.

## Deep Links

The app supports deep link enrollment via:
```
yuno://www.y.uno/enrollment?customerSession=XXX
```
Declared on `EnrollmentLiteActivity` in `AndroidManifest.xml`.

## PR Rules

- Default branch is `master`; branch from it and open the PR against it.
- Touch only what the work item needs. No drive-by reformatting, import reordering or dependency bumps.
- Improve what you touch: leave edited code clearer than you found it, within the files the item already changes.
- A refactor that would touch more than 3 files beyond what the item needs goes to a separate PR with its own AnyForge work item in the same initiative. Mention it in the original PR.
- Keep commits atomic with imperative messages; reference the work item in the PR (`Fixes <item code>`).
- The PR description says what changed, why, and which commands from "How to Test" you ran.
- Update `README.md`, `docs/` and this file when the change makes them stale.

## Documentation Maintenance
If you detect significant structural changes (new modules, renamed packages,
new demo flows, removed components) during this session, flag to the developer
that docs may need refreshing with `/update-docs yuno-sdk-android`.
