package ru.study.officeops.repository;
import ru.study.officeops.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
public interface TicketRepository extends JpaRepository<Ticket, Long> { }
