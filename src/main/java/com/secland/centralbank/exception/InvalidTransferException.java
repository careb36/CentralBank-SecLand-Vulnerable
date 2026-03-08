package com.secland.centralbank.exception;

public class InvalidTransferException extends BusinessException {
    public InvalidTransferException(String message) {
        super(message);
    }
}
