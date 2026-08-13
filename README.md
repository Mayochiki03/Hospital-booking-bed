# Hospital Bed Booking (ระบบจองเตียงโรงพยาบาล)

REST API สำหรับจัดการเตียง (Bed) ผู้ป่วย (Patient) การจองเตียง (Reservation) และการอนุมัติการจอง เขียนด้วย **Spring Boot 3 + Spring Data JPA**

> โปรเจกต์นี้ถูกตรวจสอบและแก้ไขให้รันได้จริงแล้ว (ดูหัวข้อ "สิ่งที่แก้ไข" ด้านล่าง) เดิมโปรเจกต์รันไม่ได้เพราะตั้งค่า dependency ผิดหลายจุด

## เทคโนโลยีที่ใช้

- Java 21
- Spring Boot 3.4.3 (Web, Data JPA, Validation)
- H2 (in-memory) สำหรับรันตอน dev แบบไม่ต้องติดตั้งอะไร
- MySQL 8 (ผ่าน Docker) สำหรับใช้งานจริง/เก็บข้อมูลถาวร
- Maven (มี Maven Wrapper `mvnw` ให้แล้ว ไม่ต้องติดตั้ง Maven เอง)

## หน้าเว็บใช้งาน (Ward Board)

โปรเจกต์นี้แนบหน้าเว็บ dashboard มาให้ในตัว (อยู่ที่ `src/main/resources/static/`) — Spring Boot เสิร์ฟให้อัตโนมัติ ไม่ต้องตั้งค่าอะไรเพิ่ม แค่รันแอปแล้วเปิด `http://localhost:8080` ก็ใช้งานได้เลย

หน้าเว็บนี้เป็น Vanilla HTML/CSS/JS ล้วน (ไม่มี build step, ไม่ต้องลง `npm`) เรียก REST API ของแอปเองทั้งหมด รองรับมือถือ/แท็บเล็ต (responsive) ครอบคลุม:

- **ภาพรวม** — สรุปจำนวนเตียงว่าง/ไม่ว่าง, คำขอจองที่รออนุมัติ, สัดส่วนการครองเตียงตามแผนก
- **เตียง** — ผังเตียงแบบการ์ดคล้ายป้ายหน้าห้องผู้ป่วย ค้นหา/กรองตามแผนกและสถานะ เพิ่ม/ลบ/ปรับสถานะเตียงได้
- **การจอง** — สร้างคำขอจองเตียงใหม่ อนุมัติ (เลือกผู้ดูแลที่เป็น ADMIN + เตียงว่าง) หรือปฏิเสธคำขอ
- **ผู้ป่วย** — ดูรายชื่อ ค้นหา เพิ่ม/ลบผู้ป่วย
- **ผู้ใช้** — จัดการผู้ใช้ระบบและบทบาท (ต้องมีผู้ใช้ role `ADMIN` อย่างน้อย 1 คน ก่อนจึงจะอนุมัติการจองได้)

> เพื่อให้หน้าเว็บนี้ใช้งานได้ครบ ได้เพิ่ม endpoint `GET /api/patients` และ `GET /api/users` (ดึงรายการทั้งหมด) เข้าไปในแบ็กเอนด์ด้วย เนื่องจากของเดิมมีแต่ endpoint ดึงทีละรายการตาม id เท่านั้น

## ข้อมูล mock (สั่งรันแล้วใช้ได้เลย ไม่ต้องเพิ่มเอง)

โปรเจกต์นี้จะใส่ข้อมูลตัวอย่างให้อัตโนมัติตอนสตาร์ทแอป (ถ้าฐานข้อมูลยังว่างอยู่) ไม่ต้องพิมพ์เพิ่มเอง ประกอบด้วย:

- ผู้ใช้ 2 คน: `admin` / `admin123` (role `ADMIN` — ใช้อนุมัติการจองได้) และ `staff1` / `staff123` (role `STAFF`)
- เตียง 15 เตียง กระจาย 4 แผนก (อายุรกรรมชาย, อายุรกรรมหญิง, ศัลยกรรม, ICU, กุมารเวช) — บางเตียงว่าง บางเตียงไม่ว่าง
- ผู้ป่วย 5 คน ที่ครองเตียงอยู่แล้ว พร้อมข้อมูลวินิจฉัย/สิทธิการรักษา
- คำขอจองเตียง 3 รายการ (สถานะรออนุมัติ 2 รายการ, ปฏิเสธแล้ว 1 รายการ) ให้ลองกดอนุมัติ/ปฏิเสธได้ทันที

โค้ดของตัวใส่ข้อมูลอยู่ที่ `src/main/java/com/mayochiki/hospital_bed_book/config/DataSeeder.java` แก้ไข/เพิ่มข้อมูลเองได้ตามต้องการ

