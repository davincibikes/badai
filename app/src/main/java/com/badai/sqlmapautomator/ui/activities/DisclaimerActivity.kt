package com.badai.sqlmapautomator.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.badai.sqlmapautomator.R
import com.badai.sqlmapautomator.utils.LegalComplianceChecker

class DisclaimerActivity : AppCompatActivity() {
    
    private lateinit var complianceChecker: LegalComplianceChecker
    private lateinit var acceptCheckbox: CheckBox
    private lateinit var acceptButton: Button
    private lateinit var declineButton: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disclaimer)
        
        complianceChecker = LegalComplianceChecker(this)
        
        initViews()
        setupClickListeners()
    }
    
    private fun initViews() {
        acceptCheckbox = findViewById(R.id.checkbox_accept_terms)
        acceptButton = findViewById(R.id.button_accept)
        declineButton = findViewById(R.id.button_decline)
        
        val disclaimerText = findViewById<TextView>(R.id.text_disclaimer)
        disclaimerText.text = """
            ⚠️⚠️⚠️ ADVERTENCIA LEGAL CRÍTICA ⚠️⚠️⚠️
            
            BADAI SQLMap Automator es una herramienta de auditoría de seguridad diseñada EXCLUSIVAMENTE para:
            
            ✅ Pruebas de penetración autorizadas
            ✅ Auditorías de seguridad en sistemas propios
            ✅ Investigación académica en entornos controlados
            ✅ Fines educativos con autorización explícita
            
            ❌ EL USO NO AUTORIZADO ES ILEGAL ❌
            
            RESPONSABILIDADES LEGALES:
            
            • El usuario es COMPLETAMENTE RESPONSABLE del uso de esta herramienta
            • Debe obtener autorización EXPLÍCITA Y POR ESCRITO antes de usar esta herramienta
            • El uso sin autorización puede resultar en PRISIÓN y MULTAS SEVERAS
            • Esta herramienta cumple con GDPR, CCPA, PCI DSS y otras regulaciones
            • Todas las actividades son registradas para auditoría legal
            
            LEYES APLICABLES:
            
            • Ley de Fraude y Abuso Informático (CFAA) - EE.UU.
            • Ley de Delitos Informáticos - España
            • Reglamento General de Protección de Datos (GDPR) - UE
            • Ley de Privacidad del Consumidor de California (CCPA)
            • Estándar de Seguridad de Datos PCI DSS
            
            ADVERTENCIAS ESPECÍFICAS:
            
            🔴 La extracción de datos sensibles sin autorización es un DELITO GRAVE
            🔴 El acceso no autorizado a sistemas puede resultar en hasta 20 AÑOS DE PRISIÓN
            🔴 Las multas pueden alcanzar MILLONES de dólares
            🔴 Los antecedentes penales pueden arruinar su carrera profesional
            
            CONSENTIMIENTO INFORMADO:
            
            Al aceptar estos términos, usted declara que:
            
            1. Ha leído y comprende completamente estas advertencias legales
            2. Utilizará esta herramienta únicamente para propósitos legales y autorizados
            3. Obtendrá autorización explícita antes de cualquier prueba
            4. Acepta la responsabilidad legal completa por su uso
            5. Comprende las consecuencias legales del uso indebido
            6. No utilizará esta herramienta para actividades maliciosas
            7. Cumplirá con todas las leyes y regulaciones aplicables
            
            DESCARGO DE RESPONSABILIDAD:
            
            Los desarrolladores de BADAI SQLMap Automator:
            • NO se hacen responsables del uso indebido de esta herramienta
            • NO proporcionan asesoramiento legal
            • NO autorizan ningún uso específico
            • NO son responsables de consecuencias legales derivadas del uso
            
            Si no acepta estos términos completamente, debe SALIR INMEDIATAMENTE de esta aplicación.
            
            ¿Acepta estos términos y condiciones legales?
        """.trimIndent()
        
        // Inicialmente deshabilitar el botón de aceptar
        acceptButton.isEnabled = false
    }
    
    private fun setupClickListeners() {
        acceptCheckbox.setOnCheckedChangeListener { _, isChecked ->
            acceptButton.isEnabled = isChecked
        }
        
        acceptButton.setOnClickListener {
            if (acceptCheckbox.isChecked) {
                // Registrar aceptación legal
                val signature = "User accepted legal terms at ${System.currentTimeMillis()}"
                complianceChecker.acceptLegalAgreement(signature)
                
                // Ir a la actividad principal
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
        
        declineButton.setOnClickListener {
            // Usuario rechazó los términos, cerrar aplicación
            finish()
        }
    }
    
    override fun onBackPressed() {
        // Prevenir que el usuario evite la aceptación de términos
        finish()
    }
}