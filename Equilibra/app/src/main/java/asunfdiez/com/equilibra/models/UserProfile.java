package asunfdiez.com.equilibra.models;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import asunfdiez.com.equilibra.utils.Constants;


/**
 * Entidad que almacena la información detallada del perfil del usuario.
 * 
 * <p>Contiene datos personales, preferencias configuradas durante el onboarding 
 * (estado emocional, preocupaciones financieras), indicadores de progreso en el uso 
 * de la app y estadísticas acumuladas. Al estar limitado a un único registro (ID fijo), 
 * actúa como la configuración global del usuario.</p>
 */
@Entity(tableName = Constants.TABLE_USER_PROFILE)
public class UserProfile {

    @PrimaryKey
    private long id = 1; // Solo habrá un perfil, usamos un ID fijo.
    
    // Nombre del usuario
    private String name;
    
    // Correo electrónico de contacto
    private String email;
    
    // Teléfono (opcional)
    private String phone;
    
    // Hash de la contraseña para seguridad básica local
    private String passwordHash;

    // Estado emocional detectado en el onboarding
    private EmotionalState emotionalState;
    
    // Identificador de la principal preocupación financiera
    private String mainConcernId;
    
    // Descripción de la principal preocupación financiera
    private String mainConcern;
    
    // Tiempo disponible declarado por el usuario para gestionar finanzas
    private String timeAvailable;
    
    // Marcas de tiempo de creación y actualización del perfil
    private long createdAt;
    private long updatedAt;

    // Ingresos mensuales declarados
    private double monthlyIncome;

    // Niveles declarados de conocimiento y hábitos financieros (1-10)
    private int financialKnowledge;
    private int budgetExperience;
    private int savingsHabit;

    // Contadores para el seguimiento de la racha y progreso semanal
    private int dayCount = 1;
    private int weekProgress = 0;
    private int weekGoal = 7;
    private int weekCount = 0;

    // Marcas de tiempo para control de reinicios y última conexión
    private long lastResetTimestamp = System.currentTimeMillis();
    private long lastConnectionTimestamp = System.currentTimeMillis();


    public UserProfile() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public long getId() {
        return id;
    }

    public void setId(long a_id) {
        id = a_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String a_name) {
        name = a_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String a_email) {
        email = a_email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String a_phone) {
        phone = a_phone;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String a_passwordHash) {
        passwordHash = a_passwordHash;
    }

    public EmotionalState getEmotionalState() {
        return emotionalState;
    }

    public void setEmotionalState(EmotionalState a_emotion) {
        emotionalState = a_emotion;
    }

    public String getMainConcern() {
        return mainConcern;
    }

    public void setMainConcern(String a_mainConcern) {
        mainConcern = a_mainConcern;
    }

    public String getMainConcernId() {
        return mainConcernId;
    }

    public void setMainConcernId(String mainConcernId) {
        this.mainConcernId = mainConcernId;
    }

    public String getTimeAvailable() {
        return timeAvailable;
    }

    public void setTimeAvailable(String a_timeAvailable) {
        timeAvailable = a_timeAvailable;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long a_createdAt) {
        createdAt = a_createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long a_updatedAt) {
        updatedAt = a_updatedAt;
    }

    public double getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(double a_monthlyIncome) {
        monthlyIncome = a_monthlyIncome;
    }

    public int getFinancialKnowledge() {
        return financialKnowledge;
    }

    public void setFinancialKnowledge(int a_financialKnowledge) {
        financialKnowledge = a_financialKnowledge;
    }

    public int getBudgetExperience() {
        return budgetExperience;
    }

    public void setBudgetExperience(int a_budgetExperience) {
        budgetExperience = a_budgetExperience;
    }

    public int getSavingsHabit() {
        return savingsHabit;
    }

    public void setSavingsHabit(int a_savingsHabit) {
        savingsHabit = a_savingsHabit;
    }

    public int getDayCount() {
        return dayCount;
    }

    public void setDayCount(int dayCount) {
        this.dayCount = dayCount;
    }

    public int getWeekProgress() {
        return weekProgress;
    }

    public void setWeekProgress(int weekProgress) {
        this.weekProgress = weekProgress;
    }

    public int getWeekGoal() {
        return weekGoal;
    }

    public void setWeekGoal(int weekGoal) {
        this.weekGoal = weekGoal;
    }

    public int getWeekCount() {
        return weekCount;
    }

    public void setWeekCount(int weekCount) {
        this.weekCount = weekCount;
    }

    public long getLastResetTimestamp() {
        return lastResetTimestamp;
    }

    public void setLastResetTimestamp(long lastResetTimestamp) {
        this.lastResetTimestamp = lastResetTimestamp;
    }

    public long getLastConnectionTimestamp() {
        return lastConnectionTimestamp;
    }

    public void setLastConnectionTimestamp(long lastConnectionTimestamp) {
        this.lastConnectionTimestamp = lastConnectionTimestamp;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                ", emotionalState=" + emotionalState +
                ", mainConcernId='" + mainConcernId + '\'' +
                ", mainConcern='" + mainConcern + '\'' +
                ", timeAvailable='" + timeAvailable + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", monthlyIncome=" + monthlyIncome +
                ", financialKnowledge=" + financialKnowledge +
                ", budgetExperience=" + budgetExperience +
                ", savingsHabit=" + savingsHabit +
                ", dayCount=" + dayCount +
                ", weekProgress=" + weekProgress +
                ", weekGoal=" + weekGoal +
                '}';
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfile that = (UserProfile) o;
        return id == that.id &&
                Double.compare(that.monthlyIncome, monthlyIncome) == 0 &&
                financialKnowledge == that.financialKnowledge &&
                budgetExperience == that.budgetExperience &&
                savingsHabit == that.savingsHabit &&
                dayCount == that.dayCount &&
                weekProgress == that.weekProgress &&
                weekGoal == that.weekGoal &&
                weekCount == that.weekCount &&
                lastResetTimestamp == that.lastResetTimestamp &&
                lastConnectionTimestamp == that.lastConnectionTimestamp &&
                java.util.Objects.equals(name, that.name) &&
                java.util.Objects.equals(email, that.email) &&
                java.util.Objects.equals(phone, that.phone) &&
                java.util.Objects.equals(passwordHash, that.passwordHash) &&
                java.util.Objects.equals(emotionalState, that.emotionalState) &&
                java.util.Objects.equals(mainConcernId, that.mainConcernId) &&
                java.util.Objects.equals(mainConcern, that.mainConcern) &&
                java.util.Objects.equals(timeAvailable, that.timeAvailable);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, name, email, phone, passwordHash, emotionalState, mainConcernId, mainConcern, timeAvailable, monthlyIncome, financialKnowledge, budgetExperience, savingsHabit, dayCount, weekProgress, weekGoal, weekCount, lastResetTimestamp, lastConnectionTimestamp);
    }
}
