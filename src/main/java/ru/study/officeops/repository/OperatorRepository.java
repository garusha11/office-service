package ru.study.officeops.repository;
import ru.study.officeops.domain.Operator;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface OperatorRepository extends JpaRepository<Operator, Long> { Optional<Operator> findByUsername(String username); }
