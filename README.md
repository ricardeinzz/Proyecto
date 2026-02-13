# 🏴‍☠️ Pirate Adventure - 2D Java Platformer

Bienvenido a **Pirate Adventure**, un motor de juego de plataformas 2D completo desarrollado íntegramente en Java. Este proyecto demuestra la implementación de físicas, estados de juego, gestión de recursos y un bucle de juego (game loop) optimizado de 120 FPS.

## ✨ Características Principales
- **Game States**: Sistema robusto de estados (Menú, Jugando, Opciones, Créditos, Selección de Personaje).
- **Físicas y Colisiones**: Motor de físicas personalizado para saltos, gravedad y colisiones precisas con el entorno.
- **Gráficos**: Uso de `Graphics2D` con soporte para escalado (`SCALE = 1.5f`) y spritesheets dinámicos.
- **Audio**: Sistema dedicado para efectos de sonido y música de fondo.
- **Ciclo de Juego**: Implementación de `Thread` con separación entre actualizaciones de lógica (UPS: 200) y renderizado (FPS: 120).

## 🚀 Cómo Ejecutar el Proyecto
### Requisitos
- **JDK 17** o superior.
- Algún IDE de Java (Eclipse, IntelliJ IDEA, VS Code).

### Instalación
1. Clona el repositorio:
   ```bash
   git clone https://github.com/ricardeinzz/Proyecto.git
   ```
2. Abre el proyecto en tu IDE preferido.
3. Ejecuta la clase `MainClass` situada en `src/main/MainClass.java`.

## 📂 Estructura del Código
- `src/main/`: Clases núcleo (Juego, Ventana, Panel).
- `src/gamestates/`: Lógica para cada estado del juego.
- `src/entidades/`: Jugadores, enemigos y objetos interactuables.
- `src/niveles/`: Gestión de mapas y datos de nivel.
- `res/`: Todos los assets gráficos (PNG) y de audio.

## 🛠️ Tecnologías Usadas
- Java Standard Edition (Swing/AWT para gráficos).
- Programación Orientada a Objetos (POO).
- Patrones de diseño para gestión de estados y recursos.

---
Desarrollado con ❤️ por [ricardeinzz](https://github.com/ricardeinzz)
