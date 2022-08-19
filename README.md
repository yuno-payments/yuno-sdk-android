
# Yuno Mobile SDKs
## Android SDK Requirements.

- Yuno Android SDK needs your minSdkVersion to be of 21 or above.
- Your project must have Java 8 enabled and use AndroidX instead of older support libraries.
- The android-gradle-plugin version must be 4.0.0 or above. 
- The Proguard version must be 6.2.2 or above. 
- The kotlin-gradle-plugin version must be 1.4.0 or above.

## Adding the SDK to the project
Add the repository source using the following line of code:

```Gradle
maven { url "https://yunopayments.jfrog.io/artifactory/snapshots-libs-release" }
```

Include the following code snippet in the build.gradle file to add the Yuno SDK dependency to the application

```Gradle 
dependencies {
    implementation 'com.yuno.payments:android-sdk:{last_version}'
}
```
#### Permissions
We included the INTERNET permission by default as we need it to make network requests:

```xml 
<uses-permission android:name="android.permission.INTERNET"/>
```

### Initialize Yuno
To initialize the Yuno SDK, first you need to get your public API keys from the Yuno dashboard. Then, if you haven’t implemented a custom application yet, you’ll need to create one and call the initialize function in the onCreate() method of your application class.

The following code includes an example of a custom application:

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
Call the following method from your activity to start an enrollment flow of a new payment method.
```Kotlin 
startEnrollment(
    requestCode: Int, //Optional
    customerSession: String,
    showEnrollmentStatus: Boolean, //Optional - Default true
)
```
**Note:** If you need to change the request code of the flow, you can specify that on the “StartEnrollment” method with the new requestCode as a parameter.

### Checkout
To start a new payment process you need to call the following method on the onCreate method of activity containing your checkout view:

```Kotlin 
startCheckout(
    checkoutSession: "checkout_session",
    countryCode: "country_code_iso",
)
```
#### Show Payment Methods
When implementing the Full  SDK version you need to add the next view on your layout to show the available payment methods:

```XML 
<com.yuno.payments.features.payment.ui.views.PaymentMethodListView
        android:id="@+id/list_payment_methods"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
```

#### Start Payment
To start a payment process you have to call the method `startPayment`. However,  if you are using the lite version of the SDK, you must call the method `startPaymentLite`.

###### Full SDK Version
```Kotlin 
startPayment(
    requestCode: 12345 //Optional,
)
```
###### Lite SDK Version
```Kotlin 
startPaymentLite(
    requestCode: 12345 //Optional,
    paymentSelected: PaymentSelected,
)
```
For the Lite version you need to send an additional parameter,  which is the vaulted token and/or the payment type that the user selected to make the payment.

```Kotlin 
PaymentSelected(  
    id: "payment_vaulted_token",  
    type: "payment_type",  
)
```
At the end of this process you will obtain the One Time Token (OTT) which is a required parameter to create a payment via the Payments API POST/payments, you can obtain this data  from to `onActivityResult` explained in the callback section.

#### Complete Payment
If in the create_payment response the sdk_action_required parameter is true you need to call the following method:
```Kotlin 
continuePayment(
    requestCode: Int, //Optional
    showPaymentStatus: Boolean, //Optional - Deault true
)
```
To show your own payment status screens, you should send `false` in the `showPaymentStatus` parameter and then get the payment state in the activity result callback.

The possible states are:
```Kotlin 
const val PAYMENT_STATE_SUCCESS : String
const val PAYMENT_STATE_FAIL : String
const val PAYMENT_STATE_PROCESSING : String
const val PAYMENT_STATE_REJECT : String
```

#### Callbacks
All activities displayed could return three different states: Success, Canceled or Error. To get those states you have to implement the method `onActivityResult` of your activity, like shown in the following code snippet:

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
You can get some information about the payment or enrollment, such as the OTT, with the key `PAYMENT_RESULT_DATA_TOKEN` after calling a `startPayment` or `startPaymentLite` method.
The following code snippet shows an example of how to get information about the OTT:


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