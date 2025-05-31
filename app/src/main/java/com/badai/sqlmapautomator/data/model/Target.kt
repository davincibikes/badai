package com.badai.sqlmapautomator.data.model

/**
 * Simplified Target model for APK compilation
 */
data class Target(
    val id: Long = 0,
    val url: String,
    val description: String = "",
    val vulnerabilityTypes: List<VulnerabilityType> = emptyList(),
    val riskLevel: RiskLevel = RiskLevel.LOW,
    val createdAt: Long = System.currentTimeMillis(),
    val lastScanned: Long? = null,
    val isActive: Boolean = true,
    val tags: List<String> = emptyList(),
    val metadata: Map<String, String> = emptyMap()
)