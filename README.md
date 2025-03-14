# Hospital Bed Booking System

## Overview

This is a hospital bed booking system designed to help users book, manage, and check bed availability within a hospital. The system provides functionalities for both **Admin** and **User** roles.

- **Admin** can:
  - Add, delete, and edit bed information.
  - Approve or reject bed bookings made by users.

- **User** can:
  - Check available bed status.
  - Book a bed online.
  - Cancel or modify their bookings.

The system is built using **React.js**, **Tailwind CSS**, **Java Spring Boot**, and **MySQL** for a smooth user experience and robust backend functionality.

---

## Technologies Used

- **Frontend:**
  - React.js
  - Tailwind CSS
  
- **Backend:**
  - Java Spring Boot
  - Spring Data JPA for database interaction
  
- **Database:**
  - MySQL
  
---

## Features

### Admin Functions
- **Manage Bed Information:**
  - Admins can add, edit, and delete bed details in the system.
  
- **Booking Approval/Reject:**
  - Admins can approve or reject user booking requests for beds.

### User Functions
- **Check Bed Availability:**
  - Users can check the current status of beds to see if a bed is available or not.
  
- **Book Bed Online:**
  - Users can book a bed online through a simple user interface.
  
- **Cancel or Modify Booking:**
  - Users can cancel or modify their bed bookings before the scheduled date.

---

## Project Setup

### Prerequisites

1. **Java 23** or newer (for the backend).
2. **Node.js** and **npm** (for the frontend).
3. **MySQL** Database running locally or remotely.
4. **Maven** (for managing backend dependencies).

### Backend Setup (Spring Boot)

1. Clone the repository and navigate to the backend folder.
2. Install dependencies:
   ```bash
   mvn clean install
