package ru.study.officeops.workflow;
import org.springframework.stereotype.Component;
import ru.study.officeops.domain.*;
@Component
public class ResolveTicket implements TicketCommand {
    public String code() { return "resolve"; }
    public String title() { return "Отметить решённой"; }
    public Permission permission() { return Permission.TICKET_PROGRESS; }
    public boolean supports(Ticket ticket, Operator actor) { return ticket.getStatus() == TicketStatus.IN_PROGRESS && ticket.getAssignee() != null && ticket.getAssignee().getUsername().equals(actor.getUsername()); }
    public void execute(Ticket ticket, Operator actor) { ticket.setStatus(TicketStatus.RESOLVED); }
}
