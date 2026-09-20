package com.nexova.pos.core.dates

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateUtils {
    private val JAKARTA_ZONE = ZoneId.of("Asia/Jakarta")
    private val DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
        .withZone(JAKARTA_ZONE)
        .withLocale(Locale("id", "ID"))

    /**
     * Rule 108: Store UTC, display business timezone (Asia/Jakarta).
     */
    fun formatToDisplay(timestamp: Long): String {
        return DISPLAY_FORMATTER.format(Instant.ofEpochMilli(timestamp))
    }
}
