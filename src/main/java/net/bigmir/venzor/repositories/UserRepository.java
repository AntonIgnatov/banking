package net.bigmir.venzor.repositories;

import net.bigmir.venzor.entities.SimpleUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<SimpleUser, Long> {
    SimpleUser findByPhoneIs(String phone);
    boolean existsByPhoneIs(String phone);
    boolean existsByIpnIs(String ipn);
    boolean existsByEmailIs(String email);
//    int countByPhone(String phone);
}
