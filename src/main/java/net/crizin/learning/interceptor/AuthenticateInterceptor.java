package net.crizin.learning.interceptor;

import net.crizin.learning.security.AuthenticationUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AuthenticateInterceptor extends HandlerInterceptorAdapter {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		HttpSession session = request.getSession(false);

		if (session != null) {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication != null && authentication.getPrincipal() instanceof AuthenticationUser) {
				AuthenticationUser authenticationUser = (AuthenticationUser) authentication.getPrincipal();
				if (authenticationUser != null) {
					request.setAttribute("currentMember", authenticationUser.getMember());
				}
			}
		}

		return super.preHandle(request, response, handler);
	}
}