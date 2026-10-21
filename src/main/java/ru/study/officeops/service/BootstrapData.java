package ru.study.officeops.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.study.officeops.domain.*;
import ru.study.officeops.repository.*;
import java.util.EnumSet;

@Configuration
public class BootstrapData {
    @Bean CommandLineRunner seed(RoleRepository roles, OperatorRepository operators, PasswordEncoder encoder,
                                 @Value("${office.admin.password:}") String password) {
        return args -> {
            Role admin = roles.findByCode("ADMIN").orElseGet(() -> roles.save(new Role("ADMIN", "Администратор", EnumSet.allOf(Permission.class))));
            roles.findByCode("DISPATCHER").orElseGet(() -> roles.save(new Role("DISPATCHER", "Диспетчер",
                    EnumSet.of(Permission.ASSET_READ, Permission.TICKET_READ, Permission.TICKET_CREATE, Permission.TICKET_ASSIGN, Permission.TICKET_CLOSE))));
            roles.findByCode("TECHNICIAN").orElseGet(() -> roles.save(new Role("TECHNICIAN", "Техник",
                    EnumSet.of(Permission.ASSET_READ, Permission.TICKET_READ, Permission.TICKET_PROGRESS))));
            roles.findByCode("AUDITOR").orElseGet(() -> roles.save(new Role("AUDITOR", "Аудитор",
                    EnumSet.of(Permission.ASSET_READ, Permission.TICKET_READ, Permission.AUDIT_READ))));
            if (operators.findByUsername("admin").isEmpty()) {
                if (password == null || password.length() < 12) throw new IllegalStateException("Для первого запуска задайте OFFICE_ADMIN_PASSWORD длиной от 12 символов");
                operators.save(new Operator("admin", "Главный администратор", encoder.encode(password), admin));
            }
        };
    }
}
