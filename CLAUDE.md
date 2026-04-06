# Yuno Android SDK - Example App

Demo application showcasing all integration patterns for the Yuno Payments Android SDK (`com.yuno.payments:android-sdk`).

## Tech Stack
- **Language:** Kotlin 1.8.0
- **Platform:** Android (compileSdk 35, minSdk 21, targetSdk 35)
- **UI:** Jetpack Compose with Material 3
- **Architecture:** MVVM (Activity + ViewModel + Compose Screen)
- **Build System:** Gradle 8.4.0 (Groovy DSL)
- **SDK Version:** `com.yuno.payments:android-sdk:2.12.0`

## Quick Commands

| Action | Command |
|--------|---------|
| Build | `./gradlew build` |
| Test (unit) | `./gradlew test` |
| Test (instrumented) | `./gradlew connectedAndroidTest` |
| Clean | `./gradlew clean` |
| Install debug APK | `./gradlew installDebug` |

## Project Structure
```
app/src/main/java/com/yuno/payments/example/
  CustomApplication.kt              # SDK initialization (Yuno.initialize)
  extensions/
    ClipboardExtensions.kt          # Clipboard utility
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

## Local Configuration

Test credentials are read from `local.properties` (git-ignored):
```properties
test.api_key="your-api-key"
test.checkout_session="your-checkout-session"
test.customer_session="your-customer-session"
test.countryCode="CO"
```

These are exposed as `BuildConfig.YUNO_TEST_*` fields in debug builds.

## Deep Links

The app supports deep link enrollment via:
```
yuno://www.y.uno/enrollment?customerSession=XXX
```
Declared on `EnrollmentLiteActivity` in `AndroidManifest.xml`.

## Documentation Maintenance
If you detect significant structural changes (new modules, renamed packages,
new demo flows, removed components) during this session, flag to the developer
that docs may need refreshing with `/update-docs yuno-sdk-android`.
