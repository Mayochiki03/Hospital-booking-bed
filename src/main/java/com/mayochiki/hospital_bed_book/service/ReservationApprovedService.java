package com.mayochiki.hospital_bed_book.service;

import com.mayochiki.hospital_bed_book.entity.Bed;
import com.mayochiki.hospital_bed_book.entity.Reservation;
import com.mayochiki.hospital_bed_book.entity.ReservationApproved;
import com.mayochiki.hospital_bed_book.entity.User;
import com.mayochiki.hospital_bed_book.repository.ReservationApprovedRepository;
import com.mayochiki.hospital_bed_book.repository.BedRepository;  // ต้องเพิ่ม BedRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReservationApprovedService {

    private final ReservationApprovedRepository reservationApprovedRepository;
    private final BedRepository bedRepository;  // ประกาศ BedRepository

    @Autowired
    public ReservationApprovedService(ReservationApprovedRepository reservationApprovedRepository,
                                      BedRepository bedRepository) {  // เพิ่ม BedRepository ใน constructor
        this.reservationApprovedRepository = reservationApprovedRepository;
        this.bedRepository = bedRepository;  // inject BedRepository
    }

    // ค้นหาการอนุมัติการจองจาก reservation และ user
    public Optional<ReservationApproved> findByReservationAndUser(Reservation reservation, User user) {
        return reservationApprovedRepository.findByReservationAndUser(reservation, user);
    }

    // การอนุมัติการจอง
    public ReservationApproved approveReservation(Reservation reservation, User user, Long bedId, String ward) {
        // ค้นหาเตียงจาก bedId
        Optional<Bed> bedOptional = bedRepository.findById(bedId);
        if (!bedOptional.isPresent()) {
            throw new IllegalStateException("Bed not found.");
        }

        Bed bed = bedOptional.get();

        // อัปเดตข้อมูลเตียงและแผนกในการจอง
        reservation.setBed(bed);
        reservation.setWard(ward);  // กำหนดแผนกที่เลือก

        // ลบการตรวจสอบว่า admin เดิมอนุมัติแล้วหรือไม่
        ReservationApproved reservationApproved = new ReservationApproved(reservation, user, true);
        return reservationApprovedRepository.save(reservationApproved);
    }

    // ฟังก์ชันนี้ใช้เพื่อให้แน่ใจว่าไม่มีการอนุมัติการจองซ้ำ
    public boolean hasAlreadyApproved(Reservation reservation, User user) {
        return findByReservationAndUser(reservation, user).isPresent();
    }
}
