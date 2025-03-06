import { useState } from "react";
import { useNavigate } from "react-router-dom";

const Home = () => {
  const navigate = useNavigate(); // สำหรับเปลี่ยนหน้า
  const [showPanel, setShowPanel] = useState(false); // State สำหรับควบคุม Panel

  return (
    <div className="min-h-screen bg-gray-100 flex flex-col items-center p-4">
      {/* โลโก้และชื่อโรงพยาบาล */}
      <div className="absolute top-20 left-4 flex items-center">
        {/* เพิ่มโลโก้หรือชื่อโรงพยาบาลที่นี่ */}
      </div>

      {/* เนื้อหาหลัก */}
      <div className="flex flex-col items-center mt-40">
        <h1 className="text-2xl font-bold">Home Pages</h1>
        <p className="mt-2 text-gray-600">This is the management section.</p>
        <button
          onClick={() => setShowPanel(true)}
          className="mt-4 px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-all"
        >
          Open Panel
        </button>
      </div>

      {/* Slide-in Panel */}
      <div
        className={`fixed top-0 right-0 w-1/3 h-full bg-white shadow-2xl border-l-2 border-gray-300 p-6 transform ${
          showPanel ? "translate-x-0 opacity-100" : "translate-x-full opacity-0"
        } transition-all duration-500 ease-in-out z-50`}
        style={{ willChange: 'transform, opacity' }}
      >
        <h2 className="text-xl font-bold mb-4">Panel Title</h2>
        <p>รายละเอียดเพิ่มเติม...</p>

        {/* ปุ่มปิด Panel */}
        <button
          onClick={() => setShowPanel(false)}
          className="mt-4 px-4 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600 transition-all"
        >
          Close
        </button>
      </div>

      {/* Overlay สำหรับคลิกปิด Panel */}
      {showPanel && (
        <div
          role="button"
          tabIndex={0}
          className="fixed inset-0 bg-black opacity-30 z-40"
          onClick={() => setShowPanel(false)}
          onKeyDown={(e) => {
            if (e.key === "Enter" || e.key === " ") {
              setShowPanel(false);
            }
          }}
        ></div>
      )}
    </div>
  );
};

export default Home;
