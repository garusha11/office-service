package ru.study.officeops.workflow;
import org.springframework.stereotype.Component;
import ru.study.officeops.domain.*;
@Component
public class CloseTicket implements TicketCommand {
    public String code() { return "close"; }
    public String title() { return "Закрыть заявку"; }
    public Permission permission() { return Permission.TICKET_CLOSE; }
    public boolean supports(Ticket ticket, Operator actor) { return ticket.getStatus() == TicketStatus.RESOLVED; }
    public void execute(Ticket ticket, Operator actor) { ticket.setStatus(TicketStatus.CLOSED); }
}
