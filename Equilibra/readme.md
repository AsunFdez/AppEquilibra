Estructura de proyecto y propósito de archivos

app/src/main/java/asunfdiez/com/equilibra/
├── activities/
│   ├── OnboardingActivity.java    - UI y navegación del flujo de onboarding; coordina vistas y llama a controllers/utils.
│   ├── MainActivity.java          - Actividad principal de la aplicación (después onboarding/main app UI).
│   └── SplashActivity.java        - Pantalla de inicio y animaciones de presentación.
├── controllers/
│   └── OnboardingController.java  - Persistencia y carga de datos de onboarding (SharedPreferences, serialización JSON).
├── models/
│   ├── OnboardingData.java        - DTO que contiene los datos recopilados durante el onboarding (emoción, objetivo, cantidad, fecha).
│   ├── EmotionalState.java        - Modelo inmutable que representa un estado emocional (id, texto, emoji, mensaje de validación).
│   └── QuestionOption.java        - Modelo simple para opciones de pregunta (id y texto).
├── adapters/
│   └── QuestionOptionAdapter.java - Adapter/gestor de opciones que infla items y notifica selección a la activity.
└── utils/
    ├── AnimationHelper.java       - Centraliza animaciones reutilizables (entradas, salidas, botones, progreso).
    ├── OnboardingConstants.java   - Constantes de configuración (duraciones, delays, formatos, ids).
    ├── InputValidator.java        - Reglas de validación estáticas para formularios y cantidades.
    ├── DatePickerHelper.java      - Encapsula lógica del DatePickerDialog y formateo de fechas.
    └── SavingGoalValidator.java   - TextWatcher y lógica para habilitar/inhabilitar botón de envío.

Otros ficheros y recursos importantes
-----------------------------------
build.gradle.kts                    - Script de Gradle para configuración del módulo raíz.
settings.gradle.kts                 - Configuración de proyecto multi-módulo (incluye el módulo app).
local.properties                    - Propiedades locales (SDK paths, etc.).
gradle.properties                   - Propiedades compartidas de Gradle.
gradlew / gradlew.bat               - Wrappers de Gradle para construir en cualquier equipo.

app/
├── build.gradle.kts                - Configuración específica del módulo `app` (dependencias, flavours, signing).
├── proguard-rules.pro              - Reglas de ofuscación para builds de release.
└── src/
    ├── main/
    │   ├── AndroidManifest.xml    - Declaración de actividades, permisos y componentes.
    │   ├── java/                  - Código fuente (estructura descrita arriba).
    │   └── res/
    │       ├── layout/            - XML de layouts (activity_onboarding, step_emotional, step_validation, step_question, step_saving_goal, step_summary, step_mission, activity_splash, item_question_option, etc.).
    │       ├── values/
    │       │   ├── strings.xml    - Textos de la UI (localizables). Añadir los strings sugeridos para el onboarding.
    │       │   └── colors, styles - Temas y colores de la app.
    │       └── drawable/          - Recursos gráficos (ic_onb_target, ic_onb_clock, vectores generados de Lucide, bg_splash, etc.).
    └── test/                       - Tests unitarios (si existen).

Lucide-icons/                       - SVG fuente; contiene iconos por carpetas (se usa script para convertir a drawables).
convert-lucide-to-drawables.ps1     - Script PowerShell para convertir SVGs Lucide a XML vector drawables en `res/drawable`.

plantUML/                           - Diagramas PlantUML (arquitectura, onboarding flows, etc.).

Notas y recomendaciones
-----------------------
- Mantener `OnboardingActivity` enfocada en UI y navegación; mover toda lógica de negocio a `controllers` o `utils`.
- Mantener strings en `res/values/strings.xml` (localización y evitar hardcode).
- Usar `OnboardingController` para todas las operaciones de persistencia y evitar lógica de SharedPreferences en activities.
- Reutilizar `AnimationHelper` desde otras activities si se comparte comportamiento de animación.
- Agregar tests unitarios para `InputValidator` y `OnboardingController.convertToJson/convertFromJson`.
 - Agregar tests unitarios para `InputValidator` y `OnboardingController.convertToJson/convertFromJson`.

SplashActivity
----------------
`SplashActivity.java` - Implementa la pantalla de bienvenida y las animaciones de entrada. Se encarga de:
- Mostrar logo y animaciones (ObjectAnimator, AnimatorSet, ValueAnimator) en `res/layout/activity_splash.xml`.
- Decidir la navegación inicial (por ejemplo: si `OnboardingController.isOnboardingCompleted()` es true -> `MainActivity`, si no -> `OnboardingActivity`).
- Mantener la lógica de animaciones reutilizables en `AnimationHelper` o encapsular helpers específicos para la pantalla splash.

Res (carpeta `app/src/main/res/`)
------------------------------
`res/` organiza los recursos usados por la UI:

- `layout/` : XML de layouts (activity_onboarding.xml, activity_splash.xml, step_*.xml, item_question_option.xml, etc.).
- `values/` : `strings.xml`, `colors.xml`, `styles.xml` (temas y textos localizables). Añadir los strings sugeridos para onboarding y splash.
- `drawable/` : Imágenes y `vector` drawables (ic_onb_target, ic_onb_clock, iconos y fondos usados por splash). Los vectores pueden provenir de `Lucide-icons/` mediante `convert-lucide-to-drawables.ps1`.
- `anim/` (opcional) : Recursos XML de animación si prefieres declararlos en recursos en lugar de código.

Consejo: mantener los recursos de la pantalla splash ligeros y con tamaños adecuados para evitar retrasos en el arranque visual.




