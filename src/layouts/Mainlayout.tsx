//layouts/Mainlayout.tsx
import Header from "../components/Header";
import Footer from "../components/Footer";
import { Outlet } from "react-router-dom";
import { wardBeds } from "../data/wardBeds"; // นำเข้าข้อมูล wardBeds
import { useState } from "react";

export default function MainLayout() {
  const [wardsData, setWardsData] = useState(wardBeds); // สถานะของ wardBeds

  return (
    <>
      <Header />
      <div>
        {/* ส่งข้อมูลผ่าน context ของ Outlet */}
        <Outlet context={{ wardsData, setWardsData }} />
      </div>
      <Footer />
    </>
  );
}
