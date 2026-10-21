package ru.study.officeops.workflow;
import org.springframework.stereotype.Component;
import ru.study.officeops.domain.*;
@Component
public class StartWork implements TicketCommand {
    public String code() { return "start"; }
    public String title() { return "Начать работу"; }
    public Permission permission() { return Permission.TICKET_PROGRESS; }
    public boolean supports(Ticket ticket, Operator actor) { return ticket.getStatus() == TicketStatus.ASSIGNED && ticket.getAssignee() != null && ticket.getAssignee().getUsername().equals(actor.getUsername()); }
    public void execute(Ticket ticket, Operator actor) { ticket.setStatus(TicketStatus.IN_PROGRESS); }
}
