package com.yuno.payments.example.features.enrollment.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yuno.payments.example.ui.components.YunoButton
import com.yuno.payments.example.ui.components.YunoTextField

@Composable
fun EnrollmentLiteScreen(
    initialCustomerSession: String,
    initialCountryCode: String,
    onStartEnrollment: (customerSession: String, countryCode: String) -> Unit,
) {
    var customerSession by rememberSaveable { mutableStateOf(initialCustomerSession) }
    var countryCode by rememberSaveable { mutableStateOf(initialCountryCode) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Enrollment Lite",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = "Add a new payment method",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))
        YunoTextField(
            value = customerSession,
            onValueChange = { customerSession = it },
            label = "Customer Session",
        )
        YunoTextField(
            value = countryCode,
            onValueChange = { countryCode = it },
            label = "Country Code",
        )
        YunoButton(
            text = "Add Payment Method",
            onClick = { onStartEnrollment(customerSession, countryCode.uppercase()) },
            enabled = customerSession.isNotBlank() && countryCode.isNotBlank(),
        )
    }
}
