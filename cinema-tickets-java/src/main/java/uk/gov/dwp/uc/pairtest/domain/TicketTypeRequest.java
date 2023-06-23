package uk.gov.dwp.uc.pairtest.domain;

/**
 * Immutable Object
 */

public class TicketTypeRequest {

    private final int noOfTickets;
    private final Type type;

    public TicketTypeRequest(Type type, int noOfTickets) {
        this.type = type;
        this.noOfTickets = noOfTickets;
    }

    public int getNoOfTickets() {
        return noOfTickets;
    }

    public Type getTicketType() {
        return type;
    }

    /**
     * Get price of ticket type
     * 
     * @return price of ticket type
     * 
     */
    public int getPrice() {
        return type.price;
    }

    /**
     * Get total price of tickets
     * 
     * @return total price of tickets
     * 
     */
    public int getTotalPrice() {
        return this.getPrice() * noOfTickets;
    }

    public enum Type {
        ADULT(20), CHILD(10), INFANT(0);

        public final int price;

        private Type(int price) {
            this.price = price;
        }
    }
}
