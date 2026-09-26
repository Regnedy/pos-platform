# Plataforma POS Inteligente - FASE 1: Arquitectura Base

**Estado:** Esqueleto Funcional - Frontend Android  
**Versión:** 1.0.0  
**Última actualización:** Septiembre 2026

---

## 📱 Estructura del Proyecto Android

```
android/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── kotlin/com/neveriaventa/pos/
│   │       │   ├── MainActivity.kt              # Punto de entrada
│   │       │   ├── PosApplication.kt            # App initialization con Hilt
│   │       │   ├── data/
│   │       │   │   ├── local/
│   │       │   │   │   ├── db/
│   │       │   │   │   │   ├── AppDatabase.kt  # Room Database
│   │       │   │   │   │   └── Converters.kt   # Type converters
│   │       │   │   │   └── dao/
│   │       │   │   │       └── DAOs.kt         # Todas las DAOs
│   │       │   │   └── model/
│   │       │   │       └── Models.kt           # Entity definitions
│   │       │   ├── di/
│   │       │   │   └── DatabaseModule.kt       # Dependency injection
│   │       │   ├── ui/
│   │       │   │   ├── screen/
│   │       │   │   │   ├── auth/
│   │       │   │   │   │   └── LoginScreen.kt
│   │       │   │   │   └── pos/
│   │       │   │   │       └── PosScreen.kt    # Interfaz POS principal
│   │       │   │   ├── viewmodel/
│   │       │   │   │   ├── AuthViewModel.kt
│   │       │   │   │   └── PosViewModel.kt
│   │       │   │   └── theme/
│   │       │   │       └── Theme.kt
│   │       │   └── sync/
│   │       │       └── SyncService.kt          # (Placeholder)
│   │       └── AndroidManifest.xml
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── settings.gradle.kts
└── .gitignore
```

---

## 🏗️ Componentes Implementados

### 1. **Base de Datos Local (Room)**
- **AppDatabase:** 16 entidades definidas
- **Converters:** Serialización JSON y timestamps
- **DAOs:** Acceso tipo-seguro a todas las entidades

**Entidades principales:**
- Empresa, Sucursal, Usuario, Rol
- Producto, Sabor, Presentación, Precio
- Venta, DetalleVenta
- Evento, ParticipanteEvento
- Inventario, MovimientoInventario
- Gasto, Proveedor

### 2. **Autenticación (Login Screen)**
- Campo email y contraseña
- Validación básica
- Indicador de carga
- Manejo de errores
- ViewModel con AuthViewModel

### 3. **Interfaz POS Rápido**
- **Header:** Info de punto, vendedor, estado conexión
- **Grid de Sabores:** 2x3 botones grandes
- **Carrito:** Resumen items y total
- **Botones de acción:** LIMPIAR y COBRAR

### 4. **Inyección de Dependencias (Hilt)**
- DatabaseModule para acceso a DAOs
- ViewModel factories automáticas
- Ciclo de vida correcto

### 5. **Navegación**
- NavHost con rutas: "login" → "pos"
- Manejo de autenticación
- Pop-up de login al completar

---

## 🎨 UI/UX Implementado

### Pantalla de Login
```
┌─────────────────────────┐
│   NEVERIA VENTA POS     │
│                         │
│  ┌──────────────────┐   │
│  │ Email            │   │
│  ├──────────────────┤   │
│  │ Contraseña       │   │
│  ├──────────────────┤   │
│  │ [INGRESAR]       │   │
│  └──────────────────┘   │
└─────────────────────────┘
```

### Pantalla POS
```
┌──────────────────────────────────────────┐
│ PUNTO: Centro | VENDEDOR: Juan           │
├──────────────────────────────────────────┤
│  [VAINILLA]  [LIMÓN]                    │
│  [MARACUYÁ]  [NUEZ]                     │
│  [PITAYA]    [BESO ÁNGEL]               │
│                │ CARRITO        │
│                │ 2x V $40 $80   │
│                │ 1x L $55 $55   │
│                │ TOTAL:   $135  │
│                │ [LIMPIAR COBRAR]
└──────────────────────────────────────────┘
```

---

## 🔧 Tecnología Stack

