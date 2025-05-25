package org.petstore.stepDefinition;

import io.cucumber.java.en.*;
import io.cucumber.datatable.DataTable;
import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;
import org.petstore.generic.RestAssuredExtension;
import org.petstore.pojo.request.PetRequest;
import org.petstore.pojo.response.PetResponse;
import org.petstore.pojo.common.Category;
import org.petstore.pojo.common.Tag;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.assertThat;

public class PetSteps {

    private RestAssuredExtension restAssuredExtension;
    private PetRequest petRequest;
    private PetResponse petResponse;
    private Response response;
    private Long petId;

    public PetSteps() {
        restAssuredExtension = new RestAssuredExtension();
    }

    @Given("I have pet details with following attributes")
    public void iHavePetDetailsWithFollowingAttributes(DataTable dataTable) {
        List<Map<String, String>> petData = dataTable.asMaps();
        Map<String, String> pet = petData.get(0);

        petRequest = new PetRequest();
        petRequest.setName(pet.get("name"));
        petRequest.setStatus(pet.get("status"));

        // Configurar la categoría
        Category category = new Category();
        category.setName(pet.get("category"));
        petRequest.setCategory(category);

        // Configurar tags
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
        response = SerenityRest.given()
                .spec(restAssuredExtension.getRequestSpecification())
                .body(petRequest)
                .when()
                .post("/pet");

        petResponse = response.as(PetResponse.class);
        petId = petResponse.getId();
    }

    @Then("the pet should be created successfully")
    public void thePetShouldBeCreatedSuccessfully() {
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(petResponse).isNotNull();
        assertThat(petResponse.getId()).isNotNull();
    }

    @And("the response should include the correct pet details")
    public void theResponseShouldIncludeTheCorrectPetDetails() {
        assertThat(petResponse.getName()).isEqualTo(petRequest.getName());
        assertThat(petResponse.getStatus()).isEqualTo(petRequest.getStatus());
        assertThat(petResponse.getCategory().getName())
                .isEqualTo(petRequest.getCategory().getName());
    }

    @Given("I have a valid pet ID")
    public void iHaveAValidPetID() {
        // Podemos usar el ID de la mascota creada anteriormente
        assertThat(petId).isNotNull();
    }

    @When("I send request to get pet details")
    public void iSendRequestToGetPetDetails() {
        response = SerenityRest.given()
                .spec(restAssuredExtension.getRequestSpecification())
                .when()
                .get("/pet/" + petId);

        petResponse = response.as(PetResponse.class);
    }

    @Then("I should receive the pet information")
    public void iShouldReceiveThePetInformation() {
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(petResponse).isNotNull();
        assertThat(petResponse.getId()).isEqualTo(petId);
    }

    @And("the pet status should be {string}")
    public void thePetStatusShouldBe(String expectedStatus) {
        assertThat(petResponse.getStatus()).isEqualTo(expectedStatus);
    }
}