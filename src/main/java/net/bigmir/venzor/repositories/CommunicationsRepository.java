package net.bigmir.venzor.repositories;

import net.bigmir.venzor.entities.BackMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunicationsRepository extends JpaRepository<BackMessage, Long> {
}
