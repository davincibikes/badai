package com.badai.sqlmapautomator.dorker

import android.content.Context
import android.util.Log
import com.badai.sqlmapautomator.data.model.*
import com.badai.sqlmapautomator.utils.LegalComplianceChecker
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.net.URLDecoder
import java.util.*
import java.util.regex.Pattern

/**
 * ⚠️⚠️⚠️ ADVERTENCIA LEGAL ⚠️⚠️⚠️
 * 
 * Este módulo automatiza la búsqueda de objetivos potencialmente vulnerables.
 * USO EXCLUSIVO PARA:
 * - Auditorías de seguridad autorizadas
 * - Investigación académica en entornos controlados
 * - Pruebas de penetración con autorización explícita
 * 
 * EL USO PARA ACTIVIDADES MALICIOSAS ES ILEGAL
 */
class AutoDorker(
    private val context: Context,
    private val complianceChecker: LegalComplianceChecker
) {
    
    companion object {
        private const val TAG = "AutoDorker"
        private const val MAX_RESULTS_PER_DORK = 100
        private const val MAX_CONCURRENT_SEARCHES = 3
        private const val SEARCH_DELAY_MS = 2000L // Delay entre búsquedas
        private const val USER_AGENT = "Mozilla/5.0 (Android; Mobile; rv:40.0) Gecko/40.0 Firefox/40.0"
    }
    
    private val searchEngines = mapOf(
        SearchEngine.GOOGLE to GoogleSearcher(),
        SearchEngine.BING to BingSearcher(),
        SearchEngine.DUCKDUCKGO to DuckDuckGoSearcher(),
        SearchEngine.SHODAN to ShodanSearcher()
    )
    
    /**
     * Ejecuta búsqueda automática de objetivos usando dorks
     */
    suspend fun executeAutomaticDorking(
        dorkQueries: List<DorkQuery>,
        maxTargetsPerDork: Int = MAX_RESULTS_PER_DORK,
        countries: List<String> = emptyList(),
        excludeDomains: List<String> = emptyList()
    ): DorkingResult = withContext(Dispatchers.IO) {
        
        // Verificar cumplimiento legal
        if (!complianceChecker.verifyLegalCompliance(
                0L, 
                "Búsqueda automática de objetivos para auditoría de seguridad autorizada",
                "dork_search"
            )) {
            return@withContext DorkingResult.failure("Verificación de cumplimiento legal fallida")
        }
        
        val allTargets = mutableListOf<Target>()
        val searchResults = mutableMapOf<DorkQuery, List<SearchResult>>()
        
        try {
            // Ejecutar búsquedas en paralelo con límite de concurrencia
            val semaphore = Semaphore(MAX_CONCURRENT_SEARCHES)
            
            val jobs = dorkQueries.map { dorkQuery ->
                async {
                    semaphore.withPermit {
                        delay(SEARCH_DELAY_MS) // Rate limiting
                        executeSearchForDork(dorkQuery, maxTargetsPerDork, countries, excludeDomains)
                    }
                }
            }
            
            // Recopilar resultados
            jobs.forEach { job ->
                val (dork, results) = job.await()
                searchResults[dork] = results
                
                // Convertir resultados a objetivos
                val targets = results.map { result ->
                    Target(
                        url = result.url,
                        source = "dork",
                        dorkQuery = dork.query,
                        searchEngine = dork.searchEngine.name,
                        country = result.country,
                        technology = result.technology,
                        status = TargetStatus.PENDING,
                        createdAt = Date()
                    )
                }
                
                allTargets.addAll(targets)
            }
            
            // Filtrar duplicados
            val uniqueTargets = allTargets.distinctBy { it.url }
            
            Log.i(TAG, "Dorking completado: ${uniqueTargets.size} objetivos únicos encontrados")
            
            DorkingResult.success(uniqueTargets, searchResults)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error durante el dorking automático", e)
            DorkingResult.failure("Error durante la búsqueda: ${e.message}")
        }
    }
    
    /**
     * Ejecuta búsqueda para un dork específico
     */
    private suspend fun executeSearchForDork(
        dorkQuery: DorkQuery,
        maxResults: Int,
        countries: List<String>,
        excludeDomains: List<String>
    ): Pair<DorkQuery, List<SearchResult>> {
        
        val searcher = searchEngines[dorkQuery.searchEngine]
            ?: throw IllegalArgumentException("Motor de búsqueda no soportado: ${dorkQuery.searchEngine}")
        
        Log.d(TAG, "Ejecutando dork: ${dorkQuery.name} en ${dorkQuery.searchEngine}")
        
        val results = searcher.search(
            query = buildEnhancedQuery(dorkQuery.query, countries, excludeDomains),
            maxResults = maxResults
        )
        
        // Filtrar y validar resultados
        val validResults = results.filter { result ->
            isValidTarget(result.url) && !isExcludedDomain(result.url, excludeDomains)
        }
        
        Log.d(TAG, "Dork ${dorkQuery.name}: ${validResults.size} resultados válidos")
        
        return Pair(dorkQuery, validResults)
    }
    
    /**
     * Construye query mejorada con filtros adicionales
     */
    private fun buildEnhancedQuery(
        baseQuery: String,
        countries: List<String>,
        excludeDomains: List<String>
    ): String {
        var enhancedQuery = baseQuery
        
        // Añadir filtros de país
        if (countries.isNotEmpty()) {
            val countryFilter = countries.joinToString(" OR ") { "site:.$it" }
            enhancedQuery += " ($countryFilter)"
        }
        
        // Excluir dominios
        if (excludeDomains.isNotEmpty()) {
            val excludeFilter = excludeDomains.joinToString(" ") { "-site:$it" }
            enhancedQuery += " $excludeFilter"
        }
        
        // Filtros adicionales para mejorar precisión
        enhancedQuery += " -site:github.com -site:stackoverflow.com -site:pastebin.com"
        
        return enhancedQuery
    }
    
    /**
     * Valida si una URL es un objetivo válido
     */
    private fun isValidTarget(url: String): Boolean {
        try {
            val urlObj = URL(url)
            
            // Verificar protocolo
            if (urlObj.protocol !in listOf("http", "https")) {
                return false
            }
            
            // Verificar que tenga parámetros (potencialmente vulnerable)
            if (urlObj.query.isNullOrEmpty()) {
                return false
            }
            
            // Verificar patrones de parámetros vulnerables
            val vulnerableParams = listOf("id", "pid", "cid", "uid", "sid", "page", "cat", "item")
            val hasVulnerableParam = vulnerableParams.any { param ->
                urlObj.query?.contains("$param=") == true
            }
            
            return hasVulnerableParam
            
        } catch (e: Exception) {
            return false
        }
    }
    
    /**
     * Verifica si un dominio está excluido
     */
    private fun isExcludedDomain(url: String, excludeDomains: List<String>): Boolean {
        try {
            val domain = URL(url).host.lowercase()
            return excludeDomains.any { excludeDomain ->
                domain.contains(excludeDomain.lowercase())
            }
        } catch (e: Exception) {
            return true // Si no se puede parsear, excluir por seguridad
        }
    }
    
    /**
     * Genera dorks automáticamente basados en tecnologías específicas
     */
    fun generateAutomaticDorks(
        technologies: List<String> = listOf("php", "asp", "jsp"),
        categories: List<DorkCategory> = listOf(DorkCategory.SQL_INJECTION)
    ): List<DorkQuery> {
        val generatedDorks = mutableListOf<DorkQuery>()
        
        technologies.forEach { tech ->
            categories.forEach { category ->
                when (category) {
                    DorkCategory.SQL_INJECTION -> {
                        generatedDorks.addAll(generateSQLInjectionDorks(tech))
                    }
                    DorkCategory.LOGIN_PAGES -> {
                        generatedDorks.addAll(generateLoginPageDorks(tech))
                    }
                    DorkCategory.ADMIN_PANELS -> {
                        generatedDorks.addAll(generateAdminPanelDorks(tech))
                    }
                    else -> {
                        // Otros tipos de dorks
                    }
                }
            }
        }
        
        return generatedDorks
    }
    
    /**
     * Genera dorks específicos para inyección SQL
     */
    private fun generateSQLInjectionDorks(technology: String): List<DorkQuery> {
        val commonParams = listOf("id", "pid", "cid", "uid", "page", "cat", "item", "news", "article")
        val dorks = mutableListOf<DorkQuery>()
        
        commonParams.forEach { param ->
            dorks.add(
                DorkQuery(
                    name = "SQL Injection - $technology - $param",
                    query = "inurl:\"$param=\" filetype:$technology",
                    category = DorkCategory.SQL_INJECTION,
                    searchEngine = SearchEngine.GOOGLE,
                    description = "Busca páginas $technology con parámetro $param potencialmente vulnerable",
                    riskLevel = 3
                )
            )
        }
        
        return dorks
    }
    
    /**
     * Genera dorks para páginas de login
     */
    private fun generateLoginPageDorks(technology: String): List<DorkQuery> {
        return listOf(
            DorkQuery(
                name = "Login Pages - $technology",
                query = "inurl:login filetype:$technology OR inurl:admin filetype:$technology",
                category = DorkCategory.LOGIN_PAGES,
                searchEngine = SearchEngine.GOOGLE,
                description = "Páginas de login en $technology",
                riskLevel = 2
            )
        )
    }
    
    /**
     * Genera dorks para paneles de administración
     */
    private fun generateAdminPanelDorks(technology: String): List<DorkQuery> {
        return listOf(
            DorkQuery(
                name = "Admin Panels - $technology",
                query = "intitle:\"admin\" OR intitle:\"administrator\" filetype:$technology",
                category = DorkCategory.ADMIN_PANELS,
                searchEngine = SearchEngine.GOOGLE,
                description = "Paneles de administración en $technology",
                riskLevel = 3
            )
        )
    }
}

