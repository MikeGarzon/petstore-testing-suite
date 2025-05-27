Feature: Store Error Handling
  As a store manager
  I want to handle error scenarios gracefully
  So that the system remains robust

  Background:
    Given I am authenticated as a store manager
      | username | password |
      | manager  | store123 |

  Scenario Outline: Handle invalid order operations
    # Test retrieving a non-existent order
    When I try to retrieve an order with ID <invalid_id>
    Then I should get status code 404
    And I should receive an error message with "not found" for the order

    # Test deleting a non-existent order
    When I try to delete an order with ID <invalid_id>
    Then I should get status code 404
    And I should receive an error message with "not found" for the order

    Examples:
      | invalid_id |
      | 9999999    |
      | 8888888    |

  Scenario: Place an order with invalid data
    Given I have an invalid order with missing required fields
    When I try to place the invalid order
    Then the order creation should fail
    And I should get appropriate validation errors