package com.badai.sqlmapautomator.data.models

data class ScanConfiguration(
    val techniques: List<String> = listOf("B", "E", "U", "S", "T"),
    val level: Int = 1,
    val risk: Int = 1,
    val threads: Int = 1,
    val timeout: Int = 30,
    val retries: Int = 3,
    val dumpAll: Boolean = false,
    val dumpFormat: String = "CSV",
    val userAgent: String? = null,
    val proxy: String? = null,
    val headers: Map<String, String> = emptyMap(),
    val cookies: String? = null,
    val data: String? = null,
    val method: String = "GET",
    val tamper: List<String> = emptyList(),
    val dbms: String? = null,
    val os: String? = null,
    val skipWaf: Boolean = false,
    val randomAgent: Boolean = true,
    val verbose: Int = 1
)