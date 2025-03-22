package com.mayochiki.hospital_bed_book;

import com.mayochiki.hospital_bed_book.entity.Bed;
import com.mayochiki.hospital_bed_book.service.BedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class 	HospitalBedBookApplication  {

	public static void main(String[] args) {
		SpringApplication.run(HospitalBedBookApplication.class, args);
	}
}
