package com.badai.sqlmapautomator.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "scan_results")
data class ScanResult(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val targetId: Long,
    val targetUrl: String,
    val vulnerabilityFound: Boolean,
    val vulnerabilityType: VulnerabilityType? = null,
    val dumpedData: String? = null,
    val confidence: Int = 0,
    val risk: RiskLevel? = null,
    val timestamp: Date = Date(),
    val executionTime: Long = 0,
    val sqlmapOutput: String? = null
)