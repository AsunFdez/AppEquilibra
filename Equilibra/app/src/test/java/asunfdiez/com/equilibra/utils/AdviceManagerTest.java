package asunfdiez.com.equilibra.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import asunfdiez.com.equilibra.models.Advice;

/**
 * Pruebas unitarias para AdviceManager
 *
 * Verifica:
 * - Rotación diaria correcta
 * - 25 consejos diferentes
 * - Determinismo (mismo día = mismo consejo)
 * - Un consejo diferente cada día
 */
@RunWith(AndroidJUnit4.class)
public class AdviceManagerTest {

    private Context context;

    @Before
    public void setUp() {
        // Contexto para acceder a los strings del proyecto
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    /**
     * Test 1: Verificar que getDailyAdvice() no retorna null
     */
    @Test
    public void testGetDailyAdviceNotNull() {
        Advice advice = AdviceManager.getDailyAdvice(context);
        assertNotNull("Daily advice no debe ser null", advice);
        assertNotNull("Title no debe ser null", advice.title());
        assertNotNull("Description no debe ser null", advice.description());
    }

    /**
     * Test 2: Verificar que hay 25 consejos disponibles
     */
    @Test
    public void testTotalAdviceCount() {
        int total = AdviceManager.getTotalAdvices();
        assertEquals("Debe haber exactamente 25 consejos", 25, total);
    }

    /**
     * Test 3: Verificar que getAdviceByIndex() funciona para todos los índices
     */
    @Test
    public void testGetAdviceByIndex() {
        for (int i = 0; i < AdviceManager.getTotalAdvices(); i++) {
            Advice advice = AdviceManager.getAdviceByIndex(context, i);
            assertNotNull("Consejo en índice " + i + " no debe ser null", advice);
            assertNotNull("Title en índice " + i + " no debe ser null", advice.title());
            assertTrue("Title debe tener contenido", !advice.title().isEmpty());
        }
    }

    /**
     * Test 4: Verificar que getAdviceByIndex() retorna valores diferentes para índices diferentes
     */
    @Test
    public void testAdviceIndexesDifferent() {
        Advice advice0 = AdviceManager.getAdviceByIndex(context, 0);
        Advice advice1 = AdviceManager.getAdviceByIndex(context, 1);
        Advice advice24 = AdviceManager.getAdviceByIndex(context, 24);

        // Los consejos en índices diferentes deben tener titles diferentes
        assertNotEquals("Índice 0 y 1 deben ser diferentes", advice0.title(), advice1.title());
        assertNotEquals("Índice 0 y 24 deben ser diferentes", advice0.title(), advice24.title());
        assertNotEquals("Índice 1 y 24 deben ser diferentes", advice1.title(), advice24.title());
    }

    /**
     * Test 5: Verificar que hoy ≠ ayer
     */
    @Test
    public void testTodayDifferentFromYesterday() {
        boolean isDifferent = AdviceManager.isDifferentFromYesterday(context);
        assertTrue("El consejo de hoy debe ser diferente al de ayer", isDifferent);
    }

    /**
     * Test 6: Verificar que getYesterdayAdvice() no es nulo
     */
    @Test
    public void testGetYesterdayAdviceNotNull() {
        Advice yesterday = AdviceManager.getYesterdayAdvice(context);
        assertNotNull("Yesterday advice no debe ser null", yesterday);
    }

    /**
     * Test 7: Verificar que getRandomAdvice() no es nulo
     */
    @Test
    public void testGetRandomAdviceNotNull() {
        Advice random = AdviceManager.getRandomAdvice(context);
        assertNotNull("Random advice no debe ser null", random);
        assertNotNull("Random title no debe ser null", random.title());
    }

    /**
     * Test 8: Verificar que getRandomAdvice() retorna valores del conjunto de 25
     */
    @Test
    public void testGetRandomAdviceIsFromSet() {
        // Ejecutar varias veces para asegurar que siempre retorna valores válidos
        for (int i = 0; i < 10; i++) {
            Advice random = AdviceManager.getRandomAdvice(context);
            assertNotNull("Random advice debe ser válido", random);

            // Comparar con cada uno de los 25 consejos
            boolean found = false;
            for (int j = 0; j < AdviceManager.getTotalAdvices(); j++) {
                Advice original = AdviceManager.getAdviceByIndex(context, j);
                if (original.title().equals(random.title())) {
                    found = true;
                    break;
                }
            }
            assertTrue("Random advice debe ser parte del conjunto de 25", found);
        }
    }

    /**
     * Test 9: Verificar que getAdviceByIndex() con índice inválido retorna primer consejo (fallback)
     */
    @Test
    public void testGetAdviceByIndexFallback() {
        Advice invalidIndex1 = AdviceManager.getAdviceByIndex(context, -1);
        Advice invalidIndex2 = AdviceManager.getAdviceByIndex(context, 999);
        Advice valid0 = AdviceManager.getAdviceByIndex(context, 0);

        // Ambos índices inválidos deben retornar el primer consejo
        assertEquals("Índice -1 debe retornar consejo 0", valid0.title(), invalidIndex1.title());
        assertEquals("Índice 999 debe retornar consejo 0", valid0.title(), invalidIndex2.title());
    }

    /**
     * Test 10: Verificar que el mismo índice siempre retorna el mismo consejo (determinismo)
     */
    @Test
    public void testDeterminism() {
        Advice advice1FirstCall = AdviceManager.getAdviceByIndex(context, 5);
        Advice advice1SecondCall = AdviceManager.getAdviceByIndex(context, 5);
        Advice advice1ThirdCall = AdviceManager.getAdviceByIndex(context, 5);

        assertEquals("Mismo índice debe retornar siempre el mismo consejo",
                advice1FirstCall.title(), advice1SecondCall.title());
        assertEquals("Mismo índice debe retornar siempre el mismo consejo",
                advice1SecondCall.title(), advice1ThirdCall.title());
    }

    /**
     * Test 11: Verificar que los consejos tienen descripción no vacía
     */
    @Test
    public void testAdviceDescriptionsNotEmpty() {
        for (int i = 0; i < AdviceManager.getTotalAdvices(); i++) {
            Advice advice = AdviceManager.getAdviceByIndex(context, i);
            assertTrue("Consejo " + i + " debe tener descripción",
                    !advice.description().isEmpty());
            // Descripción debe tener al menos 10 caracteres
            assertTrue("Consejo " + i + " debe tener descripción suficientemente larga",
                    advice.description().length() > 10);
        }
    }
}
