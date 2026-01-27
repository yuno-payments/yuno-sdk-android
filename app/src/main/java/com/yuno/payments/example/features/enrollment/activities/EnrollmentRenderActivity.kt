package com.yuno.payments.example.features.enrollment.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.yuno.payments.example.BuildConfig
import com.yuno.payments.example.R
import com.yuno.payments.example.ui.views.CustomEditText
import com.yuno.sdk.enrollment.render.YunoEnrollmentFragmentController
import com.yuno.sdk.enrollment.render.YunoEnrollmentRenderListener
import com.yuno.sdk.enrollment.render.startEnrollmentRender

class EnrollmentRenderActivity : AppCompatActivity(), YunoEnrollmentRenderListener {

    private lateinit var fragmentController: YunoEnrollmentFragmentController

    private lateinit var editTextCustomerSession: CustomEditText
    private lateinit var editTextCountryCode: CustomEditText
    private lateinit var buttonStartEnrollment: Button
    private lateinit var buttonSubmit: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var fragmentContainer: FrameLayout

    private var needsSubmit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enrollment_render)

        initViews()
        initListeners()
    }

    private fun initViews() {
        editTextCustomerSession = findViewById(R.id.editText_customer_session)
        editTextCountryCode = findViewById(R.id.editText_country_code)
        buttonStartEnrollment = findViewById(R.id.button_start_enrollment)
        buttonSubmit = findViewById(R.id.button_submit)
        progressBar = findViewById(R.id.progress_bar)
        fragmentContainer = findViewById(R.id.enrollment_fragment_container)

        // Pre-fill with test data
        editTextCustomerSession.setText(BuildConfig.YUNO_TEST_CUSTOMER_SESSION)
        editTextCountryCode.setText(BuildConfig.YUNO_TEST_COUNTRY_CODE)

        // Hide submit button initially
        buttonSubmit.visibility = View.GONE
    }

    private fun initListeners() {
        buttonStartEnrollment.setOnClickListener {
            if (editTextCustomerSession.isValid && editTextCountryCode.isValid) {
                startEnrollmentRenderFlow()
            }
        }

        buttonSubmit.setOnClickListener {
            if (::fragmentController.isInitialized) {
                fragmentController.submitForm()
            }
        }
    }

    private fun startEnrollmentRenderFlow() {
        // Hide start button and show container
        buttonStartEnrollment.isEnabled = false

        fragmentController = startEnrollmentRender(
            customerSession = editTextCustomerSession.text.toString(),
            countryCode = editTextCountryCode.text.toString().uppercase(),
            coroutineScope = lifecycleScope,
            listener = this
        )
    }

    // YunoEnrollmentRenderListener implementation

    /**
     * Receives the fragment to display in the UI container.
     * @param fragment The fragment containing the enrollment form
     * @param needSubmit Indicates if a custom submit button should be shown
     */
    override fun showView(fragment: Fragment, needSubmit: Boolean) {
        // Replace the fragment in the container
        supportFragmentManager.beginTransaction()
            .replace(R.id.enrollment_fragment_container, fragment)
            .commit()

        // Show/hide submit button based on needSubmit
        needsSubmit = needSubmit
        buttonSubmit.isVisible = needSubmit

        Log.d("EnrollmentRender", "Fragment loaded, needSubmit: $needSubmit")
    }

    /**
     * Receives the final enrollment status.
     * @param resultCode Result code from the enrollment process
     * @param paymentStatus The final enrollment status (SUCCEEDED, FAIL, etc.)
     */
    override fun returnStatus(resultCode: Int, paymentStatus: String) {
        Log.d("EnrollmentRender", "Enrollment Status: $paymentStatus, Result Code: $resultCode")

        // Handle the enrollment result
        when (paymentStatus) {
            "SUCCEEDED" -> {
                Log.i("EnrollmentRender", "Enrollment succeeded!")
                
                // Remove the fragment
                clearEnrollmentFragment()
                
                // Show success toast
                Toast.makeText(this, "Payment method enrolled successfully!", Toast.LENGTH_LONG).show()
                
                // Close activity after a delay
                Handler(Looper.getMainLooper()).postDelayed({
                    finish()
                }, 2000)
            }

            "FAIL" -> {
                Log.e("EnrollmentRender", "Enrollment failed")
                
                // Remove the fragment
                clearEnrollmentFragment()
                
                // Show error toast
                Toast.makeText(this, "Enrollment failed. Please try again.", Toast.LENGTH_LONG).show()
                
                // Re-enable start button for retry
                buttonStartEnrollment.isEnabled = true
            }

            "CANCELED" -> {
                Log.w("EnrollmentRender", "Enrollment canceled by user")
                
                // Remove the fragment
                clearEnrollmentFragment()
                
                // Show canceled toast
                Toast.makeText(this, "Enrollment canceled", Toast.LENGTH_SHORT).show()
                
                // Close activity
                finish()
            }

            else -> {
                Log.w("EnrollmentRender", "Enrollment status: $paymentStatus")
                
                // Remove the fragment for any other status
                clearEnrollmentFragment()
                
                // Re-enable start button
                buttonStartEnrollment.isEnabled = true
            }
        }
    }
    
    /**
     * Clears the enrollment fragment from the container
     */
    private fun clearEnrollmentFragment() {
        supportFragmentManager.findFragmentById(R.id.enrollment_fragment_container)?.let { fragment ->
            supportFragmentManager.beginTransaction()
                .remove(fragment)
                .commit()
        }
        
        // Hide submit button
        buttonSubmit.visibility = View.GONE
        needsSubmit = false
    }

    /**
     * Receives loading state changes.
     * @param isLoading True if the SDK is processing, false otherwise
     */
    override fun loadingListener(isLoading: Boolean) {
        // Show/hide progress bar
        progressBar.isVisible = isLoading

        // Disable/enable submit button during loading
        if (needsSubmit) {
            buttonSubmit.isEnabled = !isLoading
        }

        Log.d("EnrollmentRender", "Loading state: $isLoading")
    }
}
