package ru.study.officeops.service;

import java.util.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.study.officeops.domain.*;
import ru.study.officeops.repository.*;
import ru.study.officeops.security.AccessGuard;

@Service
public class AdminService {
    private final AssetRepository assets;
    private final OperatorRepository operators;
    private final RoleRepository roles;
    private final AuditRepository audit;
    private final AccessGuard guard;
    private final PasswordEncoder encoder;
    public AdminService(AssetRepository assets, OperatorRepository operators, RoleRepository roles, AuditRepository audit,
                        AccessGuard guard, PasswordEncoder encoder) {
        this.assets = assets; this.operators = operators; this.roles = roles; this.audit = audit; this.guard = guard; this.encoder = encoder;
    }
    @Transactional(readOnly = true) public List<Asset> assets() { guard.require(Permission.ASSET_READ); return assets.findAll(); }
    @Transactional public void addAsset(String inventoryNumber, String name, String location) {
        guard.require(Permission.ASSET_WRITE);
        if (inventoryNumber.isBlank() || name.isBlank() || location.isBlank()) throw new IllegalArgumentException("Заполните все поля оборудования");
        if (assets.findByInventoryNumber(inventoryNumber.trim()).isPresent()) throw new IllegalArgumentException("Инвентарный номер занят");
        Asset asset = assets.save(new Asset(inventoryNumber.trim(), name.trim(), location.trim()));
        audit.save(new AuditEntry(guard.actor(), "Добавлено оборудование", asset.getInventoryNumber()));
    }
    @Transactional(readOnly = true) public List<Role> roles() { guard.require(Permission.ACCESS_MANAGE); return roles.findAll(); }
    @Transactional(readOnly = true) public List<Operator> operators() { guard.require(Permission.ACCESS_MANAGE); return operators.findAll(); }
    @Transactional(readOnly = true) public List<Operator> availableAssignees() {
        guard.require(Permission.TICKET_ASSIGN);
        return operators.findAll().stream().filter(Operator::isEnabled)
                .filter(op -> op.getRole().getPermissions().contains(Permission.TICKET_PROGRESS)).toList();
    }
    @Transactional public void updatePermissions(Long roleId, Set<Permission> selected) {
        guard.require(Permission.ACCESS_MANAGE);
        Role role = roles.findById(roleId).orElseThrow();
        if (role.getCode().equals("ADMIN")) throw new IllegalArgumentException("Права главного администратора фиксированы");
        if (selected.contains(Permission.ACCESS_MANAGE)) throw new IllegalArgumentException("Выдавать управление доступом может только главный администратор");
        role.setPermissions(selected);
        audit.save(new AuditEntry(guard.actor(), "Изменены права роли", role.getCode() + ": " + selected));
    }
    @Transactional public void addOperator(String username, String displayName, String password, Long roleId) {
        guard.require(Permission.ACCESS_MANAGE);
        if (!username.matches("[a-zA-Z0-9._-]{3,40}") || displayName.isBlank() || password.length() < 12)
            throw new IllegalArgumentException("Логин: 3–40 латинских символов; имя обязательно; пароль от 12 символов");
        if (operators.findByUsername(username).isPresent()) throw new IllegalArgumentException("Логин занят");
        Role role = roles.findById(roleId).orElseThrow();
        if (role.getCode().equals("ADMIN")) throw new IllegalArgumentException("Создание дополнительного ADMIN запрещено");
        operators.save(new Operator(username, displayName.trim(), encoder.encode(password), role));
        audit.save(new AuditEntry(guard.actor(), "Создан оператор", username));
    }
    @Transactional public void changeOperator(Long operatorId, Long roleId, boolean enabled) {
        guard.require(Permission.ACCESS_MANAGE);
        Operator operator = operators.findById(operatorId).orElseThrow();
        if (operator.getUsername().equals("admin")) throw new IllegalArgumentException("Главного администратора нельзя изменить");
        Role role = roles.findById(roleId).orElseThrow();
        if (role.getCode().equals("ADMIN")) throw new IllegalArgumentException("Назначение ADMIN запрещено");
        operator.setRole(role); operator.setEnabled(enabled);
        audit.save(new AuditEntry(guard.actor(), "Изменён оператор", operator.getUsername() + ": " + role.getCode() + ", enabled=" + enabled));
    }
    @Transactional(readOnly = true) public List<AuditEntry> audit() { guard.require(Permission.AUDIT_READ); return audit.findTop100ByOrderByOccurredAtDesc(); }
}
