package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

public class TicketTypeRequestUtilities {

    /**
     * Get Tickets Count By Ticket Type
     * 
     * @param ticketType ticket type
     * @param ticketTypeRequests ticket type request array
     * @return total tickets count by ticket type
     * 
     */
    public int getTicketsCount(TicketTypeRequest.Type ticketType, TicketTypeRequest... ticketTypeRequests) {
        int totalTicketsCount = 0;
        for (TicketTypeRequest ticketTypeRequest : ticketTypeRequests) {
            if (ticketTypeRequest.getTicketType().equals(ticketType)) {
                totalTicketsCount += ticketTypeRequest.getNoOfTickets();
            }
        }
        return totalTicketsCount;
    }
}
