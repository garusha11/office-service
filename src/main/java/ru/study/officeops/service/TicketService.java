package ru.study.officeops.service;

import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.study.officeops.domain.*;
import ru.study.officeops.repository.*;
import ru.study.officeops.security.AccessGuard;
import ru.study.officeops.workflow.TicketCommand;

@Service
public class TicketService {
    private final TicketRepository tickets;
    private final AssetRepository assets;
    private final OperatorRepository operators;
    private final AuditRepository audit;
    private final AccessGuard guard;
    private final List<TicketCommand> commands;
    public TicketService(TicketRepository tickets, AssetRepository assets, OperatorRepository operators, AuditRepository audit,
                         AccessGuard guard, List<TicketCommand> commands) {
        this.tickets = tickets; this.assets = assets; this.operators = operators; this.audit = audit; this.guard = guard; this.commands = commands;
    }
    @Transactional(readOnly = true) public List<Ticket> list() { guard.require(Permission.TICKET_READ); return tickets.findAll(); }
    @Transactional(readOnly = true) public Ticket get(Long id) { guard.require(Permission.TICKET_READ); return tickets.findById(id).orElseThrow(); }
    @Transactional public void create(String subject, String description, Long assetId) {
        guard.require(Permission.TICKET_CREATE);
        if (subject.isBlank() || description.isBlank()) throw new IllegalArgumentException("Заполните тему и описание");
        Ticket ticket = tickets.save(new Ticket(subject.trim(), description.trim(), assets.findById(assetId).orElseThrow()));
        audit.save(new AuditEntry(guard.actor(), "Создана заявка", "#" + ticket.getId()));
    }
    @Transactional public void assign(Long ticketId, Long operatorId) {
        guard.require(Permission.TICKET_ASSIGN);
        Ticket ticket = tickets.findById(ticketId).orElseThrow();
        if (ticket.getStatus() != TicketStatus.NEW) throw new IllegalArgumentException("Назначить можно только новую заявку");
        Operator operator = operators.findById(operatorId).orElseThrow();
        if (!operator.isEnabled() || !operator.getRole().getPermissions().contains(Permission.TICKET_PROGRESS))
            throw new IllegalArgumentException("Исполнитель должен быть активным и иметь право работать с заявками");
        ticket.assign(operator);
        audit.save(new AuditEntry(guard.actor(), "Назначен исполнитель", "Заявка #" + ticketId + " → " + operator.getUsername()));
    }
    @Transactional public void run(Long ticketId, String commandCode) {
        TicketCommand command = commands.stream().filter(c -> c.code().equals(commandCode)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Неизвестное действие"));
        guard.require(command.permission());
        Ticket ticket = tickets.findById(ticketId).orElseThrow();
        Operator actor = operators.findByUsername(guard.actor()).orElseThrow();
        if (!command.supports(ticket, actor)) throw new AccessDeniedException("Действие недоступно для этой заявки");
        command.execute(ticket, actor);
        audit.save(new AuditEntry(actor.getUsername(), command.title(), "Заявка #" + ticketId));
    }
    @Transactional(readOnly = true) public List<TicketCommand> available(Ticket ticket) {
        Operator actor = operators.findByUsername(guard.actor()).orElseThrow();
        return commands.stream().filter(c -> guard.can(c.permission().name()) && c.supports(ticket, actor)).toList();
    }
}
