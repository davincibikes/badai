package com.badai.sqlmapautomator.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "scan_results")
data class ScanResult(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val targetId: Long,
    val sessionId: String,
    val status: ScanStatus,
    val startTime: Date,
    val endTime: Date? = null,
    val duration: Long = 0, // milliseconds
    val sqlmapCommand: String,
    val sqlmapOutput: String? = null,
    val vulnerabilityFound: Boolean = false,
    val vulnerabilityType: VulnerabilityType? = null,
    val injectionPoint: String? = null,
    val dbms: String? = null,
    val dbmsVersion: String? = null,
    val databases: List<String> = emptyList(),
    val tables: List<String> = emptyList(),
    val columns: List<String> = emptyList(),
    val dumpedData: String? = null,
    val errorMessage: String? = null,
    val confidence: Int = 0,
    val risk: Int = 0,
    val payloadsUsed: Int = 0,
    val requestsMade: Int = 0,
    val exported: Boolean = false,
    val exportPath: String? = null
)

enum class ScanStatus {
    PENDING,
    INITIALIZING,
    SCANNING,
    EXPLOITING,
    DUMPING,
    COMPLETED,
    FAILED,
    CANCELLED,
    TIMEOUT
}

@Entity(tableName = "scan_sessions")
data class ScanSession(
    @PrimaryKey
    val sessionId: String,
    val name: String,
    val description: String? = null,
    val targetCount: Int = 0,
    val completedCount: Int = 0,
    val vulnerableCount: Int = 0,
    val status: SessionStatus = SessionStatus.PENDING,
    val startTime: Date = Date(),
    val endTime: Date? = null,
    val autoMode: Boolean = true,
    val dorkQueries: List<String> = emptyList(),
    val searchEngines: List<SearchEngine> = emptyList(),
    val maxTargets: Int = 100,
    val scanConfig: ScanConfiguration
)

enum class SessionStatus {
    PENDING,
    DORKING,
    SCANNING,
    COMPLETED,
    FAILED,
    CANCELLED
}

data class ScanConfiguration(
    val level: Int = 1,
    val risk: Int = 1,
    val threads: Int = 1,
    val delay: Int = 0,
    val timeout: Int = 30,
    val retries: Int = 3,
    val techniques: List<String> = listOf("B", "E", "U", "S", "T", "Q"),
    val dbms: String? = null,
    val os: String? = null,
    val tamper: List<String> = emptyList(),
    val userAgent: String? = null,
    val proxy: String? = null,
    val randomAgent: Boolean = true,
    val skipWaf: Boolean = true,
    val dumpAll: Boolean = false,
    val dumpFormat: String = "CSV",
    val outputDir: String? = null,
    val verbose: Int = 1
)