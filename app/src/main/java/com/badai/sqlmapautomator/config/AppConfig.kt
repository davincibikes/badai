package com.badai.sqlmapautomator.config

/**
 * Application configuration constants
 * All configuration is embedded within the APK for security and portability
 */
object AppConfig {
    
    // Application Information
    const val APP_NAME = "BadAI"
    const val APP_VERSION = "1.0.0-alpha"
    const val APP_BUILD = 1
    
    // Database Configuration
    const val DATABASE_NAME = "badai_database"
    const val DATABASE_VERSION = 1
    
    // Network Configuration
    const val DEFAULT_TIMEOUT = 30000L // 30 seconds
    const val MAX_RETRIES = 3
    const val CONNECTION_POOL_SIZE = 10
    
    // SQLMap Configuration
    const val SQLMAP_DEFAULT_THREADS = 5
    const val SQLMAP_DEFAULT_DELAY = 1
    const val SQLMAP_MAX_TIMEOUT = 300 // 5 minutes
    const val SQLMAP_DEFAULT_RISK = 1
    const val SQLMAP_DEFAULT_LEVEL = 1
    
    // Proxy Configuration
    const val PROXY_VALIDATION_TIMEOUT = 10000L // 10 seconds
    const val MAX_PROXY_RETRIES = 3
    const val PROXY_ROTATION_INTERVAL = 300000L // 5 minutes
    
    // Dorking Configuration
    const val DORK_SEARCH_DELAY = 2000L // 2 seconds between searches
    const val MAX_DORK_RESULTS = 100
    const val DORK_TIMEOUT = 15000L // 15 seconds
    
    // AI Configuration
    const val AI_DEFAULT_MODEL = "ollama"
    const val AI_MAX_TOKENS = 2048
    const val AI_TEMPERATURE = 0.7f
    const val AI_TIMEOUT = 30000L // 30 seconds
    
    // Security Configuration
    const val ENCRYPTION_ALGORITHM = "AES/GCM/NoPadding"
    const val KEY_SIZE = 256
    const val IV_SIZE = 12
    const val TAG_SIZE = 16
    
    // File Paths (Internal Storage)
    const val SQLMAP_DIR = "sqlmap"
    const val RESULTS_DIR = "results"
    const val LOGS_DIR = "logs"
    const val TEMP_DIR = "temp"
    const val PROXIES_FILE = "proxies.txt"
    const val TARGETS_FILE = "targets.txt"
    
    // Legal and Compliance
    const val LEGAL_DISCLAIMER_VERSION = 1
    const val AUDIT_LOG_RETENTION_DAYS = 90
    const val MAX_AUDIT_LOG_SIZE = 10 * 1024 * 1024 // 10MB
    
    // Performance Configuration
    const val MAX_CONCURRENT_SCANS = 3
    const val SCAN_QUEUE_SIZE = 50
    const val RESULT_CACHE_SIZE = 100
    const val LOG_BUFFER_SIZE = 1024
    
    // UI Configuration
    const val ANIMATION_DURATION = 300L
    const val SPLASH_SCREEN_DURATION = 2000L
    const val TOAST_DURATION = 3000L
    
    // Termux Integration
    const val TERMUX_PACKAGE = "com.termux"
    const val TERMUX_API_PACKAGE = "com.termux.api"
    const val TERMUX_BOOT_PACKAGE = "com.termux.boot"
    
