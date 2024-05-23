package org.example.apiblitz.error;

public class TokenParsingException extends Exception {
	public TokenParsingException(Throwable cause) {
		super("Parsing token error: ", cause);
	}
}
