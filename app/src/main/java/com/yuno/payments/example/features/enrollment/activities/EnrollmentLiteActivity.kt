package com.yuno.payments.example.features.enrollment.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.yuno.payments.example.BuildConfig
import com.yuno.payments.example.features.enrollment.ui.EnrollmentLiteScreen
import com.yuno.payments.example.ui.theme.YunoTheme
import com.yuno.sdk.enrollment.initEnrollment
import com.yuno.sdk.enrollment.startEnrollment

/**
 * Enrollment Lite — demonstrates the simplest enrollment flow where the SDK manages
 * the enrollment UI and the merchant just calls initEnrollment() + startEnrollment().
 *
 * This Activity also supports deep link enrollment via the URI scheme:
 *   yuno://www.y.uno/enrollment?customerSession=XXX
 * (Declared in AndroidManifest.xml as an intent filter.)
 *
 * SDK constraint: initEnrollment() MUST be called in onCreate() to register lifecycle
 * observers before the enrollment flow can start.
 */
class EnrollmentLiteActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // CRITICAL: initEnrollment() must be called in onCreate() before startEnrollment().
        // It registers lifecycle observers and initializes SDK internals.
        initEnrollment(::onEnrollmentStateChange)

        // Deep link handling: if this Activity was launched via the yuno:// URI scheme,
        // extract the customerSession from the URL and start enrollment immediately.
        // This enables external apps/links to trigger enrollment directly.
        intent.data?.let { uri ->
            val uriString = uri.toString()
            if (uriString.contains("yuno://www.y.uno/enrollment")) {
                startEnrollment(
                    customerSession = uriString.substringAfter("yuno://www.y.uno/enrollment?customerSession="),
                    countryCode = BuildConfig.YUNO_TEST_COUNTRY_CODE.uppercase(),
                )
            }
        }

        setContent {
            YunoTheme {
                EnrollmentLiteScreen(
                    initialCustomerSession = BuildConfig.YUNO_TEST_CUSTOMER_SESSION,
                    initialCountryCode = BuildConfig.YUNO_TEST_COUNTRY_CODE,
                    onStartEnrollment = { customerSession, countryCode ->
                        startEnrollment(
                            customerSession = customerSession,
                            countryCode = countryCode,
                        )
                    },
                )
            }
        }
    }

    private fun onEnrollmentStateChange(enrollmentState: String?) {
        enrollmentState?.let {
            Log.d("EnrollmentLite", "Enrollment state: $it")
        }
    }
}
