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


## MainActivity: según el nivel del usuario:
    - muestra diferentes widgets/funcionalidades (utilizando tres layouts activity_main.xml, activity_intermediate_user.xml y activity_advanced_user.xml
    - Ajustar la complejidad de los gráficos
    - Ofrecer consejos específicos al nivel

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




