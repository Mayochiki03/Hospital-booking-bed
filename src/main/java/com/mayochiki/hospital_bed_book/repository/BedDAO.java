package com.mayochiki.hospital_bed_book.repository;

import com.mayochiki.hospital_bed_book.entity.Bed;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class BedDAO {

    private final EntityManager entityManager;

    @Autowired
    public BedDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    // Method to save a Bed within a transaction
    @Transactional
    public void save(Bed bed) {
        entityManager.persist(bed);
    }

    // Method to get all beds
    public List<Bed> getAll() {
        TypedQuery<Bed> query = entityManager.createQuery("SELECT b FROM Bed b", Bed.class);
        return query.getResultList();
    }
}
