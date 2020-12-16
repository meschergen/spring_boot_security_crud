package com.meschergen.spring_boot_security_crud.repository;

import com.meschergen.spring_boot_security_crud.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 16.12.2020
 *
 * @author MescheRGen
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
