import { useState } from "react";
import { useOutletContext } from "react-router-dom";
import { wardBeds } from "../data/wardBeds";
import WardSelector from "../components/WardSelector";
import BedManagementPanel from "../components/BedManagementPanel";

const Status = () => {
  const { wardsData, setWardsData } = useOutletContext<{
    wardsData: typeof wardBeds;
    setWardsData: React.Dispatch<React.SetStateAction<typeof wardBeds>>;
  }>();

  const [ward, setWard] = useState<keyof typeof wardsData>("ICU");
  const [beds, setBeds] = useState(wardsData[ward]);
  const [showBedManagement, setShowBedManagement] = useState(false);
  const [selectedBed, setSelectedBed] = useState<{
    id: string;
    occupied: boolean;
    special?: boolean;
    patientStatus: string;
  } | null>(null);
  const [newBedId, setNewBedId] = useState("");
  const [deleteMode, setDeleteMode] = useState(false);

  // สถานะของเตียง
  const [selectedBedStatus, setSelectedBedStatus] = useState<"ว่าง" | "ไม่ว่าง" | null>(null);

  // สถานะของผู้ป่วย
  const [selectedPatientStatus, setSelectedPatientStatus] = useState<
    "หนัก" | "กำลังให้การรักษา (CI)" | "กำลังให้การรักษา (SI)" | "กำลังให้การรักษา (MI)" | "รอกลับบ้าน (CL)" | null
  >(null);

  const handleWardChange = (newWard: keyof typeof wardsData) => {
    setWard(newWard);
    setBeds(wardsData[newWard]);
  };

  const addBed = () => {
    if (newBedId.trim()) {
      const newBed = { id: newBedId, occupied: false, patientStatus: "red" };
      const updatedBeds = [...beds, newBed];
      updatedBeds.sort((a, b) => a.id.localeCompare(b.id));
      setBeds(updatedBeds);
      setNewBedId("");
    }
  };

  const removeBed = (bedId: string) => {
    setBeds(beds.filter((bed) => bed.id !== bedId));
  };

  const openBedManagement = (bed: {
    id: string;
    occupied: boolean;
    special?: boolean;
    patientStatus: string;
  }) => {
    setSelectedBed(bed);
    setSelectedBedStatus(bed.occupied ? "ไม่ว่าง" : "ว่าง");
    setSelectedPatientStatus(bed.patientStatus as any);
    setShowBedManagement(true);
  };

  const closeBedManagement = () => {
    setSelectedBed(null);
    setShowBedManagement(false);
    setSelectedBedStatus(null);
    setSelectedPatientStatus(null);
  };

  const handleBedStatusChange = (status: "ว่าง" | "ไม่ว่าง" | null) => {
    setSelectedBedStatus(status);
  };

  const handlePatientStatusChange = (
    status: "หนัก" | "กำลังให้การรักษา (CI)" | "กำลังให้การรักษา (SI)" | "กำลังให้การรักษา (MI)" | "รอกลับบ้าน (CL)" | null
  ) => {
    setSelectedPatientStatus(status);
  };

  const handleSave = () => {
    if (selectedBed && selectedBedStatus !== null && selectedPatientStatus !== null) {
      setBeds(
        beds.map((bed) =>
          bed.id === selectedBed.id
            ? {
                ...bed,
                occupied: selectedBedStatus === "ไม่ว่าง", // ถ้าสถานะเป็น "ไม่ว่าง" ให้เตียงไม่ว่าง
                patientStatus: selectedPatientStatus, // ตั้งค่าสถานะผู้ป่วย
              }
            : bed
        )
      );
      closeBedManagement();
    }
  };

  const getBackgroundColor = (status: string) => {
    switch (status) {
      case "red":
        return "bg-red-500";
      case "pink":
        return "bg-pink-500";
      case "yellow":
        return "bg-yellow-500";
      case "green":
        return "bg-green-500";
      case "white":
        return "bg-white";
      default:
        return "bg-white";
    }
  };

  return (
    <div className="min-h-screen bg-gray-100 flex flex-col items-center p-6">
      <div className="relative bg-white shadow-lg rounded-lg p-6 border w-full max-w-4xl">
        <div className="flex gap-2 mb-6">
          <input
            type="text"
            value={newBedId}
            onChange={(e) => setNewBedId(e.target.value)}
            placeholder="New Bed ID"
            className="p-3 border rounded-lg w-full max-w-xs"
          />
          <button
            onClick={addBed}
            className="p-3 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition"
          >
            Add Bed
          </button>
          <button
            onClick={() => setDeleteMode(!deleteMode)}
            className={`p-3 rounded-lg ${deleteMode ? "bg-red-500 text-white" : "bg-gray-300 hover:bg-gray-400"}`}
          >
            {deleteMode ? "Cancel Delete" : "Delete Mode"}
          </button>
        </div>

        <div className="flex flex-col gap-4">
          <WardSelector
            currentWard={ward}
            wards={Object.keys(wardsData)}
            onWardChange={handleWardChange}
          />

          <div className="grid grid-cols-4 gap-6 p-4">
            {beds.map((bed) => (
              <div
                key={bed.id}
                className={`relative p-6 text-center text-black rounded-xl cursor-pointer shadow-md transition-all duration-300 ${getBackgroundColor(bed.patientStatus)}`}
                onClick={() => openBedManagement(bed)}
              >
                <div className="flex flex-col justify-between h-full">
                  <div className="flex-grow"></div>
                  <div className="text-xl">{`เตียง ${bed.id}`}</div>
                  {deleteMode && (
                    <button
                      className="absolute top-1 right-1 p-1 bg-black text-white text-xs rounded-full"
                      onClick={(e) => {
                        e.stopPropagation();
                        if (window.confirm(`Are you sure you want to delete ${bed.id}?`)) {
                          removeBed(bed.id);
                        }
                      }}
                    >
                      ❌
                    </button>
                  )}
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      <BedManagementPanel
        selectedBed={selectedBed}
        show={showBedManagement}
        onClose={closeBedManagement}
        onSave={handleSave}
        selectedBedStatus={selectedBedStatus}
        onBedStatusChange={handleBedStatusChange}
        selectedPatientStatus={selectedPatientStatus}
        onPatientStatusChange={handlePatientStatusChange}
      />

      {showBedManagement && (
        <div className="fixed inset-0 bg-black opacity-30 z-40" onClick={closeBedManagement}></div>
      )}
    </div>
  );
};

export default Status;
