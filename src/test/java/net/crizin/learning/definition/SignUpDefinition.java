package net.crizin.learning.definition;

import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.api.java8.En;
import net.crizin.learning.config.MvcConfig;
import net.crizin.learning.config.RootConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
@ContextConfiguration(classes = {RootConfig.class, MvcConfig.class})
public class SignUpDefinition implements En {
	@Autowired
	private WebApplicationContext webApplicationContext;
	@Autowired
	private Filter springSecurityFilterChain;

	private MockMvc mockMvc;
	private MvcResult mvcResult;
	private MockHttpSession mockHttpSession;
	private MultiValueMap<String, String> requestParameter = new LinkedMultiValueMap<>();


	@Before
	public void setup() {
		mockMvc = MockMvcBuilders
				.webAppContextSetup(webApplicationContext)
				.addFilter(springSecurityFilterChain)
				.build();

		mockHttpSession = new MockHttpSession(webApplicationContext.getServletContext());
	}

	@Given("^I am not logged in yet$")
	public void iAmNotLoggedInYet() throws Throwable {
		mvcResult = mockMvc.perform(get("/").session(mockHttpSession)).andReturn();
		assertTrue(mvcResult.getResponse().getContentAsString().contains("<a class=\"btn btn-secondary-outline pull-xs-right\" href=\"/log-in\">Log in</a>"));
	}

	@When("^I am on login page$")
	public void iAmOnLoginPage() throws Throwable {
		mvcResult = mockMvc.perform(get("/log-in").session(mockHttpSession)).andReturn();
	}

	@Then("^I should see sign up link$")
	public void iShouldSeeSignUpLink() throws Throwable {
		assertTrue(mvcResult.getResponse().getContentAsString().contains("<a class=\"btn btn-link pull-right\" href=\"/sign-up\">Sign up</a>"));
	}

	@When("^I click sign up link$")
	public void iClickSignUpLink() throws Throwable {
		mvcResult = mockMvc.perform(get("/sign-up").session(mockHttpSession)).andReturn();
	}

	@Then("^I should see sign up button$")
	public void iShouldSeeSignUpButton() throws Throwable {
		assertTrue(mvcResult.getResponse().getContentAsString().contains("<button type=\"submit\" class=\"btn btn-primary\">Sign Up</button>"));
	}

	@And("^I fill in \"([^\"]*)\" with \"([^\"]*)\"$")
	public void iFillInWith(String fieldName, String value) throws Throwable {
		requestParameter.put(fieldName, Collections.singletonList(value));
	}

	@And("^I click sign up button$")
	public void iClickSignUpButton() throws Throwable {
		mvcResult = mockMvc
				.perform(post("/sign-up")
						.with(csrf())
						.session(mockHttpSession)
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.params(requestParameter))
				.andReturn();

		requestParameter = new LinkedMultiValueMap<>();
	}

	@Then("^I redirected to my notes page$")
	public void iRedirectedToMyNotesPage() throws Throwable {
		assertEquals(HttpStatus.FOUND.value(), mvcResult.getResponse().getStatus());
		assertEquals("/note", mvcResult.getResponse().getRedirectedUrl());

		mvcResult = mockMvc.perform(get(mvcResult.getResponse().getRedirectedUrl()).session(mockHttpSession)).andExpect(status().isOk()).andReturn();
	}

	@And("^I should see \"([^\"]*)\" text$")
	public void iShouldSeeText(String text) throws Throwable {
		assertTrue(mvcResult.getResponse().getContentAsString().replaceAll("<[^>]+>", "").contains(text));
	}

	@Then("^I should see error message \"([^\"]*)\"$")
	public void iShouldSeeErrorMessage(String errorMessage) throws Throwable {
		assertTrue(mvcResult.getResponse().getContentAsString().contains("<div class=\"alert alert-danger\">" + errorMessage + "</div>"));
	}
}