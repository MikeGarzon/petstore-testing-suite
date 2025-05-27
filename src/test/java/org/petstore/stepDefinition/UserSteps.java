package org.petstore.stepDefinition;

import io.cucumber.java.en.*;
import io.cucumber.datatable.DataTable;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.serenitybdd.core.Serenity;
import org.petstore.pojo.request.UserRequest;
import org.petstore.utils.AssertionReporter;

import java.util.List;
import java.util.Map;

import static net.serenitybdd.rest.SerenityRest.*;
import static org.assertj.core.api.Assertions.assertThat;

public class UserSteps {

    private UserRequest userRequest;
    private String username;
    private String password;
    private String adminUsername;
    private String adminPassword;

    @Given("I have admin credentials")
    public void iHaveAdminCredentials(DataTable dataTable) {
        List<Map<String, String>> credentials = dataTable.asMaps();
        Map<String, String> adminCreds = credentials.get(0);

        adminUsername = adminCreds.get("username");
        adminPassword = adminCreds.get("password");
    }

    @Given("I have user details")
    public void iHaveUserDetails(DataTable dataTable) {
        List<Map<String, String>> userData = dataTable.asMaps();
        Map<String, String> user = userData.get(0);

        userRequest = new UserRequest();
        userRequest.setUsername(user.get("username"));
        userRequest.setFirstName(user.get("firstName"));
        userRequest.setLastName(user.get("lastName"));
        userRequest.setEmail(user.get("email"));
        userRequest.setPassword(user.get("password"));
        userRequest.setPhone(user.get("phone"));
        userRequest.setUserStatus(Integer.parseInt(user.get("userStatus")));

        username = user.get("username");
        password = user.get("password");
    }

    @When("I send request to create a new user")
    public void iSendRequestToCreateANewUser() {
        Response response = given()
                .baseUri("https://petstore.swagger.io/v2")
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("api_key", "special-key") // Example API key
                .body(userRequest)
                .when()
                .post("/user");

        Serenity.recordReportData()
                .withTitle("User Creation Response")
                .andContents(response.getBody().asString());
    }

    @Then("the user should be created successfully")
    public void theUserShouldBeCreatedSuccessfully() {
        then().statusCode(200);
        AssertionReporter.verifyNumberEquals("Status code", lastResponse().statusCode(), 200);
    }

    @And("I can login with the new user credentials")
    public void iCanLoginWithTheNewUserCredentials() {
        Response response = given()
                .baseUri("https://petstore.swagger.io/v2")
                .queryParam("username", username)
                .queryParam("password", password)
                .when()
                .get("/user/login");

        then().statusCode(200);

        AssertionReporter.verifyNumberEquals("Login Status code", response.getStatusCode(), 200);
    }

    @Given("I have an existing user {string}")
    public void iHaveAnExistingUser(String existingUsername) {
        username = existingUsername;
    }

    @When("I send request to delete the user")
    public void iSendRequestToDeleteTheUser() {
        Response response = given()
                .baseUri("https://petstore.swagger.io/v2")
                .header("api_key", "special-key") // Example API key
                .when()
                .delete("/user/" + username);

        Serenity.recordReportData()
                .withTitle("User Deletion Response")
                .andContents(response.getBody().asString());
    }

    @Then("the user should be deleted successfully")
    public void theUserShouldBeDeletedSuccessfully() {
        then().statusCode(200);
        AssertionReporter.verifyNumberEquals("Status code", lastResponse().statusCode(), 200);
    }

    @And("I cannot login with the deleted user credentials")
    public void iCannotLoginWithTheDeletedUserCredentials() {
        Response response = given()
                .baseUri("https://petstore.swagger.io/v2")
                .queryParam("username", username)
                .queryParam("password", password)
                .when()
                .get("/user/login");

        then().statusCode(400); // Should fail with 400 Bad Request

        AssertionReporter.verifyNumberEquals("Failed Login Status code", response.getStatusCode(), 400);
    }
}