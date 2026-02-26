package com.yuno.payments.example.features.payment.viewmodel

import androidx.lifecycle.ViewModel
import com.yuno.payments.example.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * UI states for the Checkout Lite flow.
 * Config -> user enters session info -> PaymentEntry -> user enters payment method
 * -> SDK returns OTT -> OttResult
 */
sealed interface CheckoutLiteUiState {
    object Config : CheckoutLiteUiState
    object PaymentEntry : CheckoutLiteUiState
    data class OttResult(val token: String) : CheckoutLiteUiState
}

class CheckoutLiteViewModel : ViewModel() {

    private val _checkoutSession = MutableStateFlow(BuildConfig.YUNO_TEST_CHECKOUT_SESSION)
    val checkoutSession: StateFlow<String> = _checkoutSession.asStateFlow()

    private val _countryCode = MutableStateFlow(BuildConfig.YUNO_TEST_COUNTRY_CODE)
    val countryCode: StateFlow<String> = _countryCode.asStateFlow()

    private val _paymentMethodType = MutableStateFlow("")
    val paymentMethodType: StateFlow<String> = _paymentMethodType.asStateFlow()

    private val _vaultedToken = MutableStateFlow("")
    val vaultedToken: StateFlow<String> = _vaultedToken.asStateFlow()

    private val _uiState = MutableStateFlow<CheckoutLiteUiState>(CheckoutLiteUiState.Config)
    val uiState: StateFlow<CheckoutLiteUiState> = _uiState.asStateFlow()

    fun onCheckoutSessionChanged(value: String) {
        _checkoutSession.value = value
    }

    fun onCountryCodeChanged(value: String) {
        _countryCode.value = value
    }

    fun onSessionConfirmed() {
        _paymentMethodType.value = ""
        _vaultedToken.value = ""
        _uiState.value = CheckoutLiteUiState.PaymentEntry
    }

    fun onPaymentMethodTypeChanged(value: String) {
        _paymentMethodType.value = value
    }

    fun onVaultedTokenChanged(value: String) {
        _vaultedToken.value = value
    }

    /** Called when the SDK returns a one-time token via callbackOTT. */
    fun onOttReceived(token: String) {
        _uiState.value = CheckoutLiteUiState.OttResult(token)
    }

    /**
     * Called when the SDK reports a payment status via callbackPaymentState (e.g. "SUCCEEDED",
     * "FAIL"). This is NOT a token — reset the flow so the user can start a new payment.
     */
    fun onPaymentStateReceived() {
        _uiState.value = CheckoutLiteUiState.Config
    }

    fun onReset() {
        _uiState.value = CheckoutLiteUiState.Config
    }
}
