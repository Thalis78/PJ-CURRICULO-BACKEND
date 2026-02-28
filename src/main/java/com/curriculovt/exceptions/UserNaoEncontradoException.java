package com.curriculovt.exceptions;

public class UserNaoEncontradoException extends RuntimeException {
    public UserNaoEncontradoException(String message) {
        super(message);
    }
}