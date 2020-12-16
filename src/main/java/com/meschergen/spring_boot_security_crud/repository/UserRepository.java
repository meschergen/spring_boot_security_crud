package com.meschergen.spring_boot_security_crud.repository;

import com.meschergen.spring_boot_security_crud.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 16.12.2020
 *
 * @author MescheRGen
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User getByUsername(String username);
}
