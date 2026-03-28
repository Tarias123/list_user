# List User App

Una aplicación Android moderna desarrollada en Kotlin que permite gestionar una lista de usuarios con autenticación múltiple y sincronización en tiempo real con Firebase.

---

## Descripción general

List User App es una aplicación de gestión de usuarios que combina una interfaz limpia con Material Design 3 y un backend robusto basado en Firebase. Permite a los usuarios autenticarse de múltiples formas, ver una lista de usuarios registrados en tiempo real y administrar su cuenta con opciones de registro y recuperación de contraseña.

---

## Características principales

- **Lista de usuarios en tiempo real** — RecyclerView conectado a Firebase Firestore con `addSnapshotListener` para actualizaciones instantáneas
- **Carga de imágenes** — Integración con Picasso para mostrar fotos de perfil de forma eficiente
- **Autenticación con Email/Password** — Registro e inicio de sesión con Firebase Authentication
- **Autenticación con Google** — Integración con Google Credential Manager
- **Autenticación con Facebook** — Integración con el SDK oficial de Facebook
- **Registro de nuevos usuarios** — Formulario con validaciones completas (formato de email, coincidencia de contraseñas, campos requeridos)
- **Recuperación de contraseña** — Envío de correo de restablecimiento mediante Firebase Auth
- **Diseño moderno con Material Design 3** — Tema claro con colores índigo, campos redondeados, tarjetas con elevación y botones con esquinas redondeadas
- **Efectos de error con animación** — Campos en rojo con animación de vibración (shake) y mensajes de error específicos por tipo de fallo

---

## Tecnologías utilizadas

| Tecnología | Uso |
|---|---|
| **Kotlin** | Lenguaje principal de desarrollo |
| **Android Studio** | IDE de desarrollo |
| **Firebase Firestore** | Base de datos en tiempo real |
| **Firebase Authentication** | Gestión de identidad y sesiones |
| **Firebase Storage** | Almacenamiento de archivos |
| **Picasso** | Carga y caché de imágenes |
| **Facebook Android SDK** | Autenticación con Facebook |
| **Google Credential Manager** | Autenticación con Google |
| **Material Design 3** | Sistema de diseño visual |
| **RecyclerView** | Listado eficiente de usuarios |

---

## Requisitos previos

Antes de clonar y ejecutar el proyecto asegúrate de tener lo siguiente:

- **Android Studio** Hedgehog o superior instalado
- **JDK 11** o superior
- **Archivo `google-services.json`** generado desde Firebase Console y colocado en la carpeta `app/`
- **Firebase Console** con los siguientes servicios activados:
  - Authentication (Email/Password, Google y Facebook habilitados)
  - Firestore Database (con colección `usuarios`)
  - Storage
- **Facebook Developers** — App configurada con el Key Hash del dispositivo registrado
- **SHA-1** del keystore de debug registrado en Firebase Console para Google Sign-In

---

## Estructura del proyecto

```
list_user/
├── app/
│   ├── google-services.json          # Configuración de Firebase (no incluido en git)
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/list_user/
│       │   ├── MainActivity.kt       # Lista de usuarios + cerrar sesión
│       │   ├── LoginActivity.kt      # Login con Email, Google y Facebook
│       │   ├── RegisterActivity.kt   # Registro de nuevos usuarios
│       │   ├── ForgotPasswordActivity.kt  # Recuperación de contraseña
│       │   ├── adapter/
│       │   │   └── UserAdapter.kt    # Adapter del RecyclerView
│       │   └── model/
│       │       └── User.kt           # Data class del modelo de usuario
│       └── res/
│           ├── anim/
│           │   └── shake.xml         # Animación de vibración para errores
│           ├── drawable/
│           │   ├── ic_user.xml       # Ícono vectorial de usuario
│           │   ├── ic_google.xml     # Ícono de Google
│           │   └── ic_facebook.xml   # Ícono de Facebook
│           ├── layout/
│           │   ├── activity_login.xml
│           │   ├── activity_register.xml
│           │   ├── activity_forgot_password.xml
│           │   ├── activity_main.xml
│           │   └── item_user.xml     # Layout de cada tarjeta de usuario
│           ├── menu/
│           │   └── menu_main.xml
│           └── values/
│               ├── colors.xml
│               ├── strings.xml
│               ├── themes.xml
│               └── facebook.xml      # Credenciales de Facebook SDK
├── gradle/
│   └── libs.versions.toml            # Version Catalog de dependencias
├── build.gradle.kts
└── settings.gradle.kts
```

---

## Instalación y configuración

### 1. Clonar el repositorio

```bash
git clone https://github.com/tu-usuario/list_user.git
cd list_user
```

### 2. Configurar Firebase

1. Ve a [Firebase Console](https://console.firebase.google.com/) y crea un proyecto
2. Registra tu app Android con el package name `com.example.list_user`
3. Descarga el archivo `google-services.json` y colócalo en `app/google-services.json`
4. Activa en **Authentication** los métodos: Email/Password, Google y Facebook
5. Crea la base de datos en **Firestore** con la colección `usuarios`
6. Registra el SHA-1 de tu keystore en la configuración de la app en Firebase

Para obtener el SHA-1 del keystore de debug, ejecuta en la terminal de Android Studio:

```bash
./gradlew signingReport
```

### 3. Configurar Facebook

1. Ve a [Facebook Developers](https://developers.facebook.com/) y crea una app
2. Agrega el Key Hash de tu dispositivo en **Configuración → Básica → Android**
3. Habilita **Facebook Login** en los productos de tu app
4. Actualiza `app/src/main/res/values/facebook.xml` con tu App ID y Client Token

### 4. Sincronizar y ejecutar

1. Abre el proyecto en Android Studio
2. Haz clic en **Sync Project with Gradle Files** (ícono del elefante)
3. Conecta un dispositivo o inicia el emulador
4. Presiona **Run** (`Shift + F10`)

---

## Estructura de datos en Firestore

Cada documento en la colección `usuarios` tiene la siguiente estructura:

```json
{
  "nombre": "Juan Pérez",
  "email": "juan@example.com",
  "fotoUrl": "https://..."
}
```

El `id` del documento corresponde al `uid` de Firebase Authentication.

---

## Capturas de pantalla

Las capturas de pantalla del proyecto se encuentran en la carpeta `/screenshots`.

| Login | Registro | Lista de Usuarios |
|---|---|---|
| ![Login](screenshots/login.png) | ![Registro](screenshots/register.png) | ![Usuarios](screenshots/main.png) |

---

## Licencia

Este proyecto es de uso educativo y personal.
