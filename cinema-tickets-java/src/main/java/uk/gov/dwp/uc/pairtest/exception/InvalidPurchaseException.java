package uk.gov.dwp.uc.pairtest.exception;

/**
 * Exception thrown when a purchase is invalid
 * 
 * @param message the exception message
 * 
 */
public class InvalidPurchaseException extends RuntimeException {
    public InvalidPurchaseException(String message) {
        super(message);
    }
}