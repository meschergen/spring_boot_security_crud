package com.meschergen.spring_boot_security_crud.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 16.12.2020
 *
 * @author MescheRGen
 */

@Entity
@Table(name = "users")
public class User implements UserDetails {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private Set<Role> roles;


    public User(){ }

    public User(String firstName, String lastName, String email, String username, String password, Set<Role> roles){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public User(Long id, String firstName, String lastName, String email, String username, String password, Set<Role> roles){
        this(firstName, lastName, email, username, password, roles);
        this.id = id;
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() { return id; }

    @Column(name = "first_name")
    @NotNull(message = "First name is required")
    @Size(min = 2, max = 40, message = "Valid size of first name is from 2, to 30 characters")
    public String getFirstName() { return firstName; }

    @Column(name = "last_name")
    @NotNull(message = "Last name is required")
    @Size(min = 2, max = 40, message = "Valid size of last name is from 2, to 30 characters")
    public String getLastName() { return lastName; }

    @Column(name = "email")
    @NotNull(message = "Email is required")
    @Email(message = "Invalid email")
    public String getEmail() { return email; }

    @Override
    @Column(name = "username", unique = true)
    @NotNull(message = "Username is required")
    @Size(min = 4, max = 20, message = "Valid size of username is from 4, to 20 characters")
    public String getUsername() {
        return username;
    }

    @Override
    @Column(name = "password")
    @NotNull(message = "Password is required")
    @Size(min = 4, message = "Password must contain, at least, 4 symbols")
    public String getPassword() {
        return password;
    }

    @ManyToMany
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    public Set<Role> getRoles() {
        return roles;
    }

    @Override
    @Transient
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(r -> new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList());
    }


    public void setId(Long id) { this.id = id; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public void setEmail(String email) { this.email = email; }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }


    @Override
    public String toString() {
        return "\nId = "              + id
                + "\nFirst Name = "   + firstName
                + "\nLast Name = "    + lastName
                + "\nEmail = "        + email
                + "\n";
    }

    public String toStringHTML() {
        return "<hr>Id = "              + id
                + "<br>First Name = "   + firstName
                + "<br>Last Name = "    + lastName
                + "<br>Email = "        + email
                + "<hr>";
    }

    @Override
    @Transient
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @Transient
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @Transient
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @Transient
    public boolean isEnabled() {
        return true;
    }
}

