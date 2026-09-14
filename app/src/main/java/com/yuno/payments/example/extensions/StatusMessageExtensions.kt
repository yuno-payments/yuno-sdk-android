package com.yuno.payments.example.extensions

import com.yuno.payments.features.payment.models.StatusMessage

/**
 * Formats a [StatusMessage] for logging. The SDK (2.22.0+) attaches a StatusMessage to
 * FAIL / INTERNAL_ERROR outcomes so you can tell a backend rejection ([StatusMessage.SOURCE_BACKEND])
 * apart from an SDK failure ([StatusMessage.SOURCE_SDK]) and read its code and reason.
 * It is always null on success, drop-off and user cancellation.
 */
fun StatusMessage?.toLogString(): String =
    this?.let { "[${it.source}] code=${it.code} reason=${it.reason}" } ?: "none"
