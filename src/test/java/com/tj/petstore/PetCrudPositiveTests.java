package com.tj.petstore;

import com.tj.petstore.dto.Pet;
import com.tj.petstore.util.Endpoints;
import com.tj.petstore.util.SimpleRetry;
import com.tj.petstore.util.TestObjectFactory;
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

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Positive CRUD flow for PetStore API.
 * Covers: create, get, update, and delete operations for a pet resource.
 */
@Slf4j
@Epic("PetStore API Tests")
@Feature("Pet CRUD Operations")
public class PetCrudPositiveTests extends Endpoints {
    
    private static String createdPetId;

    @BeforeClass
    public static void setup() {
        RestAssured.filters(
                new AllureRestAssured(),
                new RequestLoggingFilter(),
                new ResponseLoggingFilter()
        );
    }

    @Test(
            priority = 1,
            description = "Create a new pet and verify the response matches the sent data.",
            retryAnalyzer = SimpleRetry.class
    )
    @Severity(SeverityLevel.CRITICAL)
    @Story("Create Pet")
    @Description("Creates a new pet in the system with status 'available'. "
            + "Verifies that the response contains the correct id, name, and status. "
            + "Logs the created pet and its id.")
    public void testCreatePet() {
        Pet pet = TestObjectFactory.basicPet(Utility.generateId(), "Rex");

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

    @Test(
            priority = 2,
            description = "Retrieve the newly created pet and ensure its data is correct.",
            retryAnalyzer = SimpleRetry.class
    )
    @Severity(SeverityLevel.NORMAL)
    @Story("Get Pet")
    @Description("Fetches the pet created in the previous test using its id. "
            + "Verifies the retrieved pet's id, name, and status match the expected values.")
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


    @Test(
            priority = 3,
            description = "Update the pet's information and verify the update.",
            retryAnalyzer = SimpleRetry.class
    )
    @Severity(SeverityLevel.CRITICAL)
    @Story("Update Pet")
    @Description("Updates the created pet with new name, category, tag, and status. "
            + "Verifies that the response reflects the updated values.")
    public void testUpdatePet() {
        log.info("Update the pet");

        Pet updatedPet = TestObjectFactory.updatedPet(Integer.parseInt(createdPetId), "RexUpdated");

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

    @Test(
            priority = 4,
            description = "Retrieve the updated pet and check for updated values.",
            retryAnalyzer = SimpleRetry.class
    )
    @Severity(SeverityLevel.NORMAL)
    @Story("Get Updated Pet")
    @Description("Fetches the pet after update by its id. "
            + "Verifies the pet's id, name, and status reflect the updated values.")
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

    @Test(
            priority = 5,
            description = "Delete the pet and check that the deletion is successful.",
            retryAnalyzer = SimpleRetry.class
    )
    @Severity(SeverityLevel.CRITICAL)
    @Story("Delete Pet")
    @Description("Deletes the created pet by its id. "
            + "Verifies that the API returns status 200 and the response message matches the id of the deleted pet.")
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
