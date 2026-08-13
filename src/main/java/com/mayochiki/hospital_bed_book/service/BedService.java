package com.mayochiki.hospital_bed_book.service;

import com.mayochiki.hospital_bed_book.entity.*;
import com.mayochiki.hospital_bed_book.repository.BedRepository;
import com.mayochiki.hospital_bed_book.repository.PatientRepository;
import com.mayochiki.hospital_bed_book.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BedService {

	private final BedRepository bedRepository;
	private final ReservationRepository reservationRepository;
	private final PatientRepository patientRepository;

	@Autowired
	public BedService(BedRepository bedRepository, ReservationRepository reservationRepository, PatientRepository patientRepository) {
		this.bedRepository = bedRepository;
		this.reservationRepository = reservationRepository;
		this.patientRepository = patientRepository;
	}

	// การดึงข้อมูลเตียงทั้งหมด
	public List<Bed> getAllBeds() {
		return bedRepository.findAll();
	}

	// การค้นหาเตียงจาก Ward และหมายเลขเตียง
	public Bed findBedByWardAndBedNumber(String ward, String bedNumber) {
		return bedRepository.findByWardAndBedNumber(ward, bedNumber)
				.orElseThrow(() -> new IllegalArgumentException("ไม่พบเตียงในแผนกหรือหมายเลขเตียงที่ระบุ"));
	}

	// การบันทึกข้อมูลเตียงใหม่
	public Bed saveBedData(Bed bed) {
		return bedRepository.save(bed);
	}

	// การอัปเดตสถานะของเตียง
	@Transactional
	public void updateBedStatus(Long id, BedStatus bedStatus) {
		Bed bed = bedRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("ไม่พบเตียงที่ระบุ"));
		if (bed.getStatus() == BedStatus.ไม่ว่าง) {
			throw new IllegalArgumentException("ไม่สามารถเปลี่ยนสถานะเตียงที่ไม่ว่าง");
		}
		bed.setStatus(bedStatus);
		bedRepository.save(bed);
	}

	// การค้นหาเตียงที่ว่าง
	public List<Bed> findAvailableBeds() {
		return bedRepository.findByStatusAndPatientIsNull(BedStatus.ว่าง);
	}

	// การค้นหาเตียงตาม Ward
	public List<Bed> findBedsByWard(String ward) {
		return bedRepository.findByWard(ward);
	}

	// การค้นหาเตียงตามสถานะ
	public List<Bed> findBedsByStatus(BedStatus bedStatus) {
		return bedRepository.findByStatusAndPatientIsNull(bedStatus);
	}

	// การอัปเดตเตียง
	@Transactional
	public Optional<Bed> updateBed(Long id, Bed bed) {
		return bedRepository.findById(id)
				.map(existingBed -> {
					existingBed.setStatus(bed.getStatus());
					return bedRepository.save(existingBed);
				});
	}

	// การจองเตียง
	@Transactional
	public Reservation bookBed(Long bedId, Long patientId, LocalDate reservationDate) {
		// ตรวจสอบการจองเตียงที่มีอยู่แล้ว
		Optional<Reservation> existingReservation = reservationRepository
				.findByBed_IdAndPatient_IdAndReservationDate(bedId, patientId, reservationDate);

		if (existingReservation.isPresent()) {
			throw new IllegalArgumentException("มีการจองเตียงนี้สำหรับผู้ป่วยนี้ในวันที่ระบุแล้ว");
		}

		Bed bed = bedRepository.findById(bedId)
				.orElseThrow(() -> new IllegalArgumentException("เตียงไม่พบ"));

		if (bed.getStatus() != BedStatus.ว่าง) {
			throw new IllegalArgumentException("เตียงไม่ว่างสำหรับการจอง");
		}

		Patient patient = patientRepository.findById(patientId)
				.orElseThrow(() -> new IllegalArgumentException("ผู้ป่วยไม่พบ"));

		// ตั้งสถานะเตียงให้ไม่ว่าง
		bed.setStatus(BedStatus.ไม่ว่าง);
		bedRepository.save(bed);

		// สร้างการจองเตียงใหม่
		Reservation reservation = new Reservation();
		reservation.setBed(bed);
		reservation.setPatient(patient);
		reservation.setReservationDate(reservationDate);
		reservation.setStatus(ReservationStatus.รออนุมัติ);  // ตั้งค่าเริ่มต้นให้เป็น "รออนุมัติ"

		return reservationRepository.save(reservation);
	}

	// การค้นหาผู้ป่วยที่ครอบครองเตียง
	public Optional<Patient> getPatientByBedId(Long bedId) {
		Optional<Bed> bed = bedRepository.findById(bedId);
		return bed.map(Bed::getPatient);
	}

	// การค้นหาเตียงตาม ID
	public Bed findBedById(Long id) {
		return bedRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("ไม่พบเตียงที่ระบุ"));
	}

	// การลบเตียง
	@Transactional
	public boolean deleteBed(Long id) {
		if (bedRepository.existsById(id)) {
			bedRepository.deleteById(id);
			return true;
		}
		return false;
	}

	// การค้นหาการจองเตียงตามเตียง, ผู้ป่วย และวันที่
	public Optional<Reservation> findReservationByBedAndPatient(Long bedId, Long patientId, LocalDate reservationDate) {
		return reservationRepository.findByBed_IdAndPatient_IdAndReservationDate(bedId, patientId, reservationDate);
	}

	// การพยายามจองเตียง
	public Reservation attemptToBookBed(Long bedId, Long patientId, LocalDate reservationDate) {
		// ตรวจสอบว่าเตียงว่างไหม
		Bed bed = findBedById(bedId);
		if (bed == null || bed.getStatus() == BedStatus.ไม่ว่าง) {
			return null; // คืนค่า null หากเตียงไม่ว่าง
		}

		// ตรวจสอบว่ามีการจองเตียงนี้แล้วในวันที่เดียวกันหรือไม่
		Optional<Reservation> existingReservation = findReservationByBedAndPatient(bedId, patientId, reservationDate);
		if (existingReservation.isPresent()) {
			return null; // คืนค่า null หากมีการจองแล้ว
		}

		// จองเตียง
		return bookBed(bedId, patientId, reservationDate); // ดำเนินการจอง
	}

	public void assignFoodTypeToPatient(Long patientId, String foodTypeStr) {
		// Use fromString to safely handle invalid enum values
		FoodType foodType = FoodType.fromString(foodTypeStr);

		// Find the patient by ID
		Patient patient = patientRepository.findById(patientId)
				.orElseThrow(() -> new IllegalArgumentException("ไม่พบผู้ป่วย"));

		// Set the food type for the patient
		patient.setFoodType(foodType);

		// Save the patient
		patientRepository.save(patient);
	}
	@Transactional
	public void assignBedToPatient(Long bedId, Long patientId) {
		Bed bed = bedRepository.findById(bedId)
				.orElseThrow(() -> new IllegalArgumentException("ไม่พบเตียงที่ระบุ"));

		Patient patient = patientRepository.findById(patientId)
				.orElseThrow(() -> new IllegalArgumentException("ไม่พบผู้ป่วยที่ระบุ"));

		// ตรวจสอบว่าเตียงว่างหรือไม่
		if (bed.getStatus() != BedStatus.ว่าง) {
			throw new IllegalStateException("เตียงนี้ไม่ว่างสำหรับการจอง");
		}

		// กำหนดผู้ป่วยให้กับเตียง
		bed.setPatient(patient);
		bed.setStatus(BedStatus.ไม่ว่าง); // เปลี่ยนสถานะเตียงเป็นไม่ว่าง

		// บันทึกการเปลี่ยนแปลง
		bedRepository.save(bed);
	}



}
