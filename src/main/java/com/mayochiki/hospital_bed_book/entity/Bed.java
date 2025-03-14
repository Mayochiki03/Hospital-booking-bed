package com.mayochiki.hospital_bed_book.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "bed")
public class Bed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "bed_number", nullable = false)
    private String bedNumber;  // หมายเลขเตียง

    @Column(name = "ward", nullable = false)
    private String ward;  // ชื่อแผนก

    @Column(name = "status", nullable = false)
    private String status;  // สถานะเตียง

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;  // การเชื่อมโยงกับ Patient

    @Column(name = "date_assigned", nullable = false)
    private LocalDate dateAssigned;  // วันที่บันทึกเตียง

    // Default constructor (จำเป็นสำหรับ JPA)
    public Bed() {
    }

    // Constructor สำหรับสร้าง Object พร้อมค่าทั้งหมด
    public Bed(String bedNumber, String ward, String status, Patient patient, LocalDate dateAssigned) {
        this.bedNumber = bedNumber;
        this.ward = ward;
        this.status = status;
        this.patient = patient;
        this.dateAssigned = dateAssigned;
    }

    // Getter และ Setter
    public int getId() {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    @Override
    public String toString() {
        return "Bed{" +
                "id=" + id +
                ", bedNumber='" + bedNumber + '\'' +
                ", ward='" + ward + '\'' +
                ", status='" + status + '\'' +
                ", patient=" + patient +
                ", dateAssigned=" + dateAssigned +
                '}';
    }
}
