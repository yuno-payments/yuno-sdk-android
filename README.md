# Yuno SDK Android - Example App

![Kotlin](https://img.shields.io/badge/Kotlin-1.8.0-blue?logo=kotlin)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-1.5.4-green?logo=jetpackcompose)
![Min SDK](https://img.shields.io/badge/Min%20SDK-21-yellow)
![Target SDK](https://img.shields.io/badge/Target%20SDK-35-brightgreen)
![Yuno SDK](https://img.shields.io/badge/Yuno%20SDK-2.9.4-purple)

An Android example app that demonstrates the integration of the **Yuno Payments SDK**, including enrollment, checkout, payment flows, and render mode (advanced integration).

---

## Table of Contents

- [Requirements](#requirements)
- [Adding the SDK to the project](#adding-the-sdk-to-the-project)
  - [Initialize Yuno](#initialize-yuno)
  - [Card Form Types](#card-form-types)
  - [Available Languages](#available-languages)
  - [Styles](#styles)
- [ProGuard / R8](#proguard--r8)
- [Functions](#functions)
  - [Enroll a new payment method](#enroll-a-new-payment-method)
  - [Enrollment Render Mode (Advanced)](#enrollment-render-mode-advanced-integration)
  - [Checkout](#checkout)
  - [Payment Render Mode (Advanced)](#payment-render-mode-advanced-integration)

---

## Requirements

| Requirement | Minimum version |
|---|---|
| `minSdkVersion` | 21 |
| Java | 17 |
| Android Gradle Plugin | 8.0.0 |
| Kotlin Gradle Plugin | 1.8.0 |
| AndroidX | Required |

## Adding the SDK to the project

First, add the repository source using the following code line:

```Gradle
maven { url "https://yunopayments.jfrog.io/artifactory/snapshots-libs-release" }
```

After that, include the code snippet below in the "build.gradle" file to add the Yuno SDK dependency
to the application.

```Gradle
dependencies {
    implementation 'com.yuno.payments:android-sdk:{last_version}'
}
```

#### Permissions

We have already included the INTERNET permission by default as we need it to make network requests.

```xml

<uses-permission android:name="android.permission.INTERNET" />
```

### Initialize Yuno

To initialize the Yuno SDK, first, you need to get your public API keys from the Yuno dashboard.
Then, if you have not implemented a custom application yet, you will need to create one and call the
initialize function in the onCreate() method of your application class.

The following code snippet includes an example of a custom application:

```kotlin
class CustomApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Yuno.initialize(
            this,
            "your api key",
            config: YunoConfig, // This is a data class to use custom configs in the SDK.
        )
    }
}
```

Please use the YunoConfig data class presented as follows:

```kotlin
data class YunoConfig(
    val cardFlow: CardFormType = CardFormType.ONE_STEP,
    val saveCardEnabled: Boolean = false,
    val cardFormDeployed: Boolean = false,
    val language: YunoLanguage? = null,
    val styles: YunoStyles? = null,
)
```

The following table describes each customization option:

| Customization option | Description |
|---------------------|-------------|
| `cardFlow` | This optional configuration defines Payment and Enrollment Card flow. By default, the `CardFormType.ONE_STEP` option is used. See Card Form Types below for more information. |
| `saveCardEnabled` | Enables the Save card checkbox on card flows. |
| `cardFormDeployed` | This is only for SDK FULL. Choose if show card form deployed on payment methods list (TRUE) or if show normal card form in another screen (FALSE). |
| `language` | Defines the language to be used in the payment forms. You can set it to one of the available language options (see Available Languages below). If you send null or don't send it, Yuno SDK will take device language. |
| `styles` | Enables SDK-wide UI customization. Use it to define global visual styles like font family and button appearance (color, padding, radius, typography) through a `YunoStyles` object. For more information, see the Styles section below. |

### Card Form Types

The `cardFlow` parameter accepts the following `CardFormType` values:

```kotlin
enum class CardFormType {
    ONE_STEP,      // Single-page card form with all fields visible at once
    STEP_BY_STEP   // Multi-step card form with fields shown progressively
}
```

| Card Form Type | Description |
|----------------|-------------|
| `ONE_STEP` | Displays all card input fields (card number, expiry date, CVV, cardholder name, etc.) on a single screen. This is the default option and provides a traditional form experience. |
| `STEP_BY_STEP` | Presents the card input fields in a progressive, step-by-step manner. Each field or group of related fields is shown sequentially, providing a more guided user experience. |

### Available Languages

The following languages are supported:

```kotlin
enum class YunoLanguage {
    SPANISH,                // es - Spanish
    ENGLISH,                // en - English
    PORTUGUESE,             // pt - Portuguese
    INDONESIAN,             // id - Indonesian
    MALAYSIAN,              // ms - Malay
    FRENCH,                 // fr - French
    POLISH,                 // pl - Polish
    ITALIAN,                // it - Italian
    GERMAN,                 // de - German
    RUSSIAN,                // ru - Russian
    TURKISH,                // tr - Turkish
    DUTCH,                  // nl - Dutch
    SWEDISH,                // sv - Swedish
    THAI,                   // th - Thai
    FILIPINO,               // fil - Filipino
    VIETNAMESE,             // vi - Vietnamese
    CHINESE_SIMPLIFIED,     // zh-CN - Chinese (Simplified, China)
    CHINESE_TRADITIONAL     // zh-TW - Chinese (Traditional, Taiwan)
}
```

### Styles

With the `styles` customization option, you can define global visual styles through a `YunoStyles` object. It lets you apply consistent branding across the SDK by customizing button appearance and typography.

```kotlin
data class YunoStyles(
    val buttonStyles: YunoButtonStyles? = null,
    val fontFamily: FontFamily? = null
)
```

| Parameter | Description |
|-----------|-------------|
| `buttonStyles` | Customizes the primary buttons displayed in the SDK. |
| `fontFamily` | Sets the font family used across all SDK text elements. |

The `YunoButtonStyles` object lets you define specific settings for button appearance:

```kotlin
data class YunoButtonStyles(
    val backgroundColor: Color? = null,
    val contentColor: Color? = null,
    val cornerRadius: Dp? = null,
    val elevation: Dp? = null,
    val padding: Dp? = null,
    val fontFamily: FontFamily? = null,
    val fontSize: TextUnit? = null,
    val fontStyle: FontStyle? = null
)
```

In addition, you need to update your manifest to use your application:

```XML

<application android:name=".CustomApplication"></application>
```

## ProGuard / R8

If your project has `minifyEnabled = true`, add these rules to your `proguard-rules.pro`:

```proguard
# Preserve generic type signatures and annotations
-keepattributes Signature
-keepattributes *Annotation*

# Yuno SDK
-keep class com.yuno.** { *; }
-dontwarn com.yuno.**

# Gson — R8 full mode compatibility
-keep class com.google.gson.** { *; }
-keep,allowobfuscation,allowshrinking,allowoptimization class * extends com.google.gson.reflect.TypeToken
-dontwarn com.google.gson.**

# Retrofit — R8 full mode compatibility
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
-dontwarn retrofit2.**

# OkHttp + Okio — Suppress warnings
-dontwarn okhttp3.**
-dontwarn okio.**
```

## Functions

### Enroll a new payment method

To start an enrollment flow is a must to call initEnrollment method on the onCreate method of your
activity, this because we use it to register the contract to give you the final enrollment state.

````Kotlin
fun ComponentActivity.initEnrollment(
    callbackEnrollmentState: ((String?) -> Unit)? = null, //Default null | To register this callback is a must to call ```initEnrollment``` method on the onCreate method of activity.
)
````

Call the following method from your activity to start an enrollment flow of a new payment method.

````Kotlin
fun Activity.startEnrollment(
    customerSession: String,
    countryCode: String,
    showEnrollmentStatus: Boolean = true, //Optional - Default true
    callbackEnrollmentState: ((String?) -> Unit)? = null, //Optional - You can send again another callback that is gonna override the one you sent on initEnrollment function.
)
````

### Callback Enrollment State

To register a callback to get the final enrollment state, it is necessary call the `initEnrollment`
method on the onCreate method of activity.

### Get Enrollment State

To only get the final enrollment state, it is necessary call the `initEnrollment`
method on the onCreate method of activity, in that method you register a callback function
called `callbackEnrollmentState`, we are gonna give you the status in that callback.

# IMPORTANT: This function is not gonna start the enrollment process, is only to check a previous enrollment state this based on customer session.

````Kotlin
fun AppCompatActivity.enrollmentStatus(
    customerSession: String,
    countryCode: String,
    showEnrollmentStatus: Boolean = false, //Optional - Default false
    callbackEnrollmentState: ((String?) -> Unit)? = null, //Optional - You can send again another callback that is gonna override the one you sent on initEnrollment function.
)
````

### Enrollment Render Mode (Advanced Integration)

For developers requiring advanced UI control, the SDK supports **Render Mode** integration. This mode provides fragment-based UI components that you can integrate into custom layouts, offering more flexibility while maintaining SDK functionality.

#### Setup

**Step 1:** Call `startEnrollment()` in your activity's `onCreate()` method. This initializes the enrollment flow and injects necessary dependencies:

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_enrollment)

    // IMPORTANT: Must be called before startEnrollmentRender
    startEnrollment()

    initViews()
    initListeners()
}
```

**Step 2:** Implement `YunoEnrollmentRenderListener` in your activity:

```kotlin
class EnrollmentActivity : AppCompatActivity(), YunoEnrollmentRenderListener {

    private lateinit var fragmentController: YunoEnrollmentFragmentController

    private fun startEnrollmentFlow() {
        fragmentController = startEnrollmentRender(
            customerSession = "customer_session_id",
            countryCode = "CO",
            coroutineScope = lifecycleScope,
            listener = this
        )
    }

    // Implement listener methods below
}
```

#### Listener Methods

**showView() - Display the enrollment form:**

```kotlin
override fun showView(fragment: Fragment, needSubmit: Boolean) {
    // Insert the SDK fragment into your layout
    supportFragmentManager.beginTransaction()
        .replace(R.id.enrollment_fragment_container, fragment)
        .commit()

    // Show/hide custom submit button based on needSubmit
    if (needSubmit) {
        customSubmitButton.visibility = View.VISIBLE
        customSubmitButton.setOnClickListener {
            fragmentController.submitForm()
        }
    }
}
```

**returnStatus() - Handle enrollment completion:**

```kotlin
override fun returnStatus(resultCode: Int, paymentStatus: String) {
    when (paymentStatus) {
        "SUCCEEDED" -> {
            // Payment method enrolled successfully
            // Remove fragment and show success message
        }
        "FAIL" -> {
            // Enrollment failed
            // Show error and allow retry
        }
        "CANCELED" -> {
            // User canceled enrollment
        }
    }
}
```

**loadingListener() - Handle loading states:**

```kotlin
override fun loadingListener(isLoading: Boolean) {
    progressBar.isVisible = isLoading
    if (needsSubmit) {
        customSubmitButton.isEnabled = !isLoading
    }
}
```

#### Key Benefits

- **Custom UI Integration**: Embed enrollment components in your existing layouts
- **Fragment Compatibility**: Works with both XML and Jetpack Compose
- **Flow Control**: Manage form submission manually with custom buttons

#### Layout Example

```xml
<FrameLayout
    android:id="@+id/enrollment_fragment_container"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />

<Button
    android:id="@+id/custom_submit_button"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="Submit Enrollment"
    android:visibility="gone" />
```

### Checkout

To start a new payment process, you need to call the following method on the onCreate method of
activity that calls the SDK:

```Kotlin
startCheckout(
    checkoutSession: "checkout_session",
countryCode: "country_code_iso",
callbackOTT: (String?) -> Unit,
callbackPaymentState: ((String?, String?) -> Unit)?,
merchantSessionId: String? = null //Optional - Default null
)
```

#### Callback One Time Token (OTT)

The `callbackOTT` parameter is a function that returns an OTT needed to complete the payment back to
back. This function is mandatory.

#### Callback Payment State

The `callbackPaymentState` parameter is a function that returns the current payment process state and sub-state. Sending
this function is not mandatory if you do not need the result.

The callback receives two parameters:
- **paymentState** (String?): The main payment state
- **paymentSubState** (String?): Additional sub-state information providing more details about the payment status

The possible payment states are:

```Kotlin
const val PAYMENT_STATE_SUCCEEDED = "SUCCEEDED"
const val PAYMENT_STATE_FAIL = "FAIL"
const val PAYMENT_STATE_PROCESSING = "PROCESSING"
const val PAYMENT_STATE_REJECT = "REJECT"
const val PAYMENT_STATE_INTERNAL_ERROR = "INTERNAL_ERROR"
const val PAYMENT_STATE_STATE_CANCELED_BY_USER = "CANCELED"
```

#### Show Payment Methods

When implementing the Full SDK version, you need to add the following view on your layout to show
the available payment methods:

```XML

<com.yuno.payments.features.payment.ui.views.PaymentMethodListView
    android:id="@+id/list_payment_methods" android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

#### Start Payment

To start a payment process, you have to call the method `startPayment`. However, if you are using
the lite version of the SDK, you must call the method `startPaymentLite`.

###### Full SDK Version

```Kotlin
startPayment(
    showStatusYuno: Boolean = true, //Optional - Default true
    callbackOTT: (String?) -> Unit, //Optional - Default null
    callBackTokenWithInformation: (OneTimeTokenModel?) -> Unit //Optional - Default null
)
```

Configure the payment with the following options:

| Parameter | Description |
|-----------|-------------|
| `showStatusYuno` | A boolean that specifies whether the payment status should be displayed within the Yuno interface. Default is `true`. |
| `callbackOTT` | A function that returns the updated one-time token (OTT) needed to complete the payment process. This token is required to complete the payment. |
| `callBackTokenWithInformation` | A function that supplies detailed information about the one-time token, wrapped in a `OneTimeTokenModel` object, allowing for comprehensive handling of token details. |

###### Lite SDK Version

```Kotlin
startPaymentLite(
    paymentSelected: PaymentSelected,
    showPaymentStatus: Boolean = true, //Optional - Default true
    callbackOTT: (String?) -> Unit, //Optional - Default null
    callBackTokenWithInformation: (OneTimeTokenModel?) -> Unit //Optional - Default null
)
```

The following table describes the required parameters to start the payment:

| Parameter | Description |
|-----------|-------------|
| `paymentSelected` | Inform the payment method selected by the buyer. |
| `showPaymentStatus` | A boolean that specifies whether the payment status should be displayed within the Yuno interface. Default is `true`. |
| `callbackOTT` | A function that returns the updated one-time token needed to complete the payment process. |
| `callBackTokenWithInformation` | A function that supplies detailed information about the one-time token, wrapped in a `OneTimeTokenModel` object, allowing for comprehensive handling of token details. |

For the Lite version, you need to send an additional parameter, which is the vaulted token and/or
the payment type that the user selected to make the payment.

```Kotlin
PaymentSelected(
    vaultedToken: String? = null, //Optional - The vaulted token for saved payment methods
    paymentMethodType: String //Required - The payment method type (e.g., "CARD", "NEQUI", etc.)
)
```

At the end of this process, you will obtain the OTT, which is a required parameter to create a
payment via the Payments API POST/payments.

##### OneTimeTokenModel

The `callBackTokenWithInformation` callback provides detailed information about the one-time token through the `OneTimeTokenModel` object:

```kotlin
@Parcelize
data class OneTimeTokenModel(
    val token: String? = null,
    val vaultedToken: String? = null,
    val vaultOnSuccess: Boolean? = null,
    val type: String? = null,
    val cardData: CardInformationModel? = null,
    val customer: CustomerPayerInformationModel? = null,
) : Parcelable
```

**Card Information Model:**

```kotlin
@Parcelize
data class CardInformationModel(
    val holderName: String? = null,
    val iin: String? = null,
    val lfd: String? = null,
    val numberLength: Int? = null,
    val securityCodeLength: Int? = null,
    val brand: String? = null,
    val type: String? = null,
    val category: String? = null,
    val issuerName: String? = null,
    val issuerCode: String? = null,
    val countryCode: String? = null,
) : Parcelable
```

**Customer Payer Information Model:**

```kotlin
@Parcelize
data class CustomerPayerInformationModel(
    val name: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val document: Document? = null,
    val phone: Phone? = null,
    val address: Address? = null,
    val deviceFingerPrint: String? = null,
    val thirdPartySessionId: String? = null,
) : Parcelable
```

**Supporting Models:**

```kotlin
@Parcelize
data class Document(
    val documentNumber: String? = null,
    val documentType: String? = null,
) : Parcelable

@Parcelize
data class Phone(
    val number: String,
    val countryCode: String,
) : Parcelable

@Parcelize
data class Address(
    val addressLine1: String? = null,
    val addressLine2: String? = null,
    val country: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zipCode: String? = null,
    val neighborhood: String? = null,
) : Parcelable
```

#### Complete Payment

If in the create_payment response the sdk_action_required parameter is true you need to call the
following method:

```Kotlin
continuePayment(
    showPaymentStatus: Boolean, //Optional - Default true
    callbackPaymentState:((String?, String?) -> Unit)?, //Optional - Default null
)
```

To show your own payment status screens, you should send `false` in the `showPaymentStatus`
parameter and then get the payment state by callback.

### Payment Render Mode (Advanced Integration)

For developers requiring advanced UI control, the SDK supports **Payment Render Mode**. This mode provides fragment-based UI components that you can integrate into custom layouts, offering complete control over the payment flow presentation.

#### Setup

**Step 1:** Call `startCheckout()` in your activity's `onCreate()` method. This initializes the payment flow and injects necessary dependencies:

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_payment)

    // IMPORTANT: Must be called before startPaymentRender
    startCheckout()

    initViews()
    initListeners()
}
```

**Step 2:** Update the checkout session and country before starting the render:

```kotlin
private fun startPaymentFlow() {
    // Update session configuration
    updateCheckoutSession(
        checkoutSession = "checkout_session_id",
        countryCode = "CO"
    )

    // Start payment render
    fragmentController = startPaymentRender(
        checkoutSession = "checkout_session_id",
        countryCode = "CO",
        coroutineScope = lifecycleScope,
        paymentSelected = PaymentSelected(
            vaultedToken = null,
            paymentMethodType = "CARD"
        ),
        listener = this
    )
}
```

**Step 3:** Implement `YunoPaymentRenderListener` in your activity:

```kotlin
class PaymentActivity : AppCompatActivity(), YunoPaymentRenderListener {

    private lateinit var fragmentController: YunoPaymentFragmentController

    // Implement listener methods below
}
```

#### Listener Methods

**showView() - Display the payment form:**

```kotlin
override fun showView(fragment: Fragment) {
    // Insert the SDK fragment into your layout
    supportFragmentManager.beginTransaction()
        .replace(R.id.payment_fragment_container, fragment)
        .commit()

    // Hide configuration fields and show submit button
    configurationContainer.visibility = View.GONE
    customSubmitButton.visibility = View.VISIBLE
    customSubmitButton.setOnClickListener {
        fragmentController.submitForm()
    }
}
```

**returnOneTimeToken() - Receive the OTT after form submission:**

```kotlin
override fun returnOneTimeToken(oneTimeToken: String, additionalData: OneTimeTokenModel?) {
    // Remove the SDK form fragment
    supportFragmentManager.findFragmentById(R.id.payment_fragment_container)?.let {
        supportFragmentManager.beginTransaction().remove(it).commit()
    }

    // Display the OTT (e.g., in a TextView for testing/debugging)
    ottTextView.text = "One-Time Token: $oneTimeToken"
    ottTextView.visibility = View.VISIBLE

    // Show continue button
    continuePaymentButton.visibility = View.VISIBLE

    // Here you would call your backend to create the payment with this OTT
    // After creating the payment, user clicks "Continue Payment"
}
```

**continuePayment() - Continue with payment after backend creation:**

```kotlin
continuePaymentButton.setOnClickListener {
    if (::fragmentController.isInitialized) {
        // This handles 3DS, redirects, and other payment method requirements
        fragmentController.continuePayment()
    }
}
```

**returnStatus() - Handle payment completion:**

```kotlin
override fun returnStatus(resultCode: Int, paymentStatus: String, paymentSubStatus: String?) {
    when (paymentStatus) {
        "SUCCEEDED" -> {
            // Payment completed successfully
            // Remove fragment and show success
        }
        "FAIL" -> {
            // Payment failed
            // Show error and allow retry
        }
        "PROCESSING" -> {
            // Payment is being processed
            // Show processing message, can include paymentSubStatus for details
        }
        "REJECT" -> {
            // Payment rejected
        }
        "CANCELED" -> {
            // User canceled payment
        }
    }
}
```

**loadingListener() - Handle loading states:**

```kotlin
override fun loadingListener(isLoading: Boolean) {
    progressBar.isVisible = isLoading
}
```

#### Payment Flow Steps

1. **User fills form**: SDK fragment displays payment form
2. **Submit form**: Custom button calls `fragmentController.submitForm()`
3. **Receive OTT**: SDK calls `returnOneTimeToken()` with the token
4. **Remove form**: Remove SDK fragment, display OTT
5. **Create payment**: Call your backend with the OTT
6. **Continue**: User clicks continue, call `fragmentController.continuePayment()`
7. **Complete**: SDK handles 3DS/redirects and calls `returnStatus()` with final result

#### Key Benefits

- **Custom UI Integration**: Embed payment components in your existing layouts
- **Complete Control**: Manage when to show/hide form, display OTT, continue payment
- **Fragment Compatibility**: Works with both XML and Jetpack Compose
- **Flow Control**: Manually control form submission and payment continuation

#### Layout Example

```xml
<!-- Configuration inputs (hidden after form loads) -->
<LinearLayout
    android:id="@+id/configuration_container"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical">

    <EditText
        android:id="@+id/checkout_session_input"
        android:hint="Checkout Session" />

    <Button
        android:id="@+id/start_payment_button"
        android:text="Start Payment" />
</LinearLayout>

<!-- SDK payment form container -->
<FrameLayout
    android:id="@+id/payment_fragment_container"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />

<!-- Custom submit button (shown below form) -->
<Button
    android:id="@+id/submit_button"
    android:text="Submit Payment"
    android:visibility="gone" />

<!-- OTT display (shown after submission) -->
<TextView
    android:id="@+id/ott_text_view"
    android:text="One-Time Token: "
    android:clickable="true"
    android:visibility="gone" />

<!-- Continue button (shown after OTT) -->
<Button
    android:id="@+id/continue_payment_button"
    android:text="Continue Payment"
    android:visibility="gone" />
```

#### Best Practices

- Always call `startCheckout()` in `onCreate()` before using render functions
- Call `updateCheckoutSession()` before `startPaymentRender()`
- Remove the SDK fragment after receiving the OTT for cleaner UI
- Create the payment in your backend after receiving the OTT
- Call `continuePayment()` after payment creation for 3DS/redirect handling
- Handle all payment states in `returnStatus()` (SUCCEEDED, FAIL, PROCESSING, etc.)
