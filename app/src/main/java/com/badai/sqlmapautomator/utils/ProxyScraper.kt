package com.badai.sqlmapautomator.utils

import android.content.Context
import android.util.Log
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern
import kotlin.random.Random

/**
 * Scraper automático de proxies para anonimización de tráfico
 * Obtiene proxies de múltiples fuentes públicas y los valida automáticamente
 */
class ProxyScraper(private val context: Context) {
    
    companion object {
        private const val TAG = "ProxyScraper"
        private const val PROXY_TIMEOUT_MS = 5000
        private const val MAX_CONCURRENT_TESTS = 10
        private const val USER_AGENT = "Mozilla/5.0 (Android 11; Mobile; rv:68.0) Gecko/68.0 Firefox/88.0"
        private const val TEST_URL = "http://httpbin.org/ip"
        
        // Fuentes públicas de proxies
        private val PROXY_SOURCES = listOf(
            "https://www.proxy-list.download/api/v1/get?type=http",
            "https://api.proxyscrape.com/v2/?request=get&protocol=http&timeout=5000&country=all&ssl=all&anonymity=all",
            "https://raw.githubusercontent.com/TheSpeedX/PROXY-List/master/http.txt",
            "https://raw.githubusercontent.com/clarketm/proxy-list/master/proxy-list-raw.txt",
            "https://raw.githubusercontent.com/sunny9577/proxy-scraper/master/proxies.txt",
            "https://raw.githubusercontent.com/ShiftyTR/Proxy-List/master/http.txt",
            "https://raw.githubusercontent.com/monosans/proxy-list/main/proxies/http.txt",
            "https://raw.githubusercontent.com/hookzof/socks5_list/master/proxy.txt"
        )
        
        // Sitios web para scraping manual
        private val SCRAPING_SITES = listOf(
            ScrapingSite(
                "https://free-proxy-list.net/",
                "table#proxylisttable tbody tr",
                ProxyPattern("td:nth-child(1)", "td:nth-child(2)", "td:nth-child(7)")
            ),
            ScrapingSite(
                "https://www.sslproxies.org/",
                "table#proxylisttable tbody tr",
                ProxyPattern("td:nth-child(1)", "td:nth-child(2)", "td:nth-child(7)")
            ),
            ScrapingSite(
                "https://www.us-proxy.org/",
                "table#proxylisttable tbody tr",
                ProxyPattern("td:nth-child(1)", "td:nth-child(2)", "td:nth-child(7)")
            ),
            ScrapingSite(
                "https://www.proxy-list.download/HTTP",
                "div.table-responsive table tbody tr",
                ProxyPattern("td:nth-child(1)", "td:nth-child(2)", "td:nth-child(3)")
            )
        )
    }
    
    private val validProxies = ConcurrentHashMap<String, ProxyInfo>()
    private val proxyRotationIndex = AtomicInteger(0)
    
    /**
     * Obtiene proxies automáticamente de todas las fuentes disponibles
     */
    suspend fun scrapeProxiesAutomatically(
        maxProxies: Int = 100,
        testProxies: Boolean = true,
        countries: List<String> = emptyList(),
        anonymityLevels: List<AnonymityLevel> = listOf(AnonymityLevel.HIGH, AnonymityLevel.ELITE)
    ): List<ProxyInfo> = withContext(Dispatchers.IO) {
        
        Log.i(TAG, "Iniciando scraping automático de proxies...")
        
        val allProxies = mutableSetOf<ProxyInfo>()
        
        try {
            // Obtener proxies de APIs públicas
            val apiProxies = scrapeFromAPIs()
            allProxies.addAll(apiProxies)
            Log.d(TAG, "Obtenidos ${apiProxies.size} proxies de APIs")
            
            // Scraping de sitios web
            val webProxies = scrapeFromWebsites()
            allProxies.addAll(webProxies)
            Log.d(TAG, "Obtenidos ${webProxies.size} proxies de sitios web")
            
            // Filtrar por país y nivel de anonimato
            val filteredProxies = allProxies.filter { proxy ->
                (countries.isEmpty() || proxy.country in countries) &&
                (anonymityLevels.isEmpty() || proxy.anonymityLevel in anonymityLevels)
            }.take(maxProxies)
            
            Log.i(TAG, "Proxies filtrados: ${filteredProxies.size}")
            
            // Validar proxies si se solicita
            val finalProxies = if (testProxies) {
                validateProxies(filteredProxies)
            } else {
                filteredProxies
            }
            
            // Guardar proxies válidos
            finalProxies.forEach { proxy ->
                validProxies[proxy.address] = proxy
            }
            
            Log.i(TAG, "Scraping completado: ${finalProxies.size} proxies válidos")
            finalProxies
            
        } catch (e: Exception) {
            Log.e(TAG, "Error durante el scraping de proxies", e)
            emptyList()
        }
    }
    
