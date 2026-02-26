package com.yuno.payments.example.features.payment.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yuno.payments.example.features.payment.viewmodel.CheckoutLiteUiState
import com.yuno.payments.example.features.payment.viewmodel.CheckoutLiteViewModel
import com.yuno.payments.example.ui.components.OttResultPanel
import com.yuno.payments.example.ui.components.YunoButton
import com.yuno.payments.example.ui.components.YunoOutlinedButton
import com.yuno.payments.example.ui.components.YunoTextField
import com.yuno.payments.example.ui.components.YunoTonalButton

@Composable
fun CheckoutLiteScreen(
    viewModel: CheckoutLiteViewModel,
    onUpdateCheckoutSession: (checkoutSession: String, countryCode: String) -> Unit,
    onStartPaymentLite: (paymentMethodType: String, vaultedToken: String) -> Unit,
    onContinuePayment: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val checkoutSession by viewModel.checkoutSession.collectAsState()
    val countryCode by viewModel.countryCode.collectAsState()
    val paymentMethodType by viewModel.paymentMethodType.collectAsState()
    val vaultedToken by viewModel.vaultedToken.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Checkout Lite",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(4.dp))

        // contentKey maps each state to its class so AnimatedContent only animates when the
        // state *type* changes (e.g., Config -> PaymentEntry), not when data within the same
        // state changes (e.g., token update in OttResult). Without this, Compose may fail to
        // match the old and new states correctly, causing visual glitches.
        AnimatedContent(
            targetState = uiState,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            contentKey = { it::class },
            label = "checkout-lite-state",
        ) { state ->
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                when (state) {
                    is CheckoutLiteUiState.Config -> {
                        Text(
                            text = "Configure your checkout session",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        YunoTextField(
                            value = checkoutSession,
                            onValueChange = viewModel::onCheckoutSessionChanged,
                            label = "Checkout Session",
                        )
                        YunoTextField(
                            value = countryCode,
                            onValueChange = viewModel::onCountryCodeChanged,
                            label = "Country Code",
                        )
                        YunoButton(
                            text = "Set Checkout Session",
                            onClick = {
                                onUpdateCheckoutSession(checkoutSession, countryCode.uppercase())
                                viewModel.onSessionConfirmed()
                            },
                            enabled = checkoutSession.isNotBlank() && countryCode.isNotBlank(),
                        )
                    }

                    is CheckoutLiteUiState.PaymentEntry -> {
                        Text(
                            text = "Enter payment details",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        YunoTextField(
                            value = paymentMethodType,
                            onValueChange = viewModel::onPaymentMethodTypeChanged,
                            label = "Payment Method Type",
                        )
                        YunoTextField(
                            value = vaultedToken,
                            onValueChange = viewModel::onVaultedTokenChanged,
                            label = "Vaulted Token",
                        )
                        YunoButton(
                            text = "Pay",
                            onClick = { onStartPaymentLite(paymentMethodType, vaultedToken) },
                            enabled = paymentMethodType.isNotBlank(),
                        )
                    }

                    is CheckoutLiteUiState.OttResult -> {
                        OttResultPanel(token = state.token)
                        YunoTonalButton(
                            text = "Continue",
                            onClick = onContinuePayment,
                        )
                        YunoOutlinedButton(
                            text = "Reset",
                            onClick = viewModel::onReset,
                        )
                    }
                }
            }
        }
    }
}
