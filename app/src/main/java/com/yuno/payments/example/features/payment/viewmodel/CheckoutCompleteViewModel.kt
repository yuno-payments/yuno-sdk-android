package com.yuno.payments.example.features.payment.viewmodel

import androidx.lifecycle.ViewModel
import com.yuno.payments.example.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * UI states for the Checkout Complete flow.
 * Uses a sealed interface so the screen can render different layouts per state.
 * Field values (checkoutSession, countryCode) are held in the ViewModel as separate StateFlows
 * because they persist across state transitions (e.g., visible in both Config and PaymentList).
 */
sealed interface CheckoutCompleteUiState {
    object Config : CheckoutCompleteUiState
    data class PaymentList(val isPayEnabled: Boolean) : CheckoutCompleteUiState
    data class OttResult(val token: String) : CheckoutCompleteUiState
}

class CheckoutCompleteViewModel : ViewModel() {

    // Editable field values persist across state transitions (Config fields remain visible in PaymentList state)
    private val _checkoutSession = MutableStateFlow(BuildConfig.YUNO_TEST_CHECKOUT_SESSION)
    val checkoutSession: StateFlow<String> = _checkoutSession.asStateFlow()

    private val _countryCode = MutableStateFlow(BuildConfig.YUNO_TEST_COUNTRY_CODE)
    val countryCode: StateFlow<String> = _countryCode.asStateFlow()

    private val _uiState = MutableStateFlow<CheckoutCompleteUiState>(CheckoutCompleteUiState.Config)
    val uiState: StateFlow<CheckoutCompleteUiState> = _uiState.asStateFlow()

    fun onCheckoutSessionChanged(value: String) {
        _checkoutSession.value = value
    }

    fun onCountryCodeChanged(value: String) {
        _countryCode.value = value
    }

    fun onSetCheckoutSession() {
        if (_checkoutSession.value.isNotBlank() && _countryCode.value.isNotBlank()) {
            _uiState.value = CheckoutCompleteUiState.PaymentList(isPayEnabled = false)
        }
    }

    fun onPaymentSelectionChanged(isSelected: Boolean) {
        _uiState.value = CheckoutCompleteUiState.PaymentList(isPayEnabled = isSelected)
    }

    fun onTokenUpdated(token: String) {
        _uiState.value = CheckoutCompleteUiState.OttResult(token)
    }

    fun onPaymentStateChange(paymentState: String) {
        _uiState.value = CheckoutCompleteUiState.PaymentList(isPayEnabled = false)
    }
}
