package net.bigmir.venzor.repositories;

import net.bigmir.venzor.entities.cards.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository<T extends Card> extends JpaRepository<T, Long> {
}
