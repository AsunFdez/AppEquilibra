package asunfdiez.com.equilibra.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import asunfdiez.com.equilibra.models.Expense;
import asunfdiez.com.equilibra.models.UserProfile;
import asunfdiez.com.equilibra.utils.Constants;

@RunWith(AndroidJUnit4.class)
public class ExpenseDatabaseTest {
    private AppDatabase db;
    private ExpenseDao expenseDao;
    private UserProfileDao userProfileDao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();

        // Base de datos en memoria para testing (se destruye al finalizar la prueba)
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();

        expenseDao = db.expenseDao();
        userProfileDao = db.userProfileDao();

        // Crear un perfil de usuario (requerido por la clave foránea de la tabla de gastos)
        UserProfile profile = new UserProfile();
        profile.setId(1);
        profile.setName("Usuario Test");
        userProfileDao.insertOrUpdate(profile);
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void testInsertAndReadExpense() throws Exception {
        // Preparar el gasto de prueba
        Expense expense = new Expense();
        expense.setUserId(1);
        expense.setType(Constants.TYPE_EXPENSE);
        expense.setAmount(45.90);
        expense.setDescription("Cena de prueba");
        expense.setDate("18/10/2026");
        expense.setTime("21:00");
        expense.setTimestamp(System.currentTimeMillis());

        // Insertar en la base de datos
        long id = expenseDao.insertExpense(expense);
        assertTrue("El ID generado debe ser mayor que 0", id > 0);

        // Leer el gasto guardado por su ID
        Expense readExpense = expenseDao.getExpenseById(id);

        // Aserciones de JUnit
        assertNotNull("El gasto recuperado no debe ser nulo", readExpense);
        assertEquals("El importe debe coincidir", 45.90, readExpense.getAmount(), 0.001);
        assertEquals("La descripción debe coincidir", "Cena de prueba", readExpense.getDescription());
    }

    @Test
    public void testUpdateExpense() throws Exception {
        Expense expense = new Expense();
        expense.setUserId(1);
        expense.setType(Constants.TYPE_EXPENSE);
        expense.setAmount(20.0);
        expense.setDescription("Café");
        expense.setDate("19/10/2026");
        expense.setTime("10:00");
        expense.setTimestamp(System.currentTimeMillis());

        long id = expenseDao.insertExpense(expense);
        expense.setId(id);
        expense.setAmount(25.5);
        expense.setDescription("Café y tostada");

        expenseDao.updateExpenses(expense);

        Expense updated = expenseDao.getExpenseById(id);
        assertNotNull("El gasto actualizado no debe ser nulo", updated);
        assertEquals("El importe actualizado debe coincidir", 25.5, updated.getAmount(), 0.001);
        assertEquals("La descripción actualizada debe coincidir", "Café y tostada", updated.getDescription());
    }

    @Test
    public void testDeleteExpense() throws Exception {
        Expense expense = new Expense();
        expense.setUserId(1);
        expense.setType(Constants.TYPE_EXPENSE);
        expense.setAmount(10.0);
        expense.setDescription("Eliminar esto");
        expense.setDate("19/10/2026");
        expense.setTime("11:00");
        expense.setTimestamp(System.currentTimeMillis());

        long id = expenseDao.insertExpense(expense);
        assertNotNull(expenseDao.getExpenseById(id));

        expenseDao.deleteExpense(id);
        assertNull("El gasto eliminado debe ser nulo", expenseDao.getExpenseById(id));
    }

    @Test
    public void testGetExpensesInRange() throws Exception {
        long now = System.currentTimeMillis();
        long oneHourAgo = now - 3600000;
        long oneHourLater = now + 3600000;

        Expense expense = new Expense();
        expense.setUserId(1);
        expense.setType(Constants.TYPE_EXPENSE);
        expense.setAmount(15.0);
        expense.setDescription("Gasto en rango");
        expense.setDate("19/10/2026");
        expense.setTime("12:00");
        expense.setTimestamp(now);

        expenseDao.insertExpense(expense);

        List<Expense> expensesInRange = expenseDao.getExpensesInRange(oneHourAgo, oneHourLater);
        assertNotNull(expensesInRange);
        assertEquals("Debe haber 1 gasto en el rango", 1, expensesInRange.size());
        assertEquals("Gasto en rango", expensesInRange.get(0).getDescription());
    }

    @Test
    public void testInsertListOfExpenses() throws Exception {
        List<Expense> list = new java.util.ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Expense e = new Expense();
            e.setUserId(1);
            e.setType(Constants.TYPE_EXPENSE);
            e.setAmount(i * 10.0);
            e.setDescription("Gasto " + i);
            e.setDate("19/10/2026");
            e.setTime("12:0" + i);
            e.setTimestamp(System.currentTimeMillis() + i);
            list.add(e);
        }

        expenseDao.insertListOfExpenses(list);
        List<Expense> all = expenseDao.getAllExpenses();
        assertNotNull(all);
        assertEquals("Debe haber 3 gastos insertados", 3, all.size());
    }

    @Test
    public void testPopulateDummyData() {
        Context context = ApplicationProvider.getApplicationContext();
        AppDatabase db = AppDatabase.getInstance(context);
        
        // Ejecutar la función dummyData() explícitamente mediante este test concreto
        AppDatabase.dummyData();

        List<Expense> allExpenses = db.expenseDao().getAllExpenses();
        assertNotNull("Los gastos dummy no deben ser nulos", allExpenses);
        assertTrue("Debe haberse poblado una cantidad significativa de gastos históricos para análisis", allExpenses.size() > 10);

        // Crear y guardar el archivo CSV en la ubicación de SharedPreferences
        try {
            java.io.File sharedPrefsDir = new java.io.File(context.getApplicationInfo().dataDir, "shared_prefs");
            if (!sharedPrefsDir.exists()) {
                sharedPrefsDir.mkdirs();
            }
            java.io.File csvFile = new java.io.File(sharedPrefsDir, "equilibra_dummy_expenses.csv");
            
            try (java.io.FileWriter writer = new java.io.FileWriter(csvFile, java.nio.charset.StandardCharsets.UTF_8)) {
                writer.write("type,amount,category,description,date,time,timestamp\n");
                for (Expense e : allExpenses) {
                    writer.write(String.format(java.util.Locale.US, "%s,%.2f,%d,\"%s\",%s,%s,%d\n",
                            e.getType() != null ? e.getType() : "expense",
                            e.getAmount(),
                            e.getCategory() != null ? e.getCategory() : 1L,
                            e.getDescription() != null ? e.getDescription().replace("\"", "\"\"") : "",
                            e.getDate() != null ? e.getDate() : "",
                            e.getTime() != null ? e.getTime() : "12:00",
                            e.getTimestamp()
                    ));
                }
                writer.flush();
            }
            android.util.Log.d("ExpenseDatabaseTest", "Dummy CSV created at SharedPreferences dir: " + csvFile.getAbsolutePath());
        } catch (Exception e) {
            android.util.Log.e("ExpenseDatabaseTest", "Error creating dummy CSV file", e);
        }
    }
}
