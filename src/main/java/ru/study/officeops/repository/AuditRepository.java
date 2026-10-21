package ru.study.officeops.repository;
import ru.study.officeops.domain.AuditEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface AuditRepository extends JpaRepository<AuditEntry, Long> { List<AuditEntry> findTop100ByOrderByOccurredAtDesc(); }
