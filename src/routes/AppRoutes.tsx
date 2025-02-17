import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Home from "../pages/Home";
import Booking from "../pages/Booking";
import Status from "../pages/Status";
import Manage from "../pages/Manage";

const AppRoutes = () => {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/booking" element={<Booking />} />
        <Route path="/status" element={<Status />} />
        <Route path="/manage" element={<Manage />} />
      </Routes>
    </Router>
  );
};

export default AppRoutes;
