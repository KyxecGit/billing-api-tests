package qa.tests;

import org.testng.annotations.Test;
import io.restassured.response.Response;

/**
 * Тесты для профилей абонентов.
 * Полный цикл: создание → чтение → обновление → удаление.
 */
public class ProfileTests extends BaseTest {
    
    private static Integer createdId;  // ID созданного профиля

    @Test(priority = 1)
    public void getAll() {
        // Получить все профили
        checkStatus(get("/api/admin/profile/all"), 200);
    }
    
    @Test(priority = 2)
    public void getAllRemoved() {
        // Получить удаленные профили
        checkStatus(get("/api/admin/profile/all-removed"), 200);
    }
    
    @Test(priority = 3)
    public void create() {
        // Создать новый профиль
        String phone = newPhone();
        
        Response response = post("/api/admin/profile/create", json(phone));
        checkStatus(response, 201);
        createdId = number(response, "content.id");
    }
    
    @Test(priority = 4)
    public void getById() {
        // Получить профиль по ID
        if (createdId != null) {
            checkStatus(get("/api/admin/profile/" + createdId), 200);
        }
    }
}