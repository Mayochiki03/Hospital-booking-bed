package com.mayochiki.hospital_bed_book.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @OneToOne
    @JoinColumn(name = "bed_id")
    @JsonIgnore
    private Bed bed;

    @Column(name = "reservation_date", nullable = true)
    private LocalDate reservationDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Column(name = "admission_date")
    private LocalDate admissionDate;

    @ManyToOne
    @JoinColumn(name = "reservation_approved_id")
    @JsonIgnore
    private ReservationApproved reservationApproved;

    private String ward;
    private String fname;
    private String lname;

    @Column(name = "diagnostics")
    private String diagnostics;

    @Column(name = "identification_card")
    private String identificationCard;

    // Default constructor
    public Reservation() {}

    // Constructor with mandatory fields
    public Reservation(Patient patient, Bed bed, LocalDate reservationDate, LocalDate admissionDate) {
        if (patient == null || bed == null) {
            throw new IllegalArgumentException("Patient and Bed cannot be null");
        }
        this.patient = patient;
        this.bed = bed;
        this.reservationDate = reservationDate;
        this.admissionDate = admissionDate;
        this.status = ReservationStatus.รออนุมัติ;
        this.fname = patient.getFname();
        this.lname = patient.getLname();
        this.diagnostics = patient.getDiagnostics();
        this.identificationCard = patient.getIdentificationCard();
    }

    // Getter and Setter methods

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        if (patient != null) {
            this.fname = patient.getFname();
            this.lname = patient.getLname();
            this.diagnostics = patient.getDiagnostics();
            this.identificationCard = patient.getIdentificationCard();
        }
        this.patient = patient;
    }

    public Bed getBed() {
        return bed;
    }

    public void setBed(Bed bed) {
        this.bed = bed;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }

    public LocalDate getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(LocalDate admissionDate) {
        this.admissionDate = admissionDate;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
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

    public ReservationApproved getReservationApproved() {
        return reservationApproved;
    }

    public void setReservationApproved(ReservationApproved reservationApproved) {
        this.reservationApproved = reservationApproved;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", patient=" + patient +
                ", bed=" + bed +
                ", reservationDate=" + reservationDate +
                ", admissionDate=" + admissionDate +
                ", status=" + status +
                ", reservationApproved=" + reservationApproved +
                ", diagnostics='" + diagnostics + '\'' +
                ", identificationCard='" + identificationCard + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(patient, that.patient) &&
                Objects.equals(bed, that.bed) &&
                Objects.equals(reservationDate, that.reservationDate) &&
                Objects.equals(admissionDate, that.admissionDate) &&
                status == that.status &&
                Objects.equals(reservationApproved, that.reservationApproved);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, patient, bed, reservationDate, admissionDate, status, reservationApproved);
    }

    // Additional utility methods
    public String getIdentificationCard() {
        return this.identificationCard;
    }

    public String getDiagnostics() {
        return this.diagnostics;
    }

    public String getPatientFullName() {
        return this.fname + " " + this.lname;
    }

    public String getPatientNationalId() {
        return this.identificationCard;
    }

    public LocalDate getPatientDateOfBirth() {
        return this.patient != null ? this.patient.getDateOfBirth() : null;
    }
}
