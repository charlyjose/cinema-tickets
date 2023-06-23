package uk.gov.dwp.uc.pairtest;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

/**
 *
 * Validation rules:
 * - Account ID is valid (not 0 or negative)
 * - Number of tickets is less than or equal to 20
 * - Number of infant tickets is less than or equal to number of adult tickets
 * - Child or infant tickets are purchased with an adult ticket
 * - Number of child tickets is 0 if number of adult tickets is 0
 * - Number of infant tickets is 0 if number of adult tickets is 0
 * - Number of seats reserved = (no. of adult tickets + no. of child tickets)
 * - Total ticket cost = (no. of adult tickets * adult ticket price) + (no. of
 * - child tickets * child ticket price) + (no. of infant tickets * infant
 * ticket price)
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class TicketServiceImplTest {

    ValidationService validationService;
    TicketPurchaseService ticketPurchaseService;
    TicketServiceImpl ticketService;

    @Mock
    TicketPaymentService mockTicketPaymentService;

    @Mock
    SeatReservationService mockSeatReservationService;

    @Before
    public void setup() {
        validationService = new ValidationService();
        ticketPurchaseService = new TicketPurchaseService(mockTicketPaymentService, mockSeatReservationService);
        ticketService = new TicketServiceImpl(validationService, ticketPurchaseService);
    }

    /**
     * 
     * Invalid Account Id causes exception
     *
     * Parameters:
     * Account Id: NULL
     * Adult Tickets: 1
     *
     * Expected:
     * Exception: InvalidPurchaseException
     * Message: Invalid Account Id
     *
     * Reasoning:
     * Invalid account id (NULL is not a valid account id)
     * 
     */
    @Test
    public void invalidAccountIdNullRaiseException() {
        try {
            ticketService.purchaseTickets(null, new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 3));
        } catch (InvalidPurchaseException e) {
            assertEquals("Invalid Account Id", e.getMessage());

            Mockito.verifyNoInteractions(mockTicketPaymentService);
            Mockito.verifyNoInteractions(mockSeatReservationService);
        }
    }

    /**
     * 
     * Invalid Account Id causes exception
     * 
     * Parameters:
     * Account Id: 0
     * Adult Tickets: 3
     * 
     * Expected:
     * Exception: InvalidPurchaseException
     * Message: Invalid Account Id
     * 
     * Reasoning:
     * Invalid account id (0 is not a valid account id)
     * 
     */
    @Test
    public void invalidAccountIdZeroRaiseException() {
        try {
            ticketService.purchaseTickets(0L, new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 3));
        } catch (InvalidPurchaseException e) {
            assertEquals("Invalid Account Id", e.getMessage());

            Mockito.verifyNoInteractions(mockTicketPaymentService);
            Mockito.verifyNoInteractions(mockSeatReservationService);
        }
    }

    /**
     * 
     * Invalid Account Id causes exception
     * 
     * Parameters:
     * Account Id: -1
     * Adult Tickets: 3
     * 
     * Expected:
     * Exception: InvalidPurchaseException
     * Message: Invalid Account Id
     * 
     * Reasoning:
     * Invalid account id (negative values are not a permitted account id)
     * 
     */
    @Test
    public void invalidAccountIdNegativeRaiseException() {
        try {
            ticketService.purchaseTickets(-1L, new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 3));
        } catch (InvalidPurchaseException e) {
            assertEquals("Invalid Account Id", e.getMessage());

            Mockito.verifyNoInteractions(mockTicketPaymentService);
            Mockito.verifyNoInteractions(mockSeatReservationService);
        }
    }

    /**
     * 
     * Valid Account Id do not cause exception
     * 
     * Parameters:
     * Account Id: 1
     * Adult Tickets: 3
     * 
     * Expected:
     * Total Ticket Cost: 60
     * Total Seats Reserved: 3
     * No Exception
     * 
     * Reasoning:
     * Valid account id
     * Total tickets is less than 20
     * Infant tickets is less than adult tickets
     * Child and infant tickets are purchased with adult tickets
     * Number of seats reserved is equal to number of adult and child tickets
     * Total ticket cost is calculated correctly
     * 
     */
    @Test
    public void validAccountIdAboveZeroRaiseNoException() {
        ticketService.purchaseTickets(1L, new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 3));

        Mockito.verify(mockTicketPaymentService, Mockito.times(1)).makePayment(Mockito.anyLong(), Mockito.anyInt());
        Mockito.verify(mockSeatReservationService, Mockito.times(1)).reserveSeat(Mockito.anyLong(), Mockito.anyInt());

        Mockito.verify(mockTicketPaymentService).makePayment(1L, 60);
        Mockito.verify(mockSeatReservationService).reserveSeat(1L, 3);
    }

    /**
     * 
     * Valid ticket request do not cause exception
     * 
     * Parameters:
     * Account ID: 1
     * Adult Tickets: 6
     * Child Tickets: 5
     * Infant Tickets: 2
     * 
     * Expected:
     * Total Ticket Cost: 170
     * Total Seats Reserved: 11
     * No Exception
     * 
     * Reasoning:
     * Valid account id
     * Total tickets is less than 20
     * Infant tickets is less than adult tickets
     * Child and infant tickets are purchased with adult tickets
     * Number of seats reserved is equal to number of adult and child tickets
     * Total ticket cost is calculated correctly
     * 
     */
    @Test
    public void validTicketRequestRaiseNoException() {
        ticketService.purchaseTickets(1L,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 6),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 5),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2));

        Mockito.verify(mockTicketPaymentService, Mockito.times(1)).makePayment(Mockito.anyLong(), Mockito.anyInt());
        Mockito.verify(mockSeatReservationService, Mockito.times(1)).reserveSeat(Mockito.anyLong(), Mockito.anyInt());

        Mockito.verify(mockTicketPaymentService).makePayment(1L, 170);
        Mockito.verify(mockSeatReservationService).reserveSeat(1L, 11);
    }

    /**
     * 
     * Valid ticket request do not cause exception
     * 
     * Parameters:
     * Account ID: 5
     * Adult Tickets: 10
     * Child Tickets: 6
     * Infant Tickets: 4
     * 
     * Expected:
     * Total Ticket Cost: 260
     * Total Seats Reserved: 16
     * No Exception
     * 
     * Reasoning:
     * Valid account id
     * Total tickets is less than 20
     * Infant tickets is less than adult tickets
     * Child and infant tickets are purchased with adult tickets
     * Number of seats reserved is equal to number of adult and child tickets
     * Total ticket cost is calculated correctly
     * 
     */
    @Test
    public void validTicketRequestNotExceedingMaxAllowedTicketPurchaseLimitRaiseNoException() {
        ticketService.purchaseTickets(5L,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 10),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 6),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 4));

        Mockito.verify(mockTicketPaymentService, Mockito.times(1)).makePayment(Mockito.anyLong(), Mockito.anyInt());
        Mockito.verify(mockSeatReservationService, Mockito.times(1)).reserveSeat(Mockito.anyLong(), Mockito.anyInt());

        Mockito.verify(mockTicketPaymentService).makePayment(5L, 260);
        Mockito.verify(mockSeatReservationService).reserveSeat(5L, 16);
    }

    /**
     * 
     * Invalid ticket request causes exception with message "Ticket type cannot be
     * null"
     * 
     * Parameters:
     * Account ID: 3
     * Ticket Type: NULL
     * 
     * Adult Tickets: 7
     * 
     * Expected:
     * Exception: InvalidPurchaseException
     * 
     * Reasoning:
     * Ticket type is null
     * 
     */
    @Test
    public void invalidTicketRequestWithNullTicketTypeRaiseException() {
        try {
            ticketService.purchaseTickets(3L, new TicketTypeRequest(null, 7));
        } catch (InvalidPurchaseException e) {
            assertEquals("Ticket type cannot be null", e.getMessage());

            Mockito.verifyNoInteractions(mockTicketPaymentService);
            Mockito.verifyNoInteractions(mockSeatReservationService);
        }
    }

    /**
     * 
     * Invalid ticket request causes exception with message "Ticket should be
     * greater than 0"
     * 
     * Parameters:
     * Account ID: 3
     * Ticket Type: ADULT
     * Adult Tickets: -1
     * 
     * Expected:
     * Exception: InvalidPurchaseException
     * 
     * Reasoning:
     * Ticket count is negative
     * 
     */
    @Test
    public void invalidTicketRequestWithNegativeTicketCountRaiseException() {
        try {
            ticketService.purchaseTickets(3L, new TicketTypeRequest(TicketTypeRequest.Type.ADULT, -1));
        } catch (InvalidPurchaseException e) {
            assertEquals("Ticket count should be greater than 0", e.getMessage());

            Mockito.verifyNoInteractions(mockTicketPaymentService);
            Mockito.verifyNoInteractions(mockSeatReservationService);
        }
    }

    /**
     * 
     * Invalid ticket request causes exception with message "Ticket count should be
     * greater than 0"
     * 
     * Parameters:
     * Account ID: 3
     * Ticket Type: ADULT
     * Adult Tickets: 0
     * 
     * Expected:
     * Exception: InvalidPurchaseException
     * 
     * Reasoning:
     * Ticket count is zero
     * 
     */
    @Test
    public void invalidTicketRequestWithZeroTicketCountRaiseException() {
        try {
            ticketService.purchaseTickets(3L, new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 0));
        } catch (InvalidPurchaseException e) {
            assertEquals("Ticket count should be greater than 0", e.getMessage());

            Mockito.verifyNoInteractions(mockTicketPaymentService);
            Mockito.verifyNoInteractions(mockSeatReservationService);
        }
    }

    /**
     * 
     * Invalid ticket request causes exception with message "Ticket count should be
     * greater than 0"
     * 
     * Parameters:
     * Account ID: 3
     * Adult Tickets: 3
     * Child Tickets: -1
     * Infant Tickets: 0
     * 
     * Expected:
     * Exception: InvalidPurchaseException
     * 
     * Reasoning:
     * Ticket count is negative
     * 
     */
    @Test
    public void invalidTicketRequestWithNegativeAndZeroTicketCountRaiseException() {
        try {
            ticketService.purchaseTickets(3L,
                    new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 3),
                    new TicketTypeRequest(TicketTypeRequest.Type.CHILD, -1),
                    new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 0));
        } catch (InvalidPurchaseException e) {
            assertEquals("Ticket count should be greater than 0", e.getMessage());

            Mockito.verifyNoInteractions(mockTicketPaymentService);
            Mockito.verifyNoInteractions(mockSeatReservationService);
        }
    }

    /**
     * 
     * Invalid ticket request causes exception with message "Maximum allowed tickets
     * exceeded"
     * 
     * Parameters:
     * Account ID: 3
     * Adult Tickets: 7
     * Child Tickets: 7
     * Infant Tickets: 7
     * 
     * Expected:
     * Exception: InvalidPurchaseException
     * 
     * Reasoning:
     * Total tickets is more than 20 (7 + 7 + 7 = 21)
     * 
     */
    @Test
    public void invalidTicketRequestExceedingMaxAllowedTicketPurchaseLimitRaiseException() {
        try {
            ticketService.purchaseTickets(3L,
                    new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 7),
                    new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 7),
                    new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 7));
        } catch (InvalidPurchaseException e) {
            assertEquals("Maximum allowed tickets exceeded", e.getMessage());

            Mockito.verifyNoInteractions(mockTicketPaymentService);
            Mockito.verifyNoInteractions(mockSeatReservationService);
        }
    }

    /**
     * 
     * Invalid ticket request causes exception with message "Infant tickets cannot
     * be more than adult tickets"
     * 
     * Parameters:
     * Account ID: 8
     * Adult Tickets: 1
     * Infant Tickets: 2
     * 
     * Expected:
     * Exception: InvalidPurchaseException
     * 
     * Reasoning:
     * Infants sit on the lap of an adult and hence cannot be more than adult
     * tickets
     * 
     */
    @Test
    public void invalidTicketRequestInfantTicketsExceedingAdultTicketsRaiseException() {
        try {
            ticketService.purchaseTickets(8L,
                    new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                    new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2));
        } catch (InvalidPurchaseException e) {
            assertEquals("Infant tickets cannot be more than adult tickets", e.getMessage());

            Mockito.verifyNoInteractions(mockTicketPaymentService);
            Mockito.verifyNoInteractions(mockSeatReservationService);
        }
    }

    /**
     * 
     * Invalid ticket request causes exception with message "Child or infant tickets
     * cannot be purchased without adult tickets"
     * 
     * Parameters:
     * Account ID: 6
     * Child Tickets: 1
     * Infant Tickets: 1
     * 
     * Expected:
     * Exception: InvalidPurchaseException
     * 
     * Reasoning:
     * Child and infant tickets are purchased without adult tickets
     * 
     */
    @Test
    public void invalidTicketRequestWithNoAdultOnlyChildAndInfantRaiseException() {
        try {
            ticketService.purchaseTickets(6L,
                    new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1),
                    new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1));
        } catch (InvalidPurchaseException e) {
            assertEquals("Child or infant tickets cannot be purchased without adult tickets", e.getMessage());

            Mockito.verifyNoInteractions(mockTicketPaymentService);
            Mockito.verifyNoInteractions(mockSeatReservationService);
        }
    }

    /**
     * 
     * Invalid ticket request causes exception with message "Child or infant tickets
     * cannot be purchased without adult tickets"
     * 
     * Parameters:
     * Account ID: 2
     * Child Tickets: 1
     * 
     * Expected:
     * Exception: InvalidPurchaseException
     * 
     * Reasoning:
     * Child tickets are purchased without adult tickets
     * 
     */
    @Test
    public void invalidTicketRequestWithNoAdultOnlyChildRaiseException() {
        try {
            ticketService.purchaseTickets(2L, new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1));
        } catch (InvalidPurchaseException e) {
            assertEquals("Child or infant tickets cannot be purchased without adult tickets", e.getMessage());

            Mockito.verifyNoInteractions(mockTicketPaymentService);
            Mockito.verifyNoInteractions(mockSeatReservationService);
        }
    }

    /**
     * 
     * Invalid ticket request causes exception with message "Child or infant tickets
     * cannot be purchased without adult tickets"
     * 
     * Parameters:
     * Account ID: 9
     * Infant Tickets: 1
     * 
     * Expected:
     * Exception: InvalidPurchaseException
     * 
     * Reasoning:
     * Infant tickets are purchased without adult tickets
     * 
     */
    @Test
    public void invalidTicketRequestWithNoAdultOnlyInfantRaiseException() {
        try {
            ticketService.purchaseTickets(9L, new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1));
        } catch (InvalidPurchaseException e) {
            assertEquals("Child or infant tickets cannot be purchased without adult tickets", e.getMessage());

            Mockito.verifyNoInteractions(mockTicketPaymentService);
            Mockito.verifyNoInteractions(mockSeatReservationService);
        }
    }

    /**
     * 
     * Test to validate cost calculation for valid ticket request with one adult
     * 
     * Parameters:
     * Account ID: 10
     * Adult Tickets: 1
     * 
     * Expected:
     * Total Ticket Cost: 20
     * Total Seats Reserved: 1
     * 
     * Reasoning:
     * Seats reserved = no. of adult tickets (1)
     * Total ticket cost (1 * 20) = 20
     * 
     */
    @Test
    public void validTicketRequestForOneAdultRaiseNoException() {
        ticketService.purchaseTickets(10L, new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1));

        Mockito.verify(mockTicketPaymentService, Mockito.times(1)).makePayment(Mockito.anyLong(), Mockito.anyInt());
        Mockito.verify(mockSeatReservationService, Mockito.times(1)).reserveSeat(Mockito.anyLong(), Mockito.anyInt());

        Mockito.verify(mockTicketPaymentService).makePayment(10L, 20);
        Mockito.verify(mockSeatReservationService).reserveSeat(10L, 1);
    }

    /**
     * 
     * Test to validate cost calculation for valid ticket request with twenty adults
     * 
     * Parameters:
     * Account ID: 10
     * Adult Tickets: 20
     * 
     * Expected:
     * Total Ticket Cost: 400
     * Total Seats Reserved: 20
     * 
     * Reasoning:
     * Seats reserved = no. of adult tickets (20)
     * Total ticket cost (20 * 20) = 400
     * 
     */
    @Test
    public void validTicketRequestForTwentyAdultsRaiseNoException() {
        ticketService.purchaseTickets(10L, new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 20));

        Mockito.verify(mockTicketPaymentService, Mockito.times(1)).makePayment(Mockito.anyLong(), Mockito.anyInt());
        Mockito.verify(mockSeatReservationService, Mockito.times(1)).reserveSeat(Mockito.anyLong(), Mockito.anyInt());

        Mockito.verify(mockTicketPaymentService).makePayment(10L, 400);
        Mockito.verify(mockSeatReservationService).reserveSeat(10L, 20);
    }

    /**
     * 
     * Test to validate cost calculation for valid ticket request with one adult and
     * one infant
     * 
     * Parameters:
     * Account ID: 10
     * Adult Tickets: 1
     * Infant Tickets: 1
     * 
     * Expected:
     * Total Ticket Cost: 20
     * Total Seats Reserved: 1
     * 
     * Reasoning:
     * Seats reserved = no. of adult tickets (1)
     * Infant seats are not reserved
     * Total ticket cost is calculated correctly (1 * 20 = 20)
     * 
     */
    @Test
    public void validTicketRequestForOneAdultOneInfantRaiseNoException() {
        ticketService.purchaseTickets(10L,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1));

        Mockito.verify(mockTicketPaymentService, Mockito.times(1)).makePayment(Mockito.anyLong(), Mockito.anyInt());
        Mockito.verify(mockSeatReservationService, Mockito.times(1)).reserveSeat(Mockito.anyLong(), Mockito.anyInt());

        Mockito.verify(mockTicketPaymentService).makePayment(10L, 20);
        Mockito.verify(mockSeatReservationService).reserveSeat(10L, 1);
    }

    /**
     * 
     * Test to validate cost calculation for valid ticket request with one adult and
     * one child
     * 
     * Parameters:
     * Account ID: 10
     * Adult Tickets: 1
     * Child Tickets: 1
     * 
     * Expected:
     * Total Ticket Cost: 30
     * Total Seats Reserved: 2
     * 
     * Reasoning:
     * Seats reserved = no. of adult tickets + no. of child tickets (1 + 1 = 2)
     * Total ticket cost (1 * 20 + 1 * 10) = 30
     * 
     */
    @Test
    public void validTicketRequestForOneAdultOneChildRaiseNoException() {
        ticketService.purchaseTickets(10L,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1));

        Mockito.verify(mockTicketPaymentService, Mockito.times(1)).makePayment(Mockito.anyLong(), Mockito.anyInt());
        Mockito.verify(mockSeatReservationService, Mockito.times(1)).reserveSeat(Mockito.anyLong(), Mockito.anyInt());

        Mockito.verify(mockTicketPaymentService).makePayment(10L, 30);
        Mockito.verify(mockSeatReservationService).reserveSeat(10L, 2);
    }

    /**
     * 
     * Test to validate cost calculation for valid ticket request with one adult,
     * one child and one infant
     * 
     * Parameters:
     * Account ID: 10
     * Adult Tickets: 1
     * Child Tickets: 1
     * Infant Tickets: 1
     * 
     * Expected:
     * Total Ticket Cost: 30
     * Total Seats Reserved: 2
     * 
     * Reasoning:
     * Seats reserved = no. of adult tickets + no. of child tickets (1 + 1 = 2)
     * Infant seats are not reserved
     * Total ticket cost (1 * 20 + 1 * 10 + 1 * 0) = 30
     * 
     */
    @Test
    public void validTicketRequestForOneAdultOneChildOneInfantRaiseNoException() {
        ticketService.purchaseTickets(10L,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1));

        Mockito.verify(mockTicketPaymentService, Mockito.times(1)).makePayment(Mockito.anyLong(), Mockito.anyInt());
        Mockito.verify(mockSeatReservationService, Mockito.times(1)).reserveSeat(Mockito.anyLong(), Mockito.anyInt());

        Mockito.verify(mockTicketPaymentService).makePayment(10L, 30);
        Mockito.verify(mockSeatReservationService).reserveSeat(10L, 2);
    }
}
