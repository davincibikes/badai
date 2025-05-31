# BadAI - AI-Powered SQLMap Automation Suite

**⚠️ LEGAL WARNING ⚠️**

This application is designed exclusively for:
- Authorized penetration testing
- Security audits on owned systems
- Educational purposes in controlled environments
- Bug bounty programs with valid scope

**UNAUTHORIZED USE OF THIS TOOL IS ILLEGAL AND MAY RESULT IN SERIOUS LEGAL CONSEQUENCES.**

## 🚀 Description

BadAI (Base de Datos Automática de Inyección) is a comprehensive Android application that combines AI-powered intelligence with automated SQL injection testing and database exploitation. It features a revolutionary chat-based control system and 100% autonomous operation capabilities.

## ✨ Key Features

### 🤖 AI-Powered Intelligence
- **Multi-Model Chat Interface**: Control entire app through natural language
  - Ollama (local privacy)
  - Groq (fast inference)
  - OpenAI GPT models
  - HuggingFace models
- **Intelligent Target Analysis**: AI-powered vulnerability assessment
- **100% Automated Mode**: Fully autonomous operation with learning capabilities
- **Smart Attack Vector Selection**: AI chooses optimal exploitation methods

### 🎯 Comprehensive Exploitation Suite
- **SQLMap Integration**: Full automation with intelligent parameter detection
- **API Exploitation**: GraphQL and REST API vulnerability testing
- **NoSQL & LDAP Injection**: MongoDB, CouchDB, LDAP attacks
- **Chain Exploitation**: Multi-stage attack sequences
- **Subdomain Discovery**: DNS enumeration and service discovery
- **Cryptocurrency Attacks**: Blockchain and crypto wallet exploitation

### 🔍 Advanced Discovery & Automation
- **Multi-Engine Dorking**: Google, Bing, DuckDuckGo, Shodan integration
- **Automatic Target Discovery**: Continuous background scanning
- **Proxy Scraping**: Automatic proxy discovery and validation
- **Termux Integration**: Automatic tool installation and management

### 🛡️ Security & Compliance
- **Sensitive Data Extraction**: Credit cards, banking data, documents
- **Encryption**: AES-256 encryption for extracted data
- **Legal Compliance**: Built-in audit logging and violation tracking
- **Responsible Disclosure**: Automated vulnerability reporting

### 🎨 User Experience
- **Dark Minimalist UI**: Material 3 theme optimized for security professionals
- **Chat-Based Control**: Natural language commands for all functions
- **Real-time Monitoring**: Live scan progress and result visualization
- **Comprehensive Reporting**: Detailed vulnerability reports with remediation

## 🏗️ Architecture

### Core AI Modules
```
ai/
├── IntelligentAnalyzer.kt    # AI-powered target analysis
├── AIAssistant.kt           # Multi-model chat interface
└── FullyAutomatedMode.kt    # Autonomous operation engine
```

### Exploitation Framework
```
core/
├── SQLMapExecutor.kt        # Core SQLMap integration
├── APIExploiter.kt         # GraphQL/REST API attacks
├── AlternativeDBExtractor.kt # NoSQL/LDAP injection
├── ChainExploiter.kt       # Multi-stage attacks
├── SubdomainExploiter.kt   # DNS enumeration
└── CryptoExploiter.kt      # Cryptocurrency attacks
```

### Automation & Discovery
```
automation/
├── AutoDorker.kt           # Multi-engine dorking
├── AutomaticDorkingService.kt # Continuous discovery
├── TermuxIntegration.kt    # External tool management
└── ProxyScraper.kt         # Proxy discovery
```

## 📱 Requirements

- **Android**: 7.0+ (API 24+)
- **Storage**: 500MB+ free space
- **Network**: Internet connection for AI models
- **Termux**: For external tool integration
- **Permissions**: Storage, Network, Termux access

## 🛠️ Installation

### Method 1: APK Installation (Recommended)
```bash
# Download latest APK
wget https://github.com/davincibikes/badai/releases/latest/download/badai.apk

# Install APK
adb install badai.apk
```

