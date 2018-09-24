package net.bigmir.venzor.repositories;

import net.bigmir.venzor.entities.cards.DebitCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DebitCardRepository extends JpaRepository<DebitCard, Long> {
    @Query("SELECT id FROM DebitCard c")
    List<Long> getAllid();
}
