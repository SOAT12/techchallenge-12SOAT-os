package com.fiap.soat12.os.cleanarch.exception;

public class BadCredentialsException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String USER_MESSAGE = "CREDENCIAL INVALIDA!";

    public BadCredentialsException(String message, Throwable cause) {
        super(USER_MESSAGE + ": " + message, cause);
    }

}



