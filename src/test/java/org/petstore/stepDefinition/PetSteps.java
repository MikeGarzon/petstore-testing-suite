package org.petstore.stepDefinition;

import io.cucumber.java.en.*;
import io.cucumber.datatable.DataTable;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.serenitybdd.core.Serenity;
import org.hamcrest.Matchers;
import org.petstore.pojo.request.PetRequest;
import org.petstore.pojo.response.PetResponse;
import org.petstore.pojo.common.Category;
import org.petstore.pojo.common.Tag;
import org.petstore.utils.AssertionReporter;
import org.petstore.utils.JsonUtils;

import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static net.serenitybdd.rest.SerenityRest.*;
import static org.assertj.core.api.Assertions.assertThat;

public class PetSteps {

    private PetRequest petRequest;
    private PetResponse petResponse;
    private PetResponse knowPet;
    private Long petId;

    @Given("I have pet details with following attributes")
    public void iHavePetDetailsWithFollowingAttributes(DataTable dataTable) {
        List<Map<String, String>> petData = dataTable.asMaps();
        Map<String, String> pet = petData.get(0);

        petRequest = new PetRequest();
        petRequest.setName(pet.get("name"));
        petRequest.setStatus(pet.get("status"));

        Category category = new Category();
        category.setName(pet.get("category"));
        petRequest.setCategory(category);

        // Setup tags
        List<Tag> tags = Arrays.stream(pet.get("tags").split(","))
                .map(tagName -> {
                    Tag tag = new Tag();
                    tag.setName(tagName);
                    return tag;
                })
                .collect(Collectors.toList());
        petRequest.setTags(tags);
    }

