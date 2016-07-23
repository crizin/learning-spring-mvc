package net.crizin.learning.controller;

import net.crizin.learning.AbstractControllerTest;
import net.crizin.learning.config.MvcConfig;
import net.crizin.learning.config.RootConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootConfig.class, MvcConfig.class})
public class AuthenticateControllerTest extends AbstractControllerTest {
	@Test
	public void testSignUpSuccess() throws Exception {
		mockMvc
				.perform(post("/sign-up")
						.with(csrf())
						.param("userName", userName)
						.param("password1", password)
						.param("password2", password))
				.andExpect(authenticated().withUsername(userName))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/note"));

	}

	@Test
	public void testSignUpFailedByDuplicateUserName() throws Exception {
		memberService.createMember(userName, password);

		mockMvc
				.perform(post("/sign-up")
						.with(csrf())
						.param("userName", userName)
						.param("password1", password)
						.param("password2", password))
				.andExpect(unauthenticated())
				.andExpect(status().isOk())
				.andExpect(model().attribute("error", "User name already taken."));
	}

	@Test
	public void testSignUpFailedByMisMatchingConfirmPassword() throws Exception {
		mockMvc
				.perform(post("/sign-up")
						.with(csrf())
						.param("userName", userName)
						.param("password1", password)
						.param("password2", "invalid-password"))
				.andExpect(unauthenticated())
				.andExpect(status().isOk())
				.andExpect(model().attribute("error", "Password does not match the confirm password."));
	}

	@Test
	public void testLoginSuccess() throws Exception {
		memberService.createMember(userName, password);

		mockMvc.perform(formLogin("/log-in-process")
				.user("userName", userName)
				.password("password", password))
				.andExpect(authenticated().withUsername(userName))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/"));
	}

	@Test
	public void testLoginFailed() throws Exception {
		mockMvc.perform(formLogin("/log-in-process")
				.user("userName", userName)
				.password("password", password))
				.andExpect(unauthenticated())
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/log-in?error"));
	}

	@Test
	public void testLogout() throws Exception {
		mockMvc.perform(logout("/log-out"))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/"));
	}
}