package ru.study.officeops.workflow;

import ru.study.officeops.domain.*;

public interface TicketCommand {
    String code();
    String title();
    Permission permission();
    boolean supports(Ticket ticket, Operator actor);
    void execute(Ticket ticket, Operator actor);
}
