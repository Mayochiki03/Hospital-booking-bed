package com.mayochiki.hospital_bed_book.entity;

import jakarta.persistence.*;
import java.util.List;

// Patient Entity (1 to N with Reservation)
@Entity
@Table(name = "patient")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "firstname", nullable = false)
    private String fname;

    @Column(name = "lastname", nullable = false)
    private String lname;

    @Column(name = "identification_card", unique = true, nullable = false, length = 13)
    private String identificationCard;

    @Column(name = "hn", unique = true, length = 10)
    private String hn;

    @Column(name = "an", unique = true, length = 10)
    private String an;

    @Column(name = "healthcare_rights", nullable = false)
    private String healthcareRights;

    @Column(name = "diagnostics", nullable = false, columnDefinition = "TEXT")
    private String diagnostics;

    @Column(name = "food_type", nullable = false)
    private String foodType;

    // 1 to many relationship with Reservation
    @OneToMany(mappedBy = "patient")
    private List<Reservation> reservations;

    // Default Constructor
    public Patient() {}

    // Constructor with all fields
    public Patient(String fname, String lname, String identificationCard, String hn, String an, String healthcareRights, String diagnostics, String foodType) {
        this.fname = fname;
        this.lname = lname;
        this.identificationCard = identificationCard;
        this.hn = hn;
        this.an = an;
        this.healthcareRights = healthcareRights;
        this.diagnostics = diagnostics;
        this.foodType = foodType;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getIdentificationCard() {
        return identificationCard;
    }

    public void setIdentificationCard(String identificationCard) {
        this.identificationCard = identificationCard;
    }

    public String getHn() {
        return hn;
    }

    public void setHn(String hn) {
        this.hn = hn;
    }

    public String getAn() {
        return an;
    }

    public void setAn(String an) {
        this.an = an;
    }

    public String getHealthcareRights() {
        return healthcareRights;
    }

    public void setHealthcareRights(String healthcareRights) {
        this.healthcareRights = healthcareRights;
    }

    public String getDiagnostics() {
        return diagnostics;
    }

    public void setDiagnostics(String diagnostics) {
        this.diagnostics = diagnostics;
    }

    public String getFoodType() {
        return foodType;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id=" + id +
                ", fname='" + fname + '\'' +
                ", lname='" + lname + '\'' +
                ", identificationCard='" + identificationCard + '\'' +
                ", hn='" + hn + '\'' +
                ", an='" + an + '\'' +
                ", healthcareRights='" + healthcareRights + '\'' +
                ", diagnostics='" + diagnostics + '\'' +
                ", foodType='" + foodType + '\'' +
                '}';
    }
}