### Method 2: Build from Source
```bash
# Clone repository
git clone https://github.com/davincibikes/badai.git
cd badai

# Build APK
./gradlew assembleDebug

# Install
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Termux Setup (Automatic)
The app automatically installs required tools:
```bash
# Tools installed automatically:
- SQLMap
- Nmap
- Subfinder
- HTTPx
- Nuclei
- Custom exploitation scripts
```

## 🎯 Usage

### Quick Start with AI Chat
1. **Launch App**: Accept legal disclaimer
2. **AI Setup**: Choose preferred AI model
3. **Chat Control**: Use natural language commands

```
Chat Examples:
"Scan example.com for SQL injection vulnerabilities"
"Find admin panels using Google dorks"
"Enable aggressive mode and use rotating proxies"
"Extract all credit card data from last scan"
"Generate comprehensive vulnerability report"
"Learn from successful attacks and improve strategy"
```

### 100% Automated Mode
```
"Enable fully automated mode"
"Continuously discover and attack new targets"
"Learn from each successful exploitation"
"Adapt attack strategies based on results"
```

### Manual Operation
1. **Target Discovery**: Dorking or manual input
2. **AI Analysis**: Automatic vulnerability assessment
3. **Attack Selection**: AI-recommended vectors
4. **Execution**: Real-time monitoring
5. **Exploitation**: Automated data extraction
6. **Reporting**: Comprehensive vulnerability reports

## 🔧 Configuration

### AI Models Setup
```kotlin
// Ollama (Local)
aiAssistant.configureOllama("llama2", "localhost:11434")

// Groq (Fast)
aiAssistant.configureGroq("your-api-key")

// OpenAI
aiAssistant.configureOpenAI("your-api-key", "gpt-4")

// HuggingFace
aiAssistant.configureHuggingFace("your-api-key", "model-name")
```

### Advanced Settings
```kotlin
// Automated Mode Configuration
automatedMode.configure(
    learningEnabled = true,
    aggressiveMode = false,
    proxyRotation = true,
    maxConcurrentScans = 5
)
```

## 📊 Current Status

### ✅ Completed Features
- ✅ Complete AI-powered automation system
- ✅ Multi-model chat interface with natural language control
- ✅ Comprehensive exploitation suite (6+ attack vectors)
- ✅ Intelligent target analysis and attack selection
- ✅ 100% automated mode with learning capabilities
- ✅ Termux integration for external tools
- ✅ Dark minimalist UI with Material 3 theme
- ✅ Complete Android app structure with navigation
- ✅ Legal compliance system with audit logging
- ✅ Sensitive data extraction with encryption
- ✅ Multi-engine dorking and target discovery
- ✅ Proxy scraping and validation system

### 🔄 In Progress
- 🔄 Build system optimization (compilation issues)
- 🔄 Room database integration (temporarily disabled)
- 🔄 APK generation and testing
- 🔄 Voice recognition for AI chat

### 🎯 Roadmap
- 🎯 Advanced machine learning algorithms
- 🎯 More AI model integrations
- 🎯 Enhanced cryptocurrency attack vectors
- 🎯 Cloud-based result synchronization
- 🎯 Team collaboration features

## 🤖 AI Chat Commands

### Basic Operations
```
"Scan [target] for vulnerabilities"
"Use [attack-type] on [target]"
"Enable/disable [feature]"
"Show results for [target]"
"Generate report"
```

### Advanced Automation
```
"Learn from successful attacks"
"Adapt strategy based on target type"
"Optimize attack parameters"
"Discover similar targets"
"Chain multiple attack vectors"
```

### Configuration
```
"Use [proxy-list] for scanning"
"Set aggressive mode [on/off]"
"Configure [ai-model] with [settings]"
"Enable continuous discovery"
"Set scan intensity to [level]"
```

## ⚖️ Legal & Ethical Use

### ✅ Authorized Uses
- Penetration testing with written authorization
- Security audits on owned systems
- Educational research in controlled environments
- Bug bounty programs within defined scope
- Security training and certification

### ❌ Prohibited Uses
- Unauthorized access to systems
- Malicious attacks or data theft
- Violation of computer crime laws
- Any illegal activities
- Testing without explicit permission

### 📋 Compliance Features
- Audit logging of all activities
- Legal compliance checker
- Automatic violation detection
- Responsible disclosure templates
- Evidence preservation tools

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

### Development Setup
```bash
# Clone and setup
git clone https://github.com/davincibikes/badai.git
cd badai

# Install dependencies
./gradlew build

# Run tests
./gradlew test
```

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🔗 Links

- **Repository**: [GitHub](https://github.com/davincibikes/badai)
- **Documentation**: [Wiki](https://github.com/davincibikes/badai/wiki)
- **Issues**: [Bug Reports](https://github.com/davincibikes/badai/issues)
- **Releases**: [Download APK](https://github.com/davincibikes/badai/releases)

## 📞 Support

For support, questions, or responsible disclosure of security issues:
- Open an issue on GitHub
- Contact: security@badai.dev
- Discord: [BadAI Community](https://discord.gg/badai)

## 🙏 Acknowledgments

- SQLMap Project for the core injection engine
- Android Security Community
- AI/ML researchers and developers
- Ethical hacking community
- Open source contributors

---

**⚠️ Disclaimer**: The developers of BadAI are not responsible for any misuse of this tool. This software is provided "as is" without warranty. Users are solely responsible for ensuring their use complies with applicable laws and regulations. Use responsibly and ethically.