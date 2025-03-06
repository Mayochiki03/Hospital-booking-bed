import { Link } from "react-router";

function Header() {
  return (
    <nav className="flex justify-between items-center py-4 px-10 bg-gray-800 text-white">
      {/* โลโก้ + ชื่อระบบ */}
      <div className="flex items-center">
        <img
          src="/public/images/Hospital-logo.jpg"
          alt="Hospital Logo"
          className="w-20 h-20 rounded-full border-4 border-gray-300"
        />
        <div className="text-lg font-bold ml-4">Bed Management System</div> {/* ใช้ ml-4 เพื่อให้ใกล้กันมากขึ้น */}
      </div>

      {/* เมนู */}
      <ul className="flex space-x-5 font-bold">
        <li className="hover:text-gray-300">
          <Link to="/">Home</Link>
        </li>
        <li className="hover:text-gray-300">
          <Link to="/Status">Status</Link>
        </li>
        <li className="hover:text-gray-300">
          <Link to="/Booking">Booking</Link>
        </li>
        <li className="hover:text-gray-300">
          <Link to="/about">About</Link>
        </li>
      </ul>
    </nav>
  );
}

export default Header;
