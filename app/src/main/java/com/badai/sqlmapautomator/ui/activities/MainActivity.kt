package com.badai.sqlmapautomator.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.badai.sqlmapautomator.R
import com.badai.sqlmapautomator.utils.LegalComplianceChecker
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var complianceChecker: LegalComplianceChecker
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Verificar cumplimiento legal antes de continuar
        complianceChecker = LegalComplianceChecker(this)
        
        if (complianceChecker.isApplicationBlocked()) {
            showBlockedDialog()
            return
        }
        
        if (!complianceChecker.hasAcceptedLegalAgreement()) {
            startActivity(Intent(this, DisclaimerActivity::class.java))
            finish()
            return
        }
        
        setContentView(R.layout.activity_main)
        
        setupNavigation()
    }
    
    private fun setupNavigation() {
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_scanner,
                R.id.navigation_templates,
                R.id.navigation_results,
                R.id.navigation_settings
            )
        )
        
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
    
    private fun showBlockedDialog() {
        val reason = complianceChecker.getBlockReason() ?: "Violaciones de cumplimiento legal"
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Aplicación Bloqueada")
            .setMessage("Esta aplicación ha sido bloqueada debido a: $reason\n\nContacte al administrador para más información.")
            .setPositiveButton("Salir") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_emergency_stop -> {
                // Parada de emergencia de todos los escaneos
                true
            }
            R.id.action_legal_info -> {
                startActivity(Intent(this, DisclaimerActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}