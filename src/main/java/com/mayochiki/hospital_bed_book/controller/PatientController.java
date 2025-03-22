package com.mayochiki.hospital_bed_book.controller;

import com.mayochiki.hospital_bed_book.entity.Patient;
import com.mayochiki.hospital_bed_book.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*") // อนุญาตทุกโดเมน
public class PatientController {

    private final PatientService patientService;

    @Autowired
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    // API สำหรับบันทึกผู้ป่วย
    @PostMapping
    public ResponseEntity<Patient> createPatient(@RequestBody Patient patient) {
        Patient savedPatient = patientService.saveOrUpdatePatient(patient);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPatient);
    }

    // API สำหรับดึงข้อมูลผู้ป่วยตาม ID
    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        Optional<Patient> patient = patientService.findById(id);
        if (patient.isPresent()) {
            return ResponseEntity.ok(patient.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable Long id, @RequestBody Patient patient) {
        Optional<Patient> existingPatient = patientService.findById(id);
        if (existingPatient.isPresent()) {
            Patient existing = existingPatient.get();

            // ตรวจสอบและอัปเดตฟิลด์ diagnostics
            if (patient.getDiagnostics() != null) {
                existing.setDiagnostics(patient.getDiagnostics());
            }

            // ตรวจสอบและอัปเดตฟิลด์ healthcareRights
            if (patient.getHealthcareRights() != null) {
                existing.setHealthcareRights(patient.getHealthcareRights());
            }

            // ตรวจสอบและอัปเดตฟิลด์ food
            if (patient.getFood() != null) {
                existing.setFood(patient.getFood());
            }

            // บันทึกข้อมูลที่อัปเดต
            Patient updatedPatient = patientService.saveOrUpdatePatient(existing);
            return ResponseEntity.ok(updatedPatient);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    // API สำหรับลบข้อมูลผู้ป่วย
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        if (patientService.findById(id).isPresent()) {
            patientService.deletePatient(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
