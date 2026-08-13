package com.mayochiki.hospital_bed_book.config;

import com.mayochiki.hospital_bed_book.entity.*;
import com.mayochiki.hospital_bed_book.repository.BedRepository;
import com.mayochiki.hospital_bed_book.repository.PatientRepository;
import com.mayochiki.hospital_bed_book.repository.ReservationRepository;
import com.mayochiki.hospital_bed_book.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Seeds the database with mock data on startup so the app is usable
 * immediately after `mvnw spring-boot:run`, without any manual data entry.
 * <p>
 * Only runs when the beds table is empty, so it never duplicates data on
 * restarts against a persistent database (e.g. the "mysql" profile) — it
 * only fires the first time. With the default H2 in-memory profile the
 * database is recreated from scratch on every run, so this seeds fresh
 * mock data every time the app starts.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final BedRepository bedRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    public DataSeeder(BedRepository bedRepository,
                       PatientRepository patientRepository,
                       UserRepository userRepository,
                       ReservationRepository reservationRepository) {
        this.bedRepository = bedRepository;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public void run(String... args) {
        if (bedRepository.count() > 0) {
            return; // already seeded, don't duplicate
        }

        // ---------- users ----------
        User admin = userRepository.save(new User("admin", "admin123", "ADMIN", "หัวหน้าพยาบาล ประจำวอร์ด"));
        userRepository.save(new User("staff1", "staff123", "STAFF", "เจ้าหน้าที่พยาบาล สมใจ ดีเวช"));

        // ---------- beds across 4 wards ----------
        List<Bed> beds = List.of(
                new Bed("A-101", "อายุรกรรมชาย", BedStatus.ไม่ว่าง, null, LocalDate.now().minusDays(4)),
                new Bed("A-102", "อายุรกรรมชาย", BedStatus.ว่าง, null, LocalDate.now().minusDays(10)),
                new Bed("A-103", "อายุรกรรมชาย", BedStatus.ว่าง, null, LocalDate.now().minusDays(10)),
                new Bed("A-104", "อายุรกรรมชาย", BedStatus.ไม่ว่าง, null, LocalDate.now().minusDays(2)),

                new Bed("B-201", "อายุรกรรมหญิง", BedStatus.ว่าง, null, LocalDate.now().minusDays(12)),
                new Bed("B-202", "อายุรกรรมหญิง", BedStatus.ไม่ว่าง, null, LocalDate.now().minusDays(1)),
                new Bed("B-203", "อายุรกรรมหญิง", BedStatus.ว่าง, null, LocalDate.now().minusDays(12)),

                new Bed("S-301", "ศัลยกรรม", BedStatus.ว่าง, null, LocalDate.now().minusDays(20)),
                new Bed("S-302", "ศัลยกรรม", BedStatus.ไม่ว่าง, null, LocalDate.now().minusDays(3)),
                new Bed("S-303", "ศัลยกรรม", BedStatus.ว่าง, null, LocalDate.now().minusDays(20)),
                new Bed("S-304", "ศัลยกรรม", BedStatus.ว่าง, null, LocalDate.now().minusDays(20)),

                new Bed("ICU-01", "ICU", BedStatus.ไม่ว่าง, null, LocalDate.now().minusDays(1)),
                new Bed("ICU-02", "ICU", BedStatus.ว่าง, null, LocalDate.now().minusDays(30)),
                new Bed("ICU-03", "ICU", BedStatus.ว่าง, null, LocalDate.now().minusDays(30)),

                new Bed("P-401", "กุมารเวช", BedStatus.ว่าง, null, LocalDate.now().minusDays(15)),
                new Bed("P-402", "กุมารเวช", BedStatus.ว่าง, null, LocalDate.now().minusDays(15))
        );
        beds.forEach(bedRepository::save);

        // ---------- patients occupying the "ไม่ว่าง" beds ----------
        seedOccupant(beds.get(0), "สมชาย", "ใจดี", "1100200300401", "HN00000001", "สิทธิบัตรทอง",
                "ปอดอักเสบ ให้ยาปฏิชีวนะทางหลอดเลือดดำ", FoodType.อาหารอ่อน, LocalDate.now().minusDays(4));

        seedOccupant(beds.get(3), "วิชัย", "แข็งแรง", "1100200300402", "HN00000002", "ประกันสังคม",
                "เบาหวานควบคุมไม่ได้ เฝ้าระวังระดับน้ำตาล", FoodType.อาหารปกติ, LocalDate.now().minusDays(2));

        seedOccupant(beds.get(5), "สมหญิง", "รักษ์ดี", "1100200300403", "HN00000003", "จ่ายเอง",
                "หลังผ่าตัดไส้ติ่ง เฝ้าระวังแผลผ่าตัด", FoodType.อาหารเหลว, LocalDate.now().minusDays(1));

        seedOccupant(beds.get(8), "อนุชา", "มั่นคง", "1100200300404", "HN00000004", "สิทธิข้าราชการ",
                "กระดูกขาหักจากอุบัติเหตุ รอผ่าตัดใส่เหล็กดาม", FoodType.อาหารปกติ, LocalDate.now().minusDays(3));

        seedOccupant(beds.get(11), "ดวงใจ", "ปลอดภัย", "1100200300405", "HN00000005", "สิทธิบัตรทอง",
                "ภาวะหัวใจล้มเหลวเฉียบพลัน เฝ้าระวังใกล้ชิดใน ICU", FoodType.งดอาหาร, LocalDate.now().minusDays(1));

        // ---------- reservations: mix of pending / approved / rejected ----------
        Reservation r1 = new Reservation();
        r1.setFname("มานะ"); r1.setLname("อดทน");
        r1.setIdentificationCard("1100200300406");
        r1.setDiagnostics("ไข้สูงเฉียบพลัน สงสัยไข้เลือดออก รอผลตรวจเลือด");
        r1.setWard("อายุรกรรมชาย");
        r1.setAdmissionDate(LocalDate.now().plusDays(1));
        r1.setReservationDate(LocalDate.now());
        r1.setStatus(ReservationStatus.รออนุมัติ);
        reservationRepository.save(r1);

        Reservation r2 = new Reservation();
        r2.setFname("พรทิพย์"); r2.setLname("งามเลิศ");
        r2.setIdentificationCard("1100200300407");
        r2.setDiagnostics("นัดผ่าตัดถุงน้ำดีแบบส่องกล้อง");
        r2.setWard("ศัลยกรรม");
        r2.setAdmissionDate(LocalDate.now().plusDays(2));
        r2.setReservationDate(LocalDate.now());
        r2.setStatus(ReservationStatus.รออนุมัติ);
        reservationRepository.save(r2);

        Reservation r3 = new Reservation();
        r3.setFname("สุรีย์"); r3.setLname("แจ่มใส");
        r3.setIdentificationCard("1100200300408");
        r3.setDiagnostics("คลอดก่อนกำหนด เฝ้าระวังทารกแรกเกิด");
        r3.setWard("กุมารเวช");
        r3.setAdmissionDate(LocalDate.now().minusDays(1));
        r3.setReservationDate(LocalDate.now().minusDays(2));
        r3.setStatus(ReservationStatus.ปฏิเสธ);
        reservationRepository.save(r3);
    }

    private void seedOccupant(Bed bed, String fname, String lname, String idCard, String hn,
                               String rights, String diagnostics, FoodType foodType, LocalDate admissionDate) {
        Patient patient = new Patient(fname, lname, idCard, hn, null, rights, diagnostics, foodType);
        patient.setFoodType(foodType);
        patient.setAdmissionDate(admissionDate);
        patient.setBed(bed);
        patient = patientRepository.save(patient);

        bed.setPatient(patient);
        bedRepository.save(bed);
    }
}
