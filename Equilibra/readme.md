# Proyecto Equilibra

## Estado actual del proyecto

Este documento refleja el estado observado del proyecto a fecha de 2026-06-17.
La aplicación es un proyecto Android nativo en Java con Gradle Kotlin DSL y utiliza una arquitectura basada en actividades, view models, repositorios y Room para el almacenamiento local.

Además, el proyecto ya incluye integración con Firebase para autenticación y sincronización de datos, así como un flujo de sincronización en segundo plano mediante WorkManager.

---

## Objetivo de la app

Equilibra es una app para el seguimiento financiero personal y el bienestar económico. Permite:

- registrar ingresos y gastos;
- analizar el comportamiento mensual;
- visualizar metas de ahorro;
- gestionar perfil y configuración;
- mostrar intervenciones suaves cuando el gasto o el patrón de consumo lo requiere;
- sincronizar datos locales con Firebase.

---

## Arquitectura actual

### Capas principales

- **Activities / UI**: pantallas principales y configuración.
- **ViewModels**: preparación de datos para la interfaz.
- **Controllers**: lógica de negocio específica.
- **Repositories**: acceso a datos locales y sincronización remota.
- **Database / Room**: persistencia local mediante `AppDatabase` y DAOs.
- **Workers**: operaciones en segundo plano para sincronización.

### Tecnologías principales

- Android SDK / Jetpack
- Java 21
- Room 2.6.1
- ViewModel + LiveData
- WorkManager
- Firebase Auth
- Firebase Firestore
- Google Services plugin

---

## Persistencia local

El proyecto usa Room para la capa local. El componente principal es:

- `AppDatabase`: base de datos principal.
- `ExpenseDao`: acceso a gastos.
- Otros DAOs y repositorios gestionan perfil, metas, presupuestos, logros y otras entidades.

La capa de repositorios centraliza el acceso a datos y, en el caso de los gastos, también dispara la sincronización con la nube tras cambios.

---

## Sincronización con Firebase

La sincronización con Firebase ya está integrada en el proyecto.

### Piezas relevantes

- `CloudSyncRepository`
  - gestiona la autenticación;
  - usa inicio de sesión anónimo si no hay usuario activo;
  - sube datos locales a Firestore.

- `CloudSyncWorker`
  - ejecuta la sincronización en segundo plano con WorkManager;
  - reintenta la tarea cuando la red no está disponible.

- `ExpenseRepository`
  - tras insertar, actualizar o eliminar un gasto, programa una sincronización inmediata.

### Flujo esperado

1. El usuario crea, modifica o elimina un gasto.
2. El repositorio local actualiza la base de datos.
3. Se programa una sincronización con `CloudSyncRepository`.
4. WorkManager ejecuta la tarea cuando hay red disponible.
5. Firebase Auth / Firestore reciben los datos sincronizados.

> Nota: la infraestructura base ya existe; conviene revisar reintentos, consistencia de datos y sincronización de entidades adicionales.

---

## Estructura general del proyecto

```text
app/
├── src/main/
│   ├── java/asunfdiez/com/equilibra/
│   │   ├── activities/
│   │   ├── adapters/
│   │   ├── controllers/
│   │   ├── database/
│   │   ├── models/
│   │   ├── repositories/
│   │   ├── utils/
│   │   ├── viewmodels/
│   │   ├── views/
│   │   └── workers/
│   ├── res/
│   │   ├── layout/
│   │   ├── drawable/
│   │   ├── values/
│   │   └── xml/
│   └── AndroidManifest.xml
├── build.gradle.kts
└── google-services.json
```

---

## Pantallas principales

La app incluye pantallas para:

- splash / inicio;
- onboarding;
- dashboard principal;
- añadir y listar gastos;
- análisis financiero;
- logros;
- perfil y configuración;
- ajustes de presupuesto, apariencia y notificaciones;
- intervención suave.

---

## Configuración de Firebase

La configuración actual del proyecto incluye:

- `google-services.json` en la carpeta `app/`;
- plugin `com.google.gms.google-services` en Gradle;
- dependencias de Firebase Auth, Firestore y Analytics;
- uso de Firebase para la sincronización remota.

---

## Cómo trabajar con el proyecto

### Requisitos

- Android Studio con soporte para Java 21 y Android Gradle Plugin.
- SDK Android suficiente para la configuración del proyecto.
- Acceso a la configuración de Firebase correspondiente.

### Revisión recomendada

