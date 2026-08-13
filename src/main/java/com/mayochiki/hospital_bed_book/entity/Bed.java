package com.mayochiki.hospital_bed_book.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Bed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bedNumber; // เพิ่มตัวแปร bedNumber เพื่อเก็บหมายเลขเตียง

    private String ward;
    @Column(nullable = true)
    private LocalDate admitDate;
    @OneToMany(mappedBy = "bed")
    @JsonManagedReference  // ใช้ @JsonManagedReference เพื่อให้สามารถแปลงข้อมูลของฝั่งนี้ได้
    private List<Patient> patients;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BedStatus status; // ใช้ BedStatus enum แทน String status

    @ManyToOne
    private Patient patient; // ผู้ป่วย

    private LocalDate dateAssigned; // วันที่บันทึกเตียง

    // Constructor, Getters, and Setters

    public Bed() {}

    public Bed(String bedNumber, String ward, BedStatus status, Patient patient, LocalDate dateAssigned) {
        this.bedNumber = bedNumber;
        this.ward = ward;
        this.status = status;
        this.patient = patient;
        this.dateAssigned = dateAssigned;
    }

    // Automatically set the dateAssigned field to the current date if it's not provided
    @PrePersist
    public void onPrePersist() {
        if (this.dateAssigned == null) {
            this.dateAssigned = LocalDate.now(); // Set current date if not assigned
        }
    }

    public Long getId() {
        return id;
    }

    public String getBedNumber() {
        return bedNumber;
    }

    public void setBedNumber(String bedNumber) {
        this.bedNumber = bedNumber;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public BedStatus getStatus() {
        return status;
    }

    public void setStatus(BedStatus status) {
        this.status = status;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public LocalDate getDateAssigned() {
        return dateAssigned;
    }

    public void setDateAssigned(LocalDate dateAssigned) {
        this.dateAssigned = dateAssigned;
    }
    public void setAdmitDate(LocalDate admitDate) {
        this.admitDate = admitDate;
    }
    @Override
    public String toString() {
        return "Bed{" +
                "id=" + id +
                ", bedNumber='" + bedNumber + '\'' +
                ", ward='" + ward + '\'' +
                ", status=" + status + // เปลี่ยนจาก 'status' เป็น 'status.toString()'
                ", patient=" + (patient != null ? patient.getFname() + " " + patient.getLname() : "ไม่มีผู้ป่วย") +
                ", dateAssigned=" + dateAssigned +
                '}';
    }
}
