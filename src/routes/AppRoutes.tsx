import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Home from "../pages/Home";
import Booking from "../pages/Booking";
import Status from "../pages/Status";



const AppRoutes = () => {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/booking" element={<Booking />} />
        <Route path="/status" element={<Status />} />
      </Routes>
    </Router>
  );
};

export default AppRoutes;
