package com.yuno.payments.example.ui.components

/**
 * Shared Compose components for the Yuno SDK demo app.
 *
 * These are thin wrappers around Material 3 components to keep the demo screens
 * concise and visually consistent. They are intentionally simple — in a real app
 * you would use your own design system components instead.
 */

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.yuno.payments.example.extensions.copyToClipboard
import com.yuno.payments.example.ui.theme.LocalYunoColors

/** Simple outlined text field styled for the demo. Shows "Required" hint when isError is true. */
@Composable
fun YunoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    enabled: Boolean = true,
    singleLine: Boolean = true,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        isError = isError,
        enabled = enabled,
        singleLine = singleLine,
        shape = MaterialTheme.shapes.medium,
        supportingText = if (isError) {
            { Text("Required") }
        } else {
            null
        },
    )
}

/** Primary action button (e.g., "Pay", "Start Payment"). */
@Composable
fun YunoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        shape = MaterialTheme.shapes.medium,
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

/** Secondary action button with tonal fill (e.g., "Continue", "Set Session"). */
@Composable
fun YunoTonalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        shape = MaterialTheme.shapes.medium,
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

/** Tertiary action button with outline (e.g., "Reset", "Try Again"). */
@Composable
fun YunoOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        shape = MaterialTheme.shapes.medium,
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

/** Displays a One-Time Token (OTT) with a copy button. Used after the SDK returns a token. */
@Composable
fun OttResultPanel(
    token: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "One Time Token",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            Text(
                text = token,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(modifier = Modifier.weight(1f))
                OutlinedButton(
                    onClick = { context.copyToClipboard("OTT", token) },
                    shape = MaterialTheme.shapes.small,
                ) {
                    Text("Copy", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

/** Section divider with title, used on the HomeScreen. */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        HorizontalDivider(
            modifier = Modifier.padding(top = 4.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
        )
    }
}

/** Displays a payment/enrollment status result with color-coded icon and optional retry button. */
@Composable
fun StatusCard(
    status: String,
    label: String,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
) {
    val extendedColors = LocalYunoColors.current
    val statusUpper = status.uppercase()

    val icon: ImageVector
    val containerColor: Color
    val contentColor: Color
    val statusText: String

    when (statusUpper) {
        "SUCCEEDED", "SUCCEED", "SUCCESS" -> {
            icon = Icons.Default.CheckCircle
            containerColor = extendedColors.successContainer
            contentColor = extendedColors.onSuccessContainer
            statusText = "$label Successful"
        }
        "FAIL" -> {
            icon = Icons.Default.Close
            containerColor = MaterialTheme.colorScheme.errorContainer
            contentColor = MaterialTheme.colorScheme.onErrorContainer
            statusText = "$label Failed"
        }
        "REJECT" -> {
            icon = Icons.Default.Close
            containerColor = MaterialTheme.colorScheme.errorContainer
            contentColor = MaterialTheme.colorScheme.onErrorContainer
            statusText = "$label Rejected"
        }
        "PROCESSING" -> {
            icon = Icons.Default.Refresh
            containerColor = extendedColors.warningContainer
            contentColor = extendedColors.onWarningContainer
            statusText = "$label Processing"
        }
        else -> {
            icon = Icons.Default.Info
            containerColor = MaterialTheme.colorScheme.surfaceVariant
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            statusText = "$label $status"
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = statusText,
                tint = contentColor,
                modifier = Modifier.size(48.dp),
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = statusText,
                style = MaterialTheme.typography.headlineSmall,
                color = contentColor,
            )
            if (onRetry != null) {
                Spacer(Modifier.height(16.dp))
                YunoOutlinedButton(
                    text = "Try Again",
                    onClick = onRetry,
                )
            }
        }
    }
}
