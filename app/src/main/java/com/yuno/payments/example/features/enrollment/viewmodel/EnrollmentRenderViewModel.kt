package com.yuno.payments.example.features.enrollment.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.yuno.payments.example.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * UI states for the Enrollment Render flow.
 * Config -> user enters session info -> FragmentVisible (SDK fragment shown)
 * -> StatusResult (final enrollment status)
 * Loading is a transient overlay state shown during SDK processing.
 *
 * needsSubmit in FragmentVisible: some payment methods (e.g., credit cards) require
 * the merchant to provide a "Submit" button that calls fragmentController.submitForm().
 */
sealed interface EnrollmentRenderUiState {
    object Config : EnrollmentRenderUiState
    data class FragmentVisible(val needsSubmit: Boolean) : EnrollmentRenderUiState
    object Loading : EnrollmentRenderUiState
    data class StatusResult(val status: String) : EnrollmentRenderUiState
}

class EnrollmentRenderViewModel : ViewModel() {

    var customerSession by mutableStateOf(BuildConfig.YUNO_TEST_CUSTOMER_SESSION)
        private set
    var countryCode by mutableStateOf(BuildConfig.YUNO_TEST_COUNTRY_CODE)
        private set
    var showErrors by mutableStateOf(false)
        private set

    private val _uiState = MutableStateFlow<EnrollmentRenderUiState>(EnrollmentRenderUiState.Config)
    val uiState: StateFlow<EnrollmentRenderUiState> = _uiState.asStateFlow()

    // Tracks the state before Loading so we can restore it when loading finishes.
    // The SDK's loadingListener(false) means "loading done, go back to what you were showing."
    private var preLoadingState: EnrollmentRenderUiState = EnrollmentRenderUiState.Config

    fun onCustomerSessionChange(value: String) { customerSession = value }
    fun onCountryCodeChange(value: String) { countryCode = value }

    /** Validates fields and sets showErrors. Returns true if Activity should proceed. */
    fun validateAndStart(): Boolean {
        showErrors = true
        return customerSession.isNotBlank() && countryCode.isNotBlank()
    }

    fun onFragmentVisible(needsSubmit: Boolean) {
        _uiState.value = EnrollmentRenderUiState.FragmentVisible(needsSubmit)
    }

    fun onLoading(isLoading: Boolean) {
        // NOTE: This logic is not thread-safe in the general case, but it is safe here because
        // the Yuno SDK always invokes its listener callbacks on the main thread. All calls to
        // onLoading(), onFragmentVisible(), and onStatusResult() therefore happen sequentially
        // with no concurrent access to _uiState or preLoadingState.
        if (isLoading) {
            // Only save preLoadingState if we're not already Loading.
            // If the SDK calls loadingListener(true) twice, we don't want to overwrite
            // preLoadingState with Loading itself (which would cause an unrecoverable stuck state).
            if (_uiState.value !is EnrollmentRenderUiState.Loading) {
                preLoadingState = _uiState.value
            }
            _uiState.value = EnrollmentRenderUiState.Loading
        } else if (_uiState.value is EnrollmentRenderUiState.Loading) {
            // Only restore preLoadingState if we're still in Loading.
            // If a terminal state (StatusResult) arrived while loading was active,
            // it already replaced Loading — don't overwrite it.
            _uiState.value = preLoadingState
        }
    }

    fun onStatusResult(status: String) {
        _uiState.value = EnrollmentRenderUiState.StatusResult(status)
    }

    fun onReset() {
        showErrors = false
        preLoadingState = EnrollmentRenderUiState.Config
        _uiState.value = EnrollmentRenderUiState.Config
    }
}
