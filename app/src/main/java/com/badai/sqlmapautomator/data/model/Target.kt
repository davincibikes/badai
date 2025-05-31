package com.badai.sqlmapautomator.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "targets")
data class Target(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val url: String,
    val method: String = "GET",
    val data: String? = null,
    val cookies: String? = null,
    val headers: String? = null,
    val userAgent: String? = null,
    val proxy: String? = null,
    val source: String, // "manual", "dork", "crawler"
    val dorkQuery: String? = null,
    val searchEngine: String? = null,
    val country: String? = null,
    val technology: String? = null,
    val status: TargetStatus = TargetStatus.PENDING,
    val vulnerability: VulnerabilityType? = null,
    val confidence: Int = 0,
    val risk: Int = 0,
    val createdAt: Date = Date(),
    val scannedAt: Date? = null,
    val notes: String? = null
)

enum class TargetStatus {
    PENDING,
    SCANNING,
    VULNERABLE,
    NOT_VULNERABLE,
    ERROR,
    SKIPPED
}

enum class VulnerabilityType {
    BOOLEAN_BASED,
    TIME_BASED,
    ERROR_BASED,
    UNION_BASED,
    STACKED_QUERIES,
    INLINE_QUERIES
}