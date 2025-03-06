import { useNavigate } from "react-router-dom";

const Manage = () => {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen bg-gray-100 flex flex-col items-center p-4">

      <div className="flex flex-col items-center mt-20">
        <h1 className="text-2xl font-bold">Manage Pages</h1>
        <p className="mt-2 text-gray-600">This is the management section.</p>
        <button
          onClick={() => navigate("/manage/beds")}
          className="mt-4 px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-all"
        >
          Manage Beds
        </button>
      </div>
    </div>
  );
};

export default Manage;
