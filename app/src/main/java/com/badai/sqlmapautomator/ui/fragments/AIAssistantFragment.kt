package com.badai.sqlmapautomator.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.badai.sqlmapautomator.R
import com.badai.sqlmapautomator.ai.AIAssistant
import com.badai.sqlmapautomator.ai.AIResponse
import com.badai.sqlmapautomator.utils.LegalComplianceChecker
import kotlinx.coroutines.launch

/**
 * ⚠️⚠️⚠️ FRAGMENTO DEL ASISTENTE AI ⚠️⚠️⚠️
 * 
 * Interfaz de chat para el asistente AI que controla toda la aplicación.
 * Permite comandos de voz y texto para:
 * - Control total de la aplicación
 * - Análisis inteligente de objetivos
 * - Ejecución automática de herramientas
 * - Generación de reportes
 * - Configuración del sistema
 * 
 * USO EXCLUSIVO PARA AUDITORÍAS AUTORIZADAS
 */
class AIAssistantFragment : Fragment() {
    
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: Button
    private lateinit var voiceButton: Button
    private lateinit var statusText: TextView
    private lateinit var progressBar: ProgressBar
    
    private lateinit var aiAssistant: AIAssistant
    private lateinit var complianceChecker: LegalComplianceChecker
    private lateinit var chatAdapter: ChatAdapter
    
    private val chatMessages = mutableListOf<ChatMessage>()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ai_assistant, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initializeViews(view)
        setupAI()
        setupRecyclerView()
        setupClickListeners()
        
        // Mensaje de bienvenida
        addWelcomeMessage()
    }
    
    private fun initializeViews(view: View) {
        chatRecyclerView = view.findViewById(R.id.chatRecyclerView)
        messageInput = view.findViewById(R.id.messageInput)
        sendButton = view.findViewById(R.id.sendButton)
        voiceButton = view.findViewById(R.id.voiceButton)
        statusText = view.findViewById(R.id.statusText)
        progressBar = view.findViewById(R.id.progressBar)
    }
    
    private fun setupAI() {
        complianceChecker = LegalComplianceChecker(requireContext())
        aiAssistant = AIAssistant(requireContext(), complianceChecker)
    }
    
    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(chatMessages)
        chatRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = chatAdapter
        }
    }
    
    private fun setupClickListeners() {
        sendButton.setOnClickListener {
            sendMessage()
        }
        
        voiceButton.setOnClickListener {
            startVoiceInput()
        }
        
        messageInput.setOnEditorActionListener { _, _, _ ->
            sendMessage()
            true
        }
    }
    
    private fun addWelcomeMessage() {
        val welcomeMessage = """
            🤖 ¡Hola! Soy tu Asistente AI para BadAI
            
            Puedo ayudarte con:
            • Escaneo automático de objetivos
            • Análisis inteligente de vulnerabilidades
            • Ejecución de herramientas de pentesting
            • Generación de reportes
            • Configuración del sistema
            
            💬 EJEMPLOS DE COMANDOS:
            • "scan https://example.com"
            • "find php sites with sql injection"
            • "install sqlmap"
            • "generate report"
            • "help"
            
            ⚠️ RECORDATORIO LEGAL:
            Solo usar en objetivos autorizados para auditorías de seguridad.
            
            ¿En qué puedo ayudarte hoy?
        """.trimIndent()
        
        addChatMessage(ChatMessage("assistant", welcomeMessage, System.currentTimeMillis()))
    }
    
    private fun sendMessage() {
        val message = messageInput.text.toString().trim()
        if (message.isEmpty()) return
        
        // Agregar mensaje del usuario
        addChatMessage(ChatMessage("user", message, System.currentTimeMillis()))
        messageInput.text.clear()
        
        // Mostrar estado de procesamiento
        showProcessingState(true)
        
        // Procesar comando con AI
        lifecycleScope.launch {
            try {
                aiAssistant.processCommand(message).collect { response ->
                    handleAIResponse(response)
                }
            } catch (e: Exception) {
                handleError("Error procesando comando: ${e.message}")
            } finally {
                showProcessingState(false)
            }
        }
    }
    
    private fun handleAIResponse(response: AIResponse) {
        when (response) {
            is AIResponse.Success -> {
                addChatMessage(ChatMessage("assistant", response.message, System.currentTimeMillis()))
                updateStatus("Comando completado")
            }
            
            is AIResponse.Error -> {
                addChatMessage(ChatMessage("assistant", "❌ ${response.message}", System.currentTimeMillis()))
                updateStatus("Error en comando")
            }
            
            is AIResponse.Warning -> {
                addChatMessage(ChatMessage("assistant", "⚠️ ${response.message}", System.currentTimeMillis()))
                updateStatus("Advertencia")
            }
            
            is AIResponse.Progress -> {
                updateStatus("${response.message} (${response.percentage}%)")
                updateProgress(response.percentage)
            }
            
            is AIResponse.Thinking -> {
                updateStatus(response.message)
            }
        }
    }
    
    private fun handleError(error: String) {
        addChatMessage(ChatMessage("assistant", "❌ $error", System.currentTimeMillis()))
        updateStatus("Error")
    }
    
    private fun addChatMessage(message: ChatMessage) {
        chatMessages.add(message)
        chatAdapter.notifyItemInserted(chatMessages.size - 1)
        chatRecyclerView.scrollToPosition(chatMessages.size - 1)
    }
    
    private fun showProcessingState(isProcessing: Boolean) {
        progressBar.visibility = if (isProcessing) View.VISIBLE else View.GONE
        sendButton.isEnabled = !isProcessing
        
        if (isProcessing) {
            updateStatus("Procesando comando...")
        } else {
            updateStatus("Listo")
        }
    }
    
    private fun updateStatus(status: String) {
        statusText.text = status
    }
    
    private fun updateProgress(percentage: Int) {
        progressBar.progress = percentage
    }
    
    private fun startVoiceInput() {
        // TODO: Implementar reconocimiento de voz
        Toast.makeText(requireContext(), "Reconocimiento de voz próximamente", Toast.LENGTH_SHORT).show()
    }
}

/**
 * Adaptador para el chat del AI Assistant
 */
class ChatAdapter(private val messages: List<ChatMessage>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val layout = if (viewType == 0) R.layout.item_chat_user else R.layout.item_chat_assistant
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ChatViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(messages[position])
    }
    
    override fun getItemCount(): Int = messages.size
    
    override fun getItemViewType(position: Int): Int {
        return if (messages[position].role == "user") 0 else 1
    }
    
    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.messageText)
        private val timeText: TextView = itemView.findViewById(R.id.timeText)
        
        fun bind(message: ChatMessage) {
            messageText.text = message.content
            timeText.text = formatTime(message.timestamp)
        }
        
        private fun formatTime(timestamp: Long): String {
            val date = java.util.Date(timestamp)
            val format = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
            return format.format(date)
        }
    }
}

/**
 * Clase de datos para mensajes de chat
 */
data class ChatMessage(
    val role: String, // "user" o "assistant"
    val content: String,
    val timestamp: Long
)