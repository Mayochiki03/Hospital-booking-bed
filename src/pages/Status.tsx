import { useState } from "react";
import { useNavigate } from "react-router-dom";

const wardBeds = {
  ICU: [
    { id: "ICU01", occupied: true },
    { id: "ICU02", occupied: false },
    { id: "ICU03", occupied: true, special: true },
    { id: "ICU04", occupied: true },
    { id: "ICU05", occupied: true },
    { id: "ICU06", occupied: false },
    { id: "ICU07", occupied: true },
    { id: "ICU08", occupied: false },
  ],
  ER: [
    { id: "ER01", occupied: false },
    { id: "ER02", occupied: true },
    { id: "ER03", occupied: true, special: true },
    { id: "ER04", occupied: false },
  ],
  General: [
    { id: "G01", occupied: false },
    { id: "G02", occupied: true },
  ],
  Pediatric: [
    { id: "P01", occupied: false, special: true },
    { id: "P02", occupied: false },
  ],
  Surgical: [
    { id: "S01", occupied: true },
    { id: "S02", occupied: false },
  ],
};

const wards = Object.keys(wardBeds);

const Status = () => {
  const navigate = useNavigate();
  const [ward, setWard] = useState("ICU");
  const [beds, setBeds] = useState(wardBeds["ICU"]);
  const [showWardDropdown, setShowWardDropdown] = useState(false);

  const handleWardChange = (newWard: keyof typeof wardBeds) => {
    setWard(newWard);
    setBeds(wardBeds[newWard]); // เปลี่ยนเตียงตาม ward
    setShowWardDropdown(false);
  };

  return (
    <div className="min-h-screen bg-gray-100 flex flex-col items-center p-4">
      <nav className="bg-black text-white p-4 w-full flex justify-between fixed top-0 left-0 right-0 z-50 shadow-lg">
        <div className="text-lg font-bold">Bed Management System</div>
        <div className="space-x-4">
          <button className="px-4 py-2" onClick={() => navigate("/")}>Home</button>
          <button className="px-4 py-2" onClick={() => navigate("/manage")}>Manage</button>
          <button className="px-4 py-2">Book</button>
          <button className="px-4 py-2 bg-gray-800 rounded">Status</button>
          <button className="px-4 py-2">Logout</button>
        </div>
      </nav>

      <div className="flex flex-col items-start mt-20 w-full max-w-screen-lg pl-8">
        <div className="flex items-center">
          <img
            src="/public/images/Hospital-logo.jpg"
            alt="Hospital Logo"
            className="w-40 h-40 rounded-full border-4 border-gray-300"
          />
          <h1 className="text-xl font-bold ml-4">โรงพยาบาล ส่งเสริมสุขภาพ</h1>
        </div>
      </div>

      <div className="relative bg-white shadow-md rounded-md p-4 border border-gray-300 w-full max-w-lg mt-6">
        <div className="relative">
          <button
            className="text-lg font-semibold bg-white px-4 py-2 shadow rounded-md w-full text-left border border-gray-300"
            onClick={() => setShowWardDropdown(!showWardDropdown)}
          >
            Ward: {ward} ▼
          </button>
          {showWardDropdown && (
            <div className="absolute left-0 w-full bg-white shadow-md border border-gray-300 mt-1 rounded-md z-10">
              {wards.map((w) => (
                <button
                  key={w}
                  className="block w-full text-left px-4 py-2 hover:bg-gray-200"
                  onClick={() => handleWardChange(w as keyof typeof wardBeds)}
                >
                  {w}
                </button>
              ))}
            </div>
          )}
        </div>

        <div className="mt-4 flex flex-wrap justify-center gap-4 p-4 border rounded-md">
          {beds.map((bed) => (
            <div
              key={bed.id}
              className={`w-24 h-24 flex flex-col items-center justify-center text-white rounded-md shadow-md text-sm font-semibold p-2 transition-all duration-300 ${
                bed.special
                  ? "bg-pink-500"
                  : bed.occupied
                  ? "bg-red-500"
                  : "bg-green-500 text-white"
              }`}
            >
              <span className="text-lg">👤</span>
              เตียง {bed.id}
            </div>
          ))}
        </div>
      </div>

    </div>
  );
};

export default Status;