| Capa | Tecnología |
|------|-----------|
| UI | Jetpack Compose |
| Arquitectura | MVVM + Clean Architecture |
| DB Local | Room Database |
| Async | Coroutines |
| DI | Hilt |
| Networking | Retrofit (preparado) |
| Build | Gradle Kotlin DSL |

---

## 📋 Dependencias Principales

```kotlin
// Jetpack Compose
androidx.compose.ui:ui:1.6.4
androidx.compose.material3:material3:1.1.2

// Room
androidx.room:room-runtime:2.6.1
androidx.room:room-ktx:2.6.1

// Coroutines
kotlinx.coroutines:kotlinx-coroutines-android:1.7.3

// Hilt (DI)
com.google.dagger:hilt-android:2.50

// Networking
com.squareup.retrofit2:retrofit:2.10.0
com.squareup.okhttp3:okhttp:4.11.0
```

---

## 🚀 Cómo Compilar y Ejecutar

### Requisitos
- Android Studio Giraffe o superior
- JDK 17+
- Android SDK 34

### Pasos
1. Abrir proyecto en Android Studio
2. Sync Gradle files
3. Ejecutar en emulador o dispositivo físico

```bash
./gradlew assembleDebug
```

---

## 📊 Modelo de Datos

### Relaciones Principales
```
Empresa (1) ──→ (N) Sucursal
         ├──→ (N) Usuario
         ├──→ (N) Producto
         ├──→ (N) Evento
         └──→ (N) Proveedor

Usuario (1) ──→ (N) Venta
        ├──→ (N) Evento (participante)
        └──→ (1) Rol

Producto (1) ──→ (N) Sabor
        ├──→ (N) Presentación
        └──→ (1) Categoría

Venta (1) ──→ (N) DetalleVenta
    └──→ (1) Evento (opcional)
```

---

## 🔄 Flujo de Sincronización (Preparado)

```
App Local (Offline)
    ↓
    Acumula eventos
    ↓
Detecta conexión
    ↓
SyncService → Backend API
    ↓
Marca como sincronizado
```

---

## ✅ Checklist FASE 1

- [x] Estructura modular del proyecto
- [x] Database Room con 16 entidades
- [x] DAOs con queries tipo-seguras
- [x] Inyección de dependencias (Hilt)
- [x] Pantalla de Login
- [x] Pantalla POS básica
- [x] ViewModels con StateFlow
- [x] Navegación entre pantallas
- [x] Tema Material 3
- [x] Build configuration
- [ ] Tests unitarios
- [ ] Testing de DB
- [ ] Backend API (FASE 2)
- [ ] Sincronización real (FASE 2)

---

## 🎯 Siguiente: FASE 2

**Objetivo:** POS Operativo End-to-End

**Módulos a agregar:**
1. Selección de tamaño en popup
2. Persistencia real de ventas
3. Recibos/impresión
4. Sincronización de ventas
5. Validaciones y manejo de errores

**Duración estimada:** 4 semanas

---

## 📝 Notas de Desarrollo

### Características Preparadas pero No Completadas
- WorkManager para background sync (referenciado en manifest)
- QR code scanning (dependencia agregada)
- DataStore para preferencias
- Logging interceptor en OkHttp

### TODO Comentarios en Código
- `// TODO: Call API backend` en AuthViewModel
- `// TODO: Show size picker dialog` en PosViewModel
- `// TODO: Verificar password hash` en login
- `// TODO: Save session token` en SharedPreferences

### Decisiones Arquitectónicas
1. **MVVM + Clean:** Separación clara de responsabilidades
2. **Room Database:** Local-first, offline-ready
3. **Jetpack Compose:** UI declarativa, moderna
4. **Coroutines:** Async sin callbacks
5. **Hilt:** DI automática, scope-aware

---

## 🐛 Conocidos Limitaciones (Fase 1)

1. No hay backend real (mockeo local)
2. No hay sincronización automática
3. No hay autenticación segura (contraseña plain)
4. No hay manejo de carga en login
5. UI es básica (sin iconos, sin animaciones)
6. No hay pruebas unitarias

---

## 📞 Soporte

Para cambios en requisitos o issues, contactar al equipo CIO.

---

**Preparado para:** Aprobación y transición a FASE 2  
**Próxima revisión:** Cuando se complete POS operativo
