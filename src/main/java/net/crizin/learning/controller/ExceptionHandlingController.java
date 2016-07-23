package net.crizin.learning.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class ExceptionHandlingController extends AbstractController {
	private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlingController.class);

	@ExceptionHandler(Exception.class)
	public ModelAndView handleError(HttpServletRequest request, HttpServletResponse response, Exception exception) {
		logger.error("Error: " + request.getRequestURL(), exception);

		HttpStatus responseStatus;

		ResponseStatus annotation = AnnotationUtils.findAnnotation(exception.getClass(), ResponseStatus.class);

		if (annotation == null) {
			if (exception instanceof NoHandlerFoundException) {
				responseStatus = HttpStatus.NOT_FOUND;
			} else {
				responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			}
		} else {
			responseStatus = annotation.code();
		}

		return getErrorModelAndView(response, responseStatus, exception.getMessage());
	}
}