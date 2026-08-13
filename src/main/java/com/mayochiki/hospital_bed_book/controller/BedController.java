package com.mayochiki.hospital_bed_book.controller;

import com.mayochiki.hospital_bed_book.entity.*;
import com.mayochiki.hospital_bed_book.exception.PatientNotFoundException;
import com.mayochiki.hospital_bed_book.service.BedService;
import com.mayochiki.hospital_bed_book.service.PatientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/beds")
@CrossOrigin(origins = "*")
public class BedController {

	private final BedService bedService;
	private final PatientService patientService;
	private static final Logger logger = LoggerFactory.getLogger(BedController.class);

	@Autowired
	public BedController(BedService bedService, PatientService patientService) {
		this.bedService = bedService;
		this.patientService = patientService;
	}

	// Get all beds
	@GetMapping
	public ResponseEntity<List<Bed>> getAllBeds() {
		List<Bed> beds = bedService.getAllBeds();
		return beds.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(beds);
	}

	// Get a specific bed by ward and bed number
	@GetMapping("/{ward}/{bedNumber}")
	public ResponseEntity<Bed> getBedByWardAndBedNumber(@PathVariable String ward, @PathVariable String bedNumber) {
		Optional<Bed> bedOptional = Optional.ofNullable(bedService.findBedByWardAndBedNumber(ward, bedNumber));
		if (bedOptional.isPresent()) {
			return ResponseEntity.ok(bedOptional.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	// Create a new bed
	@PostMapping
	public ResponseEntity<Bed> createBed(@RequestBody Bed bed) {
		if (bed.getWard() == null || bed.getBedNumber() == null || bed.getStatus() == null) {
			return ResponseEntity.badRequest().build();  // Incomplete information
		}
		Bed createdBed = bedService.saveBedData(bed);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdBed);
	}

	// Update bed status
	@PatchMapping("/{id}")
	public ResponseEntity<Void> updateBedStatus(@PathVariable Long id, @RequestParam String status) {
		try {
			BedStatus bedStatus = BedStatus.valueOf(status.toUpperCase());
			bedService.updateBedStatus(id, bedStatus);
			logger.info("Updated bed status to {} for bed ID {}", bedStatus, id);
			return ResponseEntity.ok().build();
		} catch (IllegalArgumentException e) {
			logger.error("Invalid status value: {} for bed ID {}", status, id);
			return ResponseEntity.badRequest().build(); // Handle invalid status
		}
	}


	// Find available beds
	@GetMapping("/available")
	public ResponseEntity<List<Bed>> findAvailableBeds() {
		List<Bed> availableBeds = bedService.findAvailableBeds();
		return availableBeds.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(availableBeds);
	}

	// Get beds by status
	@GetMapping("/status/{status}")
	public ResponseEntity<List<Bed>> getBedsByStatus(@PathVariable String status) {
		try {
			BedStatus bedStatus = BedStatus.valueOf(status.toUpperCase());
			List<Bed> beds = bedService.findBedsByStatus(bedStatus);
			return beds.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(beds);
		} catch (IllegalArgumentException e) {
			logger.error("Invalid status value: {}", status);
			return ResponseEntity.badRequest().build(); // Handle invalid status input
		}
	}

	// Delete bed
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteBed(@PathVariable Long id) {
		if (bedService.deleteBed(id)) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.notFound().build();
	}

	// Book a bed for a patient
	@PostMapping("/book/{bedId}/{patientId}")
	public ResponseEntity<?> bookBed(@PathVariable Long bedId, @PathVariable Long patientId, @RequestParam LocalDate reservationDate) {
		try {
			// Find patient
			Patient patient = patientService.findById(patientId)
					.orElseThrow(() -> new PatientNotFoundException("ไม่พบข้อมูลผู้ป่วย"));

			// Use BedService to check availability and book the bed
			Reservation reservation = bedService.attemptToBookBed(bedId, patientId, reservationDate);
			if (reservation == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("เตียงไม่ว่าง หรือมีการจองในวันที่เดียวกันแล้ว");
			}

			return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
		} catch (PatientNotFoundException e) {
			logger.error("Patient not found: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (Exception e) {
			logger.error("Unexpected error occurred while booking bed: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("เกิดข้อผิดพลาดที่ไม่สามารถคาดเดาได้");
		}
	}


	// Update bed details
	@PutMapping("/{id}")
	public ResponseEntity<Bed> updateBed(@PathVariable Long id, @RequestBody Bed bed) {
		Optional<Bed> updatedBedOptional = bedService.updateBed(id, bed);
		if (updatedBedOptional.isPresent()) {
			return ResponseEntity.ok(updatedBedOptional.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	// Get beds by ward
	@GetMapping("/ward/{ward}")
	public ResponseEntity<List<Bed>> getBedsByWard(@PathVariable String ward) {
		List<Bed> beds = bedService.findBedsByWard(ward);
		return beds.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(beds);
	}

	// Get patient assigned to a bed
	@GetMapping("/{id}/patient")
	public ResponseEntity<Patient> getPatientByBedId(@PathVariable Long id) {
		Optional<Patient> patientOptional = bedService.getPatientByBedId(id);
		if (patientOptional.isPresent()) {
			return ResponseEntity.ok(patientOptional.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}
}
