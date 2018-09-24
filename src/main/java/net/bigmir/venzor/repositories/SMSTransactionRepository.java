package net.bigmir.venzor.repositories;

import net.bigmir.venzor.entities.ConfirmSMSTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SMSTransactionRepository extends JpaRepository<ConfirmSMSTransaction, Long> {
    ConfirmSMSTransaction findByCodeIs(int code);

    boolean existsByCode(int code);
}
