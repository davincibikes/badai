package com.badai.sqlmapautomator.data.models

data class AutomatedScanConfig(
    val scanConfiguration: ScanConfiguration = ScanConfiguration(),
    val enableDorking: Boolean = true,
    val enableProxyRotation: Boolean = true,
    val enableSensitiveDataExtraction: Boolean = true,
    val enableFullDatabaseDump: Boolean = false,
    val maxTargets: Int = 100,
    val maxConcurrentScans: Int = 5,
    val enableLegalCompliance: Boolean = true,
    val targetDomains: List<String> = emptyList(),
    val excludeDomains: List<String> = emptyList(),
    val customDorks: List<String> = emptyList(),
    val enableChainExploitation: Boolean = true,
    val enableCryptoExploitation: Boolean = true,
    val enableAPIExploitation: Boolean = true,
    val enableSubdomainExploitation: Boolean = true,
    val enableAlternativeDBExtraction: Boolean = true
)