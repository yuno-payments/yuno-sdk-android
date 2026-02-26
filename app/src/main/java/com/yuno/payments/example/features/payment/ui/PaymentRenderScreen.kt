package com.yuno.payments.example.features.payment.ui

import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.yuno.payments.example.R
import com.yuno.payments.example.features.payment.viewmodel.PaymentRenderUiState
import com.yuno.payments.example.ui.components.OttResultPanel
import com.yuno.payments.example.ui.components.StatusCard
import com.yuno.payments.example.ui.components.YunoButton
import com.yuno.payments.example.ui.components.YunoTextField
import com.yuno.payments.example.ui.components.YunoTonalButton
import com.yuno.payments.example.ui.theme.YunoTheme

@Composable
fun PaymentRenderScreen(
    uiState: PaymentRenderUiState,
    checkoutSession: String,
    countryCode: String,
    paymentMethod: String,
    showErrors: Boolean,
    onCheckoutSessionChange: (String) -> Unit,
    onCountryCodeChange: (String) -> Unit,
    onPaymentMethodChange: (String) -> Unit,
    onStartPayment: () -> Unit,
    onSubmitPayment: () -> Unit,
    onContinuePayment: () -> Unit,
    onReset: () -> Unit,
) {
    YunoTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                // CRITICAL: This AndroidView FrameLayout must ALWAYS be in the Compose hierarchy,
                // regardless of the current UI state. The SDK's showView() callback commits a
                // Fragment into this container via supportFragmentManager. If the FrameLayout were
                // inside a conditional (e.g., only shown in FragmentVisible state), the fragment
                // manager would crash because the container view wouldn't exist in the view tree
                // when the SDK tries to commit the fragment.
                // The overlay states (Config, OttReceived, etc.) are drawn ON TOP of this view
                // using a Crossfade, effectively hiding it when it's not needed.
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        FrameLayout(ctx).also { it.id = R.id.payment_fragment_container }
                    },
                )

                Crossfade(targetState = uiState, label = "payment-overlay") { state ->
                    when (state) {
                        is PaymentRenderUiState.Config -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(horizontal = 20.dp)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    text = "Payment Render",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                )
                                Text(
                                    text = "Configure and render a payment form",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Spacer(Modifier.height(4.dp))
                                YunoTextField(
                                    value = checkoutSession,
                                    onValueChange = onCheckoutSessionChange,
                                    label = "Checkout Session",
                                    isError = showErrors && checkoutSession.isBlank(),
                                )
                                YunoTextField(
                                    value = countryCode,
                                    onValueChange = { onCountryCodeChange(it.uppercase()) },
                                    label = "Country Code",
                                    isError = showErrors && countryCode.isBlank(),
                                )
                                YunoTextField(
                                    value = paymentMethod,
                                    onValueChange = { onPaymentMethodChange(it.uppercase()) },
                                    label = "Payment Method Type",
                                    isError = showErrors && paymentMethod.isBlank(),
                                )
                                YunoButton(text = "Start Payment", onClick = onStartPayment)
                                Spacer(Modifier.height(16.dp))
                            }
                        }

                        is PaymentRenderUiState.OttReceived -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(horizontal = 20.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    text = "Token Received",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                )
                                OttResultPanel(token = state.token)
                                Text(
                                    text = "Use this token to create a payment in your backend, then tap Continue.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                YunoTonalButton(text = "Continue Payment", onClick = onContinuePayment)
                            }
                        }

                        is PaymentRenderUiState.Loading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(48.dp),
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                    Spacer(Modifier.height(16.dp))
                                    Text(
                                        text = "Processing...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }

                        is PaymentRenderUiState.StatusResult -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                StatusCard(
                                    status = state.status,
                                    label = "Payment",
                                    onRetry = if (state.status.uppercase() in listOf("FAIL", "REJECT")) onReset else null,
                                )
                            }
                        }

                        is PaymentRenderUiState.FragmentVisible -> {
                            // No overlay — SDK fragment is visible
                        }
                    }
                }
            }

            // Merchant submit button — shown below the SDK fragment in FragmentVisible state
            AnimatedVisibility(visible = uiState is PaymentRenderUiState.FragmentVisible) {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    YunoButton(
                        text = "Submit Payment",
                        onClick = onSubmitPayment,
                    )
                }
            }
        }
    }
}
