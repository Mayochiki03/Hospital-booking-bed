package com.mayochiki.hospital_bed_book.repository;

import com.mayochiki.hospital_bed_book.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findById(Long id);
    List<Reservation> findByStatus(String status);  // ค้นหาตามสถานะ
    List<Reservation> findByPatientId(Long patientId);  // ค้นหาตาม ID ของผู้ป่วย
    Optional<Reservation> findByBed_IdAndPatient_IdAndReservationDate(Long bedId, Long patientId, LocalDate reservationDate);

}