    // Default User Agents
    val USER_AGENTS = listOf(
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:89.0) Gecko/20100101 Firefox/89.0",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:89.0) Gecko/20100101 Firefox/89.0"
    )
    
    // Default Dorks
    val DEFAULT_DORKS = listOf(
        "inurl:\"admin.php\"",
        "inurl:\"login.php\"",
        "inurl:\"admin/login.php\"",
        "inurl:\"administrator/\"",
        "inurl:\"admin/\"",
        "inurl:\"wp-admin/\"",
        "inurl:\"phpmyadmin/\"",
        "inurl:\"mysql/\"",
        "inurl:\"database/\"",
        "inurl:\"db/\"",
        "filetype:sql",
        "filetype:db",
        "filetype:mdb",
        "inurl:\"config.php\"",
        "inurl:\"settings.php\"",
        "inurl:\"configuration.php\"",
        "intext:\"mysql_connect\"",
        "intext:\"mysqli_connect\"",
        "intext:\"PDO\"",
        "inurl:\"search.php?id=\"",
        "inurl:\"category.php?id=\"",
        "inurl:\"product.php?id=\"",
        "inurl:\"news.php?id=\"",
        "inurl:\"article.php?id=\""
    )
    
    // API Endpoints for External Services
    object ApiEndpoints {
        const val GOOGLE_SEARCH = "https://www.google.com/search"
        const val BING_SEARCH = "https://www.bing.com/search"
        const val DUCKDUCKGO_SEARCH = "https://duckduckgo.com/"
        const val SHODAN_SEARCH = "https://api.shodan.io/shodan/host/search"
        
        // Proxy Sources
        const val FREE_PROXY_LIST = "https://www.proxy-list.download/api/v1/get"
        const val PROXY_NOVA = "https://www.proxynova.com/proxy-server-list/"
        const val HIDE_MY_NAME = "https://hidemy.name/en/proxy-list/"
        
        // AI Model Endpoints
        const val OLLAMA_DEFAULT = "http://localhost:11434"
        const val GROQ_API = "https://api.groq.com/openai/v1"
        const val OPENAI_API = "https://api.openai.com/v1"
        const val HUGGINGFACE_API = "https://api-inference.huggingface.co/models"
    }
    
    // Default AI Models
    object AIModels {
        const val OLLAMA_DEFAULT = "llama2"
        const val GROQ_DEFAULT = "mixtral-8x7b-32768"
        const val OPENAI_DEFAULT = "gpt-3.5-turbo"
        const val HUGGINGFACE_DEFAULT = "microsoft/DialoGPT-medium"
    }
    
    // Vulnerability Patterns
    val SQL_INJECTION_PATTERNS = listOf(
        "error in your SQL syntax",
        "mysql_fetch_array()",
        "ORA-01756",
        "Microsoft OLE DB Provider for ODBC Drivers",
        "PostgreSQL query failed",
        "Warning: mysql_",
        "MySQLSyntaxErrorException",
        "valid MySQL result",
        "check the manual that corresponds to your MySQL server version",
        "Unknown column",
        "where clause",
        "MySqlException",
        "SqlException"
    )
    
    // Sensitive Data Patterns
    val CREDIT_CARD_PATTERNS = listOf(
        "\\b4[0-9]{12}(?:[0-9]{3})?\\b", // Visa
        "\\b5[1-5][0-9]{14}\\b", // MasterCard
        "\\b3[47][0-9]{13}\\b", // American Express
        "\\b3[0-9]{4}[0-9]{6}[0-9]{5}\\b", // Diners Club
        "\\b6(?:011|5[0-9]{2})[0-9]{12}\\b" // Discover
    )
    
    val SSN_PATTERNS = listOf(
        "\\b[0-9]{3}-[0-9]{2}-[0-9]{4}\\b",
        "\\b[0-9]{9}\\b"
    )
    
    val EMAIL_PATTERNS = listOf(
        "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b"
    )
    
    // Cryptocurrency Patterns
    val CRYPTO_PATTERNS = mapOf(
        "Bitcoin" to "\\b[13][a-km-zA-HJ-NP-Z1-9]{25,34}\\b",
        "Ethereum" to "\\b0x[a-fA-F0-9]{40}\\b",
        "Litecoin" to "\\b[LM3][a-km-zA-HJ-NP-Z1-9]{26,33}\\b",
        "Dogecoin" to "\\bD{1}[5-9A-HJ-NP-U]{1}[1-9A-HJ-NP-Za-km-z]{32}\\b"
    )
    
    // Legal Compliance Settings
    object Compliance {
        const val GDPR_ENABLED = true
        const val CCPA_ENABLED = true
        const val AUDIT_LOGGING = true
        const val DATA_RETENTION_DAYS = 30
        const val ANONYMIZE_LOGS = true
        const val REQUIRE_CONSENT = true
    }
    
    // Feature Flags
    object Features {
        const val AI_ASSISTANT_ENABLED = true
        const val AUTOMATED_MODE_ENABLED = true
        const val CRYPTO_ATTACKS_ENABLED = true
        const val TERMUX_INTEGRATION_ENABLED = true
        const val PROXY_SCRAPING_ENABLED = true
        const val DORKING_ENABLED = true
        const val CHAIN_EXPLOITATION_ENABLED = true
        const val VOICE_RECOGNITION_ENABLED = false // Coming soon
        const val CLOUD_SYNC_ENABLED = false // Coming soon
    }
    
    // Build Configuration (embedded instead of external files)
    object Build {
        const val COMPILE_SDK = 34
        const val MIN_SDK = 24
        const val TARGET_SDK = 34
        const val VERSION_CODE = 1
        const val VERSION_NAME = "1.0.0-alpha"
        const val APPLICATION_ID = "com.badai.sqlmapautomator"
        
        // Gradle configuration
        const val GRADLE_VERSION = "8.0"
        const val KOTLIN_VERSION = "1.8.10"
        const val AGP_VERSION = "8.0.0"
        
        // JVM configuration
        const val JAVA_VERSION = "17"
        const val JVM_TARGET = "17"
        
        // Build types
        const val DEBUG_ENABLED = true
        const val MINIFY_ENABLED = false
        const val PROGUARD_ENABLED = false
    }
    
    // SDK Paths (for internal use)
    object Paths {
        const val INTERNAL_STORAGE = "/data/data/com.badai.sqlmapautomator/files"
        const val CACHE_DIR = "/data/data/com.badai.sqlmapautomator/cache"
        const val EXTERNAL_STORAGE = "/storage/emulated/0/BadAI"
        
        // Tool paths
        const val TOOLS_DIR = "tools"
        const val BIN_DIR = "bin"
        const val LIB_DIR = "lib"
        const val SCRIPTS_DIR = "scripts"
    }
}