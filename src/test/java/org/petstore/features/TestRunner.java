package org.petstore.features;

import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
        glue = "org.petstore.stepDefinition",
        features = "src/test/resources/features/getPet.feature",
        plugin = {"pretty", "junit:target/cucumber-reports/Cucumber.xml", "json:target/cucumber/Cucumber.json"},
        monochrome = true,
        snippets = CucumberOptions.SnippetType.CAMELCASE)

public class TestRunner {
}
//TODO create scenario outline, backgorund features