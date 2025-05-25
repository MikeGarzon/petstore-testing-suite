package org.petstore.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.petstore.pojo.response.PetResponse;

import java.io.InputStream;

public class JsonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Loads a pet from a JSON file in the resources/payloads directory
     *
     * @param filename The name of the JSON file (without path)
     * @return PetResponse object populated with the JSON data
     */
    public static PetResponse loadPetFromJson(String filename) {
        try {
            InputStream inputStream = JsonUtils.class.getClassLoader()
                    .getResourceAsStream("payloads/" + filename);

            if (inputStream == null) {
                throw new RuntimeException("File not found: payloads/" + filename);
            }

            return objectMapper.readValue(inputStream, PetResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load pet from JSON file: " + filename, e);
        }
    }

    /**
     * Loads the known pet from the known_pet.json file
     *
     * @return PetResponse object representing the known pet
     */
    public static PetResponse loadKnownPet() {
        return loadPetFromJson("known_pet.json");
    }
}