## ถ้าหยุดรัน ข้อมูลหายไหม ใช้ DB อะไร

ขึ้นอยู่กับว่าใช้ profile ไหน:

| Profile | ฐานข้อมูล | ข้อมูลหายไหมตอนปิดแอป |
|---|---|---|
| ค่า default (ไม่ระบุ profile) | **H2 in-memory** (ข้อมูลอยู่ใน RAM ของโปรเซส ไม่มีไฟล์เก็บถาวร) | **หาย** ทุกครั้งที่ปิดแอป และจะสร้างข้อมูล mock ชุดใหม่ให้ทุกครั้งที่รันใหม่ |
| `mysql` (`-Dspring-boot.run.profiles=mysql` + `docker compose up -d`) | **MySQL** (เก็บลง volume ของ Docker) | **ไม่หาย** ข้อมูลอยู่ถาวรแม้ปิด/เปิดแอปใหม่ (ตัว mock data จะใส่ให้แค่ครั้งแรกที่ตารางยังว่างอยู่เท่านั้น) |

สรุปคือ: ถ้าแค่อยากลองเล่น/เดโม ใช้ default (H2) ได้เลย สั่ง `./mvnw spring-boot:run` ครั้งเดียวจบ มีข้อมูลให้พร้อมทุกครั้ง แต่ถ้าจะเอาไปใช้งานจริงที่ต้องเก็บข้อมูลข้ามการรีสตาร์ท ต้องสลับไปใช้ profile `mysql`

## เริ่มรันแบบเร็วที่สุด (ไม่ต้องติดตั้ง DB)

โปรเจกต์นี้ตั้งค่า default profile ให้ใช้ **H2 in-memory database** ไว้แล้ว แค่มี JDK 21 ในเครื่องก็รันได้เลย

```bash
# macOS / Linux
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

แอปจะรันที่ `http://localhost:8080`
ดูข้อมูลในฐานข้อมูล H2 ได้ที่ `http://localhost:8080/h2-console`
(JDBC URL: `jdbc:h2:mem:bed_hospital`, User: `sa`, Password: เว้นว่าง)

> หมายเหตุ: H2 เป็น in-memory database ข้อมูลจะหายทุกครั้งที่ปิดแอป เหมาะสำหรับพัฒนา/ทดสอบเท่านั้น

## รันกับ MySQL (สำหรับข้อมูลถาวร)

โปรเจกต์แนบ `docker-compose.yml` ไว้ให้ ใช้ MySQL ได้ในคำสั่งเดียว

```bash
# 1) เปิด MySQL ด้วย Docker
docker compose up -d

# 2) รันแอปด้วย profile "mysql"
./mvnw spring-boot:run -Dspring-boot.run.profiles=mysql
```

ค่าเชื่อมต่อ default (แก้ได้ผ่าน environment variable): `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` (ดูใน `src/main/resources/application-mysql.properties`)

โครงสร้างตารางถูกสร้างอัตโนมัติจาก JPA entity (`spring.jpa.hibernate.ddl-auto=update`) ไม่ต้องรันสคริปต์ SQL เอง

## Build เป็นไฟล์ jar

```bash
./mvnw clean package
java -jar target/hospital_bed_book-0.0.1-SNAPSHOT.jar
```

## รันเทสต์

```bash
./mvnw test
```

## โครงสร้างโปรเจกต์

```
src/main/java/com/mayochiki/hospital_bed_book/
├── controller/   REST endpoint
├── service/      business logic
├── repository/   Spring Data JPA repositories
├── entity/       JPA entities (Bed, Patient, Reservation, ReservationApproved, User)
└── exception/    custom exceptions + global exception handler
```

## API หลัก

| Resource | Endpoint | คำอธิบาย |
|---|---|---|
| Bed | `GET /api/beds` | ดูเตียงทั้งหมด |
| Bed | `GET /api/beds/available` | ดูเตียงที่ว่าง |
| Bed | `GET /api/beds/ward/{ward}` | ดูเตียงตามแผนก |
| Bed | `POST /api/beds` | เพิ่มเตียงใหม่ |
| Bed | `PATCH /api/beds/{id}?status=ว่าง` | เปลี่ยนสถานะเตียง |
| Bed | `POST /api/beds/book/{bedId}/{patientId}?reservationDate=YYYY-MM-DD` | จองเตียงให้ผู้ป่วย |
| Patient | `GET /api/patients/{id}` , `POST /api/patients` | จัดการข้อมูลผู้ป่วย |
| Reservation | `GET /api/reservations` , `POST /api/reservations` | ดู/สร้างคำขอจองเตียง |
| Reservation | `POST /api/reservations/{id}/approve?userId=&bedId=&ward=` | อนุมัติการจอง (ต้องเป็น user role `ADMIN`) |
| Reservation | `POST /api/reservations/{id}/reject` | ปฏิเสธการจอง |
| User | `POST /api/users` | สร้างผู้ใช้ (สำหรับ admin ที่อนุมัติการจอง) |

