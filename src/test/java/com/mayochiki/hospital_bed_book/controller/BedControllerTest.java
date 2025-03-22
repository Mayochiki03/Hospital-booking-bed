package com.mayochiki.hospital_bed_book.controller;

import com.mayochiki.hospital_bed_book.entity.Bed;
import com.mayochiki.hospital_bed_book.entity.BedStatus;
import com.mayochiki.hospital_bed_book.service.BedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class BedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private BedService bedService;

    @InjectMocks
    private BedController bedController;

    @BeforeEach
    void setUp() {
        // Mock additional services or configurations if necessary
    }

    // ทดสอบ API อัปเดตสถานะเตียง (updateBedStatus)
    @Test
    public void testUpdateBedStatus_Success() throws Exception {
        Long bedId = 1L;
        String status = "ว่าง"; // สถานะที่เราต้องการอัปเดต

        // Mocking the service to simulate successful update
        doNothing().when(bedService).updateBedStatus(bedId, BedStatus.valueOf(status));

        // Performing a PATCH request to /beds/{id} with status=ว่าง
        mockMvc.perform(patch("/api/beds/{id}?status={status}", bedId, status))
                .andExpect(status().isOk());  // Ensure HTTP status code 200 (OK)
    }

    @Test
    public void testUpdateBedStatus_InvalidStatus() throws Exception {
        Long bedId = 1L;
        String status = "invalid_status";  // สถานะที่ไม่ถูกต้อง

        // Performing a PATCH request to /beds/{id} with an invalid status
        mockMvc.perform(patch("/api/beds/{id}?status={status}", bedId, status))
                .andExpect(status().isBadRequest());  // Expecting HTTP 400 (Bad Request)
    }

    // ทดสอบ GET /beds/available สำหรับเตียงที่ว่าง
    @Test
    public void testGetAvailableBeds() throws Exception {
        // Mocking the service to return a list with a single available bed
        when(bedService.findAvailableBeds()).thenReturn(List.of(new Bed("B101", "Ward 1", BedStatus.ว่าง, null, null)));

        // Performing a GET request to /beds/available and validating the response
        mockMvc.perform(get("/api/beds/available"))
                .andExpect(status().isOk())  // Ensure status code is 200 (OK)
                .andExpect(jsonPath("$.length()").value(1))  // Check the length of the response (1 available bed)
                .andExpect(jsonPath("$[0].bedNumber").value("B101"))  // Validate the bed number
                .andExpect(jsonPath("$[0].ward").value("Ward 1"))  // Validate the ward
                .andExpect(jsonPath("$[0].bedStatus").value("ว่าง"));  // Validate the bed status with Thai term
    }

    @Test
    public void testGetAvailableBeds_NoBedsAvailable() throws Exception {
        // Mocking the service to return an empty list (no available beds)
        when(bedService.findAvailableBeds()).thenReturn(List.of());

        // Performing a GET request to /beds/available when there are no available beds
        mockMvc.perform(get("/api/beds/available"))
                .andExpect(status().isOk())  // Ensure status code is 200 (OK)
                .andExpect(jsonPath("$.length()").value(0));  // Validate that there are no beds in the response
    }
}
