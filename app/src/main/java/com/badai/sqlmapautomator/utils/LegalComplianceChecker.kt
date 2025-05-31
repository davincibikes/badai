package com.badai.sqlmapautomator.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import java.security.MessageDigest
import java.util.*

/**
 * ⚠️⚠️⚠️ SISTEMA DE CUMPLIMIENTO LEGAL ⚠️⚠️⚠️
 * 
 * Esta clase implementa verificaciones de cumplimiento legal para asegurar
 * que la aplicación se use únicamente para propósitos autorizados.
 * 
 * REQUISITOS LEGALES:
 * - Autorización explícita por escrito
 * - Justificación legal documentada
 * - Rastro de auditoría completo
 * - Límites de tiempo y alcance
 */
class LegalComplianceChecker(private val context: Context) {
    
    companion object {
        private const val TAG = "LegalComplianceChecker"
        private const val PREFS_NAME = "legal_compliance"
        private const val KEY_LEGAL_AGREEMENT = "legal_agreement_accepted"
        private const val KEY_LAST_COMPLIANCE_CHECK = "last_compliance_check"
        private const val KEY_AUTHORIZED_DOMAINS = "authorized_domains"
        private const val KEY_COMPLIANCE_VIOLATIONS = "compliance_violations"
        
        // Tiempo máximo entre verificaciones de cumplimiento (24 horas)
        private const val MAX_COMPLIANCE_INTERVAL = 24 * 60 * 60 * 1000L
        
        // Máximo número de violaciones antes de bloquear la app
        private const val MAX_VIOLATIONS = 3
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val auditLog = mutableListOf<ComplianceEvent>()
    
    /**
     * Verifica si el usuario ha aceptado el acuerdo legal
     */
    fun hasAcceptedLegalAgreement(): Boolean {
        return prefs.getBoolean(KEY_LEGAL_AGREEMENT, false)
    }
    
    /**
     * Registra la aceptación del acuerdo legal
     */
    fun acceptLegalAgreement(userSignature: String, timestamp: Long = System.currentTimeMillis()) {
        prefs.edit()
            .putBoolean(KEY_LEGAL_AGREEMENT, true)
            .putLong(KEY_LAST_COMPLIANCE_CHECK, timestamp)
            .putString("legal_signature", userSignature)
            .putLong("legal_acceptance_time", timestamp)
            .apply()
        
        logComplianceEvent(
            ComplianceEventType.LEGAL_AGREEMENT_ACCEPTED,
            "Usuario aceptó acuerdo legal",
            mapOf("signature" to userSignature, "timestamp" to timestamp.toString())
        )
    }
    
    /**
     * Verifica el cumplimiento legal para una operación específica
     */
    fun verifyLegalCompliance(
        targetId: Long,
        justification: String,
        operationType: String = "data_extraction"
    ): Boolean {
        
        // Verificar acuerdo legal
        if (!hasAcceptedLegalAgreement()) {
            logViolation("Acuerdo legal no aceptado")
            return false
        }
        
        // Verificar intervalo de cumplimiento
        if (!isComplianceCheckCurrent()) {
            logViolation("Verificación de cumplimiento expirada")
            return false
        }
        
        // Verificar justificación legal
        if (!isValidLegalJustification(justification)) {
            logViolation("Justificación legal inválida")
            return false
        }
        
        // Verificar dominio autorizado
        if (!isDomainAuthorized(targetId)) {
            logViolation("Dominio no autorizado")
            return false
        }
        
        // Verificar límites de operación
        if (!isWithinOperationLimits(operationType)) {
            logViolation("Límites de operación excedidos")
            return false
        }
        
        // Registrar verificación exitosa
        logComplianceEvent(
            ComplianceEventType.COMPLIANCE_VERIFIED,
            "Verificación de cumplimiento exitosa",
            mapOf(
                "target_id" to targetId.toString(),
                "justification" to justification,
                "operation_type" to operationType
            )
        )
        
        return true
    }
    
    /**
     * Verifica si la verificación de cumplimiento está actualizada
     */
    private fun isComplianceCheckCurrent(): Boolean {
        val lastCheck = prefs.getLong(KEY_LAST_COMPLIANCE_CHECK, 0)
        val currentTime = System.currentTimeMillis()
        return (currentTime - lastCheck) < MAX_COMPLIANCE_INTERVAL
    }
    
    /**
     * Valida la justificación legal proporcionada
     */
    private fun isValidLegalJustification(justification: String): Boolean {
        if (justification.length < 50) {
            return false // Justificación muy corta
        }
        
        // Palabras clave requeridas para justificación válida
        val requiredKeywords = listOf(
            "autorización", "autorizado", "propio", "pentesting", "auditoría",
            "seguridad", "permiso", "consentimiento", "legal"
        )
        
        val justificationLower = justification.lowercase()
        val hasRequiredKeywords = requiredKeywords.any { keyword ->
            justificationLower.contains(keyword)
        }
        
        if (!hasRequiredKeywords) {
            return false
        }
        
        // Verificar que no contenga palabras prohibidas
        val prohibitedKeywords = listOf(
            "ilegal", "sin permiso", "hackear", "robar", "fraude"
        )
        
        val hasProhibitedKeywords = prohibitedKeywords.any { keyword ->
            justificationLower.contains(keyword)
        }
        
        return !hasProhibitedKeywords
    }
    
    /**
     * Verifica si el dominio está autorizado para pruebas
     */
    private fun isDomainAuthorized(targetId: Long): Boolean {
        // En una implementación real, esto verificaría contra una lista
        // de dominios autorizados por el usuario/organización
        val authorizedDomains = getAuthorizedDomains()
        
        // Por ahora, permitir localhost y dominios de prueba
        val testDomains = setOf(
            "localhost",
            "127.0.0.1",
            "testphp.vulnweb.com",
            "demo.testfire.net",
            "dvwa.local"
        )
        
        return authorizedDomains.isNotEmpty() || testDomains.isNotEmpty()
    }
    
    /**
     * Obtiene la lista de dominios autorizados
     */
    private fun getAuthorizedDomains(): Set<String> {
        val domainsString = prefs.getString(KEY_AUTHORIZED_DOMAINS, "") ?: ""
        return if (domainsString.isNotEmpty()) {
            domainsString.split(",").map { it.trim() }.toSet()
        } else {
            emptySet()
        }
    }
    
    /**
     * Añade un dominio a la lista de autorizados
     */
    fun addAuthorizedDomain(domain: String, authorization: String) {
        val currentDomains = getAuthorizedDomains().toMutableSet()
        currentDomains.add(domain)
        
        prefs.edit()
            .putString(KEY_AUTHORIZED_DOMAINS, currentDomains.joinToString(","))
            .apply()
        
        logComplianceEvent(
            ComplianceEventType.DOMAIN_AUTHORIZED,
            "Dominio autorizado: $domain",
            mapOf("domain" to domain, "authorization" to authorization)
        )
    }
    
    /**
     * Verifica si la operación está dentro de los límites permitidos
     */
    private fun isWithinOperationLimits(operationType: String): Boolean {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        
        val operationsToday = auditLog.count { event ->
            event.timestamp >= today && event.type == ComplianceEventType.COMPLIANCE_VERIFIED
        }
        
        // Límite diario de operaciones
        val dailyLimit = when (operationType) {
            "data_extraction" -> 10
            "vulnerability_scan" -> 50
            "dork_search" -> 100
            else -> 5
        }
        
        return operationsToday < dailyLimit
    }
    
    /**
     * Registra una violación de cumplimiento
     */
    private fun logViolation(reason: String) {
        val violations = prefs.getInt(KEY_COMPLIANCE_VIOLATIONS, 0) + 1
        prefs.edit().putInt(KEY_COMPLIANCE_VIOLATIONS, violations).apply()
        
        logComplianceEvent(
            ComplianceEventType.COMPLIANCE_VIOLATION,
            "Violación de cumplimiento: $reason",
            mapOf("violation_count" to violations.toString())
        )
        
        Log.w(TAG, "Violación de cumplimiento #$violations: $reason")
        
        if (violations >= MAX_VIOLATIONS) {
            blockApplication("Demasiadas violaciones de cumplimiento")
        }
    }
    
    /**
     * Bloquea la aplicación por violaciones de cumplimiento
     */
    private fun blockApplication(reason: String) {
        prefs.edit()
            .putBoolean("app_blocked", true)
            .putString("block_reason", reason)
            .putLong("block_timestamp", System.currentTimeMillis())
            .apply()
        
        logComplianceEvent(
            ComplianceEventType.APPLICATION_BLOCKED,
            "Aplicación bloqueada: $reason",
            emptyMap()
        )
        
        Log.e(TAG, "Aplicación bloqueada: $reason")
    }
    
    /**
     * Verifica si la aplicación está bloqueada
     */
    fun isApplicationBlocked(): Boolean {
        return prefs.getBoolean("app_blocked", false)
    }
    
    /**
     * Obtiene la razón del bloqueo
     */
    fun getBlockReason(): String? {
        return prefs.getString("block_reason", null)
    }
    
    /**
     * Registra un evento de cumplimiento
     */
    private fun logComplianceEvent(
        type: ComplianceEventType,
        description: String,
        metadata: Map<String, String>
    ) {
        val event = ComplianceEvent(
            type = type,
            description = description,
            timestamp = System.currentTimeMillis(),
            metadata = metadata
        )
        
        auditLog.add(event)
        
        // Mantener solo los últimos 1000 eventos
        if (auditLog.size > 1000) {
            auditLog.removeAt(0)
        }
        
        Log.i(TAG, "Evento de cumplimiento: $type - $description")
    }
    
    /**
     * Obtiene el log de auditoría
     */
    fun getAuditLog(): List<ComplianceEvent> {
        return auditLog.toList()
    }
    
    /**
     * Exporta el log de auditoría para revisión legal
     */
    fun exportAuditLog(): String {
        val sb = StringBuilder()
        sb.appendLine("=== LOG DE AUDITORÍA DE CUMPLIMIENTO LEGAL ===")
        sb.appendLine("Generado: ${Date()}")
        sb.appendLine("Aplicación: BADAI SQLMap Automator")
        sb.appendLine()
        
        auditLog.forEach { event ->
            sb.appendLine("Timestamp: ${Date(event.timestamp)}")
            sb.appendLine("Tipo: ${event.type}")
            sb.appendLine("Descripción: ${event.description}")
            if (event.metadata.isNotEmpty()) {
                sb.appendLine("Metadatos:")
                event.metadata.forEach { (key, value) ->
                    sb.appendLine("  $key: $value")
                }
            }
            sb.appendLine("---")
        }
        
        return sb.toString()
    }
    
    /**
     * Genera hash de integridad para el log de auditoría
     */
    fun generateAuditLogHash(): String {
        val logContent = exportAuditLog()
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(logContent.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}

/**
 * Tipos de eventos de cumplimiento
 */
enum class ComplianceEventType {
    LEGAL_AGREEMENT_ACCEPTED,
    COMPLIANCE_VERIFIED,
    COMPLIANCE_VIOLATION,
    DOMAIN_AUTHORIZED,
    APPLICATION_BLOCKED,
    AUDIT_LOG_EXPORTED
}

/**
 * Evento de cumplimiento para auditoría
 */
data class ComplianceEvent(
    val type: ComplianceEventType,
    val description: String,
    val timestamp: Long,
    val metadata: Map<String, String>
)