## สิ่งที่แก้ไขจากต้นฉบับ

โค้ดเดิมมีปัญหาที่ทำให้รันไม่ได้/เสี่ยงพังตอนใช้งานจริง ได้แก้ไขดังนี้:

1. **เปิด Flyway ทั้งที่ไม่มี dependency** — `application.properties` ตั้ง `spring.flyway.enabled=true` แต่ `pom.xml` ไม่มี Flyway dependency เลย และไฟล์ migration ก็ตั้งเวอร์ชันซ้ำกัน (`V1` สองไฟล์) พร้อมมีไฟล์ `.sql` ว่างเปล่า → ลบ Flyway ออกทั้งหมด ใช้ `hibernate.ddl-auto=update` สร้างตารางจาก entity โดยตรงแทน (ง่ายกว่าสำหรับโปรเจกต์ขนาดนี้)
2. **hibernate-validator เวอร์ชันไม่ตรงกับ jakarta.validation-api** — เดิมล็อก `hibernate-validator:6.2.0.Final` (implement `javax.validation` รุ่นเก่า) คู่กับ `jakarta.validation-api:3.0.0` ซึ่งเป็นคนละ namespace ทำให้ `@NotBlank`, `@Size` ที่ใช้ใน entity ทำงานไม่ถูกต้อง → เปลี่ยนไปใช้ `spring-boot-starter-validation` แทน ให้ Spring Boot จัดเวอร์ชันที่เข้ากันได้ให้เอง
3. **`java.version=23`** — ทำให้คนที่ไม่มี JDK 23 รันไม่ได้ทั้งที่โค้ดไม่ได้ใช้ฟีเจอร์ Java 23 เลย → ลดเป็น `21` (LTS หาไฟล์ติดตั้งง่ายกว่ามาก)
4. **ต้องมี MySQL ล่วงหน้าเสมอ** — เดิมต้องตั้ง MySQL เองก่อนถึงจะรันแอปได้เลยแม้แต่ตอนลองรันครั้งแรก → เพิ่ม H2 in-memory เป็นค่า default ให้รันได้ทันที และแยก MySQL ไปเป็น profile `mysql` พร้อม `docker-compose.yml`
5. **บั๊กตรรกะใน `ReservationService.approveReservation()`** — โค้ดเดิมเทียบ `String` กับค่า enum (`"รออนุมัติ".equals(reservation.getStatus())`) ซึ่งจะเป็น `false` เสมอ ทำให้เมธอดนี้ throw exception ทุกครั้งที่ถูกเรียก → แก้ให้เทียบกับ enum `ReservationStatus.รออนุมัติ` โดยตรง
6. **Query ที่ผิดหลักไวยากรณ์ JPQL** — `BedRepository` มีเมธอด `updateBedStatus(...)` ที่เขียน JPQL แบบ `SET b.patient.id = :patientId` ซึ่งไม่ถูกต้องตามหลัก JPQL (set nested path ไม่ได้) และไม่มีที่ไหนเรียกใช้จริง → ลบทิ้ง (การอัปเดตสถานะเตียงใช้ `BedService.updateBedStatus` ซึ่งทำผ่าน entity save ปกติอยู่แล้ว)
7. **Type ไม่ตรงกันใน repository** — `ReservationRepository.findByStatus(String status)` แต่ฟิลด์ `status` ใน entity เป็น enum `ReservationStatus` → แก้ signature ให้รับ `ReservationStatus`
8. **dependency versions ที่ล็อกเอง เสี่ยงชนกับที่ Spring Boot จัดให้** (`jackson-databind`, `junit-jupiter`, `mockito-core`, `maven-surefire-plugin`) → เอาเวอร์ชันที่ล็อกเองออก ให้ Spring Boot parent POM จัดการเวอร์ชันที่เข้ากันได้ทั้งหมดให้

## ข้อจำกัดที่ควรรู้ (ยังไม่ได้แก้ เผื่อพัฒนาต่อ)

- ระบบสิทธิ์ผู้ใช้ (`role == "ADMIN"`) เป็นแค่การเทียบ string ธรรมดา ยังไม่มี authentication/authorization จริงจัง (เช่น Spring Security + JWT) — **ห้ามใช้ deploy จริงแบบนี้**
- รหัสผ่านผู้ใช้เก็บเป็น plain text ใน `User.password` — ควร hash ด้วย BCrypt ก่อนใช้งานจริง
- ยังไม่มี pagination สำหรับ endpoint ที่ดึงข้อมูลทั้งหมด (`GET /api/beds`, `GET /api/reservations`) ถ้าข้อมูลเยอะควรเพิ่ม