/**
 * Resultado de la operación de dorking
 */
sealed class DorkingResult {
    data class Success(
        val targets: List<Target>,
        val searchResults: Map<DorkQuery, List<SearchResult>>
    ) : DorkingResult()
    
    data class Failure(val error: String) : DorkingResult()
    
    companion object {
        fun success(targets: List<Target>, searchResults: Map<DorkQuery, List<SearchResult>>) = 
            Success(targets, searchResults)
        fun failure(error: String) = Failure(error)
    }
}

/**
 * Resultado de búsqueda individual
 */
data class SearchResult(
    val url: String,
    val title: String,
    val snippet: String,
    val country: String? = null,
    val technology: String? = null,
    val confidence: Float = 0.5f
)

/**
 * Interfaz para motores de búsqueda
 */
interface SearchEngine {
    suspend fun search(query: String, maxResults: Int): List<SearchResult>
}

/**
 * Implementación para Google Search
 */
class GoogleSearcher : SearchEngine {
    companion object {
        private const val GOOGLE_SEARCH_URL = "https://www.google.com/search"
        private const val USER_AGENT = "Mozilla/5.0 (Android 11; Mobile; rv:68.0) Gecko/68.0 Firefox/88.0"
    }
    
    override suspend fun search(query: String, maxResults: Int): List<SearchResult> = withContext(Dispatchers.IO) {
        val results = mutableListOf<SearchResult>()
        
        try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val searchUrl = "$GOOGLE_SEARCH_URL?q=$encodedQuery&num=$maxResults"
            
            val connection = URL(searchUrl).openConnection() as HttpURLConnection
            connection.setRequestProperty("User-Agent", USER_AGENT)
            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5")
            connection.setRequestProperty("Accept-Encoding", "gzip, deflate")
            connection.setRequestProperty("Connection", "keep-alive")
            connection.connectTimeout = 10000
            connection.readTimeout = 15000
            
            val responseCode = connection.responseCode
            if (responseCode == 200) {
                val content = connection.inputStream.bufferedReader().use { it.readText() }
                results.addAll(parseGoogleResults(content))
            }
            
        } catch (e: Exception) {
            Log.w("GoogleSearcher", "Error en búsqueda Google: ${e.message}")
        }
        
