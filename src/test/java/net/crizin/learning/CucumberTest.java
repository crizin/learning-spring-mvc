package net.crizin.learning;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
		features = "classpath:feature",
		plugin = {"pretty", "html:build/cucumber"},
		glue = {"net.crizin.learning.definition", "cucumber.api.spring"})
public class CucumberTest {}