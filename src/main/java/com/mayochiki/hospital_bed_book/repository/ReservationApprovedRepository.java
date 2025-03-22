package com.mayochiki.hospital_bed_book.repository;

import com.mayochiki.hospital_bed_book.entity.Reservation;
import com.mayochiki.hospital_bed_book.entity.ReservationApproved;
import com.mayochiki.hospital_bed_book.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationApprovedRepository extends JpaRepository<ReservationApproved, Long> {

    // ค้นหาข้อมูลการอนุมัติจาก Reservation และ User
    Optional<ReservationApproved> findByReservationAndUser(Reservation reservation, User user);
}
