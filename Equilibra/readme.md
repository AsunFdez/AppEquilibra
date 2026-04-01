# Proyecto Equilibra

## Estructura actual

app/src/main/java/asunfdiez/com/equilibra/

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
│
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


## LISTADO COMPLETO DE ARCHIVOS

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

## Estado actual frente a las recomendaciones de Android Studio

- Lenguaje: Java
- Arquitectura actual: principalmente Activities con lógica de UI y negocio mezclada
- Persistencia: `SharedPreferences` a través de `SharedPrefsManager`
- ViewBinding/DataBinding: DataBinding parcial en `MainActivity`, `OnboardingActivity` aún usa `findViewById`
- MVVM: no implementado actualmente
- Room/SQLite: no implementado actualmente
- LifecycleObserver: no implementado actualmente

## Próximos objetivos

1. Refactorizar a MVVM con `ViewModel` y repositorios.
2. Usar ViewBinding en todas las Activities.
3. Añadir Room para datos estructurados y DAOs.
4. Usar `@NonNull` y `@Nullable`.
5. Mantener `Activities` limpias y relegar la lógica de negocio a `ViewModels`/controladores.

## Dependencias importantes

- AndroidX AppCompat
- Material Components
- DataBinding (actualmente usada en `MainActivity`)




