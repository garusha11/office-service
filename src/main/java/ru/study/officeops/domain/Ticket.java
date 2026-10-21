package ru.study.officeops.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Ticket {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String subject;
    @Column(nullable = false, length = 2000) private String description;
    @ManyToOne(optional = false) private Asset asset;
    @ManyToOne private Operator assignee;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private TicketStatus status = TicketStatus.NEW;
    @Column(nullable = false) private LocalDateTime createdAt = LocalDateTime.now();
    protected Ticket() { }
    public Ticket(String subject, String description, Asset asset) { this.subject = subject; this.description = description; this.asset = asset; }
    public Long getId() { return id; }
    public String getSubject() { return subject; }
    public String getDescription() { return description; }
    public Asset getAsset() { return asset; }
    public Operator getAssignee() { return assignee; }
    public TicketStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void assign(Operator assignee) { this.assignee = assignee; this.status = TicketStatus.ASSIGNED; }
    public void setStatus(TicketStatus status) { this.status = status; }
}
