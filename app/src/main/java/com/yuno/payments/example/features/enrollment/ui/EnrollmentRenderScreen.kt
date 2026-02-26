package com.yuno.payments.example.features.enrollment.ui

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
import com.yuno.payments.example.features.enrollment.viewmodel.EnrollmentRenderUiState
import com.yuno.payments.example.ui.components.StatusCard
import com.yuno.payments.example.ui.components.YunoButton
import com.yuno.payments.example.ui.components.YunoTextField
@Composable
fun EnrollmentRenderScreen(
    uiState: EnrollmentRenderUiState,
    customerSession: String,
    countryCode: String,
    showErrors: Boolean,
    onCustomerSessionChange: (String) -> Unit,
    onCountryCodeChange: (String) -> Unit,
    onStartEnrollment: () -> Unit,
    onSubmit: () -> Unit,
    onReset: () -> Unit,
) {
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
                // The overlay states (Config, Loading, etc.) are drawn ON TOP of this view
                // using a Crossfade, effectively hiding it when it's not needed.
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        FrameLayout(ctx).also { it.id = R.id.enrollment_fragment_container }
                    },
                )

                Crossfade(targetState = uiState, label = "enrollment-overlay") { state ->
                    when (state) {
                        is EnrollmentRenderUiState.Config -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(horizontal = 20.dp)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    text = "Enrollment Render",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                )
                                Text(
                                    text = "Configure and render an enrollment form",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Spacer(Modifier.height(4.dp))
                                YunoTextField(
                                    value = customerSession,
                                    onValueChange = onCustomerSessionChange,
                                    label = "Customer Session",
                                    isError = showErrors && customerSession.isBlank(),
                                )
                                YunoTextField(
                                    value = countryCode,
                                    onValueChange = { onCountryCodeChange(it.uppercase()) },
                                    label = "Country Code",
                                    isError = showErrors && countryCode.isBlank(),
                                )
                                YunoButton(text = "Start Enrollment", onClick = onStartEnrollment)
                                Spacer(Modifier.height(16.dp))
                            }
                        }

                        is EnrollmentRenderUiState.Loading -> {
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

                        is EnrollmentRenderUiState.StatusResult -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                StatusCard(
                                    status = state.status,
                                    label = "Enrollment",
                                    onRetry = if (state.status.uppercase() == "FAIL") onReset else null,
                                )
                            }
                        }

                        is EnrollmentRenderUiState.FragmentVisible -> {
                            // No overlay — SDK fragment is visible
                        }
                    }
                }
            }

            // Merchant submit button — pinned to bottom, shown when needsSubmit is true
            AnimatedVisibility(
                visible = uiState is EnrollmentRenderUiState.FragmentVisible &&
                    (uiState as? EnrollmentRenderUiState.FragmentVisible)?.needsSubmit == true,
            ) {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    YunoButton(text = "Submit Enrollment", onClick = onSubmit)
                }
            }
        }
}
