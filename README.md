# yuno-sdk-android

## Android SDK Requirements

In order to use the Yuno Android SDK, you need to meet the following requirements:

- Yuno Android SDK needs your minSdkVersion to be of 21 or above.
- Your project must have Java 8 enabled and use AndroidX instead of older support libraries.
- The android-gradle-plugin version must be 4.0.0 or above.
- The Proguard version must be 6.2.2 or above.
- The kotlin-gradle-plugin version must be 1.4.0 or above.

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
    val cardFlow: CardFormType = CardFormType.ONE_STEP, // This is optional, CardFormType.ONE_STEP by default, this is to choose Payment and Enrollment Card flow.
    val saveCardEnabled: Boolean = false, // This is to choose if show save card checkbox on cards flows.
)
```

In addition, you need to update your manifest to use your application:

```XML

<application android:name=".CustomApplication"></application>
```

## Functions

### Enroll a new payment method

Call the following method from your activity to start an enrollment flow of a new payment method.

````Kotlin
startEnrollment(
    requestCode: Int, //Optional
    customerSession: String,
    countryCode: String,
    showEnrollmentStatus: Boolean, //Optional - Default true
    callbackEnrollmentState:((String?) -> Unit)?, //Optional - Default null | To register this callback is a must to call ```initEnrollment``` method on the onCreate method of activity.
)
````

#### Callback Enrollment State

To register a callback to get the final enrollment state, it is necessary call the `initEnrollment`
method on the onCreate method of activity.

### Checkout

To start a new payment process, you need to call the following method on the onCreate method of
activity that calls the SDK:

```Kotlin
startCheckout(
    checkoutSession: "checkout_session",
countryCode: "country_code_iso",
callbackOTT: (String?) -> Unit,
callbackPaymentState: ((String?) -> Unit)?,
)
```

#### Callback One Time Token (OTT)

The `callbackOTT` parameter is a function that returns an OTT needed to complete the payment back to
back. This function is mandatory.

#### Callback Payment State

The `callbackPaymentState` parameter is a function that returns the current payment process. Sending
this function is not mandatory if you do not need the result. The possible states are:

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
    callbackOTT:(String?) -> Unit, //Optional - Default null
)
```

###### Lite SDK Version

```Kotlin
startPaymentLite(
    paymentSelected: PaymentSelected,
    callbackOTT:(String?) -> Unit, //Optional - Default null
)
```

For the Lite version, you need to send an additional parameter, which is the vaulted token and/or
the payment type that the user selected to make the payment.

```Kotlin
PaymentSelected(
    id: "payment_vaulted_token",
type: "payment_type",
)
```

At the end of this process, you will obtain the OTT, which is a required parameter to create a
payment via the Payments API POST/payments. You can obtain this data from to `onActivityResult`
explained in the callback section.

#### Complete Payment

If in the create_payment response the sdk_action_required parameter is true you need to call the
following method:

```Kotlin
continuePayment(
    showPaymentStatus: Boolean, //Optional - Default true
    callbackPaymentState:((String?) -> Unit)?, //Optional - Default null
)
```

To show your own payment status screens, you should send `false` in the `showPaymentStatus`
parameter and then get the payment state by callback.

## Styles

### Font Family

If you want to use your own font family, you can override our fonts. An example is presented in the
code snippet below:

```XML

<style name="YunoRegularFont">
    <item name="android:fontFamily">YOUR REGULAR FONT FILE ( EX: @font/inter_regular.ttf)</item>
</style>

<style name="YunoMediumFont">
<item name="android:fontFamily">YOUR MEDIUM FONT FILE ( EX: @font/inter_medium.ttf)</item>
</style>

<style name="YunoBoldFont">
<item name="android:fontFamily">YOUR BOLD FONT FILE ( EX: @font/inter_bold.ttf)</item>
</style>
```

> These are our font styles you can override:  
> -YunoRegularFont
> -YunoMediumFont
> -YunoBoldFont

### Button Styles

If you want to use your own button styles, you can override our styles. An example is presented in
the code snippet below:

```XML

<style name="Button.Normal.Purple">
    <item name="android:background">YOUR OWN COLOR ( EX: HEXCODE OR RESOURCE )</item>
    <item name="android:textColor">YOUR OWN COLOR ( EX: HEXCODE OR RESOURCE )</item>
    <item name="android:fontFamily">YOUR FONT FILE ( EX: @font/inter_regular.ttf)</item>
</style>
```

> These are our button styles you can override:  
> -Button.Normal.White  
> -Button.Normal.Green  
> -Button.Normal.Purple  
> -Button.Normal.White.TextPurple

### Color Styles

If you want to use your own color styles, you can override our styles. An example is presented in
the code snippet below:

```XML

<color name="yuno_purple_light">YOUR OWN COLOR ( EX: HEXCODE OR RESOURCE )</color>
```

> These are our color styles you can override:  
> -yuno_purple_light

## Create your own card form flow

For this you must follow the following steps:

### First Step

Create a new layout resource file called ```screen_payment_card_form.xml``` to override the current
xml and implement your own design.

### Second Step

After creating ```screen_payment_card_form.xml```, you can use your own design while ensuring the
use of Yuno Secure Fields components. This is so that the Yuno SDK can retrieve credit card
information.

#### The following are the components you should use:

* CardNumberEditText:
  This is where the user can enter the credit card number. You must use it with its defined android
  id:

```XML 
<com.yuno.payments.features.base.ui.views.CardNumberEditText
    android:id="@+id/textField_number" />
```

* CardExpiryDateEditText:
  This is where the user can enter the credit card's expiration date. You must use it with its
  defined android id:

```XML 
<com.yuno.payments.features.base.ui.views.CardExpiryDateEditText
    android:id="@+id/textField_expiration_date" />
```

* TextFieldItemView for CVV code:
  This is where the user can enter the credit card's verification code (CVV). You must use it with
  its defined android id:

```XML 
<com.yuno.payments.features.base.ui.views.TextFieldItemView
android:id="@+id/textField_verification_code" />
```

* TextFieldItemView for card holder's name:
  This is where the user can enter the credit card holder's name. You must use it with its defined
  android id:

```XML 
<com.yuno.payments.features.base.ui.views.TextFieldItemView
    android:id="@+id/textField_name" />
```

* SpinnerFieldItemView for identification document type:
  This is where the user can choose the type of identification document the credit card holder
  holds. You must use it with its defined android id:

```XML 
<com.yuno.payments.features.base.ui.views.SpinnerFieldItemView
    android:id="@+id/spinner_document_type" />
```

* TextFieldItemView for identification document number:
  This is where the user can enter the credit card holder's identification document number. You must
  use it with its defined android id:

```XML 
<com.yuno.payments.features.base.ui.views.TextFieldItemView
    android:id="@+id/textField_user_document" />
```

* AppCompatCheckBox for save card:
  This is where the user can choose whether to save the credit card for future purchases. You must
  use it with its defined android id:

```XML 
<androidx.appcompat.widget.AppCompatCheckBox
android:id="@+id/checkBox_save_card" />
```

* Button for validate card form and continue with the payment process:
  This is the component used to submit the form and send the credit card information to Yuno. You
  must use it with its defined android id:

```XML 
<Button
    android:id="@+id/button_complete_form" />
```