package com.badai.sqlmapautomator.data.model

/**
 * Search engines for dorking
 */
enum class SearchEngine {
    GOOGLE,
    BING,
    DUCKDUCKGO,
    SHODAN
}

/**
 * Types of vulnerabilities that can be detected
 */
enum class VulnerabilityType {
    SQL_INJECTION,
    XSS,
    CSRF,
    IDOR,
    RCE,
    LFI,
    RFI,
    SSTI,
    XXE,
    SSRF,
    INJECTION,
    BROKEN_AUTHENTICATION,
    SENSITIVE_DATA_EXPOSURE,
    BROKEN_ACCESS_CONTROL,
    SECURITY_MISCONFIGURATION,
    CRYPTO_FAILURE,
    JWT_BYPASS,
    FILE_UPLOAD,
    TIMING_ATTACK
}

/**
 * Types of sensitive data that can be extracted
 */
enum class SensitiveDataType {
    CREDIT_CARD,
    SSN,
    EMAIL,
    PHONE,
    PASSWORD,
    API_KEY,
    TOKEN,
    DATABASE_CREDENTIALS,
    BANKING_INFO,
    PERSONAL_ID
}

/**
 * Risk levels for vulnerabilities
 */
enum class RiskLevel {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

/**
 * Status of scans
 */
enum class ScanStatus {
    PENDING,
    RUNNING,
    COMPLETED,
    FAILED,
    CANCELLED
}

/**
 * Types of proxies
 */
enum class ProxyType {
    HTTP,
    HTTPS,
    SOCKS4,
    SOCKS5
}

/**
 * Types of compliance events
 */
enum class ComplianceEventType {
    SCAN_STARTED,
    VULNERABILITY_FOUND,
    DATA_EXTRACTED,
    SCAN_COMPLETED
}

/**
 * Types of exploits
 */
enum class ExploitType {
    AUTOMATED,
    MANUAL,
    CHAIN,
    CUSTOM
}

/**
 * Types of databases
 */
enum class DatabaseType {
    MYSQL,
    POSTGRESQL,
    MSSQL,
    ORACLE,
    SQLITE,
    MONGODB
}