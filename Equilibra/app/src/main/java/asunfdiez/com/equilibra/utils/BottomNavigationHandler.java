package asunfdiez.com.equilibra.utils;

import android.view.View;

/**
 * Interfaz que define las acciones de navegación de la barra inferior (Bottom Navigation).
 * 
 * <p>Esta interfaz es implementada por las actividades principales para gestionar 
 * la transición entre las secciones clave de la aplicación: Inicio, Análisis, 
 * Registro de Gastos, Logros y Ajustes.</p>
 */
public interface BottomNavigationHandler {
    // Navega a la pantalla de inicio (Dashboard)
    void onHomeClick();
    
    // Navega a la pantalla de estadísticas y análisis
    void onAnalysisClick();
    
    // Inicia el flujo para añadir un nuevo gasto
    void onAddExpenseClick(View view);
    
    // Navega a la pantalla de logros del usuario
    void onAchievementsClick();
    
    // Navega a la pantalla de configuración general
    void onSettingsClick();

}
