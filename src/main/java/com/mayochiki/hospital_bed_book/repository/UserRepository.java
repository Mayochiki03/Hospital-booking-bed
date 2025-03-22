package com.mayochiki.hospital_bed_book.repository;

import com.mayochiki.hospital_bed_book.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // ค้นหาผู้ใช้โดยใช้ username
    Optional<User> findByUsername(String username);

    // ตรวจสอบว่ามี username นี้อยู่ในฐานข้อมูลหรือไม่
    boolean existsByUsername(String username);

}
