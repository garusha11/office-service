package ru.study.officeops.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class AuditEntry {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private LocalDateTime occurredAt = LocalDateTime.now();
    @Column(nullable = false) private String actor;
    @Column(nullable = false) private String action;
    @Column(nullable = false) private String target;
    protected AuditEntry() { }
    public AuditEntry(String actor, String action, String target) { this.actor = actor; this.action = action; this.target = target; }
    public Long getId() { return id; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
    public String getActor() { return actor; }
    public String getAction() { return action; }
    public String getTarget() { return target; }
}
