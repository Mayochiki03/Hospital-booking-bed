package com.mayochiki.hospital_bed_book.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;  // Many reservations for one patient

    @OneToOne
    @JoinColumn(name = "bed_id")
    private Bed bed;  // One bed for one reservation

    @OneToOne(mappedBy = "reservation")
    private ReservationApproved reservationApproved;  // One approved status for one reservation

    // Default Constructor
    public Reservation() {}

    // Constructor with patient and bed
    public Reservation(Patient patient, Bed bed) {
        this.patient = patient;
        this.bed = bed;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Bed getBed() {
        return bed;
    }

    public void setBed(Bed bed) {
        this.bed = bed;
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
                ", reservationApproved=" + reservationApproved +
                '}';
    }
}
