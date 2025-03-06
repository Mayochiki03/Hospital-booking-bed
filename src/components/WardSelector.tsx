//component/wardSelector 
import React, { useState } from "react";
import { wardBeds } from "../data/wardBeds";

interface WardSelectorProps {
  currentWard: keyof typeof wardBeds;
  wards: string[];
  onWardChange: (ward: keyof typeof wardBeds) => void;
}

const WardSelector: React.FC<WardSelectorProps> = ({ currentWard, wards, onWardChange }) => {
  const [isDropdownOpen, setIsDropdownOpen] = useState(false); // สถานะของ dropdown ว่าเปิดหรือปิด

  // ฟังก์ชันที่ใช้ในการคลิกเลือก ward
  const handleWardClick = (ward: keyof typeof wardBeds) => {
    onWardChange(ward);
    setIsDropdownOpen(false); // เมื่อเลือกแล้วให้ปิด dropdown
  };

  // ฟังก์ชันในการ toggle การแสดง dropdown
  const toggleDropdown = () => {
    setIsDropdownOpen((prevState) => !prevState);
  };

  return (
    <div className="relative mb-4">
      <button
        className="text-lg font-semibold bg-white px-4 py-2 shadow rounded-md w-full text-left border border-gray-300"
        onClick={toggleDropdown} // คลิกเพื่อเปิด/ปิด dropdown
      >
        Ward: {currentWard} ▼
      </button>
      {/* แสดง dropdown เมื่อตัวแปร isDropdownOpen เป็น true */}
      {isDropdownOpen && (
        <div className="absolute left-0 w-full bg-white shadow-md border border-gray-300 mt-1 rounded-md z-10">
          {wards.map((ward) => (
            <button
              key={ward}
              className="block w-full text-left px-4 py-2 hover:bg-gray-200"
              onClick={() => handleWardClick(ward as keyof typeof wardBeds)} // เมื่อคลิกให้เลือก ward
            >
              {ward}
            </button>
          ))}
        </div>
      )}
    </div>
  );
};

export default WardSelector;
