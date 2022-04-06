# Yuno Mobile SDKs
## Android SDK Requirements.

- Yuno Android SDK needs your minSdkVersion to be of 21 or above.
- Your project must have Java 8 enabled and use AndroidX instead of older support libraries.
- android-gradle-plugin version must be 4.0.0 or above.
- Proguard version must be 6.2.2 or above. 
- kotlin-gradle-plugin version must be 1.4.0 or above.

## Adding the SDK to the project
Add the repository source
```gradle
maven { url "https://yunopayments.jfrog.io/artifactory/snapshots-libs-release" }
```

Add the Yuno SDK dependency to the application build.gradle file:

```gradle
dependencies {
    implementation 'com.yuno:android-sdk:{last_version}'
}
```
#### Permissions
We include the INTERNET permission by default as we need it to make network requests, 

```xml
<uses-permission android:name="android.permission.INTERNET"/>
```

### Initialize Yuno
First, you'll need to get your Yuno app ID and Android API key, Then, initialize Yuno by calling the following in the onCreate() method of your application class:

```Java
Yuno.initialize(this, "your api key", "your app id");
```
**Note:** If you don't currently implement a custom application, you’ll need to create one. A custom application looks like this:
```kotlin
class CustomApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Yuno.initialize(this, "your api key", "your app id")
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
### PaymentMethod Enrollment
To display an Activity with the payment enrollment flow call the following method:
```Java
Yuno.startEnrollment("user_session_id", "payment_method_id")
```
```Kotlin second
startEnrollment(sessionId: "user_session_id", paymentMethod: "payment_method_id")
```



