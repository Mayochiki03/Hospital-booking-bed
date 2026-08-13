package com.mayochiki.hospital_bed_book.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "patient")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "firstname", nullable = false)
    @NotBlank(message = "First name must not be empty")
    private String fname;

    @Column(name = "lastname", nullable = false)
    @NotBlank(message = "Last name must not be empty")
    private String lname;

    @Column(name = "identification_card", unique = true, nullable = false, length = 20)
    @Size(min = 13, max = 13, message = "Identification card must be 13 digits")
    @NotNull(message = "Identification card is required")
    private String identificationCard;

    @Column(name = "hn", unique = true, length = 10)
    @Size(min = 10, max = 10, message = "Hospital number (HN) must be 10 digits")
    private String hn;

    @Column(name = "an", unique = true, length = 10)
    @Size(min = 10, max = 10, message = "Admission number (AN) must be 10 digits")
    private String an;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "healthcare_rights", nullable = true)
    private String healthcareRights;

    @Column(name = "diagnostics", nullable = true, columnDefinition = "TEXT")
    private String diagnostics;

    @Enumerated(EnumType.STRING)  // Store the enum as a string in the database
    @Column(name = "food_type", nullable = true)
    private FoodType foodType;

    @Column(name = "admission_date")
    private LocalDate admissionDate;

    @OneToMany(mappedBy = "patient")
    private List<Reservation> reservations;

    @ManyToOne
    @JoinColumn(name = "bed_id")  // เพิ่มคอลัมน์ bed_id ในฐานข้อมูล
    @JsonBackReference  // ใช้ @JsonBackReference เพื่อหลีกเลี่ยงการวนลูป
    private Bed bed;

    public Patient() {
        this.diagnostics = null; // Default value set to null
        this.foodType = null;    // Default value set to null
        this.healthcareRights = null; // Default value set to null
    }

    public Patient(String fname, String lname, String identificationCard, String hn, String an, String healthcareRights, String diagnostics, FoodType foodType) {
        this.fname = fname;
        this.lname = lname;
        this.identificationCard = identificationCard;
        this.hn = hn;
        this.an = an;
        this.healthcareRights = healthcareRights != null ? healthcareRights : "";
        this.diagnostics = diagnostics;            // Will be null if not provided
        this.foodType = null; // กำหนดให้เป็น null หรือค่าเริ่มต้นที่คุณต้องการ
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public FoodType getFoodType() {
        return foodType;
    }

    public void setFoodType(FoodType foodType) {
        this.foodType = foodType;
    }

    public LocalDate getAdmissionDate() {
        return admissionDate;
    }
    public Bed getBed() {
        return bed;
    }
    public void setAdmissionDate(LocalDate admissionDate) {
        this.admissionDate = admissionDate;
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
                ", foodType=" + foodType +
                ", admissionDate=" + admissionDate +
                '}';
    }

    public LocalDate getDateOfBirth() {
        return this.dateOfBirth;
    }

    public void setBed(Bed bed) {
        this.bed = bed;
    }

    // ดึงข้อมูลประเภทอาหาร
    public FoodType getFood() {
        return this.foodType;  // สมมุติว่า foodType เป็นตัวแปรของชนิด FoodType
    }

    public void setFood(FoodType food) {
        this.foodType = food;  // กำหนดค่า foodType เป็นค่า food ที่รับเข้ามา
    }

}
