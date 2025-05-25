package org.petstore.utils;

import net.serenitybdd.core.Serenity;
import net.serenitybdd.annotations.Step;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * Utility class for creating detailed assertions in Serenity reports.
 * This class provides methods to verify different types of values and display
 * the actual and expected values in the Serenity reports.
 */
public class AssertionReporter {

    /**
     * Verifies that a string value matches the expected value.
     *
     * @param fieldName     Name of the field being verified
     * @param actualValue   The actual value
     * @param expectedValue The expected value
     */
    @Step("Verify that {0} '{1}' matches expected value '{2}'")
    public static void verifyStringEquals(String fieldName, String actualValue, String expectedValue) {
        boolean result = Objects.equals(actualValue, expectedValue);

        String message = String.format("Field '%s' - Expected: '%s', Actual: '%s'",
                fieldName, expectedValue, actualValue);

        Serenity.reportThat(message, () -> org.assertj.core.api.Assertions.assertThat(actualValue)
                .isEqualTo(expectedValue));
    }

    /**
     * Verifies that a number value matches the expected value.
     *
     * @param fieldName     Name of the field being verified
     * @param actualValue   The actual value
     * @param expectedValue The expected value
     */
    @Step("Verify that {0} '{1}' matches expected value '{2}'")
    public static void verifyNumberEquals(String fieldName, Number actualValue, Number expectedValue) {
        String message = String.format("Field '%s' - Expected: '%s', Actual: '%s'",
                fieldName, expectedValue, actualValue);

        Serenity.reportThat(message, () -> org.assertj.core.api.Assertions.assertThat(actualValue)
                .isEqualTo(expectedValue));
    }

    /**
     * Verifies that a long value matches the expected value.
     *
     * @param fieldName     Name of the field being verified
     * @param actualValue   The actual value
     * @param expectedValue The expected value
     */
    @Step("Verify that {0} '{1}' matches expected value '{2}'")
    public static void verifyLongEquals(String fieldName, Long actualValue, Long expectedValue) {
        String message = String.format("Field '%s' - Expected: '%s', Actual: '%s'",
                fieldName, expectedValue, actualValue);

        Serenity.reportThat(message, () -> org.assertj.core.api.Assertions.assertThat(actualValue)
                .isEqualTo(expectedValue));
    }

    /**
     * Verifies that a boolean value matches the expected value.
     *
     * @param fieldName     Name of the field being verified
     * @param actualValue   The actual value
     * @param expectedValue The expected value
     */
    @Step("Verify that {0} '{1}' matches expected value '{2}'")
    public static void verifyBooleanEquals(String fieldName, Boolean actualValue, Boolean expectedValue) {
        String message = String.format("Field '%s' - Expected: '%s', Actual: '%s'",
                fieldName, expectedValue, actualValue);

        Serenity.reportThat(message, () -> org.assertj.core.api.Assertions.assertThat(actualValue)
                .isEqualTo(expectedValue));
    }

    /**
     * Verifies that an object value matches the expected value.
     *
     * @param fieldName     Name of the field being verified
     * @param actualValue   The actual value
     * @param expectedValue The expected value
     */
    @Step("Verify that {0} matches expected value")
    public static <T> void verifyObjectEquals(String fieldName, T actualValue, T expectedValue) {
        String message = String.format("Field '%s' - Expected: '%s', Actual: '%s'",
                fieldName, expectedValue, actualValue);

        Serenity.reportThat(message, () -> org.assertj.core.api.Assertions.assertThat(actualValue)
                .isEqualTo(expectedValue));
    }

    /**
     * Verifies that a collection contains the expected values.
     *
     * @param fieldName     Name of the collection being verified
     * @param actualValues  The actual collection
     * @param expectedValue A value that should be in the collection
     */
    @Step("Verify that {0} contains expected value '{2}'")
    public static <T> void verifyCollectionContains(String fieldName, Collection<T> actualValues, T expectedValue) {
        String message = String.format("Collection '%s' should contain: '%s'", fieldName, expectedValue);

        Serenity.reportThat(message, () -> org.assertj.core.api.Assertions.assertThat(actualValues)
                .contains(expectedValue));
    }

    /**
     * Verifies that a value contains a specific substring.
     *
     * @param fieldName    Name of the field being verified
     * @param actualValue  The actual string value
     * @param expectedPart The substring that should be contained
     */
    @Step("Verify that {0} '{1}' contains '{2}'")
    public static void verifyStringContains(String fieldName, String actualValue, String expectedPart) {
        String message = String.format("Field '%s' with value '%s' should contain '%s'",
                fieldName, actualValue, expectedPart);

        Serenity.reportThat(message, () -> org.assertj.core.api.Assertions.assertThat(actualValue)
                .contains(expectedPart));
    }

    /**
     * Verifies that a value matches a specific pattern.
     *
     * @param fieldName   Name of the field being verified
     * @param actualValue The actual string value
     * @param pattern     The regex pattern to match against
     */
    @Step("Verify that {0} '{1}' matches pattern '{2}'")
    public static void verifyStringMatches(String fieldName, String actualValue, String pattern) {
        String message = String.format("Field '%s' with value '%s' should match pattern '%s'",
                fieldName, actualValue, pattern);

        Serenity.reportThat(message, () -> org.assertj.core.api.Assertions.assertThat(actualValue)
                .matches(pattern));
    }

    /**
     * Records raw JSON or complex data in the Serenity report.
     *
     * @param title    Title for the report section
     * @param contents The contents to record
     */
    public static void recordJsonData(String title, String contents) {
        Serenity.recordReportData()
                .withTitle(title)
                .andContents(contents);
    }

    /**
     * Records a key-value map as evidence in the Serenity report.
     *
     * @param title Title for the report section
     * @param data  Map of data to display
     */
    public static void recordMapData(String title, Map<String, Object> data) {
        StringBuilder content = new StringBuilder();

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            content.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append("\n");
        }

        Serenity.recordReportData()
                .withTitle(title)
                .andContents(content.toString());
    }

    /**
     * Verifies that a status code matches the expected value.
     *
     * @param actualStatusCode   The actual HTTP status code
     * @param expectedStatusCode The expected HTTP status code
     */
    @Step("Verify HTTP status code")
    public static void verifyStatusCode(int actualStatusCode, int expectedStatusCode) {
        String message = String.format("HTTP Status Code - Expected: %d, Actual: %d",
                expectedStatusCode, actualStatusCode);

        Serenity.reportThat(message, () -> org.assertj.core.api.Assertions.assertThat(actualStatusCode)
                .isEqualTo(expectedStatusCode));
    }

    /**
     * Records a comparison between expected and actual values with formatting.
     *
     * @param title         Title for the comparison
     * @param fieldName     Name of the field being compared
     * @param expectedValue The expected value
     * @param actualValue   The actual value
     */
    public static void recordComparison(String title, String fieldName, Object expectedValue, Object actualValue) {
        Serenity.recordReportData().asEvidence()
                .withTitle(title)
                .andContents(String.format("%s:\n- Expected: %s\n- Actual: %s",
                        fieldName, expectedValue, actualValue));
    }
}