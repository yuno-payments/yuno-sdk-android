package com.yuno.payments.example.features.enrollment.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.yuno.payments.example.R
import com.yuno.payments.example.features.enrollment.ui.EnrollmentRenderScreen
import com.yuno.payments.example.features.enrollment.viewmodel.EnrollmentRenderViewModel
import com.yuno.sdk.enrollment.render.YunoEnrollmentFragmentController
import com.yuno.sdk.enrollment.render.YunoEnrollmentRenderListener
import com.yuno.sdk.enrollment.render.startEnrollmentRender

/**
 * Enrollment Render — demonstrates rendering the SDK enrollment form directly inside
 * a merchant-controlled layout using startEnrollmentRender().
 *
 * IMPORTANT: This Activity must extend AppCompatActivity (not ComponentActivity) because
 * the SDK renders its enrollment form as a Fragment. The Fragment is committed into a
 * FrameLayout via supportFragmentManager, which requires AppCompatActivity.
 *
 * Flow: startEnrollmentRender() -> SDK calls showView() with Fragment
 *       -> if needsSubmit is true, merchant calls submitForm() -> returnStatus()
 */
class EnrollmentRenderActivity : AppCompatActivity(), YunoEnrollmentRenderListener {

    private lateinit var fragmentController: YunoEnrollmentFragmentController
    private val viewModel: EnrollmentRenderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val uiState by viewModel.uiState.collectAsState()
            EnrollmentRenderScreen(
                uiState = uiState,
                customerSession = viewModel.customerSession,
                countryCode = viewModel.countryCode,
                showErrors = viewModel.showErrors,
                onCustomerSessionChange = viewModel::onCustomerSessionChange,
                onCountryCodeChange = viewModel::onCountryCodeChange,
                onStartEnrollment = {
                    if (viewModel.validateAndStart()) {
                        startEnrollmentRenderFlow()
                    }
                },
                onSubmit = {
                    if (::fragmentController.isInitialized) {
                        fragmentController.submitForm()
                    }
                },
                onReset = viewModel::onReset,
            )
        }
    }

    private fun startEnrollmentRenderFlow() {
        fragmentController = startEnrollmentRender(
            customerSession = viewModel.customerSession,
            countryCode = viewModel.countryCode.uppercase(),
            coroutineScope = lifecycleScope,
            listener = this,
        )
    }

    // --- YunoEnrollmentRenderListener callbacks ---
    // The SDK calls these methods to communicate enrollment form lifecycle events.

    override fun showView(fragment: Fragment, needSubmit: Boolean) {
        // Transition UI state first so the Config overlay is removed and the FrameLayout is
        // visible, then commit the SDK fragment into it.
        // NOTE: The FrameLayout (R.id.enrollment_fragment_container) must ALWAYS be present in the
        // Compose hierarchy (see EnrollmentRenderScreen). If it were inside a conditional block,
        // the fragment manager would fail to find the container when the SDK calls showView().
        //
        // needSubmit: some payment methods require the merchant to provide a "Submit" button
        // (e.g., credit card forms). When true, show a submit button that calls submitForm().
        viewModel.onFragmentVisible(needSubmit)
        supportFragmentManager.beginTransaction()
            .replace(R.id.enrollment_fragment_container, fragment)
            .commit()
        Log.d("EnrollmentRender", "Enrollment fragment loaded, needSubmit: $needSubmit")
    }

    override fun returnStatus(resultCode: Int, paymentStatus: String) {
        // Called when the enrollment flow reaches a terminal state (SUCCEEDED, FAIL, etc.).
        Log.d("EnrollmentRender", "Status: $paymentStatus, Code: $resultCode")
        when (paymentStatus) {
            "SUCCEEDED" -> {
                removeEnrollmentFragment()
                viewModel.onStatusResult(paymentStatus)
                Toast.makeText(this, "Payment method enrolled successfully!", Toast.LENGTH_LONG).show()
                Handler(Looper.getMainLooper()).postDelayed({ finish() }, 2000)
            }
            "CANCELED" -> {
                removeEnrollmentFragment()
                viewModel.onStatusResult(paymentStatus)
                Toast.makeText(this, "Enrollment canceled", Toast.LENGTH_SHORT).show()
                finish()
            }
            "FAIL" -> {
                removeEnrollmentFragment()
                viewModel.onStatusResult(paymentStatus)
                Toast.makeText(this, "Enrollment failed. Please try again.", Toast.LENGTH_LONG).show()
            }
            else -> {
                // Unknown/unhandled status — clean up and reset so the user is not stuck.
                Log.w("EnrollmentRender", "Unhandled enrollment status: $paymentStatus")
                removeEnrollmentFragment()
                viewModel.onReset()
            }
        }
    }

    private fun removeEnrollmentFragment() {
        supportFragmentManager.findFragmentById(R.id.enrollment_fragment_container)?.let {
            supportFragmentManager.beginTransaction().remove(it).commit()
        }
    }

    override fun loadingListener(isLoading: Boolean) {
        Log.d("EnrollmentRender", "Loading: $isLoading")
        viewModel.onLoading(isLoading)
    }
}
