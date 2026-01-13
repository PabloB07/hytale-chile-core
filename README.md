# Hytale Chile Core

Un plugin para servidores de Hytale que demuestra la funcionalidad básica de los plugins.

## Características

- `/example info` - Mostrar información del plugin
- `/example tools` - Darle al jugador un conjunto de herramientas (una vez por jugador)
- Evento de interacción con puertas - Recibir un objeto de puerta cuando abres una puerta por primera vez

## Building

```bash
./gradlew build
```

El JAR compilado se encuentra en `build/libs/`.

## Instalación

1. Copia el archivo JAR al directorio `mods/` de tu servidor
2. Reinicia el servidor

## Requisitos

- Hytale Server con soporte para plugins
- Java 25+
- `HytaleServer.jar` en la raíz del proyecto para la compilación

## Documentación

Para una guía completa sobre cómo crear plugins para Hytale, consulta la documentación:

👉 [Hytale Plugin Development Guide](https://hytale-docs.pages.dev/getting-started/introduction/)

## Licencia

MIT
