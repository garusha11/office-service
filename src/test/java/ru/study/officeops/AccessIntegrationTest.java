package ru.study.officeops;

import java.util.EnumSet;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import ru.study.officeops.domain.Permission;
import ru.study.officeops.repository.RoleRepository;
import ru.study.officeops.security.AccessGuard;
import ru.study.officeops.service.AdminService;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {"spring.datasource.url=jdbc:h2:mem:access-test;DB_CLOSE_DELAY=-1", "office.admin.password=AdminPassword123!"})
class AccessIntegrationTest {
    @Autowired AdminService admin;
    @Autowired AccessGuard guard;
    @Autowired RoleRepository roles;

    @Test void rightsChangeAffectsAlreadyAuthenticatedOperator() {
        Long auditorId = roles.findByCode("AUDITOR").orElseThrow().getId();
        authenticate("admin");
        admin.addOperator("auditor1", "Аудитор", "AuditorPassword123!", auditorId);
        authenticate("auditor1");
        assertTrue(guard.can("ASSET_READ"));
        assertThrows(AccessDeniedException.class, () -> admin.addAsset("INV-1", "Монитор", "Офис"));
        authenticate("admin");
        admin.updatePermissions(auditorId, EnumSet.of(Permission.TICKET_READ, Permission.AUDIT_READ));
        authenticate("auditor1");
        assertFalse(guard.can("ASSET_READ"));
        assertThrows(AccessDeniedException.class, admin::assets);
        SecurityContextHolder.clearContext();
    }

    private void authenticate(String username) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(username, "", List.of()));
    }
}
