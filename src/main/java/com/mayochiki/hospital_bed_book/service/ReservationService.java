package com.mayochiki.hospital_bed_book.service;

import com.mayochiki.hospital_bed_book.entity.*;
import com.mayochiki.hospital_bed_book.repository.PatientRepository;
import com.mayochiki.hospital_bed_book.repository.ReservationRepository;
import com.mayochiki.hospital_bed_book.repository.UserRepository;
import com.mayochiki.hospital_bed_book.repository.BedRepository;
import com.mayochiki.hospital_bed_book.exception.AccessDeniedException;
import com.mayochiki.hospital_bed_book.exception.ReservationNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationService {

    private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final BedRepository bedRepository;
    private final PatientRepository patientRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository,
                              UserRepository userRepository,
                              BedRepository bedRepository,
                              PatientRepository patientRepository) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.bedRepository = bedRepository;
        this.patientRepository = patientRepository;
    }

    /**
     * Create a new reservation after validating the admission date.
     * @param reservation the reservation to create
     * @return the saved reservation
     * @throws IllegalArgumentException if admission date is in the past
     */
    @Transactional
    public Reservation createReservation(Reservation reservation) throws ReservationNotFoundException {
        logger.info("Creating reservation with patient information (before approval)");

        // ตรวจสอบ admissionDate ว่าไม่เป็นวันที่ในอดีต
        if (reservation.getAdmissionDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Admission date cannot be in the past");
        }

        // สามารถเก็บข้อมูลผู้ป่วยไว้ใน Reservation โดยไม่ต้องบันทึกทันที
        reservation.setPatient(null); // หรือเก็บข้อมูลผู้ป่วยไว้ในรูปแบบอื่นที่ไม่ต้องบันทึก

        // บันทึกการจองลงฐานข้อมูล
        Reservation savedReservation = reservationRepository.save(reservation);

        return savedReservation;
    }

    /**
     * Approve a reservation, assign a bed to it, and create a patient record.
     * @param reservationId the reservation id to approve
     * @param userId the admin user id approving the reservation
     * @param bedId the bed id to assign to the reservation
     * @param ward the ward to assign the bed to
     * @return the approved reservation
     * @throws AccessDeniedException if the user is not an admin
     * @throws ReservationNotFoundException if the reservation or bed is not found
     */
    @Transactional
    public Reservation approveReservation(Long reservationId, Long userId, Long bedId, String ward)
            throws AccessDeniedException, ReservationNotFoundException {
        logger.info("Attempting to approve reservation with ID: {}", reservationId);

        // ค้นหาการจองตาม ID
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> {
                    logger.error("Reservation not found with ID: {}", reservationId);
                    return new ReservationNotFoundException("Reservation not found");
                });

        // ตรวจสอบว่าผู้ใช้เป็น Admin หรือไม่
        User user = userRepository.findById(userId)
                .filter(u -> "ADMIN".equals(u.getRole()))
                .orElseThrow(() -> {
                    logger.error("Access denied for user ID: {}", userId);
                    return new AccessDeniedException("Only admins can approve reservations");
                });

        // ตรวจสอบสถานะการจอง หากสถานะไม่ใช่ "รออนุมัติ" จะไม่ดำเนินการ
        if (!"รออนุมัติ".equals(reservation.getStatus())) {
            throw new ReservationNotFoundException("Reservation is not pending approval");
        }

        // ค้นหาเตียงที่ต้องการให้การจอง
        Bed bed = bedRepository.findById(bedId)
                .orElseThrow(() -> {
                    logger.error("Bed not found with ID: {}", bedId);
                    return new ReservationNotFoundException("Bed not found");
                });

        // อัปเดตสถานะการจองเป็น "อนุมัติ" และกำหนดเตียงและตึก
        reservation.setStatus(ReservationStatus.อนุมัติ); // "Approved"
        reservation.setBed(bed);
        reservation.setWard(ward);

        // สร้างและบันทึกข้อมูลผู้ป่วยจากข้อมูลการจอง
        Patient patient = createPatientFromReservation(reservation);
        patientRepository.save(patient);

        // บันทึกการจองที่อัปเดตแล้ว
        logger.info("Reservation with ID: {} approved and patient created", reservationId);
        return reservationRepository.save(reservation);
    }

    /**
     * Helper function to create a patient from the reservation data.
     * @param reservation the reservation from which patient data will be created
     * @return a newly created patient object
     */
        private Patient createPatientFromReservation(Reservation reservation) {
        // สร้าง Patient ใหม่จากข้อมูลใน Reservation
        Patient patient = new Patient();

        // ตั้งค่าชื่อผู้ป่วยจาก Reservation
        String[] fullName = reservation.getPatientFullName().split(" ");
        if (fullName.length > 1) {
            patient.setFname(fullName[0]);  // ชื่อ
            patient.setLname(fullName[1]);  // นามสกุล
        } else {
            patient.setFname(fullName[0]);  // ถ้ามีชื่อเดียว
            patient.setLname("");  // หรือสามารถกำหนดให้เป็นค่าว่าง
        }

        // ตั้งค่ารหัสประชาชนจาก Reservation
        patient.setIdentificationCard(reservation.getPatientNationalId());

        // ตั้งค่าวันเกิดจาก Reservation
        patient.setAdmissionDate(reservation.getPatientDateOfBirth());


        // ตั้งค่าหมายเลขผู้ป่วย (สามารถสร้างตามรูปแบบที่ต้องการ)
        patient.setHn(generateHn());

        // ตั้งค่าหมายเลขการรับเข้า (Admission Number) ตามการจอง
        patient.setAn(generateAn());

        // คืนค่าผู้ป่วยที่ถูกสร้าง
        return patient;
    }

    /**
     * Helper function to generate a Hospital Number (HN).
     * @return a generated HN
     */
    private String generateHn() {
        return "HN" + System.currentTimeMillis(); // Simple example of generating HN
    }

    /**
     * Helper function to generate an Admission Number (AN).
     * @return a generated AN
     */
    private String generateAn() {
        return "AN" + System.currentTimeMillis(); // Simple example of generating AN
    }

    /**
     * Get a list of all reservations.
     * @return a list of all reservations
     */
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    /**
     * Cancel a reservation.
     * @param reservationId the reservation id to cancel
     * @throws ReservationNotFoundException if the reservation is not found
     */
    @Transactional
    public void cancelReservation(Long reservationId) throws ReservationNotFoundException {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> {
                    logger.error("Reservation not found with ID: {}", reservationId);
                    return new ReservationNotFoundException("Reservation not found");
                });

        reservation.setStatus(ReservationStatus.ปฏิเสธ); // "Cancelled"
        reservationRepository.save(reservation);
        logger.info("Reservation with ID: {} has been cancelled", reservationId);
    }
}
