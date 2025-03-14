package com.mayochiki.hospital_bed_book.service;

import com.mayochiki.hospital_bed_book.entity.Patient;
import com.mayochiki.hospital_bed_book.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    public void savePatient(Patient patient) {
        // Check if the patient already exists by identification card
        if (patientRepository.existsByIdentificationCard(patient.getIdentificationCard())) {
            throw new IllegalArgumentException("Identification card already exists: " + patient.getIdentificationCard());
        }

        // If no duplicate, save the patient data
        patientRepository.save(patient);
    }
}
