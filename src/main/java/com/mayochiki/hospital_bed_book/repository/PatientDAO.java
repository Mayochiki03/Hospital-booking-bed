package com.mayochiki.hospital_bed_book.repository;

import com.mayochiki.hospital_bed_book.entity.Patient;

import java.util.List;
import java.util.Optional;

public interface PatientDAO {
    void save(Patient patient);
    List<Patient> getAll();
    boolean existsByIdentificationCard(String identificationCard);
    boolean existsByHn(String hn);
    Optional<Patient> findByHn(String hn);
}
