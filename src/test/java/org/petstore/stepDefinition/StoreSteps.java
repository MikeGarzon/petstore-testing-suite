package org.petstore.stepDefinition;

import io.cucumber.java.en.*;
import io.cucumber.datatable.DataTable;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.serenitybdd.core.Serenity;
import org.petstore.pojo.request.OrderRequest;
import org.petstore.pojo.response.OrderResponse;
import org.petstore.utils.AssertionReporter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static net.serenitybdd.rest.SerenityRest.*;
import static org.assertj.core.api.Assertions.assertThat;

public class StoreSteps {

    private OrderRequest orderRequest;
    private OrderResponse orderResponse;
    private Long orderId;
    private String apiKey;
    private Map<String, Integer> inventoryMap;

    @Given("I am authenticated as a store manager")
    public void iAmAuthenticatedAsAStoreManager(DataTable dataTable) {
        List<Map<String, String>> credentials = dataTable.asMaps();
        Map<String, String> creds = credentials.get(0);

        String username = creds.get("username");
        String password = creds.get("password");

        // In a real scenario, you would call the login API to get a token
        // For simplicity, we'll use a fixed API key for the Swagger Pet Store
        apiKey = "special-key";

        Serenity.recordReportData()
                .withTitle("Store Manager Authentication")
                .andContents("Store manager authenticated: " + username);
    }

    @Given("I have an order with the following details")
    public void iHaveAnOrderWithTheFollowingDetails(DataTable dataTable) {
        List<Map<String, String>> orderData = dataTable.asMaps();
        Map<String, String> order = orderData.get(0);

        orderRequest = new OrderRequest();
        orderRequest.setPetId(Long.parseLong(order.get("petId")));
        orderRequest.setQuantity(Integer.parseInt(order.get("quantity")));
        orderRequest.setStatus(order.get("status"));
        orderRequest.setComplete(Boolean.parseBoolean(order.get("complete")));

        // Set the current date as ship date
        String shipDate = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        orderRequest.setShipDate(shipDate);

        Serenity.recordReportData()
                .withTitle("Order Request")
                .andContents("Pet ID: " + orderRequest.getPetId() +
                        "\nQuantity: " + orderRequest.getQuantity() +
                        "\nStatus: " + orderRequest.getStatus() +
                        "\nShip Date: " + orderRequest.getShipDate());
    }

