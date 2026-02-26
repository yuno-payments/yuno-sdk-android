package com.yuno.payments.example.features.payment.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.yuno.payments.example.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * UI states for the Payment Render flow.
 * Config -> user enters session info -> FragmentVisible (SDK fragment shown)
 * -> OttReceived (token ready) -> StatusResult (final payment status)
 * Loading is a transient overlay state shown during SDK processing.
 */
sealed interface PaymentRenderUiState {
    object Config : PaymentRenderUiState
    object FragmentVisible : PaymentRenderUiState
    data class OttReceived(val token: String) : PaymentRenderUiState
    object Loading : PaymentRenderUiState
    data class StatusResult(val status: String) : PaymentRenderUiState
}

class PaymentRenderViewModel : ViewModel() {

    var checkoutSession by mutableStateOf(BuildConfig.YUNO_TEST_CHECKOUT_SESSION)
        private set
    var countryCode by mutableStateOf(BuildConfig.YUNO_TEST_COUNTRY_CODE)
        private set
    var paymentMethod by mutableStateOf("CARD")
        private set
    var showErrors by mutableStateOf(false)
        private set

    private val _uiState = MutableStateFlow<PaymentRenderUiState>(PaymentRenderUiState.Config)
    val uiState: StateFlow<PaymentRenderUiState> = _uiState.asStateFlow()

    // Tracks the state before Loading so we can restore it when loading finishes.
    // The SDK's loadingListener(false) means "loading done, go back to what you were showing."
    private var preLoadingState: PaymentRenderUiState = PaymentRenderUiState.Config

    fun onCheckoutSessionChange(value: String) { checkoutSession = value }
    fun onCountryCodeChange(value: String) { countryCode = value }
    fun onPaymentMethodChange(value: String) { paymentMethod = value }

    /** Validates fields and sets showErrors. Returns true if Activity should proceed. */
    fun validateAndStart(): Boolean {
        showErrors = true
        return checkoutSession.isNotBlank() && countryCode.isNotBlank() && paymentMethod.isNotBlank()
    }

    fun onFragmentVisible() {
        _uiState.value = PaymentRenderUiState.FragmentVisible
    }

    fun onOttReceived(token: String) {
        _uiState.value = PaymentRenderUiState.OttReceived(token)
    }

    fun onLoading(isLoading: Boolean) {
        if (isLoading) {
            preLoadingState = _uiState.value
            _uiState.value = PaymentRenderUiState.Loading
        } else if (_uiState.value is PaymentRenderUiState.Loading) {
            // Only restore preLoadingState if we're still in Loading.
            // If a terminal state (OttReceived, StatusResult) was set while loading was
            // active, it already replaced Loading — don't overwrite it.
            _uiState.value = preLoadingState
        }
    }

    fun onStatusResult(status: String) {
        _uiState.value = PaymentRenderUiState.StatusResult(status)
    }

    fun onReset() {
        showErrors = false
        _uiState.value = PaymentRenderUiState.Config
    }
}
