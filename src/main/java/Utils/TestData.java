package Utils;

import com.github.javafaker.Faker;
import lombok.Data;

@Data
public class TestData {
    private static final Faker faker = new Faker();

    private final String customersFirstName = faker.name().firstName();
    private final String customersLastName = faker.name().lastName();
    private final String customersUsername = faker.name().username();
    private final String updatedFirstName = faker.name().firstName();
    private final String updatedLastName = faker.name().lastName();
    private final String updatedUsername = faker.name().username();

    private final String bookstoreName = faker.company().name();
    private final Boolean isBookstoreActive = true;
    private final String updatedBookstoreName = faker.company().name();
    private final Boolean isUpdatedBookstoreActive = false;

    private final String productName = faker.funnyName().name();
    private final Double productPrice = faker.number().randomDouble(2, 1, 99);
    private final Boolean isProductAvailable = true;
    private final String updatedProductName = faker.funnyName().name();
    private final Double updatedProductPrice = faker.number().randomDouble(2, 1, 1000);
    private final Boolean isUpdatedProductAvailable = false;

    private final String city = faker.address().city();
    private final String postalCode = faker.address().zipCode();
    private final String street = faker.address().streetAddress();

    private final Double topUpAmount = faker.number().randomDouble(2, 100, 1000);

}
