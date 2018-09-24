package net.bigmir.venzor.repositories;

import net.bigmir.venzor.entities.ConfirmSMSTransaction;
import net.bigmir.venzor.entities.SimpleTransaction;
import net.bigmir.venzor.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionsRepositiry extends JpaRepository<SimpleTransaction, Long> {

    List<SimpleTransaction> findByAccountFromIs(long accountId);


    List<SimpleTransaction> findByAccountToIs(long accountId);

    List<SimpleTransaction> findByCardFromIs(long cardId);

    List<SimpleTransaction> findByCardToIs(long cardId);

    @Query("SELECT c FROM SimpleTransaction c WHERE c.status = :status")
    List<SimpleTransaction> findByStatus(@Param("status") TransactionStatus status);

    boolean existsByConfirmSMSTransactionAndStatus(ConfirmSMSTransaction sms, TransactionStatus status);


}
