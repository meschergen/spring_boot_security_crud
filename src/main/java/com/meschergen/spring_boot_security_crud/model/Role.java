package com.meschergen.spring_boot_security_crud.model;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Set;

/**
 * 16.12.2020
 *
 * @author MescheRGen
 */
@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority {
    private Long id;
    private String name;
    private Set<User> users;

    public Role() { }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    @ManyToMany(mappedBy = "roles")
    public Set<User> getUsers() {
        return users;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @Override
    @Transient
    public String getAuthority() {
        return name;
    }

}