        results.take(maxResults)
    }
    
    private fun parseGoogleResults(html: String): List<SearchResult> {
        val results = mutableListOf<SearchResult>()
        
        // Regex para extraer URLs de resultados de Google
        val urlPattern = Pattern.compile("href=\"/url\\?q=([^&]+)&")
        val titlePattern = Pattern.compile("<h3[^>]*>([^<]+)</h3>")
        val snippetPattern = Pattern.compile("<span[^>]*data-ved[^>]*>([^<]+)</span>")
        
        val urlMatcher = urlPattern.matcher(html)
        val titleMatcher = titlePattern.matcher(html)
        val snippetMatcher = snippetPattern.matcher(html)
        
        while (urlMatcher.find() && titleMatcher.find()) {
            try {
                val url = URLDecoder.decode(urlMatcher.group(1), "UTF-8")
                val title = titleMatcher.group(1)
                val snippet = if (snippetMatcher.find()) snippetMatcher.group(1) else ""
                
                if (url.startsWith("http") && !url.contains("google.com")) {
                    results.add(SearchResult(
                        url = url,
                        title = title,
                        snippet = snippet,
                        confidence = 0.8f
                    ))
                }
            } catch (e: Exception) {
                // Ignorar errores de parsing individual
            }
        }
        
        return results
    }
}

