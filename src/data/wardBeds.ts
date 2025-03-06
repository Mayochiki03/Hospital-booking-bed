export const wardBeds = {
  ICU: [
    { id: "ICU01", occupied: true, patientStatus: "red" },
    { id: "ICU02", occupied: false, patientStatus: "empty" },
    { id: "ICU03", occupied: true, patientStatus: "pink" },
    { id: "ICU04", occupied: true, patientStatus: "yellow" },
    { id: "ICU05", occupied: true, patientStatus: "green" },
    { id: "ICU06", occupied: false, patientStatus: "empty" },
    { id: "ICU07", occupied: true, patientStatus: "white" },
    { id: "ICU08", occupied: false, patientStatus: "empty" },
    { id: "ICU09", occupied: false, patientStatus: "empty" },
  ],
  ER: [
    { id: "ER01", occupied: false, patientStatus: "empty" },
    { id: "ER02", occupied: true, patientStatus: "red" },
    { id: "ER03", occupied: true, patientStatus: "pink" },
    { id: "ER04", occupied: false, patientStatus: "empty" },
  ],
  General: [
    { id: "G01", occupied: false, patientStatus: "empty" },
    { id: "G02", occupied: true, patientStatus: "yellow" },
  ],
  Pediatric: [
    { id: "P01", occupied: false, patientStatus: "empty" },
    { id: "P02", occupied: false, patientStatus: "empty" },
  ],
  Surgical: [
    { id: "S01", occupied: true, patientStatus: "green" },
    { id: "S02", occupied: false, patientStatus: "empty" },
  ],
};
