import com.tj.petstore.PetStoreTestsApplication;
import io.qameta.allure.Description;
import io.restassured.http.ContentType;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.*;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(classes = PetStoreTestsApplication.class)
public class PetCrudPositiveTest extends AbstractTestNGSpringContextTests {

    private static Pet testPet;

    @BeforeClass
    public void initPet() {
        testPet = new Pet(
                987654321L,
                new Category(1L, "Cat"),
                "Cirmos",
                List.of("https://example.com/cat.jpg"),
                List.of(new Tag(1L, "cute")),
                "available"
        );
    }

    @Test(priority = 1)
    @Description("1. Create – Új házikedvenc létrehozása")
    public void createPet() {
        given()
                .contentType(ContentType.JSON)
                .body(testPet)
                .when()
                .post("/pet")
                .then()
                .statusCode(200)
                .body("id", equalTo(testPet.getId().intValue()))
                .body("name", equalTo(testPet.getName()))
                .body("status", equalTo(testPet.getStatus()));
    }

    @Test(priority = 2)
    @Description("2. Get – Az előzőleg létrehozott házikedvenc lekérdezése")
    public void getPet() {
        given()
                .when()
                .get("/pet/{petId}", testPet.getId())
                .then()
                .statusCode(200)
                .body("id", equalTo(testPet.getId().intValue()))
                .body("name", equalTo(testPet.getName()))
                .body("status", equalTo(testPet.getStatus()));
    }

    @Test(priority = 3)
    @Description("3. Update – A házikedvenc adatainak frissítése")
    public void updatePet() {
        testPet.setName("Foltos");
        testPet.setStatus("sold");

        given()
                .contentType(ContentType.JSON)
                .body(testPet)
                .when()
                .put("/pet")
                .then()
                .statusCode(200)
                .body("name", equalTo("Foltos"))
                .body("status", equalTo("sold"));
    }

    @Test(priority = 4)
    @Description("4. Get – A frissített adatok lekérdezése")
    public void getUpdatedPet() {
        given()
                .when()
                .get("/pet/{petId}", testPet.getId())
                .then()
                .statusCode(200)
                .body("id", equalTo(testPet.getId().intValue()))
                .body("name", equalTo("Foltos"))
                .body("status", equalTo("sold"));
    }

    @Test(priority = 5)
    @Description("5. Delete – A házikedvenc törlése")
    public void deletePet() {
        given()
                .when()
                .delete("/pet/{petId}", testPet.getId())
                .then()
                .statusCode(200)
                .body("message", equalTo(String.valueOf(testPet.getId())));
    }
}