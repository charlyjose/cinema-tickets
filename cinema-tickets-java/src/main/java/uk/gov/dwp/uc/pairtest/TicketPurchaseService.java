package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

public class TicketPurchaseService {
    private final TicketPaymentService ticketPaymentService;
    private final SeatReservationService seatReservationService;

    public TicketPurchaseService(TicketPaymentService ticketPaymentService,
            SeatReservationService seatReservationService) {
        this.ticketPaymentService = ticketPaymentService;
        this.seatReservationService = seatReservationService;
    }

    /**
     * 
     * Purchase tickets for a given accountId and ticketType
     * 
     * @param accountId account Id
     * @param ticketTypeRequests ticket type request array
     * 
     */
    public void purchase(Long accountId, TicketTypeRequest... ticketTypeRequests) {
        int totalCost = getTotalCost(ticketTypeRequests);
        int totalSeats = getTotalSeatsRequired(ticketTypeRequests);

        seatReservationService.reserveSeat(accountId, totalSeats);
        ticketPaymentService.makePayment(accountId, totalCost);
    }

    /**
     * Get total cost of ticket requests
     * 
     * @param ticketTypeRequests ticket type request array
     * @return total cost of tickets
     * 
     */
    private int getTotalCost(TicketTypeRequest... ticketTypeRequests) {
        int totalCost = 0;

        for (TicketTypeRequest ticketTypeRequest : ticketTypeRequests) {
            totalCost += ticketTypeRequest.getTotalPrice();
        }
        return totalCost;
    }

    /**
     * Get total seats required for ticket requests
     * 
     * @param ticketTypeRequests ticket type request
     * @return total seats required
     * 
     */
    private int getTotalSeatsRequired(TicketTypeRequest... ticketTypeRequests) {
        int totalSeats = 0;

        for (TicketTypeRequest ticketTypeRequest : ticketTypeRequests) {
            // Exclude infants from seat reservation count
            if (!ticketTypeRequest.getTicketType().equals(TicketTypeRequest.Type.INFANT)) {
                totalSeats += ticketTypeRequest.getNoOfTickets();
            }
        }
        return totalSeats;
    }
}
