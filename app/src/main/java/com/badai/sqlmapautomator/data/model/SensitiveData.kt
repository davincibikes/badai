package com.badai.sqlmapautomator.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * ⚠️⚠️⚠️ ADVERTENCIA CRÍTICA ⚠️⚠️⚠️
 * 
 * Este módulo está diseñado EXCLUSIVAMENTE para:
 * - Auditorías de seguridad autorizadas en sistemas propios
 * - Pruebas de penetración con autorización explícita por escrito
 * - Fines educativos en entornos controlados
 * 
 * LA EXTRACCIÓN NO AUTORIZADA DE DATOS SENSIBLES ES UN DELITO GRAVE
 * QUE PUEDE RESULTAR EN PRISIÓN Y MULTAS SEVERAS.
 * 
 * EL USUARIO ES COMPLETAMENTE RESPONSABLE DEL USO DE ESTA FUNCIONALIDAD.
 */

@Entity(tableName = "sensitive_data")
data class SensitiveData(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val scanResultId: Long,
    val targetUrl: String,
    val dataType: SensitiveDataType,
    val tableName: String,
    val columnName: String,
    val rawData: String, // Datos encriptados localmente
    val maskedData: String, // Datos enmascarados para mostrar
    val confidence: Float, // 0.0 - 1.0
    val pattern: String, // Patrón regex usado para detectar
    val context: String? = null, // Contexto adicional
    val severity: DataSeverity,
    val extractedAt: Date = Date(),
    val verified: Boolean = false,
    val exported: Boolean = false,
    val notes: String? = null
)

enum class SensitiveDataType {
    CREDIT_CARD,
    DEBIT_CARD,
    BANK_ACCOUNT,
    SSN, // Social Security Number
    PASSPORT,
    DRIVER_LICENSE,
    NATIONAL_ID,
    EMAIL,
    PHONE,
    ADDRESS,
    PASSWORD_HASH,
    API_KEY,
    TOKEN,
    PRIVATE_KEY,
    CERTIFICATE,
    MEDICAL_RECORD,
    FINANCIAL_RECORD,
    PERSONAL_DOCUMENT,
    BIOMETRIC_DATA,
    CUSTOM
}

enum class DataSeverity {
    LOW,      // Información pública o poco sensible
    MEDIUM,   // Información personal básica
    HIGH,     // Información financiera o médica
    CRITICAL  // Información altamente confidencial
}

@Entity(tableName = "data_patterns")
data class DataPattern(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val dataType: SensitiveDataType,
    val regex: String,
    val description: String,
    val severity: DataSeverity,
    val isActive: Boolean = true,
    val falsePositiveRate: Float = 0f,
    val country: String? = null, // Para patrones específicos de país
    val examples: List<String> = emptyList()
)

// Patrones predefinidos para detectar datos sensibles
object SensitiveDataPatterns {
    
    val CREDIT_CARD_PATTERNS = listOf(
        DataPattern(
            name = "Visa",
            dataType = SensitiveDataType.CREDIT_CARD,
            regex = "4[0-9]{12}(?:[0-9]{3})?",
            description = "Números de tarjeta Visa",
            severity = DataSeverity.CRITICAL
        ),
        DataPattern(
            name = "MasterCard",
            dataType = SensitiveDataType.CREDIT_CARD,
            regex = "5[1-5][0-9]{14}",
            description = "Números de tarjeta MasterCard",
            severity = DataSeverity.CRITICAL
        ),
        DataPattern(
            name = "American Express",
            dataType = SensitiveDataType.CREDIT_CARD,
            regex = "3[47][0-9]{13}",
            description = "Números de tarjeta American Express",
            severity = DataSeverity.CRITICAL
        ),
        DataPattern(
            name = "Discover",
            dataType = SensitiveDataType.CREDIT_CARD,
            regex = "6(?:011|5[0-9]{2})[0-9]{12}",
            description = "Números de tarjeta Discover",
            severity = DataSeverity.CRITICAL
        )
    )
    
    val BANK_PATTERNS = listOf(
        DataPattern(
            name = "IBAN",
            dataType = SensitiveDataType.BANK_ACCOUNT,
            regex = "[A-Z]{2}[0-9]{2}[A-Z0-9]{4}[0-9]{7}([A-Z0-9]?){0,16}",
            description = "Números IBAN internacionales",
            severity = DataSeverity.CRITICAL
        ),
        DataPattern(
            name = "US Bank Account",
            dataType = SensitiveDataType.BANK_ACCOUNT,
            regex = "[0-9]{8,17}",
            description = "Números de cuenta bancaria US",
            severity = DataSeverity.CRITICAL
        ),
        DataPattern(
            name = "US Routing Number",
            dataType = SensitiveDataType.BANK_ACCOUNT,
            regex = "[0-9]{9}",
            description = "Números de routing bancario US",
            severity = DataSeverity.HIGH
        )
    )
    
