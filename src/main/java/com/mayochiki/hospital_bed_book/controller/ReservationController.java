package com.mayochiki.hospital_bed_book.controller;

import com.mayochiki.hospital_bed_book.entity.*;
import com.mayochiki.hospital_bed_book.repository.BedRepository;
import com.mayochiki.hospital_bed_book.service.ReservationService;
import com.mayochiki.hospital_bed_book.service.ReservationApprovedService;
import com.mayochiki.hospital_bed_book.repository.ReservationRepository;
import com.mayochiki.hospital_bed_book.repository.UserRepository;
import com.mayochiki.hospital_bed_book.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.mayochiki.hospital_bed_book.service.BedService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationApprovedService reservationApprovedService;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final BedService bedService; // ✅ เพิ่มตัวแปร BedService
    @Autowired
    private BedRepository bedRepository;  // เพิ่มการประกาศ BedRepository


    @Autowired
    public ReservationController(ReservationService reservationService,
                                 ReservationApprovedService reservationApprovedService,
                                 ReservationRepository reservationRepository,
                                 UserRepository userRepository,
                                 PatientRepository patientRepository,
                                 BedService bedService) { // ✅ Inject BedService{
        this.reservationService = reservationService;
        this.reservationApprovedService = reservationApprovedService;
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.bedService = bedService; // ✅ กำหนดค่า
    }

    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody Reservation reservation) {
        // Check if admissionDate is not in the past
        if (reservation.getAdmissionDate().isBefore(LocalDate.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Admission date cannot be in the past.");
        }

        // Check if patient information is provided
        if (reservation.getPatient() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Patient information is required.");
        }

        // Clear reservationDate if necessary
        reservation.setReservationDate(null);

        // Create the reservation
        Reservation createdReservation = reservationService.createReservation(reservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReservation);
    }

    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(reservations);
    }

    @PostMapping("/{reservationId}/approve")
    public ResponseEntity<?> approveReservation(@PathVariable("reservationId") Long reservationId,
                                                @RequestParam("userId") Long userId,
                                                @RequestParam("bedId") Long bedId,
                                                @RequestParam("ward") String ward) {
        try {
            Reservation reservation = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new IllegalStateException("Reservation not found"));

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalStateException("User not found"));

            if (!"ADMIN".equals(user.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Only Admin can approve reservations.");
            }

            ReservationApproved reservationApproved = reservationApprovedService.approveReservation(reservation, user, bedId, ward);
            reservation.setReservationApproved(reservationApproved);
            reservation.setStatus(ReservationStatus.อนุมัติ);

            if (reservation.getStatus() == ReservationStatus.อนุมัติ) {
                if (reservation.getPatient() == null) {
                    Patient patient;
                    if (reservation.getFname() != null && reservation.getLname() != null && reservation.getIdentificationCard() != null) {
                        patient = new Patient();
                        patient.setFname(reservation.getFname());
                        patient.setLname(reservation.getLname());
                        patient.setIdentificationCard(reservation.getIdentificationCard());
                        patient.setDiagnostics(reservation.getDiagnostics());
                        patient.setAdmissionDate(reservation.getAdmissionDate());

                        // บันทึกข้อมูลผู้ป่วยในฐานข้อมูล
                        patient = patientRepository.save(patient);
                        reservation.setPatient(patient);
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("Patient data is incomplete.");
                    }

                    // เชื่อมโยง bed กับ patient
                    Bed bed = bedRepository.findById(bedId)
                            .orElseThrow(() -> new IllegalStateException("Bed not found"));
                    bed.setAdmitDate(reservation.getAdmissionDate());
                    bedRepository.save(bed);  // บันทึกการอัปเดต admitDate ในเตียง

                    patient.setBed(bed);  // เพิ่มการตั้งค่า bed ให้กับ patient
                    patientRepository.save(patient);  // บันทึก patient ที่มีการอัปเดต bed

                    // ส่งข้อมูลการจองเตียง
                    bedService.assignBedToPatient(bedId, patient.getId());
                }

                reservationRepository.save(reservation);
            }


            return ResponseEntity.ok(reservation);
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }


    @PostMapping("/{reservationId}/reject")
    public ResponseEntity<String> rejectReservation(@PathVariable("reservationId") Long reservationId) {
        Optional<Reservation> reservationOpt = reservationRepository.findById(reservationId);
        if (reservationOpt.isPresent()) {
            Reservation reservation = reservationOpt.get();
            reservation.setStatus(ReservationStatus.ปฏิเสธ);
            reservationRepository.save(reservation);
            return ResponseEntity.ok("Reservation rejected successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Reservation not found.");
        }
    }
}
