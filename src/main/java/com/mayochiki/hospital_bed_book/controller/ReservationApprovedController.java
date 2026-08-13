    package com.mayochiki.hospital_bed_book.controller;

    import com.mayochiki.hospital_bed_book.entity.Reservation;
    import com.mayochiki.hospital_bed_book.entity.ReservationApproved;
    import com.mayochiki.hospital_bed_book.entity.ReservationStatus;
    import com.mayochiki.hospital_bed_book.entity.User;
    import com.mayochiki.hospital_bed_book.repository.ReservationRepository;
    import com.mayochiki.hospital_bed_book.repository.ReservationApprovedRepository;
    import com.mayochiki.hospital_bed_book.repository.UserRepository;
    import com.mayochiki.hospital_bed_book.service.ReservationApprovalRequest;
    import com.mayochiki.hospital_bed_book.service.ReservationApprovedService;
    import jakarta.persistence.EntityNotFoundException;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.dao.DataIntegrityViolationException;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.transaction.annotation.Transactional;

    import java.util.Optional;

    @RestController
    @RequestMapping("/api/reservations/approve")
    @CrossOrigin(origins = "*") // อนุญาตทุกโดเมน
    public class ReservationApprovedController {

        private final ReservationRepository reservationRepository;
        private final UserRepository userRepository;
        private final ReservationApprovedRepository reservationApprovedRepository;
        private final ReservationApprovedService reservationApprovedService;

        @Autowired
        public ReservationApprovedController(ReservationRepository reservationRepository,
                                             UserRepository userRepository,
                                             ReservationApprovedRepository reservationApprovedRepository,
                                             ReservationApprovedService reservationApprovedService) {
            this.reservationRepository = reservationRepository;
            this.userRepository = userRepository;
            this.reservationApprovedRepository = reservationApprovedRepository;
            this.reservationApprovedService = reservationApprovedService;
        }

        @PostMapping("/{reservationId}/approve/{userId}")
        @Transactional
        public ResponseEntity<?> approveReservation(@PathVariable("reservationId") Long reservationId,
                                                    @PathVariable("userId") Long userId,
                                                    @RequestBody ReservationApprovalRequest request) {
            try {
                // ตรวจสอบว่าผู้ใช้ (userId) มีอยู่จริง
                Optional<User> userOptional = userRepository.findById(userId);
                User user = userOptional.orElseThrow(() -> new EntityNotFoundException("User not found"));

                // ตรวจสอบสิทธิ์ว่าเป็น ADMIN หรือไม่
                if (!"ADMIN".equals(user.getRole())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("Only Admin can approve reservations.");
                }

                // ตรวจสอบว่ามีการจองอยู่จริง
                Optional<Reservation> reservationOptional = reservationRepository.findById(reservationId);
                Reservation reservation = reservationOptional.orElseThrow(() -> new EntityNotFoundException("Reservation not found"));

                // อนุมัติการจอง
                ReservationApproved reservationApproved = reservationApprovedService.approveReservation(reservation, user, request.getBedId(), request.getWard());

                // อัปเดตสถานะการจอง
                reservation.setReservationApproved(reservationApproved);
                reservation.setStatus(ReservationStatus.อนุมัติ);  // ใช้ enum ของสถานะการอนุมัติ
                reservationRepository.save(reservation);

                // บันทึกการอนุมัติการจอง
                reservationApprovedRepository.save(reservationApproved);

                return ResponseEntity.ok(reservationApproved);

            } catch (EntityNotFoundException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
            } catch (DataIntegrityViolationException ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data integrity violation: " + ex.getMessage());
            } catch (Exception ex) {
                ex.printStackTrace();  // เพิ่มการพิมพ์ข้อผิดพลาดเพื่อดีบัก
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
            }
        }

        @GetMapping("/{reservationId}")
        public ResponseEntity<Reservation> getReservationById(@PathVariable("reservationId") Long reservationId) {
            Optional<Reservation> reservation = reservationRepository.findById(reservationId);
            return reservation.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
        }

        @PostMapping("/{reservationId}/reject")
        public ResponseEntity<String> rejectReservation(@PathVariable Long reservationId,
                                                        @RequestParam Long userId,
                                                        @RequestBody String reason) {
            try {
                // ตรวจสอบว่าผู้ใช้ (userId) มีอยู่จริง
                Optional<User> userOptional = userRepository.findById(userId);
                User user = userOptional.orElseThrow(() -> new EntityNotFoundException("User not found"));

                // ตรวจสอบสิทธิ์ว่าเป็น ADMIN หรือไม่
                if (!"ADMIN".equals(user.getRole())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("Only Admin can reject reservations.");
                }

                // ตรวจสอบว่ามีการจองอยู่จริง
                Optional<Reservation> reservationOptional = reservationRepository.findById(reservationId);
                Reservation reservation = reservationOptional.orElseThrow(() -> new EntityNotFoundException("Reservation not found"));

                // เปลี่ยนสถานะเป็นปฏิเสธ
                reservation.setStatus(ReservationStatus.ปฏิเสธ); // เปลี่ยนสถานะเป็นปฏิเสธ
                reservationRepository.save(reservation); // บันทึกการเปลี่ยนแปลง

                return ResponseEntity.ok("Reservation has been rejected. Reason: " + reason);

            } catch (EntityNotFoundException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
            } catch (Exception ex) {
                ex.printStackTrace();  // เพิ่มการพิมพ์ข้อผิดพลาดเพื่อดีบัก
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
            }
        }

    }
