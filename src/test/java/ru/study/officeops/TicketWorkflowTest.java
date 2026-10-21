package ru.study.officeops;

import java.util.EnumSet;
import org.junit.jupiter.api.Test;
import ru.study.officeops.domain.*;
import ru.study.officeops.workflow.*;
import static org.junit.jupiter.api.Assertions.*;

class TicketWorkflowTest {
    @Test void aTicketMustFollowItsWorkflowAndOnlyAssigneeCanWork() {
        Role role = new Role("TECHNICIAN", "Техник", EnumSet.of(Permission.TICKET_PROGRESS));
        Operator technician = new Operator("tech", "Техник", "hash", role);
        Operator stranger = new Operator("other", "Другой", "hash", role);
        Asset asset = new Asset("INV-1", "Проектор", "Офис");
        Ticket ticket = new Ticket("Не работает", "Нет изображения", asset);
        TicketCommand start = new StartWork();
        TicketCommand resolve = new ResolveTicket();
        TicketCommand close = new CloseTicket();
        assertFalse(start.supports(ticket, technician));
        ticket.assign(technician);
        assertFalse(start.supports(ticket, stranger));
        assertTrue(start.supports(ticket, technician));
        assertFalse(resolve.supports(ticket, technician));
        start.execute(ticket, technician);
        assertTrue(resolve.supports(ticket, technician));
        resolve.execute(ticket, technician);
        assertTrue(close.supports(ticket, stranger));
        close.execute(ticket, stranger);
        assertEquals(TicketStatus.CLOSED, ticket.getStatus());
        assertFalse(start.supports(ticket, technician));
    }
}
