package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

public class ValidationService {

    private final AccountValidationService accountValidationService;
    private final TicketRequestValidationService ticketRequestValidationService;

    ValidationService() {
        accountValidationService = new AccountValidationService();
        ticketRequestValidationService = new TicketRequestValidationService();
    }

    void validate(Long accountId) throws InvalidPurchaseException {
        accountValidationService.validate(accountId);
    }

    void validate(TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        ticketRequestValidationService.validate(ticketTypeRequests);
    }
}
