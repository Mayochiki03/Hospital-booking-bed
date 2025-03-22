package com.mayochiki.hospital_bed_book.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "reservation_approved")
public class ReservationApproved {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    @JsonBackReference  // ใช้เพื่อหลีกเลี่ยงการวนลูปเมื่อแปลงเป็น JSON
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "approved")
    private boolean approved;

    // Constructors
    public ReservationApproved() {}

    public ReservationApproved(Reservation reservation, User user, boolean approved) {
        this.reservation = reservation;
        this.user = user;
        this.approved = approved;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
}
