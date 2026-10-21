package ru.study.officeops.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "operators")
public class Operator {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true) private String username;
    @Column(nullable = false) private String displayName;
    @Column(nullable = false) private String passwordHash;
    @ManyToOne(optional = false, fetch = FetchType.EAGER) private Role role;
    @Column(nullable = false) private boolean enabled = true;
    protected Operator() { }
    public Operator(String username, String displayName, String passwordHash, Role role) {
        this.username = username; this.displayName = displayName; this.passwordHash = passwordHash; this.role = role;
    }
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getDisplayName() { return displayName; }
    public String getPasswordHash() { return passwordHash; }
    public Role getRole() { return role; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public void setRole(Role role) { this.role = role; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}
