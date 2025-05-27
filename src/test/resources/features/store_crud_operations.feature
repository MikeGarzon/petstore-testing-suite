Feature: Store Order Management
  As a store manager
  I want to manage orders in the pet store
  So that I can track customer purchases

  Background:
    Given I am authenticated as a store manager
      | username | password |
      | manager  | store123 |

  Scenario Outline: Full CRUD operations for store orders
    # Create an order
    Given I have an order with the following details
      | petId    | quantity | status   | complete |
      | <pet_id> | <qty>    | <status> | <done>   |
    When I send request to place the order
    Then the order should be created successfully
    And I should get a valid order ID

    # Read the order
    When I retrieve the order by ID
    Then I should get status code 200
    And the order details should match the created order

    # Update the order
    When I update the order status to "approved"
    Then the order should be updated successfully
    And the updated order should have status "approved"

    # Delete the order
    When I delete the order
    Then the order should be deleted successfully
    And I should not be able to retrieve the order

    Examples:
      | pet_id | qty | status  | done  |
      | 1      | 2   | placed  | false |
      | 5      | 1   | placed  | false |
      | 10     | 5   | placed  | false |

  Scenario: Get store inventory
    When I request the store inventory
    Then I should get the inventory status
    And the inventory should contain status counts