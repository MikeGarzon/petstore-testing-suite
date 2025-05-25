Feature: Pet Management in the store
  As a store admin
  I want to manage pets in the store
  So that I can keep track of available pets

  Scenario: Add a new pet to the store
    Given I have pet details with following attributes
      | name     | category | status    | tags          |
      | Firulais | Dog      | available | friendly,cute |
    When I send request to add new pet
    Then the pet should be created successfully
    And the response should include the correct pet details

  Scenario: Retrieve pet by ID
    Given I use a known pet ID
    When I send request to get pet details
    Then I should receive the correct pet information
    And the pet status should be "BerneseMountainDog"