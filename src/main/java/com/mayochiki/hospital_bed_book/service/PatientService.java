package com.mayochiki.hospital_bed_book.service;

import com.mayochiki.hospital_bed_book.entity.Patient;
import com.mayochiki.hospital_bed_book.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    @Autowired
    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    /**
     * Save or update patient information.
     * If a patient with the same identification card exists, update the information; otherwise, save as a new patient.
     *
     * @param patient The patient entity to save or update.
     * @return The saved or updated patient entity.
     */
    public Patient saveOrUpdatePatient(Patient patient) {
        // Search for an existing patient by identification card
        Optional<Patient> existingPatientOpt = patientRepository.findByIdentificationCard(patient.getIdentificationCard());

        if (existingPatientOpt.isPresent()) {
            // If the patient exists, update their information
            Patient existingPatient = existingPatientOpt.get();
            existingPatient.setFname(patient.getFname());
            existingPatient.setLname(patient.getLname());
            existingPatient.setHn(patient.getHn());
            existingPatient.setAn(patient.getAn());
            existingPatient.setHealthcareRights(patient.getHealthcareRights());
            existingPatient.setDiagnostics(patient.getDiagnostics());
            existingPatient.setFoodType(patient.getFoodType());
            return patientRepository.save(existingPatient);
        }

        // If no existing patient, save a new one
        return patientRepository.save(patient);
    }

    /**
     * Find a patient by their ID.
     *
     * @param id The ID of the patient.
     * @return An Optional containing the patient, if found.
     */
    public Optional<Patient> findById(Long id) {
        return patientRepository.findById(id);
    }

    /**
     * Update a patient's information.
     *
     * @param id      The ID of the patient to update.
     * @param patient The updated patient information.
     * @return The updated patient entity.
     * @throws IllegalArgumentException if the patient is not found.
     */
    public Patient updatePatient(Long id, Patient patient) {
        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));

        existingPatient.setFname(patient.getFname());
        existingPatient.setLname(patient.getLname());
        existingPatient.setIdentificationCard(patient.getIdentificationCard());
        existingPatient.setHn(patient.getHn());
        existingPatient.setAn(patient.getAn());
        existingPatient.setHealthcareRights(patient.getHealthcareRights());
        existingPatient.setDiagnostics(patient.getDiagnostics());
        existingPatient.setFoodType(patient.getFoodType());

        return patientRepository.save(existingPatient);
    }

    /**
     * Delete a patient by their ID.
     *
     * @param id The ID of the patient to delete.
     * @throws IllegalArgumentException if the patient is not found.
     */
    public void deletePatient(Long id) {
        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));
        patientRepository.delete(existingPatient);
    }

    /**
     * Update patient information after bed reservation approval.
     * This includes generating hospital number (HN) and admission number (AN), and setting healthcare rights.
     *
     * @param patientId The ID of the patient to update.
     * @return The updated patient entity.
     * @throws IllegalArgumentException if the patient is not found.
     */
    public Patient updatePatientAfterApproval(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));

        // Generate hospital number (HN) and admission number (AN)
        String hn = generateHn();  // Example of generating HN
        String an = generateAn();  // Example of generating AN

        patient.setHn(hn);
        patient.setAn(an);

        // Set healthcare rights
        patient.setHealthcareRights("สิทธิ์การรักษาประชาชน");

        return patientRepository.save(patient);
    }

    /**
     * Generate a hospital number (HN).
     * This method can be modified according to the system's specific HN generation rules.
     *
     * @return A generated HN.
     */
    private String generateHn() {
        // Example generation logic, you can customize this based on your requirements
        return "HN" + System.currentTimeMillis(); // Simple example of generating HN
    }

    /**
     * Generate an admission number (AN).
     * This method can be modified according to the system's specific AN generation rules.
     *
     * @return A generated AN.
     */
    private String generateAn() {
        // Example generation logic, you can customize this based on your requirements
        return "AN" + System.currentTimeMillis(); // Simple example of generating AN
    }
}
