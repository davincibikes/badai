package com.badai.sqlmapautomator.data.model

/**
 * Simplified ScanResult model for APK compilation
 */
data class ScanResult(
    val id: Long = 0,
    val targetId: Long,
    val vulnerabilityType: VulnerabilityType,
    val riskLevel: RiskLevel,
    val description: String,
    val details: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: ScanStatus = ScanStatus.PENDING,
    val extractedData: List<String> = emptyList(),
    val recommendations: List<String> = emptyList(),
    val metadata: Map<String, String> = emptyMap()
)