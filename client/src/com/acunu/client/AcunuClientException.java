package com.acunu.client;

/**
 * Exception thrown on the client in response to server or transport errors.
 * 
 * @author rallison
 *
 */
public class AcunuClientException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AcunuClientException() {
		super();
	}

	public AcunuClientException(String message, Throwable cause) {
		super(message, cause);
	}

	public AcunuClientException(String message) {
		super(message);
	}

	public AcunuClientException(Throwable cause) {
		super(cause);
	}

}
