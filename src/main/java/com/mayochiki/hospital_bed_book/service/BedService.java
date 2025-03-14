package com.mayochiki.hospital_bed_book.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mayochiki.hospital_bed_book.entity.Bed;
import com.mayochiki.hospital_bed_book.repository.BedRepository;

@Service
public class BedService {

    @Autowired
    private BedRepository bedRepository;

    // Check if a Bed exists by Patient HN
    public boolean doesBedExist(String hn) {
        return bedRepository.existsByHn(hn); // Check if a bed exists with this patient's HN
    }

    // Get the Bed by Patient HN
    public Bed getBedByHn(String hn) {
        return bedRepository.findByHn(hn); // Find a bed associated with this patient's HN
    }

    // Example of saving a new Bed (requires @Transactional)
    @Transactional
    public Bed saveBed(Bed bed) {
        return bedRepository.save(bed); // Save the bed data
    }

    // Example of updating a Bed (requires @Transactional)
    @Transactional
    public Bed updateBed(Bed bed) {
        return bedRepository.save(bed); // Update the bed data
    }
}