    /**
     * Obtiene proxies de APIs públicas
     */
    private suspend fun scrapeFromAPIs(): List<ProxyInfo> = withContext(Dispatchers.IO) {
        val proxies = mutableListOf<ProxyInfo>()
        
        PROXY_SOURCES.forEach { source ->
            try {
                val response = fetchUrl(source)
                val parsedProxies = parseProxyResponse(response, source)
                proxies.addAll(parsedProxies)
                
                delay(1000) // Rate limiting
                
            } catch (e: Exception) {
                Log.w(TAG, "Error obteniendo proxies de $source: ${e.message}")
            }
        }
        
        proxies.distinctBy { it.address }
    }
    
    /**
     * Scraping de sitios web usando patrones HTML
     */
    private suspend fun scrapeFromWebsites(): List<ProxyInfo> = withContext(Dispatchers.IO) {
        val proxies = mutableListOf<ProxyInfo>()
        
        SCRAPING_SITES.forEach { site ->
            try {
                val html = fetchUrl(site.url)
                val parsedProxies = parseHtmlProxies(html, site)
                proxies.addAll(parsedProxies)
                
                delay(2000) // Rate limiting más conservador para scraping
                
            } catch (e: Exception) {
                Log.w(TAG, "Error scrapeando ${site.url}: ${e.message}")
            }
        }
        
        proxies.distinctBy { it.address }
    }
    
