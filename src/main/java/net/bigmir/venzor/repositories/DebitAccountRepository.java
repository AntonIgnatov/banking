package net.bigmir.venzor.repositories;

import net.bigmir.venzor.entities.accounts.BasicAccount;
import net.bigmir.venzor.entities.accounts.DebitAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DebitAccountRepository extends JpaRepository<DebitAccount, Long> {
    List<DebitAccount> findDebitAccountsByUserId(long userId);


}
