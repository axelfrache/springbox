package io.github.axelfrache.springbox.controller;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.github.axelfrache.springbox.exception.UserAlreadyExistsException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(UserAlreadyExistsException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public String handleUserAlreadyExistsException(UserAlreadyExistsException ex, Model model) {
		model.addAttribute("error", ex.getMessage());
		return "error";
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public String handleGeneralException(Exception ex, Model model) {
		model.addAttribute("error", "An unexpected error occurred: " + ex.getMessage());
		return "error";
	}
}