    /**
     * Obtiene contenido de una URL
     */
    private suspend fun fetchUrl(url: String): String = withContext(Dispatchers.IO) {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.apply {
            requestMethod = "GET"
            setRequestProperty("User-Agent", USER_AGENT)
            setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            setRequestProperty("Accept-Language", "en-US,en;q=0.5")
            setRequestProperty("Accept-Encoding", "gzip, deflate")
            setRequestProperty("Connection", "keep-alive")
            connectTimeout = 10000
            readTimeout = 10000
        }
        
        BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
            reader.readText()
        }
    }
    
    /**
     * Parsea respuesta de API de proxies
     */
    private fun parseProxyResponse(response: String, source: String): List<ProxyInfo> {
        val proxies = mutableListOf<ProxyInfo>()
        
        try {
            when {
                response.startsWith("[") -> {
                    // JSON Array
                    val jsonArray = JSONArray(response)
                    for (i in 0 until jsonArray.length()) {
                        val proxy = parseJsonProxy(jsonArray.getJSONObject(i))
                        if (proxy != null) proxies.add(proxy)
                    }
                }
                response.startsWith("{") -> {
                    // JSON Object
                    val jsonObject = JSONObject(response)
                    if (jsonObject.has("proxies")) {
                        val proxyArray = jsonObject.getJSONArray("proxies")
                        for (i in 0 until proxyArray.length()) {
                            val proxy = parseJsonProxy(proxyArray.getJSONObject(i))
                            if (proxy != null) proxies.add(proxy)
                        }
                    }
                }
                else -> {
                    // Texto plano (IP:PORT por línea)
                    val lines = response.split("\n")
                    lines.forEach { line ->
                        val proxy = parseTextProxy(line.trim())
                        if (proxy != null) proxies.add(proxy)
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error parseando respuesta de $source", e)
        }
        
        return proxies
    }
    
    /**
     * Parsea proxy desde JSON
     */
    private fun parseJsonProxy(json: JSONObject): ProxyInfo? {
        try {
            val ip = json.optString("ip") ?: json.optString("host") ?: return null
            val port = json.optInt("port", 0)
            if (port == 0) return null
            
            val country = json.optString("country", "Unknown")
            val anonymity = when (json.optString("anonymity", "").lowercase()) {
                "elite", "high anonymity" -> AnonymityLevel.ELITE
                "anonymous", "high" -> AnonymityLevel.HIGH
                "transparent", "low" -> AnonymityLevel.LOW
                else -> AnonymityLevel.MEDIUM
            }
            
            val protocol = json.optString("protocol", "http").lowercase()
            val type = when (protocol) {
                "http", "https" -> ProxyType.HTTP
                "socks4" -> ProxyType.SOCKS4
                "socks5" -> ProxyType.SOCKS5
                else -> ProxyType.HTTP
            }
            
            return ProxyInfo(
                ip = ip,
                port = port,
                type = type,
                country = country,
                anonymityLevel = anonymity,
                responseTime = 0,
                lastTested = Date(),
                isWorking = false
            )
        } catch (e: Exception) {
            return null
        }
    }
    
    /**
     * Parsea proxy desde texto plano
     */
    private fun parseTextProxy(line: String): ProxyInfo? {
        val pattern = Pattern.compile("(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}):(\\d{1,5})")
        val matcher = pattern.matcher(line)
        
        if (matcher.find()) {
            val ip = matcher.group(1) ?: return null
            val port = matcher.group(2)?.toIntOrNull() ?: return null
            
            return ProxyInfo(
                ip = ip,
                port = port,
                type = ProxyType.HTTP,
                country = "Unknown",
                anonymityLevel = AnonymityLevel.MEDIUM,
                responseTime = 0,
                lastTested = Date(),
                isWorking = false
            )
        }
        
        return null
    }
    
    /**
     * Parsea proxies desde HTML usando patrones CSS
     */
    private fun parseHtmlProxies(html: String, site: ScrapingSite): List<ProxyInfo> {
        val proxies = mutableListOf<ProxyInfo>()
        
        try {
            // Implementación simplificada - en producción usaría JSoup
            val ipPattern = Pattern.compile("(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})")
            val portPattern = Pattern.compile(":(\\d{1,5})")
            
            val ipMatches = ipPattern.findAll(html)
            val portMatches = portPattern.findAll(html)
            
            val ips = ipMatches.map { it.groupValues[1] }.toList()
            val ports = portMatches.map { it.groupValues[1].toIntOrNull() }.filterNotNull()
            
            val minSize = minOf(ips.size, ports.size)
            for (i in 0 until minSize) {
                val proxy = ProxyInfo(
                    ip = ips[i],
                    port = ports[i],
                    type = ProxyType.HTTP,
                    country = "Unknown",
                    anonymityLevel = AnonymityLevel.MEDIUM,
                    responseTime = 0,
                    lastTested = Date(),
                    isWorking = false
                )
                proxies.add(proxy)
            }
            
        } catch (e: Exception) {
            Log.w(TAG, "Error parseando HTML de ${site.url}", e)
        }
        
        return proxies
    }
    
    /**
     * Valida proxies probando conectividad
     */
    private suspend fun validateProxies(proxies: List<ProxyInfo>): List<ProxyInfo> = withContext(Dispatchers.IO) {
        Log.i(TAG, "Validando ${proxies.size} proxies...")
        
        val validProxies = mutableListOf<ProxyInfo>()
        val semaphore = Semaphore(MAX_CONCURRENT_TESTS)
        
        val jobs = proxies.map { proxy ->
            async {
                semaphore.withPermit {
                    testProxy(proxy)
                }
            }
        }
        
        jobs.forEach { job ->
            val result = job.await()
            if (result?.isWorking == true) {
                validProxies.add(result)
            }
        }
        
        Log.i(TAG, "Validación completada: ${validProxies.size} proxies válidos")
        validProxies
    }
    
    /**
     * Prueba un proxy individual
     */
    private suspend fun testProxy(proxy: ProxyInfo): ProxyInfo? = withContext(Dispatchers.IO) {
        try {
            val startTime = System.currentTimeMillis()
            
            val proxyObj = Proxy(
                when (proxy.type) {
                    ProxyType.HTTP -> Proxy.Type.HTTP
                    ProxyType.SOCKS4, ProxyType.SOCKS5 -> Proxy.Type.SOCKS
                },
                InetSocketAddress(proxy.ip, proxy.port)
            )
            
            val connection = URL(TEST_URL).openConnection(proxyObj) as HttpURLConnection
            connection.apply {
                requestMethod = "GET"
                setRequestProperty("User-Agent", USER_AGENT)
                connectTimeout = PROXY_TIMEOUT_MS
                readTimeout = PROXY_TIMEOUT_MS
            }
            
            val responseCode = connection.responseCode
            val responseTime = System.currentTimeMillis() - startTime
            
            if (responseCode == 200) {
                val response = BufferedReader(InputStreamReader(connection.inputStream)).use { it.readText() }
                
                // Verificar que el proxy está funcionando (IP diferente)
                val isAnonymous = !response.contains(getLocalIP())
                
                return@withContext proxy.copy(
                    isWorking = true,
                    responseTime = responseTime,
                    lastTested = Date(),
                    anonymityLevel = if (isAnonymous) proxy.anonymityLevel else AnonymityLevel.LOW
                )
            }
            
        } catch (e: Exception) {
            Log.d(TAG, "Proxy ${proxy.address} falló: ${e.message}")
        }
        
        return@withContext proxy.copy(
            isWorking = false,
            lastTested = Date()
        )
    }
    
    /**
     * Obtiene un proxy aleatorio válido
     */
    fun getRandomProxy(): ProxyInfo? {
        val workingProxies = validProxies.values.filter { it.isWorking }
        return if (workingProxies.isNotEmpty()) {
            workingProxies[Random.nextInt(workingProxies.size)]
        } else null
    }
    
    /**
     * Obtiene el siguiente proxy en rotación
     */
    fun getNextProxy(): ProxyInfo? {
        val workingProxies = validProxies.values.filter { it.isWorking }.toList()
        if (workingProxies.isEmpty()) return null
        
        val index = proxyRotationIndex.getAndIncrement() % workingProxies.size
        return workingProxies[index]
    }
    
    /**
     * Obtiene la IP local para verificar anonimato
     */
    private fun getLocalIP(): String {
        return try {
            val connection = URL(TEST_URL).openConnection() as HttpURLConnection
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            
            val response = BufferedReader(InputStreamReader(connection.inputStream)).use { it.readText() }
            val json = JSONObject(response)
            json.getString("origin")
        } catch (e: Exception) {
            "unknown"
        }
    }
    
    /**
     * Obtiene estadísticas de proxies
     */
    fun getProxyStats(): ProxyStats {
        val allProxies = validProxies.values
        val workingProxies = allProxies.filter { it.isWorking }
        
        return ProxyStats(
            totalProxies = allProxies.size,
            workingProxies = workingProxies.size,
            averageResponseTime = workingProxies.map { it.responseTime }.average().toLong(),
            countriesAvailable = allProxies.map { it.country }.distinct().size,
            lastUpdate = Date()
        )
    }
    
    /**
     * Limpia proxies no funcionales
     */
    fun cleanupInvalidProxies() {
        val invalidProxies = validProxies.values.filter { !it.isWorking }
        invalidProxies.forEach { proxy ->
            validProxies.remove(proxy.address)
        }
        Log.i(TAG, "Limpiados ${invalidProxies.size} proxies inválidos")
    }
}

/**
 * Información de proxy
 */
data class ProxyInfo(
    val ip: String,
    val port: Int,
    val type: ProxyType,
    val country: String,
    val anonymityLevel: AnonymityLevel,
    val responseTime: Long,
    val lastTested: Date,
    val isWorking: Boolean
) {
    val address: String get() = "$ip:$port"
}

enum class ProxyType {
    HTTP, HTTPS, SOCKS4, SOCKS5
}

enum class AnonymityLevel {
    LOW,      // Transparent
    MEDIUM,   // Anonymous
    HIGH,     // High Anonymity
    ELITE     // Elite
}

/**
 * Sitio para scraping
 */
data class ScrapingSite(
    val url: String,
    val tableSelector: String,
    val pattern: ProxyPattern
)

/**
 * Patrón para extraer datos de proxy
 */
data class ProxyPattern(
    val ipSelector: String,
    val portSelector: String,
    val anonymitySelector: String
)

/**
 * Estadísticas de proxies
 */
data class ProxyStats(
    val totalProxies: Int,
    val workingProxies: Int,
    val averageResponseTime: Long,
    val countriesAvailable: Int,
    val lastUpdate: Date
)

/**
 * Clase auxiliar para índice atómico
 */
class AtomicInteger(private var value: Int = 0) {
    @Synchronized
    fun getAndIncrement(): Int {
        val current = value
        value++
        return current
    }
}