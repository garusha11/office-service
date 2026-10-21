package ru.study.officeops.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.study.officeops.domain.Operator;
import ru.study.officeops.domain.Permission;
import ru.study.officeops.repository.OperatorRepository;

@Component("accessGuard")
public class AccessGuard {
    private final OperatorRepository operators;
    public AccessGuard(OperatorRepository operators) { this.operators = operators; }
    public boolean can(String permissionName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;
        try {
            Permission permission = Permission.valueOf(permissionName);
            return operators.findByUsername(auth.getName()).filter(Operator::isEnabled)
                    .map(user -> user.getRole().getPermissions().contains(permission)).orElse(false);
        } catch (IllegalArgumentException ex) { return false; }
    }
    public void require(Permission permission) {
        if (!can(permission.name())) throw new AccessDeniedException("Недостаточно прав: " + permission.name());
    }
    public String actor() { return SecurityContextHolder.getContext().getAuthentication().getName(); }
}
