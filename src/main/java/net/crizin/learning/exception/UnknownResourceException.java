package net.crizin.learning.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UnknownResourceException extends RuntimeException {
	public UnknownResourceException(String message) {
		super(message);
	}
}