/**
 * Implementación para Bing Search
 */
class BingSearcher : SearchEngine {
    companion object {
        private const val BING_SEARCH_URL = "https://www.bing.com/search"
        private const val USER_AGENT = "Mozilla/5.0 (Android 11; Mobile; rv:68.0) Gecko/68.0 Firefox/88.0"
    }
    
    override suspend fun search(query: String, maxResults: Int): List<SearchResult> = withContext(Dispatchers.IO) {
        val results = mutableListOf<SearchResult>()
        
        try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val searchUrl = "$BING_SEARCH_URL?q=$encodedQuery&count=$maxResults"
            
            val connection = URL(searchUrl).openConnection() as HttpURLConnection
            connection.setRequestProperty("User-Agent", USER_AGENT)
            connection.connectTimeout = 10000
            connection.readTimeout = 15000
            
            val responseCode = connection.responseCode
            if (responseCode == 200) {
                val content = connection.inputStream.bufferedReader().use { it.readText() }
                results.addAll(parseBingResults(content))
            }
            
        } catch (e: Exception) {
            Log.w("BingSearcher", "Error en búsqueda Bing: ${e.message}")
        }
        
        results.take(maxResults)
    }
    
    private fun parseBingResults(html: String): List<SearchResult> {
        val results = mutableListOf<SearchResult>()
        
        // Regex para extraer URLs de resultados de Bing
        val urlPattern = Pattern.compile("href=\"([^\"]+)\"[^>]*data-url")
        val titlePattern = Pattern.compile("<h2[^>]*><a[^>]*>([^<]+)</a></h2>")
        
        val urlMatcher = urlPattern.matcher(html)
        val titleMatcher = titlePattern.matcher(html)
        
        while (urlMatcher.find() && titleMatcher.find()) {
            try {
                val url = urlMatcher.group(1)
                val title = titleMatcher.group(1)
                
                if (url.startsWith("http") && !url.contains("bing.com")) {
                    results.add(SearchResult(
                        url = url,
                        title = title,
                        snippet = "",
                        confidence = 0.7f
                    ))
                }
            } catch (e: Exception) {
                // Ignorar errores de parsing individual
            }
        }
        
        return results
    }
}

/**
 * Implementación para DuckDuckGo
 */
class DuckDuckGoSearcher : SearchEngine {
    companion object {
        private const val DUCKDUCKGO_SEARCH_URL = "https://html.duckduckgo.com/html/"
        private const val USER_AGENT = "Mozilla/5.0 (Android 11; Mobile; rv:68.0) Gecko/68.0 Firefox/88.0"
    }
    
