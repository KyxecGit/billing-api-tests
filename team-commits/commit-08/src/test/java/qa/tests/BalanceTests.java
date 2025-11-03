package qa.tests;

import org.testng.annotations.Test;

/**
 * Тесты для балансов абонентов.
 * Проверяем просмотр и обновление балансов.
 */
public class BalanceTests extends BaseTest {

    @Test
    public void getAll() {
        // Получить все балансы
        checkStatus(get("/api/balance/all"), 200);
    }
    
    @Test
    public void getById() {
        // Получить баланс по ID
        Integer id = firstId("/api/balance/all");
        if (id != null) {
            checkStatus(get("/api/balance/" + id), 200);
        }
    }
    
    @Test
    public void update() {
        // Обновить баланс
        Integer id = firstId("/api/balance/all");
        if (id != null) {
            checkStatusOneOf(
                put("/api/balance/update/" + id, "{\"amount\":100.0}"), 
                200, 400
            );
        }
    }
}