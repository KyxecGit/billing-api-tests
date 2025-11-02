package auc.utils;

import net.datafaker.Faker;

public class TestDataGenerator {

    private static final Faker faker = new Faker();

    public static String generateMsisdn() {
        return "99680" + faker.number().digits(7);
    }

    public static String generateFirstName() {
        return faker.name().firstName();
    }

    public static String generateLastName() {
        return faker.name().lastName();
    }

    public static String generateTelegramChatId() {
        return String.valueOf(faker.number().numberBetween(100000000, 999999999));
    }

    public static Double generateBalanceAmount() {
        return faker.number().randomDouble(2, 100, 5000);
    }
}
