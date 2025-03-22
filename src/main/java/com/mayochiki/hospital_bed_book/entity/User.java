package com.mayochiki.hospital_bed_book.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mayochiki.hospital_bed_book.entity.ReservationApproved;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "lname", nullable = false)  // Added the lname column
    private String lname;  // Field to store last name

    @OneToMany(mappedBy = "user")
    @JsonIgnore  // ❌ Prevents JSON Recursion
    private List<ReservationApproved> reservationApprovals;

    // Constructors
    public User() {}

    public User(String username, String password, String role, String lname) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.lname = lname;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getLname() {
        return lname;  // Getter for lname
    }

    public void setLname(String lname) {
        this.lname = lname;  // Setter for lname
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", lname='" + lname + '\'' +  // Added lname to the string representation
                '}';
    }
}
