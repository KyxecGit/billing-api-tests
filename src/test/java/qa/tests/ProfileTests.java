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
    
    @Test(priority = 5)
    public void getByMsisdn() {
        // Получить профиль по номеру телефона
        if (createdId != null) {
            Response response = get("/api/admin/profile/" + createdId);
            String phone = text(response, "content.msisdn");
            checkStatus(get("/api/admin/profile/getByMsisdn/" + phone), 200);
        }
    }
    
    @Test(priority = 6)
    public void update() {
        // Обновить профиль (сменить тарифный план)
        if (createdId != null) {
            Response response = get("/api/admin/profile/" + createdId);
            String phone = text(response, "content.msisdn");
            String body = json(phone, 1, 1);  // Новый тарифный план
            checkStatus(put("/api/admin/profile/update/" + createdId, body), 200);
        }
    }
    
    @Test(priority = 7)
    public void delete() {
        // Удалить профиль
        if (createdId != null) {
            checkStatusOneOf(
                delete("/api/admin/profile/delete/" + createdId), 
                200, 204
            );
        }
    }
}
