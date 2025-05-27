Feature: Pet Status Management
  As a pet store manager
  I want to update pet statuses
  So that I can track their availability

  Background:
    Given I use a known pet ID

  Scenario Outline: Update pet status with different values
    When I update the pet status to "<status>"
    Then the update should be successful
    And when I retrieve the pet details
    Then the pet status should be "<status>"

    Examples:
      | status     |
      | available  |
      | pending    |
      | sold       |
      | quarantine |