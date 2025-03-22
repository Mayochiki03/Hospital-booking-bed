package com.mayochiki.hospital_bed_book.repository;

import com.mayochiki.hospital_bed_book.entity.Bed;
import com.mayochiki.hospital_bed_book.entity.BedStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface BedRepository extends JpaRepository<Bed, Long> {

    List<Bed> findByStatusAndPatientIsNull(BedStatus status);

    @Query("SELECT b FROM Bed b WHERE b.status IN :statuses AND b.patient IS NULL")
    List<Bed> findBedsByStatusesAndPatientIsNull(List<BedStatus> statuses);

    @Modifying
    @Transactional
    @Query("UPDATE Bed b SET b.status = :status, b.patient.id = :patientId WHERE b.id = :id")
    void updateBedStatus(Long id, BedStatus status, Long patientId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Bed b WHERE b.bedNumber = :bedNumber")
    void deleteByBedNumber(String bedNumber);

    Optional<Bed> findByWardAndBedNumber(String ward, String bedNumber);

    List<Bed> findByWard(String ward);
}
