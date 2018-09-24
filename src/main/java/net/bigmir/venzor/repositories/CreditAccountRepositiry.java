package net.bigmir.venzor.repositories;

import net.bigmir.venzor.entities.accounts.CreditAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditAccountRepositiry extends JpaRepository<CreditAccount, Long> {
    List<CreditAccount> findCreditAccountsByUserId(Long userId);

}
