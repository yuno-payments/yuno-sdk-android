# Yuno Android SDK - Example App

Demo application showcasing all integration patterns for the Yuno Payments Android SDK (`com.yuno.payments:android-sdk`). Not production code — this is executable documentation for SDK consumers.

## Tech Stack
- **Language:** Kotlin 1.8.0 (JVM target 17)
- **Platform:** Android (compileSdk 35, minSdk 21, targetSdk 35)
- **UI:** Jetpack Compose 1.5.4 with Material 3 1.1.2
- **Architecture:** MVVM (Activity + ViewModel + Compose Screen)
- **Build System:** Gradle 8.4.0 (Groovy DSL)
- **SDK Version:** `com.yuno.payments:android-sdk:2.14.0` (from Artifactory `https://yunopayments.jfrog.io/artifactory/snapshots-libs-release`)
- **Other deps:** `androidx.appcompat:1.6.1`, `androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0`, `com.facebook.shimmer:0.5.0`

## Quick Commands

| Action | Command |
|--------|---------|
| Build | `./gradlew build` |
| Assemble debug | `./gradlew assembleDebug` |
| Test (unit) | `./gradlew test` |
| Test (instrumented) | `./gradlew connectedAndroidTest` |
| Clean | `./gradlew clean` |
| Install debug APK | `./gradlew installDebug` |

## Project Structure
```
app/src/main/java/com/yuno/payments/example/
  CustomApplication.kt              # SDK initialization (Yuno.initialize)
  extensions/
    ClipboardExtensions.kt          # Context.copyToClipboard()
  features/
    enrollment/
      activities/
        EnrollmentLiteActivity.kt   # Lite enrollment (SDK-managed UI, deep-link enabled)
        EnrollmentRenderActivity.kt # Render enrollment (Fragment in merchant layout)
      ui/
        EnrollmentLiteScreen.kt
        EnrollmentRenderScreen.kt
      viewmodel/
        EnrollmentRenderViewModel.kt
    payment/
      activities/
        CheckoutCompleteActivity.kt # Full checkout (SDK payment list UI)
        CheckoutLiteActivity.kt     # Lite payment (merchant-controlled method selection)
        PaymentRenderActivity.kt    # Render payment form (Fragment in merchant layout)
      ui/
        CheckoutCompleteScreen.kt
        CheckoutLiteScreen.kt
        PaymentRenderScreen.kt
      viewmodel/
        CheckoutCompleteViewModel.kt
        CheckoutLiteViewModel.kt
        PaymentRenderViewModel.kt
  ui/
    HomeActivity.kt                 # Entry point — ComponentActivity launching the 5 flows
    HomeScreen.kt                   # FlowCard grid
    components/
      YunoComponents.kt             # YunoTextField, YunoButton/TonalButton/OutlinedButton, OttResultPanel, StatusCard, SectionHeader
    theme/
      YunoTheme.kt                  # Material3 theme + YunoExtendedColors (success/warning) via CompositionLocal
```

## SDK Integration Patterns

| Flow | Activity | SDK Entry Point | Returns | UI ownership |
|------|----------|-----------------|---------|--------------|
| Checkout Lite | `CheckoutLiteActivity` | `startPaymentLite()` | OTT → `continuePayment()` for status | Merchant picks method |
| Checkout Complete | `CheckoutCompleteActivity` | `startPayment()` | OTT → `continuePayment()` for status | SDK shows payment-method list |
| Payment Render | `PaymentRenderActivity` | `startPaymentRender()` | OTT via `returnOneTimeToken()` → status via `returnStatus()` | SDK renders Fragment in merchant `FrameLayout` |
| Enrollment Lite | `EnrollmentLiteActivity` | `startEnrollment()` | Status via `initEnrollment(callback)` | SDK manages full UI |
| Enrollment Render | `EnrollmentRenderActivity` | `startEnrollmentRender()` | Status via `returnStatus()` (no OTT) | SDK Fragment + optional merchant submit button |

