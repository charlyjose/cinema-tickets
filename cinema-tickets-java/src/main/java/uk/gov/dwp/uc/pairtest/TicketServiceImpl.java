package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */

    private final ValidationService validationService;
    private final TicketPurchaseService ticketPurchaseService;

    TicketServiceImpl(ValidationService validationService, TicketPurchaseService ticketPurchaseService) {
        this.validationService = validationService;
        this.ticketPurchaseService = ticketPurchaseService;
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests)
            throws InvalidPurchaseException {

        validationService.validate(accountId);
        validationService.validate(ticketTypeRequests);
        ticketPurchaseService.purchase(accountId, ticketTypeRequests);
    }
}
