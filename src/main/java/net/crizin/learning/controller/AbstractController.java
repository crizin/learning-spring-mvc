package net.crizin.learning.controller;

import net.crizin.learning.entity.Member;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractController {
	protected Member getCurrentMember(HttpServletRequest request) {
		return (Member) request.getAttribute("currentMember");
	}

	protected ModelAndView getErrorModelAndView(HttpServletResponse response, HttpStatus httpStatus, String errorMessage) {
		response.setStatus(httpStatus.value());

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("errorTitle", String.format("%d %s", httpStatus.value(), httpStatus.getReasonPhrase()));
		modelAndView.addObject("errorDetails", errorMessage);
		modelAndView.setViewName("error");

		return modelAndView;
	}

	protected Map<String, Object> respondJsonOk() {
		return Collections.singletonMap("success", true);
	}

	protected Map<String, Object> respondJsonError(String message) {
		Map<String, Object> response = new HashMap<>(2);
		response.put("success", false);
		response.put("message", message);
		return response;
	}
}