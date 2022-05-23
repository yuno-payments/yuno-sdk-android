
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
enrollPaymentWithYuno(
    customerSession: "customer_session",
)
```
### Payment flow
To start a new payment process call the following method from you app:
```Kotlin 
startPaymentWithYuno(
    requestCode: 12345,
    checkoutSession: "checkout_session",
    countryCode: "country_code_iso",
    paymentSelected: PaymentSelected,
    getOneTimeTokenFunction: suspend { //Method to create payment }
)
```
```Kotlin 
PaymentSelected(  
    id: "payment_vaulted_token",  
    type: "payment_type",  
)
```
the activity displayed could return three different state: Success, Cancelled or Error, to listen this state you have to implement the method onActivityResult fo the activity, like in the follow piece of code
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
**Note:** If you need to change the request code of the flow, you can specify that on startPaymentWithYuno method
