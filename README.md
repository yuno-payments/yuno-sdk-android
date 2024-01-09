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
    val keepLoader: Boolean = false // This is to choose if keep Yuno loading screen until you create and continue with payment, this need an additional step that is shown below.
    val cardFormDeployed: Boolean = false, // This is only for SDK FULL, This is to choose if show card form deployed on payment methods list (TRUE) or if show normal card form in another screen (FALSE)
    val language: YunoLanguage? = null, //This is to choose the language of the SDK, if you send null or don't send it, Yuno SDK will take device language.
    val isDynamicViewEnabled: Boolean = false, //This is to choose if you want to use dynamic view or not, if you send false or don't send it, Yuno SDK will take false.
)
```

Available Languages

```kotlin
enum class YunoLanguage {
    SPANISH,
    ENGLISH,
    PORTUGUESE,
    INDONESIAN,
    MALAYSIAN
}
```

To keep Yuno loading screen until you create and continue with payment you also have to use the
function startCompletePaymentFlow() that is explained on the next session.

In addition, you need to update your manifest to use your application:

```XML

<application android:name=".CustomApplication"></application>
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
    callbackEnrollmentState: ((String?) -> Unit)? = null, // Default null | To register this callback is a must to call ```initEnrollment``` method on the onCreate method of activity.
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
merchantSessionId: String? = null //Optional - Default null 
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
    id : String "payment_vaulted_token",
    type : String "payment_type",
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

###### Complete Flow To Keep Yuno Loader

if you want to keep Yuno loading screen, you have to send in YunoConfig parameter in
Yuno.initialize() function the parameter keepLoader in TRUE and also when you decide to start the
payment you have to use the following function:

```kotlin
startCompletePaymentFlow(
    paymentSelected: PaymentSelected? = null,
showPaymentStatus: Boolean = true,
createPaymentFun: (suspend(ott: String) -> Unit)? = null,
callbackPaymentState: ((String?) -> Unit)? = null,
callbackOTT: ((String?) -> Unit)? = null,
)
```

the "createPaymentFun" parameter is a suspend function where Yuno wait until you create the payment
back to back, once the payment its created you complete the suspend function and Yuno will continue
with the payment, if you decide to use this flow you don't need to call continuePayment()
anymore.

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

* CloseButton:
  This is the button to close the form. You must use it with its defined android id:

```XML 
<ImageView
        android:id="@+id/imageView_close" />
```

* CardNumberEditText:
  This is where the user can enter the credit card number. You must use it with its defined android
  id:

```XML 
<com.yuno.payments.features.base.ui.views.CardNumberEditText
    android:id="@+id/textField_number" />
```

* CardDataStackView:
  This is where the user can enter the credit card's expiration date and can enter the credit card's verification code (CVV/CVC). You must use it with its
  defined android id:

```XML 
<com.yuno.payments.features.base.ui.views.CardDataStackView
    android:id="@+id/cardDataStackView" />
```

* TextView for Voucher Card Type:
  This is a copy we show when the card is VOUCHER type, you must set it below CVV field. You must
  use it with its defined android id:

```XML 
<TextView
    android:id="@+id/textView_voucher_copy"
    android:visibility="gone" />
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

* PhoneInformationView for customer's phone number:
  This is where the user can enter his phone number if needed. You must use it with its defined
  android id and it have to have GONE visibility:

```XML 
<com.yuno.payments.features.base.ui.views.TextFieldItemView
      android:id="@+id/textField_user_document"
      android:visibility="gone" />
```

* Installments:
  This is the component to show the spinner of card installments. You must use it with its defined
  android id, it must have GONE visibility and you must add ShimmerFrameLayout dependency: "
  implementation 'com.facebook.shimmer:shimmer:0.5.0'"

```XML 
<LinearLayout
                android:id="@+id/container_installments"
                android:orientation="vertical">

                <com.yuno.payments.features.base.ui.views.SpinnerFieldItemView
                    android:id="@+id/spinner_installments"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:visibility="gone"
                    app:spinner_title="@string/payment.form_installments" />

                <com.facebook.shimmer.ShimmerFrameLayout
                    android:id="@+id/shimmer_installments"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:foregroundGravity="center"
                    android:visibility="gone">

                    <include layout="@layout/shimmer_component_field" />
                </com.facebook.shimmer.ShimmerFrameLayout>

            </LinearLayout>
```

* Yuno's TextView:
  This is a text to show that the form is verifed by Yuno. You must use it with its defined android
  id:

```XML 
<TextView
        android:id="@+id/textView_secure_payment" />
```

* CustomYunoSwitch:
  This is a switch component to let the user choose if the card is gonna be used as credit or debit.
  You must use it with its defined android id and it must have GONE visibility:

```XML 
<com.yuno.payments.features.base.ui.views.CustomYunoSwitch
                android:id="@+id/switch_cardType"
                android:visibility="gone" />
```

* CustomYunoSwitch tooltip:
  This is a tooltip to show how the switch works. You must use it with its defined android id and it
  must have GONE visibility, as a recommendation this component should be placed next to the switch:

```XML 
<ImageView
                android:id="@+id/switch_tooltip"
                android:src="@drawable/ic_thin_info"
                android:visibility="gone"/>
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