1. validar la coherencia de datos locales y remotos;
2. revisar el comportamiento de reintentos cuando cambia la red;
3. comprobar sincronización de perfil, presupuesto y metas;
4. revisar concurrencia y errores de UI asociados a llamadas asíncronas.

---

## Resumen rápido

- La app sigue siendo una aplicación Android Java con Room.
- Ya existe una capa de sincronización con Firebase.
- `CloudSyncRepository` y `CloudSyncWorker` forman la base del sistema de nube.
- `ExpenseRepository` dispara la sincronización al editar gastos.

<!--

├── activities/                    # Activities de la aplicación
│   ├── SplashActivity.java
│   ├── OnboardingActivity.java
│   ├── IntermediateUserActivity.java
│   ├── AdvancedUserActivity.java
│   ├── MainActivity.java
│   ├── AddExpenseActivity.java
│   ├── SoftInterventionActivity.java
│   ├── AnalysisActivity.java
│   └── AchievementsActivity.java
│
├── controllers/                   # Lógica de negocio
│   ├── ExpenseController.java
│   ├── BudgetController.java
│   ├── AchievementController.java
│   ├── AnalyticsController.java
│   └── OnboardingController.java
│
├── models/                        # Modelos de datos
│   ├── Expense.java
│   ├── Budget.java
│   ├── Achievement.java
│   ├── UserProfile.java
│   ├── EmotionalState.java
│   ├── QuestionOption.java
│   ├── Category.java
│   └── Mission.java
│
├── adapters/                      # Adapters para RecyclerView
│   ├── QuestionOptionAdapter.java
│   ├── ExpenseAdapter.java
│   ├── CategoryAdapter.java
│   ├── AchievementAdapter.java
│   └── OnboardingPagerAdapter.java
│
├── database/                      # SQLite Database
│   ├── DatabaseHelper.java
│   ├── ExpenseDAO.java
│   ├── BudgetDAO.java
│   └── AchievementDAO.java
├── repositories/                  # para obtener datos
├── utils/                         # Utilidades y helpers
│   ├── SharedPrefsManager.java
│   ├── DateUtils.java
│   ├── CurrencyUtils.java
│   ├── ValidationUtils.java
│   ├── AnimationUtils.java
│   └── Constants.java
│   ├── InputValidator.java
│   ├── DatePickerHelper.java
│   └── SavingGoalValidator.java
└── views/                         # Custom Views
    ├── FeatureCard.java
    ├── CircularProgressView.java
    ├── CategoryIconView.java
    └── WaveLoadingView.java
```

## Estructura de Recursos

```
res/
├── anim/                          # Animaciones XML
│   ├── fade_in.xml
│   ├── fade_out.xml
│   ├── slide_in_right.xml
│   ├── slide_in_left.xml
│   ├── slide_out_right.xml
│   ├── slide_out_left.xml
│   ├── scale_up.xml
│   └── bounce.xml
│
├── drawable/                      # Drawables y shapes
│   ├── bg_button_primary.xml
│   ├── bg_button_secondary.xml
│   ├── bg_card.xml
│   ├── bg_circular_progress.xml
│   └── ... (otros drawables)
│
├── layout/                        # Layouts XML
│   ├── activity_splash.xml
│   ├── activity_onboarding.xml
│   ├── activity_intermediate_user.xml
│   ├── activity_advanced_user.xml
│   ├── activity_main.xml
│   ├── activity_add_expense.xml
│   ├── activity_soft_intervention.xml
│   ├── activity_analysis.xml
│   ├── activity_achievements.xml
│   ├── item_expense.xml
│   ├── item_category.xml
│   ├── item_achievement.xml
│   └── component_*.xml (componentes reutilizables)
│
├── values/
│   ├── colors.xml
│   ├── strings.xml
│   ├── dimens.xml
│   ├── styles.xml
│   └── themes.xml
│
└── 

## FLUJO DE NAVEGACIÓN

SplashActivity
    ├── OnboardingActivity (si no completado)
    │       └── MainActivity (al finalizar)
    │
    └── MainActivity (si completado)
            ├── AddExpenseActivity
            │       └── volver a MainActivity
            │
            ├── SoftInterventionActivity
            │       ├── volver a MainActivity
            │       └── AnalysisActivity
            │
            ├── AnalysisActivity
            │       └── volver a MainActivity
            │
            └── AchievementsActivity
                    └── volver a MainActivity

## Flujo de Funcionamiento añadir gasto

### Usuario ingresa importe y categoría. Presiona "Añadir Gasto"**

AddExpenseActivity 
    ↓