    val IDENTITY_PATTERNS = listOf(
        DataPattern(
            name = "US SSN",
            dataType = SensitiveDataType.SSN,
            regex = "(?!000|666|9\\d{2})\\d{3}-?(?!00)\\d{2}-?(?!0000)\\d{4}",
            description = "Números de Seguro Social US",
            severity = DataSeverity.CRITICAL,
            country = "US"
        ),
        DataPattern(
            name = "UK National Insurance",
            dataType = SensitiveDataType.NATIONAL_ID,
            regex = "[A-CEGHJ-PR-TW-Z]{1}[A-CEGHJ-NPR-TW-Z]{1}[0-9]{6}[A-D]{1}",
            description = "Números de National Insurance UK",
            severity = DataSeverity.CRITICAL,
            country = "UK"
        ),
        DataPattern(
            name = "Spanish DNI",
            dataType = SensitiveDataType.NATIONAL_ID,
            regex = "[0-9]{8}[TRWAGMYFPDXBNJZSQVHLCKE]",
            description = "DNI español",
            severity = DataSeverity.CRITICAL,
            country = "ES"
        ),
        DataPattern(
            name = "Mexican CURP",
            dataType = SensitiveDataType.NATIONAL_ID,
            regex = "[A-Z]{4}[0-9]{6}[HM][A-Z]{5}[0-9A-Z][0-9]",
            description = "CURP mexicano",
            severity = DataSeverity.CRITICAL,
            country = "MX"
        )
    )
    
    val CONTACT_PATTERNS = listOf(
        DataPattern(
            name = "Email",
            dataType = SensitiveDataType.EMAIL,
            regex = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}",
            description = "Direcciones de email",
            severity = DataSeverity.MEDIUM
        ),
        DataPattern(
            name = "US Phone",
            dataType = SensitiveDataType.PHONE,
            regex = "\\(?([0-9]{3})\\)?[-. ]?([0-9]{3})[-. ]?([0-9]{4})",
            description = "Números de teléfono US",
            severity = DataSeverity.MEDIUM,
            country = "US"
        ),
        DataPattern(
            name = "International Phone",
            dataType = SensitiveDataType.PHONE,
            regex = "\\+[1-9]\\d{1,14}",
            description = "Números de teléfono internacionales",
            severity = DataSeverity.MEDIUM
        )
    )
    
    val SECURITY_PATTERNS = listOf(
        DataPattern(
            name = "MD5 Hash",
            dataType = SensitiveDataType.PASSWORD_HASH,
            regex = "[a-fA-F0-9]{32}",
            description = "Hashes MD5",
            severity = DataSeverity.HIGH
        ),
        DataPattern(
            name = "SHA1 Hash",
            dataType = SensitiveDataType.PASSWORD_HASH,
            regex = "[a-fA-F0-9]{40}",
            description = "Hashes SHA1",
            severity = DataSeverity.HIGH
        ),
        DataPattern(
            name = "SHA256 Hash",
            dataType = SensitiveDataType.PASSWORD_HASH,
            regex = "[a-fA-F0-9]{64}",
            description = "Hashes SHA256",
            severity = DataSeverity.HIGH
        ),
        DataPattern(
            name = "API Key",
            dataType = SensitiveDataType.API_KEY,
            regex = "[a-zA-Z0-9]{32,}",
            description = "Claves API genéricas",
            severity = DataSeverity.HIGH
        ),
        DataPattern(
            name = "JWT Token",
            dataType = SensitiveDataType.TOKEN,
            regex = "eyJ[a-zA-Z0-9_-]*\\.[a-zA-Z0-9_-]*\\.[a-zA-Z0-9_-]*",
            description = "Tokens JWT",
            severity = DataSeverity.HIGH
        ),
        DataPattern(
            name = "Private Key",
            dataType = SensitiveDataType.PRIVATE_KEY,
            regex = "-----BEGIN [A-Z ]+PRIVATE KEY-----[\\s\\S]*?-----END [A-Z ]+PRIVATE KEY-----",
            description = "Claves privadas",
            severity = DataSeverity.CRITICAL
        )
    )
    
    val ALL_PATTERNS = CREDIT_CARD_PATTERNS + BANK_PATTERNS + IDENTITY_PATTERNS + 
                      CONTACT_PATTERNS + SECURITY_PATTERNS
}

@Entity(tableName = "extraction_logs")
data class ExtractionLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionId: String,
    val targetUrl: String,
    val dataType: SensitiveDataType,
    val recordsFound: Int,
    val recordsExtracted: Int,
    val extractionTime: Date = Date(),
    val userConsent: Boolean, // Usuario debe confirmar extracción
    val legalJustification: String, // Justificación legal requerida
    val auditTrail: String // Rastro de auditoría completo
)