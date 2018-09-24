package net.bigmir.venzor.repositories;

import net.bigmir.venzor.entities.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CurrencyRepository extends JpaRepository<Currency, String> {

}