btnSaveExpense.click() → viewModel.saveExpense()
    ↓
Repository.isImportantExpense(amount)?
    ├─ SÍ → showImportantExpenseDialog()
    └─ NO ↓
Repository.detectExpensePattern(category)?
    ├─ SÍ → showSmartReminderDialog()
    └─ NO ↓
Repository.shouldShowConfirmation(amount)?
    ├─ SÍ → showConfirmExpenseDialog()
    └─ NO ↓
saveExpenseDirectly()

## LISTADO DE ARCHIVOS

### **1. ACTIVITIES (7 archivos)**

#### SplashActivity.java
- **Ubicación:** `/SplashActivity.java`
- **Propósito:** Pantalla de inicio con logo y animaciones
- **Características:**
  - Animaciones de entrada (fade in, scale up)
  - Navegación automática después de 2.5 segundos
  - Determina si mostrar Onboarding o MainActivity
  - Usa SharedPrefsManager para verificar estado

####  OnboardingActivity.java
- **Ubicación:** `/OnboardingActivity.java`
- **Propósito:** Cuestionario inicial de 6 pasos
- **Características:**
  - Flujo de 6 pantallas: Emocional, Validación, Preguntas (2), Meta de Ahorro, Resumen, Misión
  - Transiciones animadas entre pasos
  - Validación de campos
  - Selector de fecha
  - Guarda perfil de usuario en SharedPreferences

####  MainActivity.java
- **Ubicación:** `/MainActivity.java`
- **Propósito:** Dashboard principal de la app
- **Características:**
  - Resumen financiero (ingresos, gastos, balance)
  - Lista de gastos recientes (RecyclerView)
  - Progreso de meta de ahorro
  - Indicador de bienestar financiero
  - FAB para añadir gastos
  - Acceso a Análisis y Logros

####  AddExpenseActivity.java
- **Ubicación:** `/AddExpenseActivity.java`
- **Propósito:** Formulario para añadir gastos/ingresos
- **Características:**
  - RadioGroup para seleccionar tipo (gasto/ingreso)
  - Campos: monto, descripción, categoría, fecha
  - AutoCompleteTextView con categorías predefinidas
  - DatePicker para selección de fecha
  - Validación en tiempo real
  - Guarda en SQLite vía ExpenseController

####  SoftInterventionActivity.java
- **Ubicación:** `/SoftInterventionActivity.java`
- **Propósito:** Intervención suave cuando gastos son altos
- **Características:**
  - Diseño tipo diálogo
  - Mensaje personalizado según nivel de gastos
  - 3 tips de bienestar financiero
  - Animaciones de entrada escalonadas
  - Marca intervención como vista para no repetir

####  AnalysisActivity.java
- **Ubicación:** `/AnalysisActivity.java`
- **Propósito:** Análisis financiero con estadísticas
- **Características:**
  - Totales de ingresos, gastos y balance del mes
  - Gráficos de barras por categoría
  - 3 insights personalizados
  - Comparación con mes anterior
  - Promedio de gasto diario
  - Animaciones de barras y números

####  AchievementsActivity.java
- **Ubicación:** `/AchievementsActivity.java`
- **Propósito:** Sistema de logros gamificado
- **Características:**
  - Nivel actual del usuario (Principiante a Maestro)
  - Barra de progreso hacia siguiente nivel
  - Grid de logros (RecyclerView en modo Grid)
  - Logros bloqueados/desbloqueados con visual diferenciado
  - Total de puntos acumulados


### **2. CONTROLLERS (3 archivos)**

#### ExpenseController.java
- **Ubicación:** `/ExpenseController.java`
- **Propósito:** Controlador de gastos e ingresos
- **Métodos principales:**
  - `addExpense()` - Añade gasto/ingreso
  - `getAllExpenses()` - Obtiene todos los gastos
  - `getRecentExpenses(limit)` - Gastos recientes limitados
  - `getExpensesThisMonth()` - Gastos del mes actual
  - `getTotalIncomeThisMonth()` - Total de ingresos
  - `getTotalExpensesThisMonth()` - Total de gastos
  - `getCategoryAnalysis()` - Análisis por categoría
  - `getAverageDailyExpense()` - Promedio diario
  - `deleteExpense(id)` - Elimina gasto

