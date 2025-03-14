package com.mayochiki.hospital_bed_book;

import com.mayochiki.hospital_bed_book.entity.Bed;
import com.mayochiki.hospital_bed_book.entity.Patient;
import com.mayochiki.hospital_bed_book.repository.BedDAO;
import com.mayochiki.hospital_bed_book.repository.BedRepository;
import com.mayochiki.hospital_bed_book.repository.PatientRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class HospitalBedBookApplication {

	private static final Logger logger = LoggerFactory.getLogger(HospitalBedBookApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(HospitalBedBookApplication.class, args);
	}

	@Bean
	public CommandLineRunner runner(PatientRepository patientRepository, BedRepository bedRepository) {
		return args -> {
			//saveBedData(patientRepository, bedRepository);
			getAllData(bedRepository);

		};
	}

	@Transactional // เพิ่ม @Transactional ที่เมธอดนี้
	public void saveBedData(PatientRepository patientRepository, BedRepository bedRepository) {
		String patientHN = "HN375369"; // HN to check
		Optional<Patient> optionalPatient = patientRepository.findByHn(patientHN);

		Patient patient;
		if (optionalPatient.isPresent()) {
			patient = optionalPatient.get();
		} else {
			// สร้างผู้ป่วยใหม่ถ้ายังไม่มี
			patient = new Patient(
					"ชิซุกะ",
					"อาราเล",
					"1765775369",
					patientHN, // HN
					"AN964787",
					"สิทธิหลักประกันสุขภาพแห่งชาติ (บัตรทอง)",
					"ไส้ติ่งอักเสบ",
					"อาหารเหลว"
			);
			patientRepository.save(patient);
			logger.info("✅ สร้างข้อมูลผู้ป่วยใหม่: {}", patient);
		}

		Bed bed = new Bed(
				"er110",      // หมายเลขเตียง
				"Ward 3",     // ชื่อแผนก
				"ว่าง",       // สถานะเตียง
				patient, // Find patient by HN
				LocalDate.now() // วันที่บันทึกเตียง
		);

		if (bedRepository.existsByBedNumber(bed.getBedNumber())) {
			bedRepository.save(bed);
			System.out.println("✅ บันทึกข้อมูลเตียงสำเร็จ");
			logger.info("✅ บันทึกข้อมูลเตียงสำเร็จ: {}", bed);
		} else {
			logger.warn("⚠️ เตียงหมายเลข {} มีอยู่แล้ว", bed.getBedNumber());
		}
	}

	public void getAllData(BedRepository bedRepository) {
		List<Bed> data = bedRepository.getAll();  // ดึงข้อมูลเตียงทั้งหมดจาก BedRepository
		if (data.isEmpty()) {
			System.out.println("⚠️ ไม่มีข้อมูลเตียงในระบบ");
			logger.warn("⚠️ ไม่มีข้อมูลเตียงในระบบ");
		} else {
			System.out.println("📋 รายการเตียงทั้งหมด:");
			for (Bed bed : data) {
				System.out.println(bed);
				logger.info("📌 {}", bed);
			}
		}
	}

}
