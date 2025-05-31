package com.badai.sqlmapautomator.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dork_queries")
data class DorkQuery(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val query: String,
    val category: DorkCategory,
    val searchEngine: SearchEngine,
    val description: String,
    val riskLevel: Int, // 1-5
    val isActive: Boolean = true,
    val successRate: Float = 0f, // 0-1
    val lastUsed: Long? = null,
    val timesUsed: Int = 0,
    val targetsFound: Int = 0
)

enum class DorkCategory {
    SQL_INJECTION,
    LOGIN_PAGES,
    ADMIN_PANELS,
    FILE_UPLOAD,
    DIRECTORY_LISTING,
    ERROR_PAGES,
    DATABASE_FILES,
    CONFIG_FILES,
    BACKUP_FILES,
    LOG_FILES,
    VULNERABLE_APPS,
    CUSTOM
}

enum class SearchEngine {
    GOOGLE,
    BING,
    DUCKDUCKGO,
    YAHOO,
    YANDEX,
    SHODAN,
    CENSYS,
    FOFA
}

// Predefined dorks for automatic target discovery
object PredefinedDorks {
    val SQL_INJECTION_DORKS = listOf(
        DorkQuery(
            name = "PHP SQL Error",
            query = "inurl:\"php?id=\" \"mysql_fetch_array()\"",
            category = DorkCategory.SQL_INJECTION,
            searchEngine = SearchEngine.GOOGLE,
            description = "Busca páginas PHP con errores MySQL visibles",
            riskLevel = 4
        ),
        DorkQuery(
            name = "ASP SQL Error",
            query = "inurl:\"asp?id=\" \"Microsoft OLE DB Provider\"",
            category = DorkCategory.SQL_INJECTION,
            searchEngine = SearchEngine.GOOGLE,
            description = "Busca páginas ASP con errores SQL Server",
            riskLevel = 4
        ),
        DorkQuery(
            name = "Generic SQL Error",
            query = "\"SQL syntax error\" OR \"mysql_fetch_array\" OR \"ORA-01756\"",
            category = DorkCategory.SQL_INJECTION,
            searchEngine = SearchEngine.GOOGLE,
            description = "Errores SQL genéricos en páginas web",
            riskLevel = 5
        ),
        DorkQuery(
            name = "Vulnerable Parameters",
            query = "inurl:\"id=\" OR inurl:\"pid=\" OR inurl:\"category=\" filetype:php",
            category = DorkCategory.SQL_INJECTION,
            searchEngine = SearchEngine.GOOGLE,
            description = "Parámetros comúnmente vulnerables en PHP",
            riskLevel = 3
        ),
        DorkQuery(
            name = "Shopping Cart Vulns",
            query = "inurl:\"product.php?id=\" OR inurl:\"item.php?id=\" OR inurl:\"show.php?id=\"",
            category = DorkCategory.SQL_INJECTION,
            searchEngine = SearchEngine.GOOGLE,
            description = "Carritos de compra potencialmente vulnerables",
            riskLevel = 4
        ),
        DorkQuery(
            name = "News/Article Pages",
            query = "inurl:\"news.php?id=\" OR inurl:\"article.php?id=\" OR inurl:\"story.php?id=\"",
            category = DorkCategory.SQL_INJECTION,
            searchEngine = SearchEngine.GOOGLE,
            description = "Páginas de noticias y artículos vulnerables",
            riskLevel = 3
        ),
        DorkQuery(
            name = "Gallery Vulnerabilities",
            query = "inurl:\"gallery.php?id=\" OR inurl:\"photo.php?id=\" OR inurl:\"image.php?id=\"",
            category = DorkCategory.SQL_INJECTION,
            searchEngine = SearchEngine.GOOGLE,
            description = "Galerías de imágenes vulnerables",
            riskLevel = 3
        ),
        DorkQuery(
            name = "Forum Vulnerabilities",
            query = "inurl:\"forum.php?id=\" OR inurl:\"thread.php?id=\" OR inurl:\"post.php?id=\"",
            category = DorkCategory.SQL_INJECTION,
            searchEngine = SearchEngine.GOOGLE,
            description = "Foros potencialmente vulnerables",
            riskLevel = 3
        ),
        DorkQuery(
            name = "Database Errors",
            query = "\"Warning: mysql_\" OR \"Warning: pg_\" OR \"Warning: oci_\"",
            category = DorkCategory.ERROR_PAGES,
            searchEngine = SearchEngine.GOOGLE,
            description = "Páginas con errores de base de datos expuestos",
            riskLevel = 5
        ),
        DorkQuery(
            name = "Admin Login Pages",
            query = "inurl:admin inurl:login OR intitle:\"admin login\" OR intitle:\"administrator login\"",
            category = DorkCategory.LOGIN_PAGES,
            searchEngine = SearchEngine.GOOGLE,
            description = "Páginas de login de administrador",
            riskLevel = 2
        )
    )
    
    val SHODAN_DORKS = listOf(
        DorkQuery(
            name = "MySQL Servers",
            query = "port:3306 mysql",
            category = DorkCategory.DATABASE_FILES,
            searchEngine = SearchEngine.SHODAN,
            description = "Servidores MySQL expuestos",
            riskLevel = 5
        ),
        DorkQuery(
            name = "PostgreSQL Servers",
            query = "port:5432 postgresql",
            category = DorkCategory.DATABASE_FILES,
            searchEngine = SearchEngine.SHODAN,
            description = "Servidores PostgreSQL expuestos",
            riskLevel = 5
        ),
        DorkQuery(
            name = "MongoDB Servers",
            query = "port:27017 mongodb",
            category = DorkCategory.DATABASE_FILES,
            searchEngine = SearchEngine.SHODAN,
            description = "Servidores MongoDB expuestos",
            riskLevel = 5
        ),
        DorkQuery(
            name = "Web Servers with SQL",
            query = "http.html:\"sql\" port:80,443",
            category = DorkCategory.SQL_INJECTION,
            searchEngine = SearchEngine.SHODAN,
            description = "Servidores web que mencionan SQL",
            riskLevel = 3
        )
    )
}