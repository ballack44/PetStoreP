package com.tj.petstore;

import com.tj.petstore.dto.Category;
import com.tj.petstore.dto.Pet;
import com.tj.petstore.dto.Tag;
import com.tj.petstore.util.Endpoints;
import com.tj.petstore.util.SimpleRetry;
import com.tj.petstore.util.Utility;
import io.qameta.allure.*;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Collections;


import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.*;

@Slf4j
@Epic("PetStore API Tests")
@Feature("Pet CRUD Operations")
public class PetCrudPositiveTest extends Endpoints {
    
    private static String createdPetId;
    public static final String DUMMY_IMAGE = "src/image/dummy.png";

    @BeforeClass
    public static void setup() {
        RestAssured.filters(
                new AllureRestAssured(),
                new RequestLoggingFilter(),
                new ResponseLoggingFilter()
        );
    }

    @Test(priority = 1, description = "Create a new pet", retryAnalyzer = SimpleRetry.class)
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Create Pet")
    @Story("Create Pet")
    @Description("This test verifies pet creation")
    public void testCreatePet() {
        Pet pet = new Pet();
        pet.setId(Utility.generateId());// unique id
        pet.setName("Rex");
        pet.setStatus("available");
        pet.setCategory(new Category(1, "Dogs"));
        pet.setPhotoUrls(Collections.singletonList(DUMMY_IMAGE));
        pet.setTags(Collections.singletonList(new Tag(1, "friendly")));

        createdPetId = String.valueOf(pet.getId());
        log.info("Create a pet with the following ID: {}", createdPetId);
        log.info("Created the following pet {}", pet);

        given()
                .relaxedHTTPSValidation()
                .and()
                .contentType(ContentType.JSON)
                .body(pet)
                .when()
                .post(baseUrl + newPet)
                .then()
                .statusCode(200)
                .body("id", equalTo(Integer.parseInt(createdPetId)))
                .body("name", equalTo("Rex"))
                .body("status", equalTo("available"));

        // Await until GET reflects the created resource
        /*
        await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofMillis(300))
                .ignoreExceptions()
                .untilAsserted(() ->
                        given()
                                .relaxedHTTPSValidation()
                                .when()
                                .get(baseUrl + petById.replace("{petId}", createdPetId))
                                .then()
                                .statusCode(200)
                                .body("id", equalTo(Integer.parseInt(createdPetId)))
                                .body("name", equalTo("Rex"))
                                .body("status", equalTo("available"))
                );

         */
    }

    @Test(priority = 2, description = "Get the created pet", retryAnalyzer = SimpleRetry.class)
    @Severity(SeverityLevel.NORMAL)
    @Story("Get Pet")
    @Description("This test verifies GET created pet")
    public void testGetPet() {
        log.info("Get the created pet");

        given()
                .relaxedHTTPSValidation()
                .and()
                .when()
                .get(baseUrl + petById.replace("{petId}", createdPetId))
                .then()
                .statusCode(200)
                .body("id", equalTo(Integer.parseInt(createdPetId)))
                .body("name", equalTo("Rex"))
                .body("status", equalTo("available"));
    }


    @Test(priority = 3, description = "Update the pet information", retryAnalyzer = SimpleRetry.class)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Update Pet")
    @Description("This test verifies UPDATE of a pet")
    public void testUpdatePet() {
        log.info("Update the pet");

        Pet updatedPet = new Pet();
        updatedPet.setId(Integer.parseInt(createdPetId));
        updatedPet.setName("RexUpdated");
        updatedPet.setStatus("sold");
        updatedPet.setCategory(new Category(2, "GuardDogs"));
        updatedPet.setPhotoUrls(Collections.singletonList(DUMMY_IMAGE));
        updatedPet.setTags(Collections.singletonList(new Tag(2, "trained")));

        given()
                .relaxedHTTPSValidation()
                .and()
                .contentType(ContentType.JSON)
                .body(updatedPet)
                .when()
                .put(baseUrl + newPet)
                .then()
                .statusCode(200)
                .body("id", equalTo(Integer.parseInt(createdPetId)))
                .body("name", equalTo("RexUpdated"))
                .body("status", equalTo("sold"))
                .body("category.id", notNullValue())
                .body("tags[0].id", notNullValue());
    }

    @Test(priority = 4, description = "Get the updated pet", retryAnalyzer = SimpleRetry.class)
    @Severity(SeverityLevel.NORMAL)
    @Story("Get Updated Pet")
    @Description("This test verifies GET updated pet")
    public void testGetUpdatedPet() {
        given()
                .relaxedHTTPSValidation()
                .and()
                .when()
                .get(baseUrl + petById.replace("{petId}", createdPetId))
                .then()
                .statusCode(200)
                .body("id", equalTo(Integer.parseInt(createdPetId)))
                .body("name", equalTo("RexUpdated"))
                .body("status", equalTo("sold"));
    }

    @Test(priority = 5, description = "Delete the pet", retryAnalyzer = SimpleRetry.class)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Delete Pet")
    @Description("This test verifies deletion of a pet")
    public void testDeletePet() {
        log.info("Delete the created pet");
        given()
                .relaxedHTTPSValidation()
                .and()
                .when()
                .delete(baseUrl + petById.replace("{petId}", createdPetId))
                .then()
                .statusCode(200)
                .body("message", equalTo(String.valueOf(createdPetId)));
    }
}
