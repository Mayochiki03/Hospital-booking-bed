package com.mayochiki.hospital_bed_book.repository;

import com.mayochiki.hospital_bed_book.entity.Patient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PatientRepository implements PatientDAO {
    private final EntityManager entityManager;

    @Autowired
    public PatientRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void save(Patient patient) {
        entityManager.persist(patient);
    }

    @Override
    public List<Patient> getAll() {
        TypedQuery<Patient> query = entityManager.createQuery("SELECT p FROM Patient p", Patient.class);
        return query.getResultList();
    }

    @Override
    public boolean existsByIdentificationCard(String identificationCard) {
        TypedQuery<Long> query = entityManager.createQuery("SELECT COUNT(p) FROM Patient p WHERE p.identificationCard = :identificationCard", Long.class);
        query.setParameter("identificationCard", identificationCard);
        Long count = query.getSingleResult();
        return count > 0; // Return true if a patient with the same identification card exists
    }

    @Override
    public boolean existsByHn(String hn) {
        TypedQuery<Long> query = entityManager.createQuery("SELECT COUNT(p) FROM Patient p WHERE p.hn = :hn", Long.class);
        query.setParameter("hn", hn);
        Long count = query.getSingleResult();
        return count > 0; // Return true if a patient with the same HN exists
    }

    @Override
    public Optional<Patient> findByHn(String hn) {
        TypedQuery<Patient> query = entityManager.createQuery("SELECT p FROM Patient p WHERE p.hn = :hn", Patient.class);
        query.setParameter("hn", hn);
        List<Patient> patients = query.getResultList();
        return patients.isEmpty() ? Optional.empty() : Optional.of(patients.get(0)); // Return an Optional
    }
}
