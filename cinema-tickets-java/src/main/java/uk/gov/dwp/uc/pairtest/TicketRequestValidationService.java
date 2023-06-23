package uk.gov.dwp.uc.pairtest;

import java.util.Arrays;
import java.util.List;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

/**
 * Ticket Request Validation Service
 * 
 */
public class TicketRequestValidationService {
    private final int MAX_ALLOWED_TICKETS;
    private final List<TicketTypeRequest.Type> ticketTypes = Arrays
            .asList(TicketTypeRequest.Type.class.getEnumConstants());

    TicketRequestValidationService() {
        this.MAX_ALLOWED_TICKETS = 20;
    }

    TicketRequestValidationService(int maxAllowedTickets) {
        this.MAX_ALLOWED_TICKETS = maxAllowedTickets;
    }

    private static final TicketTypeRequestUtilities ticketTypeRequestUtilities = new TicketTypeRequestUtilities();

    /**
     * Wrapper method to validate ticket request, could be extended to validate
     * other details related to tickets
     * 
     * @param ticketTypeRequest ticket type request
     * 
     */
    public void validate(TicketTypeRequest... ticketTypeRequest) {
        validateTicketRequest(ticketTypeRequest);

        int childTickets = ticketTypeRequestUtilities.getTicketsCount(TicketTypeRequest.Type.CHILD, ticketTypeRequest);
        int adultTickets = ticketTypeRequestUtilities.getTicketsCount(TicketTypeRequest.Type.ADULT, ticketTypeRequest);
        int infantTickets = ticketTypeRequestUtilities.getTicketsCount(TicketTypeRequest.Type.INFANT,
                ticketTypeRequest);

        validateNoAdultTicketsCount(infantTickets, childTickets, adultTickets);

        validateInfantTicketsCount(infantTickets, adultTickets);

        int totalTickets = adultTickets + childTickets + infantTickets;
        validateMaxAllowedTicketsCount(totalTickets);
    }

    /**
     * Validates ticket request for
     * - null
     * - empty
     * - invalid ticket type
     * 
     * @param ticketTypeRequest ticket type request
     * @throws InvalidPurchaseException invalid purchase exception
     * 
     */
    private void validateTicketRequest(TicketTypeRequest... ticketTypeRequests) {
        // null or empty
        if (ticketTypeRequests == null || ticketTypeRequests.length == 0) {
            throw new InvalidPurchaseException("Ticket request cannot be null or empty");
        }

        for (TicketTypeRequest ticketTypeRequest : ticketTypeRequests) {
            // null ticket request
            if (ticketTypeRequest == null) {
                throw new InvalidPurchaseException("Ticket request cannot be null");
            }

            // invalid ticket type (null or not in ticket types)
            if (ticketTypeRequest.getTicketType() == null) {
                throw new InvalidPurchaseException("Ticket type cannot be null");
            }

            // invalid ticket type not defined in enum
            if (!ticketTypes.contains(ticketTypeRequest.getTicketType())) {
                throw new InvalidPurchaseException("Ticket type not defined");
            }

            // invalid ticket count
            if (ticketTypeRequest.getNoOfTickets() <= 0) {
                throw new InvalidPurchaseException("Ticket count should be greater than 0");
            }
        }
    }

    /**
     * Validates child or infant tickets are purchased with an adult ticket
     * 
     * @param infantTicketsCount infant tickets count
     * @param childTicketsCount  child tickets count
     * @param adultTicketsCount  adult tickets count
     * @throws InvalidPurchaseException invalid purchase exception
     * 
     */
    private void validateNoAdultTicketsCount(int infantTicketsCount, int childTicketsCount, int adultTicketsCount) {
        if (adultTicketsCount == 0) {
            if ((infantTicketsCount + childTicketsCount) > 0) {
                throw new InvalidPurchaseException("Child or infant tickets cannot be purchased without adult tickets");
            }
        }
    }

    /**
     * Validates infant tickets count is less than adult tickets count (infants sits
     * on adult's lap)
     * 
     * @param infantTicketsCount infant tickets count
     * @param adultTicketsCount  adult tickets count
     * @throws InvalidPurchaseException invalid purchase pxception
     * 
     */
    private void validateInfantTicketsCount(int infantTicketsCount, int adultTicketsCount) {
        if (infantTicketsCount > adultTicketsCount) {
            throw new InvalidPurchaseException("Infant tickets cannot be more than adult tickets");
        }
    }

    /**
     * Validates total number of tickets purchased is less than 20
     * 
     * @param totalTicketsCount total tickets count to purchase
     * @throws InvalidPurchaseException Invalid Purchase Exception
     * 
     */
    private void validateMaxAllowedTicketsCount(int totalTicketsCount) {
        if (totalTicketsCount > MAX_ALLOWED_TICKETS) {
            throw new InvalidPurchaseException("Maximum allowed tickets exceeded");
        }
    }
}
