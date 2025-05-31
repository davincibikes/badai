package com.badai.sqlmapautomator.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "targets")
data class Target(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val url: String,
    val status: TargetStatus = TargetStatus.PENDING,
    val vulnerability: VulnerabilityType? = null,
    val confidence: Int = 0,
    val risk: RiskLevel? = null,
    val notes: String? = null,
    val scannedAt: Date? = null,
    val createdAt: Date = Date()
)

enum class TargetStatus {
    PENDING,
    SCANNING,
    VULNERABLE,
    NOT_VULNERABLE,
    ERROR
}