    override suspend fun search(query: String, maxResults: Int): List<SearchResult> = withContext(Dispatchers.IO) {
        val results = mutableListOf<SearchResult>()
        
        try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val searchUrl = "$DUCKDUCKGO_SEARCH_URL?q=$encodedQuery"
            
            val connection = URL(searchUrl).openConnection() as HttpURLConnection
            connection.setRequestProperty("User-Agent", USER_AGENT)
            connection.connectTimeout = 10000
            connection.readTimeout = 15000
            
            val responseCode = connection.responseCode
            if (responseCode == 200) {
                val content = connection.inputStream.bufferedReader().use { it.readText() }
                results.addAll(parseDuckDuckGoResults(content))
            }
            
        } catch (e: Exception) {
            Log.w("DuckDuckGoSearcher", "Error en búsqueda DuckDuckGo: ${e.message}")
        }
        
        results.take(maxResults)
    }
    
    private fun parseDuckDuckGoResults(html: String): List<SearchResult> {
        val results = mutableListOf<SearchResult>()
        
        // Regex para extraer URLs de resultados de DuckDuckGo
        val urlPattern = Pattern.compile("href=\"([^\"]+)\"[^>]*class=\"result__url")
        val titlePattern = Pattern.compile("class=\"result__title\"><a[^>]*>([^<]+)</a>")
        
        val urlMatcher = urlPattern.matcher(html)
        val titleMatcher = titlePattern.matcher(html)
        
        while (urlMatcher.find() && titleMatcher.find()) {
            try {
                val url = urlMatcher.group(1)
                val title = titleMatcher.group(1)
                
                if (url.startsWith("http") && !url.contains("duckduckgo.com")) {
                    results.add(SearchResult(
                        url = url,
                        title = title,
                        snippet = "",
                        confidence = 0.6f
                    ))
                }
            } catch (e: Exception) {
                // Ignorar errores de parsing individual
            }
        }
        
        return results
    }
}

/**
 * Implementación para Shodan
 */
class ShodanSearcher : SearchEngine {
    companion object {
        private const val SHODAN_SEARCH_URL = "https://www.shodan.io/search"
        private const val USER_AGENT = "Mozilla/5.0 (Android 11; Mobile; rv:68.0) Gecko/68.0 Firefox/88.0"
    }
    
    override suspend fun search(query: String, maxResults: Int): List<SearchResult> = withContext(Dispatchers.IO) {
        val results = mutableListOf<SearchResult>()
        
        try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val searchUrl = "$SHODAN_SEARCH_URL?query=$encodedQuery"
            
            val connection = URL(searchUrl).openConnection() as HttpURLConnection
            connection.setRequestProperty("User-Agent", USER_AGENT)
            connection.connectTimeout = 10000
            connection.readTimeout = 15000
            
            val responseCode = connection.responseCode
            if (responseCode == 200) {
                val content = connection.inputStream.bufferedReader().use { it.readText() }
                results.addAll(parseShodanResults(content))
            }
            
        } catch (e: Exception) {
            Log.w("ShodanSearcher", "Error en búsqueda Shodan: ${e.message}")
        }
        
        results.take(maxResults)
    }
    
    private fun parseShodanResults(html: String): List<SearchResult> {
        val results = mutableListOf<SearchResult>()
        
        // Regex para extraer IPs y puertos de Shodan
        val ipPattern = Pattern.compile("href=\"/host/([0-9.]+)\"")
        val portPattern = Pattern.compile("Port: ([0-9]+)")
        
        val ipMatcher = ipPattern.matcher(html)
        val portMatcher = portPattern.matcher(html)
        
        while (ipMatcher.find()) {
            try {
                val ip = ipMatcher.group(1)
                val port = if (portMatcher.find()) portMatcher.group(1) else "80"
                val url = "http://$ip:$port"
                
                results.add(SearchResult(
                    url = url,
                    title = "Shodan Result: $ip:$port",
                    snippet = "Host encontrado en Shodan",
                    confidence = 0.9f
                ))
            } catch (e: Exception) {
                // Ignorar errores de parsing individual
            }
        }
        
        return results
    }
}