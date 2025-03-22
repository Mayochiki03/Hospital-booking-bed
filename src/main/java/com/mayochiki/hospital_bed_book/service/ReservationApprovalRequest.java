package com.mayochiki.hospital_bed_book.service;

public class ReservationApprovalRequest {
    private boolean approved;
    private Long bedId;
    private String ward;

    // Getters and setters
    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public Long getBedId() {
        return bedId;
    }

    public void setBedId(Long bedId) {
        this.bedId = bedId;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }
}