####  WellbeingController.java
- **Ubicación:** `/WellbeingController.java`
- **Propósito:** Controlador de bienestar financiero
- **Métodos principales:**
  - `shouldShowIntervention()` - Determina si mostrar intervención
  - `getInterventionMessage()` - Genera mensaje personalizado
  - `markInterventionAsSeen()` - Marca como vista
  - `getWellbeingScore()` - Calcula score 0-100
  - `areExpensesHigh()` - Verifica gastos altos
- **Lógica:**
  - Umbral de 80% ingresos vs gastos
  - Verifica gastos concentrados en 3 días
  - No muestra intervención más de 1 vez al día

#### AchievementController.java
- **Ubicación:** `/AchievementController.java`
- **Propósito:** Sistema de logros y niveles
- **Métodos principales:**
  - `getAllAchievements()` - Lista de 10 logros
  - `unlockAchievement(key)` - Desbloquea logro
  - `checkAndUnlockAchievements()` - Verifica y desbloquea automáticamente
  - `getTotalPoints()` - Suma total de puntos
  - `getUserLevel()` - Nivel actual (5 niveles)
  - `getProgressToNextLevel()` - Porcentaje de progreso
- **Logros definidos:**
  1. Primer Paso (10 pts)
  2. Una Semana Fuerte (50 pts)
  3. Seguimiento Activo (25 pts)
  4. Mes Completo (100 pts)
  5. Equilibrio Financiero (75 pts)
  6-8. Metas de ahorro (50-150 pts)
  9. Consciente (60 pts)
  10. Maestro del Presupuesto (200 pts)


### **3. MODELS (5 archivos)**

####  Expense.java
- **Ubicación:** `/Expense.java`
- **Atributos:**
  - `id` (long)
  - `type` (String: "expense" o "income")
  - `amount` (double)
  - `description` (String)
  - `category` (String)
  - `date` (String: dd/MM/yyyy)
  - `timestamp` (long)
  - `notes` (String)
- **Métodos:** getters, setters, `isExpense()`, `isIncome()`

####  UserProfile.java
- **Ubicación:** `/UserProfile.java`
- **Atributos:**
  - `emotionalState` (String)
  - `mainConcern` (String)
  - `timeAvailable` (String)
  - `savingGoal` (String)
  - `savingAmount` (double)
  - `savingDate` (String)
  - `createdAt` (long)
- **Propósito:** Almacena datos del onboarding

#### Achievement.java
- **Ubicación:** `/Achievement.java`
- **Atributos:**
  - `id` (int)
  - `title` (String)
  - `description` (String)
  - `points` (int)
  - `key` (String)
  - `unlocked` (boolean)
  - `unlockedAt` (long)
- **Propósito:** Representa un logro gamificado

#### InterventionMessage.java
- **Ubicación:** `/InterventionMessage.java`
- **Atributos:**
  - `title` (String)
  - `message` (String)
  - `tips` (String[3])
  - `severity` (String: "low", "medium", "high")
- **Propósito:** Mensaje de intervención suave personalizado

#### CategoryAnalysis.java
- **Ubicación:** `/CategoryAnalysis.java`
- **Atributos:**
  - `category` (String)
  - `amount` (double)
  - `count` (int)
  - `percentage` (double)
- **Propósito:** Datos para gráficos de categorías


### **4. ADAPTERS (2 archivos)**

####  ExpenseAdapter.java
- **Ubicación:** `/ExpenseAdapter.java`
- **Propósito:** Adapter para RecyclerView de gastos
- **Características:**
  - ViewHolder pattern
  - Formato de moneda (NumberFormat)
  - Colores diferentes para gastos (rojo) e ingresos (verde)
  - Click listeners para item y botón eliminar
  - Animaciones de lista

####  AchievementAdapter.java
- **Ubicación:** `/AchievementAdapter.java`
- **Propósito:** Adapter para RecyclerView de logros (Grid)
- **Características:**
  - ViewHolder pattern
  - Visual diferente para logros bloqueados/desbloqueados
  - Opacity 0.5 para bloqueados
  - Iconos personalizados por tipo de logro
  - Grid layout (2 columnas)


### **5. DATABASE (1 archivo)**

#### ✅ DatabaseHelper.java
- **Ubicación:** `/DatabaseHelper.java`
- **Propósito:** Gestor de base de datos SQLite
- **Características:**
  - Singleton pattern
  - Una tabla: `expenses`
  - **CRUD completo:**
    - CREATE: `insertExpense()`
    - READ: `getAllExpenses()`, `getExpenseById()`, etc.
    - UPDATE: `updateExpense()`
    - DELETE: `deleteExpense()`
  - **Consultas de análisis:**
    - Totales por mes
    - Análisis por categoría
    - Promedio diario
    - Gastos de últimos N días
  - **Versión:** 1
  - **Nombre DB:** equilibra.db

