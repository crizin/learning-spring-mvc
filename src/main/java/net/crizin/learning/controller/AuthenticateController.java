package net.crizin.learning.controller;

import net.crizin.learning.entity.Member;
import net.crizin.learning.security.AuthenticationUser;
import net.crizin.learning.service.MemberService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class AuthenticateController extends AbstractController {
	private final AuthenticationManager authenticationManager;
	private final MemberService memberService;

	@Autowired
	public AuthenticateController(AuthenticationManager authenticationManager, MemberService memberService) {
		this.authenticationManager = authenticationManager;
		this.memberService = memberService;
	}

	@GetMapping("/log-in")
	public String logIn(Model model, @RequestParam(required = false) String error) {
		if (error != null) {
			model.addAttribute("error", "Invalid user name or password.");
		}

		return "authenticate/log-in";
	}

	@GetMapping("/sign-up")
	public String signUp() {
		return "authenticate/sign-up";
	}

	@PostMapping("/sign-up")
	public String signUp(HttpServletRequest request, Model model, @RequestParam String userName, @RequestParam String password1, @RequestParam String password2) {
		if (StringUtils.isAnyBlank(userName, password1, password2)) {
			throw new IllegalStateException();
		}

		if (!password1.equals(password2)) {
			model.addAttribute("userName", userName);
			model.addAttribute("error", "Password does not match the confirm password.");
			return "authenticate/sign-up";
		}

		Member member;

		try {
			member = memberService.createMember(userName, password1);
		} catch (DataIntegrityViolationException e) {
			model.addAttribute("error", "User name already taken.");
			return "authenticate/sign-up";
		}

		request.getSession();

		AuthenticationUser authenticationUser = new AuthenticationUser(member);

		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(member.getUserName(), password1, authenticationUser.getAuthorities());
		authenticationManager.authenticate(token);

		if (token.isAuthenticated()) {
			SecurityContextHolder.getContext().setAuthentication(authenticationManager.authenticate(token));
		}

		return "redirect:/note";
	}
}