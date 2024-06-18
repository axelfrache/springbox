package io.github.axelfrache.springbox.exception;

public class UserAlreadyExistsException extends RuntimeException {
	public UserAlreadyExistsException() {
		super("An account with this email already exists.");
	}
}