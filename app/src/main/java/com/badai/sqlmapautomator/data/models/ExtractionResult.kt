package com.badai.sqlmapautomator.data.models

data class ExtractionResult(
    val success: Boolean,
    val data: String,
    val vulnerabilities: List<VulnerabilityType> = emptyList(),
    val extractedFiles: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val errorMessage: String? = null
)