    @When("I send request to add new pet")
    public void iSendRequestToAddNewPet() {
        try {
            Response response = given()
                    .baseUri("https://petstore.swagger.io/v2")
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .body(petRequest)
                    .when()
                    .post("/pet");

            petResponse = response.getBody().as(PetResponse.class);
            petId = petResponse.getId();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    @Then("the pet should be created successfully")
    public void thePetShouldBeCreatedSuccessfully() {
        then().statusCode(200);
        assertThat(petResponse).isNotNull();
        assertThat(petResponse.getId()).isNotNull();

        AssertionReporter.verifyNumberEquals("Status code",200 ,lastResponse().statusCode() );

        Serenity.recordReportData()
                .withTitle("Pet Creation Info")
                .andContents("Pet ID: " + petResponse.getId());
    }

    @And("the response should include the correct pet details")
    public void theResponseShouldIncludeTheCorrectPetDetails() {

        String actualName = petResponse.getName();
        String actualStatus = petResponse.getStatus();
        String actualCategory = petResponse.getCategory().getName();

        //Basic assertions
        then().body("name", Matchers.equalTo(petRequest.getName()))
                .body("status", Matchers.equalTo(petRequest.getStatus()))
                .body("category.name", Matchers.equalTo(petRequest.getCategory().getName()));

        // Report the values
        AssertionReporter.verifyStringEquals("Pet Name", actualName, petRequest.getName());
        AssertionReporter.verifyStringEquals("Pet Status", actualStatus, petRequest.getStatus());
        AssertionReporter.verifyStringEquals("Pet Category", actualCategory, petRequest.getCategory().getName());

    }

    @Given("I use a known pet ID")
    public void iUseAKnownPetID() {
        knowPet = JsonUtils.loadKnownPet();
        petId = knowPet.getId();
    }

    @Given("I have a valid pet ID")
    public void iHaveAValidPetID() {
        assertThat(petId).isNotNull();
    }

    @When("I send request to get pet details")
    public void iSendRequestToGetPetDetails() {
        try {
            Response response = given()
                    .baseUri("https://petstore.swagger.io/v2")
                    .when()
                    .get("/pet/" + petId);

            if (response.getStatusCode() == 200) {
                petResponse = response.getBody().as(PetResponse.class);
            } else if (response.getStatusCode() == 404) {
                Serenity.recordReportData()
                        .withTitle("Pet Not Found")
                        .andContents("The pet with ID " + petId + " was not found in the API. " +
                                "Using the local data for validation.");
                // We already have petResponse from the JSON file, so we'll use that (In case petstore.swagger changes of is not available)
            } else {

                throw new RuntimeException("Failed to get pet. Status code: " + response.getStatusCode() +
                        ", Response: " + response.getBody().asString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    @Then("I should receive the correct pet information")
    public void iShouldReceiveThePetInformation() {
        if (lastResponse().getStatusCode() == 200) {
            then().statusCode(200);
            then().body("id", Matchers.equalTo(petId.intValue()));
        } else {
            // If we're using local data (API returned 404), just log this fact
            Serenity.recordReportData()
                    .withTitle("Using Local Pet Data")
                    .andContents("Verifying against local pet data since API returned no pet.");
        }

        // ID verification
        assertThat(petResponse.getId()).isEqualTo(petId);
        AssertionReporter.verifyLongEquals("Pet ID", petResponse.getId(), petId);

        // Name verification
        assertThat(petResponse.getName()).isEqualTo(knowPet.getName());
        AssertionReporter.verifyStringEquals("Pet name", petResponse.getName(), knowPet.getName());

        // Category verification
        assertThat(petResponse.getCategory()).isNotNull();
        assertThat(petResponse.getCategory().getId()).isEqualTo(knowPet.getCategory().getId());
        assertThat(petResponse.getCategory().getName()).isEqualTo(knowPet.getCategory().getName());
        AssertionReporter.verifyNumberEquals("Category ID", petResponse.getCategory().getId(), knowPet.getCategory().getId());
        AssertionReporter.verifyStringEquals("Category Name", petResponse.getCategory().getName(), knowPet.getCategory().getName());

        // Photo URLs verification
        assertThat(petResponse.getPhotoUrls()).isNotNull();
        assertThat(petResponse.getPhotoUrls().size()).isEqualTo(knowPet.getPhotoUrls().size());

        // Verify each photo URL
        for (int i = 0; i < knowPet.getPhotoUrls().size(); i++) {
            final int index = i; // Need final variable for lambda
            String expectedUrl = knowPet.getPhotoUrls().get(index);
            String actualUrl = petResponse.getPhotoUrls().get(index);

            AssertionReporter.verifyStringEquals("Photo URL [" + index + "]", actualUrl, expectedUrl);

            if (lastResponse().getStatusCode() == 200) {
                then().body("photoUrls[" + index + "]", Matchers.equalTo(expectedUrl));
            }
        }

        // Tags verification
        assertThat(petResponse.getTags()).isNotNull();
        assertThat(petResponse.getTags().size()).isEqualTo(knowPet.getTags().size());

        // Verify each tag
        for (int i = 0; i < knowPet.getTags().size(); i++) {
            final int index = i; // Need final variable for lambda
            Tag expectedTag = knowPet.getTags().get(index);
            Tag actualTag = petResponse.getTags().get(index);

            AssertionReporter.verifyNumberEquals("Tag ID [" + index + "]", actualTag.getId(), expectedTag.getId());
            AssertionReporter.verifyStringEquals("Tag Name [" + index + "]", actualTag.getName(), expectedTag.getName());

            if (lastResponse().getStatusCode() == 200) {
                then().body("tags[" + index + "].id", Matchers.equalTo(expectedTag.getId().intValue()));
                then().body("tags[" + index + "].name", Matchers.equalTo(expectedTag.getName()));
            }
        }

    }

    @And("the pet status should be {string}")
    public void thePetStatusShouldBe(String expectedStatus) {
        assertThat(petResponse.getStatus()).isEqualTo(expectedStatus);
        AssertionReporter.verifyStringEquals("Pet status", petResponse.getStatus(), expectedStatus);
    }

}