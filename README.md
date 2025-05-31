# BADAI - SQLMap Automator for Android

**⚠️ ADVERTENCIA LEGAL ⚠️**

Esta aplicación está diseñada exclusivamente para:
- Pruebas de penetración autorizadas
- Auditorías de seguridad en sistemas propios
- Fines educativos en entornos controlados

**EL USO NO AUTORIZADO DE ESTA HERRAMIENTA ES ILEGAL Y PUEDE RESULTAR EN CONSECUENCIAS LEGALES GRAVES.**

## Descripción

BADAI (Base de Datos Automática de Inyección) es una aplicación Android que automatiza completamente el uso de SQLMap para obtener bases de datos de manera eficiente y organizada.

## Características

- ✅ Automatización completa de SQLMap
- ✅ Interfaz intuitiva para configuración de parámetros
- ✅ Templates predefinidos para diferentes tipos de ataques
- ✅ Gestión de resultados y exportación
- ✅ Integración con Termux
- ✅ Base de datos local para historial
- ✅ Advertencias legales integradas

## Requisitos

- Android 7.0+ (API 24+)
- Termux instalado
- Python 3.x en Termux
- SQLMap instalado en Termux

## Instalación

1. Instalar Termux desde F-Droid
2. Configurar Python y SQLMap en Termux:
   ```bash
   pkg update && pkg upgrade
   pkg install python git
   git clone https://github.com/sqlmapproject/sqlmap.git
   ```
3. Instalar BADAI APK
4. Conceder permisos necesarios

## Uso Responsable

Esta herramienta debe usarse únicamente con autorización explícita del propietario del sistema objetivo. El desarrollador no se hace responsable del uso indebido de esta aplicación.

## Licencia

MIT License - Ver LICENSE file para detalles.