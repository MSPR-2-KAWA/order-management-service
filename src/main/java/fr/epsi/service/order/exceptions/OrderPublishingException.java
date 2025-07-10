package fr.epsi.service.order.exceptions;

public class OrderPublishingException extends RuntimeException {

    public OrderPublishingException(String message, Throwable cause) {
        super(message, cause);
    }

}
