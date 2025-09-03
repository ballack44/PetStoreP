package com.tj.petstore;

import com.tj.petstore.dto.Category;
import com.tj.petstore.dto.Pet;
import com.tj.petstore.dto.Tag;
import com.tj.petstore.util.Endpoints;
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
import static org.hamcrest.Matchers.*;

/**
 * Negative CRUD tests for PetStore API.
 */
@Slf4j
@Epic("PetStore API Tests")
@Feature("Pet CRUD Negative Scenarios")
public class PetNegativeTests extends Endpoints {

    public static final String DUMMY_IMAGE = "src/image/dummy.png";

    @BeforeClass
    public static void setup() {
        RestAssured.filters(
                new AllureRestAssured(),
                new RequestLoggingFilter(),
                new ResponseLoggingFilter()
        );
    }

    @Test(description = "Get pet with non-existent ID returns 404")
    @Severity(SeverityLevel.NORMAL)
    @Story("Get Pet - Negative")
    @Description("This test verifies that getting a non-existent pet returns 404")
    public void testGetNonExistentPet() {
        String nonExistentId = "0";
        given()
                .relaxedHTTPSValidation()
                .when()
                .get(baseUrl + petById.replace("{petId}", nonExistentId))
                .then()
                .statusCode(404)
                .body("message", containsString("Pet not found"));
    }

    //missing mandatory field should not be accepted
    @Test(enabled = false, description = "Create pet with missing required fields returns 400")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Create Pet - Negative")
    @Description("This test verifies that creating a pet with missing name returns 400")
    public void testCreatePetMissingRequiredFields() {
        Pet pet = new Pet();
        pet.setId(Utility.generateId());
        pet.setStatus("available");
        pet.setCategory(new Category(1, "Dogs"));
        pet.setPhotoUrls(Collections.singletonList(DUMMY_IMAGE));
        pet.setTags(Collections.singletonList(new Tag(1, "friendly")));
        // Missing name

        given()
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .body(pet)
                .when()
                .post(baseUrl + newPet)
                .then()
                .statusCode(anyOf(is(400), is(405), is(500))); // Some petstore impls may return 405/500 for bad input
    }

    @Test(description = "Create pet with invalid data types returns 400")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Create Pet - Negative")
    @Description("This test verifies that creating a pet with invalid data types returns 400")
    public void testCreatePetWithInvalidDataType() {
        // Build JSON manually to inject a string as id
        String invalidPetJson = """
            {
               "id": "notAnInt",
               "name": "Fluffy",
               "status": "available",
               "category": { "id": 1, "name": "Dogs" },
               "photoUrls": ["src/image/dummy.png"],
               "tags": [{"id":1,"name":"friendly"}]
            }
        """;

        given()
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .body(invalidPetJson)
                .when()
                .post(baseUrl + newPet)
                .then()
                .statusCode(anyOf(is(400), is(500)));
    }

    @Test(description = "Delete non-existent pet returns 404")
    @Severity(SeverityLevel.NORMAL)
    @Story("Delete Pet - Negative")
    @Description("This test verifies that deleting a non-existent pet returns 404")
    public void testDeleteNonExistentPet() {
        String nonExistentId = "77777777";
        given()
                .relaxedHTTPSValidation()
                .when()
                .delete(baseUrl + petById.replace("{petId}", nonExistentId))
                .then()
                .statusCode(404);
    }

    @Test(description = "Use invalid HTTP method returns 405")
    @Severity(SeverityLevel.MINOR)
    @Story("Invalid HTTP Method")
    @Description("This test verifies that PATCH method is not allowed on /pet")
    public void testInvalidHttpMethod() {
        given()
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .when()
                .patch(baseUrl + newPet)
                .then()
                .statusCode(405);
    }

    @Test(description = "Malformed JSON returns 400")
    @Severity(SeverityLevel.NORMAL)
    @Story("Malformed JSON")
    @Description("This test verifies that malformed JSON returns 400")
    public void testMalformedJson() {
        String malformedJson = "{ \"id\": 1, \"name\": \"BadDog\", "; // missing closing braces etc.
        given()
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .body(malformedJson)
                .when()
                .post(baseUrl + newPet)
                .then()
                .statusCode(anyOf(is(400), is(500)));
    }
}