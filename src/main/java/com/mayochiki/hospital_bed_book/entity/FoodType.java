package com.mayochiki.hospital_bed_book.entity;

public enum FoodType {
    อาหารปกติ,
    อาหารอ่อน,
    อาหารเหลว,
    งดอาหาร;

    // No need for the "ไม่ระบุ" value anymore.
    public static FoodType fromString(String foodTypeStr) {
        try {
            return FoodType.valueOf(foodTypeStr);
        } catch (IllegalArgumentException | NullPointerException e) {
            // If invalid input, return null or leave it as null
            return null;  // Returning null if invalid value is passed
        }
    }
}
