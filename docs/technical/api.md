# SDK Integration Reference

This is a mobile demo app, not a backend service. Instead of HTTP API endpoints, this document covers the Yuno Android SDK methods used across the 5 integration patterns.

## SDK Initialization

### Yuno.initialize()
**Called in:** `CustomApplication.onCreate()`
**Purpose:** Initializes the Yuno SDK globally. Must be called once before any SDK operations.

```kotlin
Yuno.initialize(
    application = this,
    apiKey = "YOUR_API_KEY",
    config = YunoConfig(saveCardEnabled = false),
)
```

## Payment SDK Methods

### startCheckout()
**Called in:** Activity `onCreate()` (MUST be called before `setContent` or any UI)
**Purpose:** Registers lifecycle observers and initializes internal SDK state for payment flows.
**Import:** `com.yuno.sdk.payments.startCheckout`

```kotlin
startCheckout(
    checkoutSession = "session-id",      // Optional: can be set later via updateCheckoutSession()
    countryCode = "CO",                   // Optional: can be set later
    callbackPaymentState = { state, subState -> ... },  // Final payment status callback
)
```

### updateCheckoutSession()
**Called in:** After user enters session info
**Purpose:** Updates the checkout session and country code for the current payment flow.
**Import:** `com.yuno.sdk.payments.updateCheckoutSession`

```kotlin
updateCheckoutSession(
    checkoutSession = "session-id",
    countryCode = "CO",
)
```

### startPaymentLite()
**Used in:** Checkout Lite flow
**Purpose:** Starts payment with a merchant-selected payment method. Returns OTT via callback.
**Import:** `com.yuno.sdk.payments.startPaymentLite`

```kotlin
startPaymentLite(
    paymentSelected = PaymentSelected(
        paymentMethodType = "CARD",
        vaultedToken = null,
    ),
    callbackOTT = { token -> ... },
    callBackTokenWithInformation = { ottModel -> ... },  // Optional: OTT with metadata
)
```

### startPayment()
**Used in:** Checkout Complete flow
**Purpose:** Starts payment using the SDK's built-in payment method selection UI.
**Import:** `com.yuno.sdk.payments.startPayment`

```kotlin
startPayment(
    callbackOTT = { token -> ... },
)
```

### startPaymentRender()
**Used in:** Payment Render flow
**Purpose:** Renders SDK payment form as a Fragment in a merchant-provided FrameLayout.
**Import:** `com.yuno.sdk.payments.render.startPaymentRender`
**Returns:** `YunoPaymentFragmentController` for form submission and payment continuation.

```kotlin
val controller = startPaymentRender(
    checkoutSession = "session-id",
    countryCode = "CO",
    coroutineScope = lifecycleScope,
    paymentSelected = PaymentSelected(
        vaultedToken = null,
        paymentMethodType = "CARD",
    ),
    listener = this,  // YunoPaymentRenderListener
)

// Later:
controller.submitForm()       // Submit the payment form
controller.continuePayment()  // Continue after backend creates payment with OTT
```

### continuePayment()
**Purpose:** Tells the SDK to finalize payment after the merchant creates a payment on their backend using the OTT.
**Import:** `com.yuno.sdk.payments.continuePayment`

```kotlin
continuePayment(
    callbackPaymentState = { state, subState -> ... },
)
```

## Enrollment SDK Methods

### initEnrollment()
**Called in:** Activity `onCreate()` (MUST be called before `startEnrollment()`)
**Purpose:** Registers lifecycle observers for enrollment flows.
**Import:** `com.yuno.sdk.enrollment.initEnrollment`

```kotlin
initEnrollment(::onEnrollmentStateChange)
```

### startEnrollment()
**Used in:** Enrollment Lite flow
**Purpose:** Starts enrollment with SDK-managed UI.
**Import:** `com.yuno.sdk.enrollment.startEnrollment`

```kotlin
startEnrollment(
    customerSession = "customer-session-id",
    countryCode = "CO",
)
```

### startEnrollmentRender()
**Used in:** Enrollment Render flow
**Purpose:** Renders SDK enrollment form as a Fragment in a merchant-provided FrameLayout.
**Import:** `com.yuno.sdk.enrollment.render.startEnrollmentRender`
**Returns:** `YunoEnrollmentFragmentController` for form submission.

```kotlin
val controller = startEnrollmentRender(
    customerSession = "customer-session-id",
    countryCode = "CO",
    coroutineScope = lifecycleScope,
    listener = this,  // YunoEnrollmentRenderListener
)

// Later:
controller.submitForm()  // Submit enrollment form (when needsSubmit is true)
```

## Listener Interfaces

### YunoPaymentRenderListener
**Implemented by:** `PaymentRenderActivity`

| Method | Description |
|--------|-------------|
| `showView(fragment: Fragment)` | SDK provides the payment form Fragment to display |
| `returnOneTimeToken(oneTimeToken: String, additionalData: OneTimeTokenModel?)` | SDK returns OTT after form submission |
| `returnStatus(resultCode: Int, paymentStatus: String, paymentSubStatus: String?)` | Final payment status (SUCCEEDED, FAIL, REJECT, CANCELED, PROCESSING) |
| `loadingListener(isLoading: Boolean)` | SDK loading state changes |

### YunoEnrollmentRenderListener
**Implemented by:** `EnrollmentRenderActivity`

| Method | Description |
|--------|-------------|
| `showView(fragment: Fragment, needSubmit: Boolean)` | SDK provides enrollment form Fragment. `needSubmit` indicates if merchant must provide a submit button |
| `returnStatus(resultCode: Int, paymentStatus: String)` | Final enrollment status (SUCCEEDED, FAIL, CANCELED) |
| `loadingListener(isLoading: Boolean)` | SDK loading state changes |

## SDK Components

### PaymentMethodListViewComponent
**Used in:** `CheckoutCompleteScreen`
**Purpose:** SDK-provided Compose component that displays available payment methods for selection.

```kotlin
PaymentMethodListViewComponent(
    activity = activity,
    onPaymentSelected = { isSelected, _ -> ... },
)
```

**Constraint:** Must NOT be placed inside a `verticalScroll` Column (causes infinite height crash). Use `Modifier.weight(1f)` in a non-scrollable parent.
