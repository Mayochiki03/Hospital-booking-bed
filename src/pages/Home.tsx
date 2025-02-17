import { useNavigate } from "react-router-dom";

const Home = () => {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen bg-gray-100 flex flex-col items-center p-4">
      <nav className="bg-black text-white p-4 w-full flex justify-between fixed top-0 left-0 right-0 z-50 shadow-lg">
        <div className="text-lg font-bold">Bed Management System</div>
        <div className="space-x-4">
          <button className="px-4 py-2 bg-gray-800 rounded">Home</button>
          <button className="px-4 py-2" onClick={() => navigate("/manage")}>
            Manage
          </button>
          <button className="px-4 py-2">Book</button>
          <button className="px-4 py-2" onClick={() => navigate("/status")}>
            Status
          </button>
          <button className="px-4 py-2">Logout</button>
        </div>
      </nav>

      {/* โลโก้และข้อความจะอยู่ที่ซ้ายสุดของหน้า */}
      <div className="absolute top-20 left-4 flex items-center">
        <img
          src="/public/images/Hospital-logo.jpg"
          alt="Hospital Logo"
          className="w-48 h-48 rounded-full border-4 border-gray-300"
        />
        <h1 className="text-xl font-bold ml-4">โรงพยาบาล ส่งเสริมสุขภาพ</h1>
      </div>

      <div className="flex flex-col items-center mt-40">
        <h1 className="text-2xl font-bold">Home Pages</h1>
        <p className="mt-2 text-gray-600">This is the management section.</p>
      </div>
    </div>
  );
};

export default Home;
