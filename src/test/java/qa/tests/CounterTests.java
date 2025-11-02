package qa.tests;

import org.testng.annotations.Test;

/**
 * Тесты для счетчиков абонентов.
 * Счетчики показывают использование минут, SMS, интернета.
 */
public class CounterTests extends BaseTest {

    @Test
    public void getAll() {
        // Получить все счетчики
        checkStatus(get("/api/admin/counter/all"), 200);
    }
    
    @Test
    public void getAllActive() {
        // Получить только активные счетчики
        checkStatusOneOf(get("/api/admin/counter/all-active"), 200, 204);
    }
    
    @Test
    public void getById() {
        // Получить счетчик по ID
        Integer id = firstId("/api/admin/counter/all");
        if (id != null) {
            checkStatus(get("/api/admin/counter/" + id), 200);
        }
    }
}
