package com.mayochiki.hospital_bed_book.repository;

import com.mayochiki.hospital_bed_book.entity.Bed;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects; // import this for checking null

@Repository
public class BedRepository {

    private final EntityManager entityManager;

    @Autowired
    public BedRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    // Method to check if a Bed exists by Patient HN
    public boolean existsByHn(String hn) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(b) FROM Bed b WHERE b.patient.hn = :hn", Long.class);
        query.setParameter("hn", hn);
        Long count = query.getSingleResult();
        return count > 0;
    }

    // Method to find a Bed by Patient HN
    public Bed findByHn(String hn) {
        TypedQuery<Bed> query = entityManager.createQuery(
                "SELECT b FROM Bed b WHERE b.patient.hn = :hn", Bed.class);
        query.setParameter("hn", hn);
        return query.getSingleResult();
    }

    // New method to check if a Bed exists by bed number
    public boolean existsByBedNumber(String bedNumber) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(b) FROM Bed b WHERE b.bedNumber = :bedNumber", Long.class);
        query.setParameter("bedNumber", bedNumber);
        Long count = query.getSingleResult();
        return count > 0;
    }

    // Method to save a Bed with @Transactional to manage the transaction
    @Transactional
    public Bed save(Bed bed) {
        if (Objects.isNull(bed.getId())) { // Use Objects.isNull() for null check
            entityManager.persist(bed); // for new entity
        } else {
            bed = entityManager.merge(bed); // for existing entity
        }
        return bed;
    }

    // Method to get all beds
    public List<Bed> getAll() {
        TypedQuery<Bed> query = entityManager.createQuery("SELECT b FROM Bed b", Bed.class);
        return query.getResultList();
    }
}
