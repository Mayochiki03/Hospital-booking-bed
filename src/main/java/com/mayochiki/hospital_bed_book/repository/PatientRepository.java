package com.mayochiki.hospital_bed_book.repository;

import com.mayochiki.hospital_bed_book.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    boolean existsByIdentificationCard(String identificationCard); // ตรวจสอบว่ามีผู้ป่วยที่มี ID นี้หรือไม่
    boolean existsByHn(String hn);
    Optional<Patient> findByIdentificationCard(String identificationCard);
    Optional<Patient> findByHn(String hn);
}
