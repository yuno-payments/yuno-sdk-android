
# Yuno Mobile SDKs
## Android SDK Requirements.

- Yuno Android SDK needs your minSdkVersion to be of 21 or above.
- Your project must have Java 8 enabled and use AndroidX instead of older support libraries.
- android-gradle-plugin version must be 4.0.0 or above.
- Proguard version must be 6.2.2 or above.
- kotlin-gradle-plugin version must be 1.4.0 or above.

## Adding the SDK to the project
Add the repository source
```Gradle
maven { url "https://yunopayments.jfrog.io/artifactory/snapshots-libs-release" }
```

Add the Yuno SDK dependency to the application build.gradle file:

```Gradle 
dependencies {
    implementation 'com.yuno.payments:android-sdk:{last_version}'
}
```
#### Permissions
We include the INTERNET permission by default as we need it to make network requests,

```xml 
<uses-permission android:name="android.permission.INTERNET"/>
```

### Initialize Yuno
First, you'll need to get your Yuno API key, Then, initialize the SDK  calling the following in the onCreate() method of your application class:

```Kotlin 
Yuno.initialize(this, "your api key");
```
**Note:** If you don't currently implement a custom application, you’ll need to create one. A custom application looks like this:
```kotlin 
class CustomApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Yuno.initialize(this, "your api key")
    }
}
```
You’ll need to update your manifest to use your application:
```XML 
<application
    android:name=".CustomApplication">
</application>
```

## Functions
### Enroll a new payment method
To display an Activity with the flow to enroll new payment method call the following method from your activity:
```Kotlin 
startEnrollment(
    requestCode: Int, //Optional
    customerSession: String,
    showEnrollmentStatus: Boolean, //Optional - Default true
)
```
**Note:** If you need to change the request code of the flow, you can specify that on enrollPaymentWithYuno method with requestCode param

### Checkout
To start a new payment process you need to call following method on the onCreate method of activity that contains your checkout view
```Kotlin 
startCheckout(
    checkoutSession: "checkout_session",
    countryCode: "country_code_iso",
)
```
#### Show Payment Methods
when you implement a SDK Full you have to add the next view on your layout to show the payment methods available
```XML 
<com.yuno.payments.features.payment.ui.views.PaymentMethodListView
        android:id="@+id/list_payment_methods"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
```

#### Start Payment
To start a payment process you have to call the method `startPayment` but if your are using the lite version you must to call `startPaymentLite`
```Kotlin 
startPayment(
    requestCode: 12345 //Optional,
)
```

```Kotlin 
startPaymentLite(
    requestCode: 12345 //Optional,
    paymentSelected: PaymentSelected,
)
```
for the Lite version you need to send an additional parameter, these consist, the vaulted token and/or payment type with which the user will pay

```Kotlin 
PaymentSelected(  
    id: "payment_vaulted_token",  
    type: "payment_type",  
)
```
At the end of this process you will obtain the One Time Token to create back-back the payment, this data you can obtain from the onActivityResult explain in the callback section

#### Complete Payment
If the payment required an start an action to complete the payment you can call the following method to execute the payment and get its state from your activity
```Kotlin 
continuePayment(
    requestCode: Int, //Optional
    showPaymentStatus: Boolean, //Optional - Deault true
)
```
To show your own payment status screens, you should send in false the `showPaymentStatus` parameter. and then get the state in the activity from result callback, possible state are:
```Kotlin 
const val PAYMENT_STATE_SUCCESS : String
const val PAYMENT_STATE_FAIL : String
const val PAYMENT_STATE_PROCESSING : String
const val PAYMENT_STATE_REJECT : String
```
**Note:** These information is under the `PAYMENT_RESULT_DATA_STATE` key. the following code is an example to how retrieve the data

```Kotlin 
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == YUNO_CONTINUE_PAYMENT_REQUEST_CODE) {
        val paymentState: String? = data?.getStringExtra(PAYMENT_RESULT_DATA_STATE)
        paymentState?.let { Log.e("Payment State", it) }
    }
}
```

#### Callbacks
All activity displayed could return three different states: Success, Cancelled or Error, to listen this state you have to implement the method `onActivityResult` fo the activity, like in the follow piece of code
```Kotlin 
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == YUNO_PAYMENT_REQUEST_CODE) {
            if (resultCode == PAYMENT_RESULT_SUCCESS) {
                //TODO: Write your code
            }
            if (resultCode == PAYMENT_RESULT_ERROR) {
                //TODO: Write your code
            }
            if (resultCode == PAYMENT_RESULT_CANCELED) {
                //TODO: Write your code
            }
        }
    }
```
you can get some information like OTT with the key PAYMENT_RESULT_DATA_TOKEN after call a `startPayment` or `startPaymentLite` method, how is showing in the next code

```Kotlin 
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == YUNO_PAYMENT_REQUEST_CODE) {
            if (resultCode == PAYMENT_RESULT_SUCCESS) {
                val token = data?.getStringExtra(PAYMENT_RESULT_DATA_TOKEN)
            }
        }
    }
```

**Note:** If you need to change the request code of the flow, you can do it in each method that start a flow.