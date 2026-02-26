package com.yuno.payments.example.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.yuno.payments.example.ui.components.SectionHeader

@Composable
fun HomeScreen(
    onEnrollmentLiteClick: () -> Unit,
    onEnrollmentRenderClick: () -> Unit,
    onCheckoutLiteClick: () -> Unit,
    onPaymentRenderClick: () -> Unit,
    onCheckoutCompleteClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
    ) {
        Text(
            text = "Yuno Payments",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = "SDK Integration Demo",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp),
        )

        Spacer(Modifier.height(32.dp))

        SectionHeader(title = "Payments")
        Spacer(Modifier.height(12.dp))
        FlowCard(
            icon = Icons.Default.ShoppingCart,
            title = "Payment Lite",
            description = "Start a lite payment flow",
            onClick = onCheckoutLiteClick,
        )
        Spacer(Modifier.height(8.dp))
        FlowCard(
            icon = Icons.Default.ShoppingCart,
            title = "Payment Complete",
            description = "Full checkout with payment selection",
            onClick = onCheckoutCompleteClick,
        )
        Spacer(Modifier.height(8.dp))
        FlowCard(
            icon = Icons.Default.ShoppingCart,
            title = "Payment Render",
            description = "Render a payment form directly",
            onClick = onPaymentRenderClick,
        )

        Spacer(Modifier.height(24.dp))

        SectionHeader(title = "Enrollment")
        Spacer(Modifier.height(12.dp))
        FlowCard(
            icon = Icons.Default.Add,
            title = "Add Payment Method",
            description = "Enroll a new payment method",
            onClick = onEnrollmentLiteClick,
        )
        Spacer(Modifier.height(8.dp))
        FlowCard(
            icon = Icons.Default.Person,
            title = "Enrollment Render",
            description = "Render the enrollment form directly",
            onClick = onEnrollmentRenderClick,
        )

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun FlowCard(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp),
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
