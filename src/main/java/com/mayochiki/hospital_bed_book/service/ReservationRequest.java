package com.mayochiki.hospital_bed_book.service;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;
import java.time.LocalDate;

public class ReservationRequest {

    // Field for Bed ID
    @NotNull(message = "Bed ID is required")
    private Long bedId;

    // Field for Patient ID
    @NotNull(message = "Patient ID is required")
    private Long patientId;

    // Field for Reservation Date
    @NotNull(message = "Reservation date is required")
    private LocalDate reservationDate;

    // Field for Admission Date, should not be in the past
    @NotNull(message = "Admission date is required")
    @FutureOrPresent(message = "Admission date cannot be in the past")  // Admission date must be today or in the future
    private LocalDate admissionDate;

    // Getters and Setters for all fields
    public Long getBedId() {
        return bedId;
    }

    public void setBedId(Long bedId) {
        this.bedId = bedId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
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
}