### State management
Each flow uses a sealed interface in its ViewModel:
- Checkout Lite: `Config → PaymentEntry → OttResult(token)` → back to `Config`
- Checkout Complete: `Config → PaymentList(isPayEnabled) → OttResult(token)`
- Payment Render: `Config → FragmentVisible → OttReceived(token) → Loading → StatusResult(status)`
- Enrollment Render: `Config → FragmentVisible(needsSubmit) → Loading → StatusResult(status)`

Render ViewModels keep a `preLoadingState` to restore the previous state when the SDK's `loadingListener(false)` fires. The logic is not thread-safe in general; it's safe here because Yuno SDK callbacks fire on the main thread (see comments in `PaymentRenderViewModel.kt`).

## Key Patterns & Conventions (read before changing flows)

- **`Yuno.initialize()` in `Application.onCreate()`** — `CustomApplication.kt`. API key currently a placeholder; populate from `BuildConfig.YUNO_TEST_API_KEY` when wiring real credentials.
- **`startCheckout()` / `initEnrollment()` MUST run in `Activity.onCreate()` before `setContent()`.** The SDK registers lifecycle observers there; calling later crashes with "Object not injected." See comments in `CheckoutLiteActivity.kt:32-34` and `PaymentRenderActivity.kt:47-49`.
- **Render flows extend `AppCompatActivity`** (not `ComponentActivity`) because they need `supportFragmentManager` to commit the SDK's Fragment.
- **Always-present `FrameLayout` in Render flows.** The `AndroidView { FrameLayout }` container must live in the Compose tree even in `Config` state — the SDK's `showView()` callback looks it up by id. Use a Crossfade *overlay* pattern (overlays drawn on top of the container) instead of conditional visibility. Example: `PaymentRenderScreen.kt:54-67`.
- **Session deferral** (Checkout Complete only): call `startCheckout()` in `onCreate` with no session, then `updateCheckoutSession(session, country)` once the user enters config. Allows configuring after init.
- **OTT vs. status flows:**
  - Payment flows: SDK → OTT → merchant creates payment on backend → `continuePayment()` → terminal status.
  - Enrollment Lite: SDK handles the whole flow; no OTT.
  - Enrollment Render: direct status via `returnStatus()`; no OTT.
- **`needSubmit` in Enrollment Render:** some payment methods (e.g., cards) require the merchant to render the submit button — the SDK signals via `showView(fragment, needSubmit)`.
- **Terminal vs non-terminal statuses:** in Render flows, `SUCCEEDED`/`FAIL`/`REJECT`/`CANCELED` remove the Fragment and finish; `PROCESSING` keeps the Fragment alive.

## Local Configuration

Test credentials live in `local.properties` (git-ignored), exposed as `BuildConfig.YUNO_TEST_*` (debug builds only):

```properties
test.api_key="your-api-key"
test.checkout_session="your-checkout-session"
test.customer_session="your-customer-session"
test.countryCode="CO"
```

## Deep Links

Enrollment Lite is launchable via:
```
yuno://www.y.uno/enrollment?customerSession=XXX
```
Intent filter declared on `EnrollmentLiteActivity` in `AndroidManifest.xml`. The activity auto-starts enrollment when launched this way — no user tap needed. It is the only `exported=true` activity besides `HomeActivity`.

## UI / Theme

`YunoTheme.kt` extends Material 3 with a custom `YunoExtendedColors` (`success`/`warning`) provided via `LocalYunoColors` CompositionLocal — used by `StatusCard` for status-coded color. Light/dark schemes supported plus dynamic colors on Android 12+. `safeDrawingPadding()` applied at the Surface level handles Android 15 gesture/notch insets.

## Test Surface

Only boilerplate: `ExampleUnitTest` (asserts `2+2=4`) and `ExampleInstrumentedTest` (verifies package name). No SDK integration tests, no ViewModel tests, no SDK mocking. Treat this as a green field if adding tests.

## Documentation Maintenance
If you detect significant structural changes (new modules, renamed packages, new demo flows, removed components) during this session, flag to the developer that docs may need refreshing with `/update-docs yuno-sdk-android`.