    @When("I send request to place the order")
    public void iSendRequestToPlaceTheOrder() {
        try {
            Response response = given()
                    .baseUri("https://petstore.swagger.io/v2")
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .header("api_key", apiKey)
                    .body(orderRequest)
                    .when()
                    .post("/store/order");

            orderResponse = response.getBody().as(OrderResponse.class);
            orderId = orderResponse.getId();

            Serenity.recordReportData()
                    .withTitle("Order Creation Response")
                    .andContents("Order ID: " + orderId);
        } catch (Exception e) {
            Serenity.recordReportData()
                    .withTitle("Order Creation Error")
                    .andContents("Exception: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Then("the order should be created successfully")
    public void theOrderShouldBeCreatedSuccessfully() {
        then().statusCode(200);
        assertThat(orderResponse).isNotNull();
        assertThat(orderResponse.getId()).isNotNull();

        AssertionReporter.verifyNumberEquals("Status code", lastResponse().statusCode(), 200);
    }

    @And("I should get a valid order ID")
    public void iShouldGetAValidOrderID() {
        assertThat(orderId).isNotNull();
        assertThat(orderId).isGreaterThan(0);

        AssertionReporter.verifyNumberEquals("Order ID > 0", 1, orderId > 0 ? 1 : 0);
    }

    @When("I retrieve the order by ID")
    public void iRetrieveTheOrderByID() {
        try {
            Response response = given()
                    .baseUri("https://petstore.swagger.io/v2")
                    .header("api_key", apiKey)
                    .when()
                    .get("/store/order/" + orderId);

            if (response.getStatusCode() == 200) {
                orderResponse = response.getBody().as(OrderResponse.class);

                Serenity.recordReportData()
                        .withTitle("Retrieved Order")
                        .andContents("Order ID: " + orderResponse.getId() +
                                "\nPet ID: " + orderResponse.getPetId() +
                                "\nStatus: " + orderResponse.getStatus());
            } else {
                Serenity.recordReportData()
                        .withTitle("Order Retrieval Failed")
                        .andContents("Status code: " + response.getStatusCode() +
                                "\nResponse: " + response.getBody().asString());
            }
        } catch (Exception e) {
            Serenity.recordReportData()
                    .withTitle("Order Retrieval Error")
                    .andContents("Exception: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Then("I should get status code {int}")
    public void iShouldGetStatusCode(int expectedStatusCode) {
        then().statusCode(expectedStatusCode);
        AssertionReporter.verifyNumberEquals("Status code", lastResponse().statusCode(), expectedStatusCode);
    }

    @And("the order details should match the created order")
    public void theOrderDetailsShouldMatchTheCreatedOrder() {
        assertThat(orderResponse.getPetId()).isEqualTo(orderRequest.getPetId());
        assertThat(orderResponse.getQuantity()).isEqualTo(orderRequest.getQuantity());
        assertThat(orderResponse.getStatus()).isEqualTo(orderRequest.getStatus());

        AssertionReporter.verifyNumberEquals("Pet ID", orderResponse.getPetId(), orderRequest.getPetId());
        AssertionReporter.verifyNumberEquals("Quantity", orderResponse.getQuantity(), orderRequest.getQuantity());
        AssertionReporter.verifyStringEquals("Status", orderResponse.getStatus(), orderRequest.getStatus());
    }

    @When("I update the order status to {string}")
    public void iUpdateTheOrderStatusTo(String newStatus) {
        try {
            // Get the current order data first
            Response getResponse = given()
                    .baseUri("https://petstore.swagger.io/v2")
                    .header("api_key", apiKey)
                    .when()
                    .get("/store/order/" + orderId);

            if (getResponse.getStatusCode() == 200) {
                orderResponse = getResponse.getBody().as(OrderResponse.class);
            }

            // Create an updated order request
            OrderRequest updatedOrder = new OrderRequest();
            updatedOrder.setId(orderId);
            updatedOrder.setPetId(orderResponse.getPetId());
            updatedOrder.setQuantity(orderResponse.getQuantity());
            updatedOrder.setShipDate(orderResponse.getShipDate());
            updatedOrder.setStatus(newStatus);
            updatedOrder.setComplete(orderResponse.getComplete());

            // Send the update request
            // Note: Pet Store API doesn't have a direct endpoint to update an order
            // In a real API that supports PUT for orders, we would use:
            Response response = given()
                    .baseUri("https://petstore.swagger.io/v2")
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .header("api_key", apiKey)
                    .body(updatedOrder)
                    .when()
                    .post("/store/order"); // Simulate update by creating a new order with same ID

            orderResponse = response.getBody().as(OrderResponse.class);

            Serenity.recordReportData()
                    .withTitle("Order Update")
                    .andContents("Updated status to: " + newStatus);
        } catch (Exception e) {
            Serenity.recordReportData()
                    .withTitle("Order Update Error")
                    .andContents("Exception: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Then("the order should be updated successfully")
    public void theOrderShouldBeUpdatedSuccessfully() {
        then().statusCode(200);
        AssertionReporter.verifyNumberEquals("Status code", lastResponse().statusCode(), 200);
    }

    @And("the updated order should have status {string}")
    public void theUpdatedOrderShouldHaveStatus(String expectedStatus) {
        assertThat(orderResponse.getStatus()).isEqualTo(expectedStatus);
        AssertionReporter.verifyStringEquals("Order status", orderResponse.getStatus(), expectedStatus);
    }

    @When("I delete the order")
    public void iDeleteTheOrder() {
        try {
            Response response = given()
                    .baseUri("https://petstore.swagger.io/v2")
                    .header("api_key", apiKey)
                    .when()
                    .delete("/store/order/" + orderId);

            Serenity.recordReportData()
                    .withTitle("Order Deletion")
                    .andContents("Status code: " + response.getStatusCode());
        } catch (Exception e) {
            Serenity.recordReportData()
                    .withTitle("Order Deletion Error")
                    .andContents("Exception: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Then("the order should be deleted successfully")
    public void theOrderShouldBeDeletedSuccessfully() {
        then().statusCode(200);
        AssertionReporter.verifyNumberEquals("Status code", lastResponse().statusCode(), 200);
    }

    @And("I should not be able to retrieve the order")
    public void iShouldNotBeAbleToRetrieveTheOrder() {
        try {
            Response response = given()
                    .baseUri("https://petstore.swagger.io/v2")
                    .when()
                    .get("/store/order/" + orderId);

            // The Pet Store API returns 404 for non-existent orders
            assertThat(response.getStatusCode()).isEqualTo(404);
            AssertionReporter.verifyNumberEquals("Status code", response.getStatusCode(), 404);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @When("I request the store inventory")
    public void iRequestTheStoreInventory() {
        try {
            Response response = given()
                    .baseUri("https://petstore.swagger.io/v2")
                    .header("api_key", apiKey)
                    .when()
                    .get("/store/inventory");

            inventoryMap = response.getBody().as(Map.class);

            // Format for better report
            StringBuilder inventoryDetails = new StringBuilder();
            for (Map.Entry<String, Integer> entry : inventoryMap.entrySet()) {
                inventoryDetails.append(entry.getKey())
                        .append(": ")
                        .append(entry.getValue())
                        .append("\n");
            }

            Serenity.recordReportData()
                    .withTitle("Store Inventory")
                    .andContents(inventoryDetails.toString());
        } catch (Exception e) {
            Serenity.recordReportData()
                    .withTitle("Inventory Request Error")
                    .andContents("Exception: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Then("I should get the inventory status")
    public void iShouldGetTheInventoryStatus() {
        then().statusCode(200);
        assertThat(inventoryMap).isNotNull();
        AssertionReporter.verifyNumberEquals("Status code", lastResponse().statusCode(), 200);
    }

    @And("the inventory should contain status counts")
    public void theInventoryShouldContainStatusCounts() {
        assertThat(inventoryMap).isNotEmpty();

        StringBuilder statusCounts = new StringBuilder();
        statusCounts.append("Inventory has ").append(inventoryMap.size()).append(" status categories\n");

        Serenity.recordReportData()
                .withTitle("Inventory Validation")
                .andContents(statusCounts.toString());

        for (String status : Arrays.asList("available", "pending", "sold")) {
            if (inventoryMap.containsKey(status)) {
                AssertionReporter.recordComparison(
                        "Inventory Count",
                        status,
                        "greater than 0",
                        inventoryMap.get(status)
                );
            }
        }
    }

    @When("I try to retrieve an order with ID {long}")
    public void iTryToRetrieveAnOrderWithID(long invalidId) {
        try {
            Response response = given()
                    .baseUri("https://petstore.swagger.io/v2")
                    .header("api_key", apiKey)
                    .when()
                    .get("/store/order/" + invalidId);

            Serenity.recordReportData()
                    .withTitle("Invalid Order Retrieval")
                    .andContents("Status code: " + response.getStatusCode() +
                            "\nResponse: " + response.getBody().asString());
        } catch (Exception e) {
            Serenity.recordReportData()
                    .withTitle("Order Retrieval Error")
                    .andContents("Exception: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @And("I should receive an error message with {string} for the order")
    public void iShouldReceiveAnErrorMessageForTheOrder(String expectedErrorMessage) {
        String responseBody = lastResponse().getBody().asString();

        try {
            //JSON path extraction
            String errorMessage = lastResponse().jsonPath().getString("message");
            assertThat(errorMessage.toLowerCase()).contains(expectedErrorMessage);

            Serenity.recordReportData()
                    .withTitle("Error Message")
                    .andContents("JSON Response: " + responseBody +
                            "\nExtracted Error Message: " + errorMessage);
        } catch (Exception e) {
            assertThat(responseBody.toLowerCase()).contains(expectedErrorMessage);

            Serenity.recordReportData()
                    .withTitle("Error Message (Raw)")
                    .andContents("JSON Response: " + responseBody +
                            "\nNote: JSON parsing failed, using raw string check");
        }
    }

    @When("I try to delete an order with ID {long}")
    public void iTryToDeleteAnOrderWithID(long invalidId) {
        try {
            Response response = given()
                    .baseUri("https://petstore.swagger.io/v2")
                    .header("api_key", apiKey)
                    .when()
                    .delete("/store/order/" + invalidId);

            Serenity.recordReportData()
                    .withTitle("Invalid Order Deletion")
                    .andContents("Status code: " + response.getStatusCode() +
                            "\nResponse: " + response.getBody().asString());
        } catch (Exception e) {
            Serenity.recordReportData()
                    .withTitle("Order Deletion Error")
                    .andContents("Exception: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Given("I have an invalid order with missing required fields")
    public void iHaveAnInvalidOrderWithMissingRequiredFields() {
        orderRequest = new OrderRequest(); // leave out required fields like petId
        orderRequest.setStatus("placed");

        Serenity.recordReportData()
                .withTitle("Invalid Order Request")
                .andContents("Deliberately missing required fields");
    }

    @When("I try to place the invalid order")
    public void iTryToPlaceTheInvalidOrder() {
        try {
            Response response = given()
                    .baseUri("https://petstore.swagger.io/v2")
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .header("api_key", apiKey)
                    .body(orderRequest)
                    .when()
                    .post("/store/order");

            Serenity.recordReportData()
                    .withTitle("Invalid Order Placement")
                    .andContents("Status code: " + response.getStatusCode() +
                            "\nResponse: " + response.getBody().asString());
        } catch (Exception e) {
            Serenity.recordReportData()
                    .withTitle("Order Placement Error")
                    .andContents("Exception: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Then("the order creation should fail")
    public void theOrderCreationShouldFail() {
        assertThat(lastResponse().getStatusCode()).isNotEqualTo(200); // The API might return 400

        AssertionReporter.recordComparison(
                "Order Creation",
                "Status code",
                "Not 200 (expected to fail)",
                lastResponse().getStatusCode()
        );
    }

    @And("I should get appropriate validation errors")
    public void iShouldGetAppropriateValidationErrors() {
        String responseBody = lastResponse().getBody().asString();

        assertThat(responseBody).containsAnyOf("error", "invalid", "missing");

        Serenity.recordReportData()
                .withTitle("Validation Error Response")
                .andContents(responseBody);
    }

}