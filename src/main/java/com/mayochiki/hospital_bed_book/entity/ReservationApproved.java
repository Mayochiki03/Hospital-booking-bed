package com.mayochiki.hospital_bed_book.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "reservation_approved")
public class ReservationApproved {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;  // One reservation for one approved status

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;  // One user (admin) for one approval

    @Column(name = "approved", nullable = false)
    private boolean approved;  // true if approved, false otherwise

    // Default Constructor
    public ReservationApproved() {}

    // Constructor with reservation, user and approval status
    public ReservationApproved(Reservation reservation, User user, boolean approved) {
        this.reservation = reservation;
        this.user = user;
        this.approved = approved;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    @Override
    public String toString() {
        return "ReservationApproved{" +
                "id=" + id +
                ", reservation=" + reservation +
                ", user=" + user +
                ", approved=" + approved +
                '}';
    }
}
