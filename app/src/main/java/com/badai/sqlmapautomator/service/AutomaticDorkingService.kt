package com.badai.sqlmapautomator.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.badai.sqlmapautomator.data.model.*
import com.badai.sqlmapautomator.dorker.AutoDorker
import com.badai.sqlmapautomator.dorker.DorkingResult
import com.badai.sqlmapautomator.utils.LegalComplianceChecker
import kotlinx.coroutines.*
import java.util.*

/**
 * ⚠️⚠️⚠️ ADVERTENCIA LEGAL ⚠️⚠️⚠️
 * 
 * Servicio de dorking automático para auditorías de seguridad autorizadas.
 * USO EXCLUSIVO PARA PRUEBAS DE PENETRACIÓN LEGALES Y AUTORIZADAS.
 */
class AutomaticDorkingService : Service() {
    
    companion object {
        private const val TAG = "AutoDorkingService"
        private const val DORKING_INTERVAL_MS = 300000L // 5 minutos
        private const val MAX_TARGETS_PER_SESSION = 500
    }
    
    private lateinit var autoDorker: AutoDorker
    private lateinit var complianceChecker: LegalComplianceChecker
    private var dorkingJob: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    override fun onCreate() {
        super.onCreate()
        
        complianceChecker = LegalComplianceChecker(this)
        autoDorker = AutoDorker(this, complianceChecker)
        
        Log.i(TAG, "Servicio de dorking automático iniciado")
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startAutomaticDorking()
        return START_STICKY // Reiniciar si el servicio es terminado
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onDestroy() {
        super.onDestroy()
        dorkingJob?.cancel()
        serviceScope.cancel()
        Log.i(TAG, "Servicio de dorking automático detenido")
    }
    
    /**
     * Inicia el proceso de dorking automático continuo
     */
    private fun startAutomaticDorking() {
        dorkingJob?.cancel()
        
        dorkingJob = serviceScope.launch {
            while (isActive) {
                try {
                    executeAutomaticDorkingCycle()
                    delay(DORKING_INTERVAL_MS)
                } catch (e: Exception) {
                    Log.e(TAG, "Error en ciclo de dorking automático", e)
                    delay(60000) // Esperar 1 minuto antes de reintentar
                }
            }
        }
    }
    
    /**
     * Ejecuta un ciclo completo de dorking automático
     */
    private suspend fun executeAutomaticDorkingCycle() {
        Log.i(TAG, "Iniciando ciclo de dorking automático")
        
        // Generar dorks automáticamente
        val automaticDorks = generateComprehensiveDorks()
        
        // Ejecutar búsquedas
        val dorkingResult = autoDorker.executeAutomaticDorking(
            dorkQueries = automaticDorks,
            maxTargetsPerDork = 50,
            countries = listOf("com", "org", "net", "edu", "gov"),
            excludeDomains = listOf(
                "github.com", "stackoverflow.com", "pastebin.com",
                "google.com", "microsoft.com", "amazon.com"
            )
        )
        
        when (dorkingResult) {
            is DorkingResult.Success -> {
                Log.i(TAG, "Dorking exitoso: ${dorkingResult.targets.size} objetivos encontrados")
                
                // Procesar objetivos encontrados
                processDiscoveredTargets(dorkingResult.targets)
                
                // Notificar resultados
                notifyDorkingResults(dorkingResult)
                
            }
            is DorkingResult.Failure -> {
                Log.w(TAG, "Dorking falló: ${dorkingResult.error}")
            }
        }
    }
    
    /**
     * Genera dorks comprehensivos para diferentes tipos de vulnerabilidades
     */
    private fun generateComprehensiveDorks(): List<DorkQuery> {
        val dorks = mutableListOf<DorkQuery>()
        
        // Dorks para SQL Injection
        dorks.addAll(generateSQLInjectionDorks())
        
        // Dorks para páginas de login
        dorks.addAll(generateLoginPageDorks())
        
        // Dorks para paneles de administración
        dorks.addAll(generateAdminPanelDorks())
        
        // Dorks para archivos sensibles
        dorks.addAll(generateSensitiveFileDorks())
        
        // Dorks para APIs expuestas
        dorks.addAll(generateAPIExposureDorks())
        
        // Dorks para bases de datos expuestas
        dorks.addAll(generateDatabaseExposureDorks())
        
        return dorks.shuffled().take(20) // Limitar y aleatorizar
    }
    
    /**
     * Genera dorks específicos para SQL Injection
     */
    private fun generateSQLInjectionDorks(): List<DorkQuery> {
        val technologies = listOf("php", "asp", "aspx", "jsp", "cfm")
        val parameters = listOf("id", "pid", "cid", "uid", "page", "cat", "item", "news", "article", "product")
        val dorks = mutableListOf<DorkQuery>()
        
        technologies.forEach { tech ->
            parameters.forEach { param ->
                dorks.add(
                    DorkQuery(
                        name = "SQLi - $tech - $param",
                        query = "inurl:\"$param=\" filetype:$tech",
                        category = DorkCategory.SQL_INJECTION,
                        searchEngine = SearchEngine.GOOGLE,
                        description = "Busca páginas $tech con parámetro $param vulnerable a SQLi",
                        riskLevel = 4
                    )
                )
            }
        }
        
        return dorks
    }
    
    /**
     * Genera dorks para páginas de login
     */
    private fun generateLoginPageDorks(): List<DorkQuery> {
        return listOf(
            DorkQuery(
                name = "Login Pages - General",
                query = "inurl:login OR inurl:signin OR inurl:admin",
                category = DorkCategory.LOGIN_PAGES,
                searchEngine = SearchEngine.GOOGLE,
                description = "Páginas de login generales",
                riskLevel = 2
            ),
            DorkQuery(
                name = "Admin Login Pages",
                query = "intitle:\"admin login\" OR intitle:\"administrator login\"",
                category = DorkCategory.LOGIN_PAGES,
                searchEngine = SearchEngine.BING,
                description = "Páginas de login de administrador",
                riskLevel = 3
            )
        )
    }
    
    /**
     * Genera dorks para paneles de administración
     */
    private fun generateAdminPanelDorks(): List<DorkQuery> {
        return listOf(
            DorkQuery(
                name = "Admin Panels - CPanel",
                query = "inurl:cpanel OR inurl:\"control panel\"",
                category = DorkCategory.ADMIN_PANELS,
                searchEngine = SearchEngine.GOOGLE,
                description = "Paneles de control CPanel",
                riskLevel = 4
            ),
            DorkQuery(
                name = "Admin Panels - PHPMyAdmin",
                query = "inurl:phpmyadmin OR intitle:\"phpMyAdmin\"",
                category = DorkCategory.ADMIN_PANELS,
                searchEngine = SearchEngine.DUCKDUCKGO,
                description = "Paneles PHPMyAdmin expuestos",
                riskLevel = 5
            )
        )
    }
    
    /**
     * Genera dorks para archivos sensibles
     */
    private fun generateSensitiveFileDorks(): List<DorkQuery> {
        return listOf(
            DorkQuery(
                name = "Config Files",
                query = "filetype:conf OR filetype:config OR filetype:cfg",
                category = DorkCategory.SENSITIVE_FILES,
                searchEngine = SearchEngine.GOOGLE,
                description = "Archivos de configuración expuestos",
                riskLevel = 3
            ),
            DorkQuery(
                name = "Database Files",
                query = "filetype:sql OR filetype:db OR filetype:mdb",
                category = DorkCategory.SENSITIVE_FILES,
                searchEngine = SearchEngine.BING,
                description = "Archivos de base de datos expuestos",
                riskLevel = 5
            ),
            DorkQuery(
                name = "Backup Files",
                query = "filetype:bak OR filetype:backup OR filetype:old",
                category = DorkCategory.SENSITIVE_FILES,
                searchEngine = SearchEngine.DUCKDUCKGO,
                description = "Archivos de respaldo expuestos",
                riskLevel = 4
            )
        )
    }
    
    /**
     * Genera dorks para APIs expuestas
     */
    private fun generateAPIExposureDorks(): List<DorkQuery> {
        return listOf(
            DorkQuery(
                name = "REST APIs",
                query = "inurl:api OR inurl:rest OR inurl:json",
                category = DorkCategory.API_ENDPOINTS,
                searchEngine = SearchEngine.GOOGLE,
                description = "APIs REST expuestas",
                riskLevel = 3
            ),
            DorkQuery(
                name = "GraphQL APIs",
                query = "inurl:graphql OR intitle:\"GraphQL\"",
                category = DorkCategory.API_ENDPOINTS,
                searchEngine = SearchEngine.BING,
                description = "APIs GraphQL expuestas",
                riskLevel = 3
            )
        )
    }
    
    /**
     * Genera dorks para bases de datos expuestas
     */
    private fun generateDatabaseExposureDorks(): List<DorkQuery> {
        return listOf(
            DorkQuery(
                name = "MongoDB Exposed",
                query = "\"MongoDB Server Information\" port:27017",
                category = DorkCategory.DATABASE_EXPOSURE,
                searchEngine = SearchEngine.SHODAN,
                description = "Instancias MongoDB expuestas",
                riskLevel = 5
            ),
            DorkQuery(
                name = "MySQL Exposed",
                query = "\"mysql\" port:3306",
                category = DorkCategory.DATABASE_EXPOSURE,
                searchEngine = SearchEngine.SHODAN,
                description = "Instancias MySQL expuestas",
                riskLevel = 5
            )
        )
    }
    
    /**
     * Procesa los objetivos descubiertos
     */
    private suspend fun processDiscoveredTargets(targets: List<Target>) {
        Log.i(TAG, "Procesando ${targets.size} objetivos descubiertos")
        
        // Filtrar objetivos de alta prioridad
        val highPriorityTargets = targets.filter { target ->
            target.url.contains("admin") || 
            target.url.contains("login") || 
            target.url.contains("phpmyadmin") ||
            target.dorkQuery?.contains("sql") == true
        }
        
        Log.i(TAG, "Objetivos de alta prioridad: ${highPriorityTargets.size}")
        
        // Aquí se podría integrar con el AutomatedScanner para análisis inmediato
        // de objetivos de alta prioridad
    }
    
    /**
     * Notifica los resultados del dorking
     */
    private fun notifyDorkingResults(result: DorkingResult.Success) {
        // Enviar broadcast con los resultados
        val intent = Intent("com.badai.sqlmapautomator.DORKING_RESULTS")
        intent.putExtra("target_count", result.targets.size)
        intent.putExtra("timestamp", System.currentTimeMillis())
        sendBroadcast(intent)
        
        Log.i(TAG, "Resultados de dorking notificados")
    }
}