import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import MainLayout from "../layouts/Mainlayout"; // import Layout
import Home from "../pages/Home"; // import Pages
import Status from "../pages/Status"; // import Status page

const AppRouter = () => {
  return (
    <Router>
      <Routes>
        {/* Route สำหรับ MainLayout */}
        <Route path="/" element={<MainLayout />}>
          {/* Route ภายใน MainLayout */}
          <Route index element={<Home />} /> {/* หน้าแรก */}
          <Route path="status" element={<Status />} /> {/* หน้า Status */}
        </Route>
      </Routes>
    </Router>
  );
};

export default AppRouter;
