package com.badai.sqlmapautomator.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.badai.sqlmapautomator.R

class DisclaimerActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disclaimer)
        
        setupViews()
    }
    
    private fun setupViews() {
        val disclaimerText = findViewById<TextView>(R.id.text_disclaimer)
        val acceptCheckbox = findViewById<CheckBox>(R.id.checkbox_accept_terms)
        val continueButton = findViewById<Button>(R.id.button_accept)
        
        disclaimerText.text = """
            LEGAL DISCLAIMER AND TERMS OF USE
            
            This application is designed for educational and authorized security testing purposes only.
            
            By using this application, you acknowledge and agree that:
            
            1. You will only use this tool on systems you own or have explicit written permission to test
            2. You understand that unauthorized access to computer systems is illegal
            3. You will comply with all applicable laws and regulations
            4. The developers are not responsible for any misuse of this tool
            5. You will use this tool ethically and responsibly
            
            IMPORTANT: Unauthorized use of this tool may violate local, state, and federal laws.
            
            By continuing, you confirm that you understand these terms and will use this tool responsibly.
        """.trimIndent()
        
        continueButton.setOnClickListener {
            if (acceptCheckbox.isChecked) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        
        acceptCheckbox.setOnCheckedChangeListener { _, isChecked ->
            continueButton.isEnabled = isChecked
        }
    }
}