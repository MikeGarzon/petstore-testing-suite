Feature: User Management
  As a system administrator
  I want to manage user accounts
  So that I can control access to the pet store

  Background:
    Given I have admin credentials
      | username | password    |
      | test    | abc123    |

  Scenario: Create a new user account
    Given I have user details
      | username  | firstName | lastName | email           | password | phone      | userStatus |
      | mgarzon | Maicol      | Garzon     | mgarzon@example.com| test123  | 1234567890 | 1          |
    When I send request to create a new user
    Then the user should be created successfully
    And I can login with the new user credentials

  Scenario: Delete a user account
    Given I have an existing user "testuser1"
    When I send request to delete the user
    Then the user should be deleted successfully
    And I cannot login with the deleted user credentials