package net.bigmir.venzor.repositories;

import net.bigmir.venzor.entities.cards.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {
    @Query("SELECT id FROM CreditCard c")
    List<Long> getAllid();
}