---

### **6. UTILS (3 archivos)**

####  SharedPrefsManager.java
- **Ubicación:** `/SharedPrefsManager.java`
- **Propósito:** Gestor de SharedPreferences
- **Características:**
  - Singleton pattern
  - Almacena onboarding completado (boolean)
  - Guarda/lee UserProfile (JSON con Gson)
  - Maneja intervenciones vistas (timestamp)
  - Gestiona logros desbloqueados (boolean por key)
  - Métodos de reset para testing
- **Nombre:** EquilibraPrefs

####  AnimationHelper.java
- **Ubicación:** `/AnimationHelper.java`
- **Propósito:** Helper para animaciones
- **Métodos de animación:**
  - `fadeIn()`, `fadeOut()`
  - `scaleUp()`, `scaleDown()`
  - `pulse()` (efecto latido)
  - `slideInFromRight()`, `slideOutToLeft()`, etc.
  - `shake()` (para errores)
  - `bounce()`
- **Transiciones de Activity:**
  - `transitionFade()`
  - `transitionSlideLeft()`, `transitionSlideRight()`
  - `transitionSlideUp()`, `transitionSlideDown()`
- **Duración:** Usa Constants

####  Constants.java
- **Ubicación:** `/Constants.java`
- **Propósito:** Constantes globales
- **Contenido:**
  - Duraciones de animaciones (200, 400, 600ms, 2500ms splash)
  - Colores en formato String y int
  - Nombre y versión de DB
  - Umbrales de intervención (80%, 3 días)
  - Categorías predefinidas (gastos e ingresos)
  - Niveles de usuario (Principiante a Maestro)
  - Puntos por nivel (0, 50, 150, 300, 500)
  - Tipos de transacción
  - Estados emocionales
  - Formatos de fecha
  - Request codes e intent extras

#### AdviceManager.java ⭐ NUEVO
- **Ubicación:** `/AdviceManager.java`
- **Propósito:** Gestor de consejos financieros diarios rotatorios
- **Características:**
  - 25 consejos diferentes (recopilados y mejorados)
  - **Un consejo diferente cada día** (basado en fecha)
  - Nunca repite el consejo del día anterior
  - Implementación determinística (mismo día = mismo consejo siempre)
  - Método para obtener consejo aleatorio
  - Método para verificar si hoy es diferente a ayer
- **Métodos principales:**
  - `getDailyAdvice(context)` - Consejo del día actual
  - `getYesterdayAdvice(context)` - Consejo del día anterior
  - `getAdviceByIndex(context, index)` - Consejo específico (0-24)
  - `getRandomAdvice(context)` - Consejo aleatorio
  - `isDifferentFromYesterday(context)` - Verifica si cambió
- **Algoritmo de rotación:**
  - Calcula número de día desde epoch (01/01/1970)
  - Usa módulo 25 para obtener índice cíclico
  - Garantiza que ciclo completo = 25 días (un mes)

## Estado actual frente a las recomendaciones de Android Studio

- Lenguaje: Java
- Arquitectura actual: principalmente Activities con lógica de UI y negocio mezclada
- Persistencia: `SharedPreferences` a través de `SharedPrefsManager` y SQLite
- ViewBinding/DataBinding: implementado
- MVVM: implementado
- Room/SQLite: implementado
- LifecycleObserver: implementado

---

## 🎯 SISTEMA DE CONSEJOS DIARIOS 

### Descripción
Sistema que muestra un consejo financiero diferente cada día. Los usuarios verán:
- **Día 1:** Bienvenida especial
- **Sin gastos registrados:** Mensaje para registrar hoy
- **Con actividad:** Consejo rotatorio del día (1 diferente cada 25 días)

### Archivos
1. **`strings_advice.xml`** - 25 consejos mejorados
2. **`AdviceManager.java`** - Lógica de rotación diaria
3. **`AdviceViewModel.java`** - ViewModel centralizado 

### Integración en UI
Data Binding automático via `AdviceViewModel`:
```xml
<!-- En beginner_user.xml -->
<asunfdiez.com.equilibra.views.FeatureCard
    app:cardTitle='@{adviceViewModel.dailyAdvice.title}'
    app:cardDescription='@{adviceViewModel.dailyAdvice.description}'/>
```

---


## Dependencias importantes

- AndroidX AppCompat
- Material Components
- DataBinding 
- Room

-->
