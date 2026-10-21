package ru.study.officeops.domain;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Role {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true) private String code;
    @Column(nullable = false) private String title;
    @ElementCollection(fetch = FetchType.EAGER) @CollectionTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role_id"))
    @Enumerated(EnumType.STRING) @Column(name = "permission", nullable = false)
    private Set<Permission> permissions = new HashSet<>();
    protected Role() { }
    public Role(String code, String title, Set<Permission> permissions) { this.code = code; this.title = title; this.permissions = new HashSet<>(permissions); }
    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getTitle() { return title; }
    public Set<Permission> getPermissions() { return permissions; }
    public void setPermissions(Set<Permission> permissions) { this.permissions.clear(); this.permissions.addAll(permissions); }
}
