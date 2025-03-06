import React, { useState, useEffect } from "react";

type Bed = {
  id: string;
  occupied: boolean;
  special?: boolean;
  patientStatus: string;
  patient?: {
    hn: string;
    an: string;
    insurance: string;
    diagnostics: string;
  };
};

type BedManagementPanelProps = {
  selectedBed: Bed | null;
  show: boolean;
  onClose: () => void;
  onSave: () => void;
  selectedBedStatus: "ว่าง" | "ไม่ว่าง" | null;
  onBedStatusChange: (status: "ว่าง" | "ไม่ว่าง" | null) => void;
  selectedPatientStatus:
    | "หนัก"
    | "กำลังให้การรักษา (CI)"
    | "กำลังให้การรักษา (SI)"
    | "กำลังให้การรักษา (MI)"
    | "รอกลับบ้าน (CL)"
    | null;
  onPatientStatusChange: (
    status:
      | "หนัก"
      | "กำลังให้การรักษา (CI)"
      | "กำลังให้การรักษา (SI)"
      | "กำลังให้การรักษา (MI)"
      | "รอกลับบ้าน (CL)"
      | null
  ) => void;
};

const BedManagementPanel = ({
  selectedBed,
  show,
  onClose,
  onSave,
  selectedBedStatus,
  onBedStatusChange,
  selectedPatientStatus,
  onPatientStatusChange,
}: BedManagementPanelProps) => {
  const [isVisible, setIsVisible] = useState(false);
  const [shouldRender, setShouldRender] = useState(show); // ควบคุม DOM Rendering

  useEffect(() => {
    if (show) {
      setShouldRender(true); // เปิด popup
      setTimeout(() => setIsVisible(true), 50); // Delay เพื่อให้ transition ทำงาน
    } else {
      setIsVisible(false); // ปิด animation ก่อนซ่อนจริง
      setTimeout(() => setShouldRender(false), 500); // รอ transition จบก่อนซ่อน
    }
  }, [show]);

  const buttonClass = (
    isActive: boolean,
    activeColor: string,
    inactiveColor: string
  ) => {
    return `px-4 py-2 text-base rounded-lg ${
      isActive ? activeColor : inactiveColor
    }`;
  };

  if (!shouldRender || !selectedBed) return null;

  return (
    <div
      className={`fixed inset-0 z-50 flex items-center justify-center transition-opacity duration-500 ease-in-out ${
        isVisible ? "opacity-100" : "opacity-0 pointer-events-none"
      }`}
      style={{
        backdropFilter: isVisible ? "blur(5px)" : "none",
        backgroundColor: isVisible ? "rgba(0, 0, 0, 0.3)" : "transparent",
      }}
    >
      <div
        className={`bg-white p-6 rounded-lg shadow-xl w-full max-w-4xl transform transition-all duration-500 ease-in-out ${
          isVisible ? "translate-x-0 opacity-100" : "translate-x-full opacity-0"
        }`}
        style={{ minHeight: "600px", maxWidth: "700px" }} // ขยายความกว้าง
      >
        <h2 className="text-xl font-bold mb-4">
          การบริหารเตียง: {selectedBed.id}
        </h2>

        {/* การครองเตียง */}
        <div className="mb-4">
          <label className="mr-2">สถานะเตียง:</label>
          <div className="flex justify-between items-center gap-2">
            {/* ปุ่ม ว่าง และ ไม่ว่าง */}
            <div className="flex gap-2">
              <button
                onClick={() => onBedStatusChange("ว่าง")}
                className={`px-4 py-2 rounded-lg ${
                  selectedBedStatus === "ว่าง" ? "bg-green-500" : "bg-gray-300"
                }`}
              >
                ว่าง
              </button>
              <button
                onClick={() => onBedStatusChange("ไม่ว่าง")}
                className={`px-4 py-2 rounded-lg ${
                  selectedBedStatus === "ไม่ว่าง" ? "bg-red-500" : "bg-gray-300"
                }`}
              >
                ไม่ว่าง
              </button>
            </div>

            {/* ปุ่มย้ายเตียง, สลับเตียง, จองเตียง ชิดขวา */}
            <div className="flex gap-4 ml-auto">
              <button className="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-all">
                ย้ายเตียง
              </button>
              <button className="px-4 py-2 bg-yellow-500 text-white rounded-lg hover:bg-yellow-600 transition-all">
                สลับเตียง
              </button>
              <button className="px-4 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600 transition-all">
                จองเตียง
              </button>
            </div>
          </div>
        </div>

        {/* ข้อมูลผู้ป่วย */}
        {selectedBed.occupied ? (
          <div className="mb-4">
            <label className="mr-2">HN:</label>
            <input
              type="text"
              value={selectedBed.patient?.hn || ""}
              className="p-2 border rounded-lg w-full mb-2"
              readOnly
            />
            <label className="mr-2">AN:</label>
            <input
              type="text"
              value={selectedBed.patient?.an || ""}
              className="p-2 border rounded-lg w-full mb-2"
              readOnly
            />
            <label className="mr-2">สิทธิการรักษา:</label>
            <select
              value={selectedBed.patient?.insurance || "insurance"}
              className="p-2 border rounded-lg w-full mb-2"
              disabled
            >
              <option value="insurance">ประกัน</option>
              <option value="cash">เงินสด</option>
            </select>
            <label className="mr-2">Diagnostics:</label>
            <textarea
              value={selectedBed.patient?.diagnostics || ""}
              className="p-2 border rounded-lg w-full mb-2"
              rows={4}
              readOnly
            ></textarea>
          </div>
        ) : (
          <div className="mb-4">
            <p>ยังไม่มีข้อมูลผู้ป่วยในเตียงนี้</p>
          </div>
        )}

        {/* สถานะผู้ป่วย */}
        <div className="mb-4">
          <label className="mr-2">สถานะผู้ป่วย:</label>
          <div className="flex gap-2">
            <button
              onClick={() => onPatientStatusChange("หนัก")}
              className={buttonClass(
                selectedPatientStatus === "หนัก",
                "bg-red-500",
                "bg-gray-300"
              )}
            >
              หนัก
            </button>
            <button
              onClick={() => onPatientStatusChange("กำลังให้การรักษา (CI)")}
              className={buttonClass(
                selectedPatientStatus === "กำลังให้การรักษา (CI)",
                "bg-pink-500",
                "bg-gray-300"
              )}
            >
              กำลังให้การรักษา (CI)
            </button>
            <button
              onClick={() => onPatientStatusChange("กำลังให้การรักษา (SI)")}
              className={buttonClass(
                selectedPatientStatus === "กำลังให้การรักษา (SI)",
                "bg-yellow-500",
                "bg-gray-300"
              )}
            >
              กำลังให้การรักษา (SI)
            </button>
            <button
              onClick={() => onPatientStatusChange("กำลังให้การรักษา (MI)")}
              className={buttonClass(
                selectedPatientStatus === "กำลังให้การรักษา (MI)",
                "bg-green-500",
                "bg-gray-300"
              )}
            >
              กำลังให้การรักษา (MI)
            </button>
            <button
              onClick={() => onPatientStatusChange("รอกลับบ้าน (CL)")}
              className={buttonClass(
                selectedPatientStatus === "รอกลับบ้าน (CL)",
                "bg-blue-500",
                "bg-gray-300"
              )}
            >
              รอกลับบ้าน (CL)
            </button>
          </div>
        </div>

        {/* ปุ่มบันทึกและปิด */}
        <div className="flex gap-4 mt-4">
          <button
            onClick={onSave}
            className="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-all"
          >
            Save
          </button>
          <button
            onClick={() => {
              setIsVisible(false);
              setTimeout(onClose, 500);
            }} // ปิดแบบ smooth
            className="px-4 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600 transition-all"
          >
            Close
          </button>
        </div>
      </div>
    </div>
  );
};

export default BedManagementPanel;
