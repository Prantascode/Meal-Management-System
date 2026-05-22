# Meal Management System

A full-stack **Meal Management System** designed to help mess owners, admins, managers, and members manage daily meals, deposits, expenses, meal requests, reports, and financial calculations in an organized and transparent way.

This project is built using **Spring Boot** for the backend and **React with TypeScript** for the frontend.

---

## Project Overview

The Meal Management System is created to solve real-life mess management problems. In many mess systems, meal counts, deposits, expenses, and monthly calculations are managed manually, which can cause confusion and mistakes.

This system provides a digital solution where admins can manage members, track meals, calculate meal rates, manage deposits and expenses, generate reports, and allow members to send meal add requests.

---

## Features

### Authentication & Authorization

- User registration and login
- JWT-based authentication
- Role-based access control
- Secure API access using Spring Security
- Separate access for Admin, Manager, and Member

---

### Admin Features

- Create and manage mess
- Register members
- Send member login password through email
- Manage member information
- Add daily meal records
- Manage deposits
- Manage expenses
- View monthly reports
- View member-wise balance
- Access AI-based insights
- Manage meal requests from members

---

### Manager Features

- Manage meal entries
- Manage deposits and expenses
- View reports
- Help admin with mess-related activities

---

### Member Features

- Login using credentials
- View own meal details
- View deposit history
- View expense/report details
- Send meal add request
- Update profile
- Update password

---

## Key Functionalities

- Daily meal tracking
- Member-wise meal calculation
- Deposit management
- Expense management
- Meal rate calculation
- Monthly report generation
- Member balance calculation
- Meal add request system
- Password sending through Gmail
- AI insights using Gemini API
- Responsive dashboard

---

## Technologies Used

### Backend

- Java
- Spring Boot
- Spring Security
- JWT Authentication
- Spring Data JPA
- Hibernate
- MySQL
- Maven
- Gmail API
- Gemini API

### Frontend

- React
- TypeScript
- Axios
- React Router
- Tailwind CSS
- Lucide React
- Vite

### Database

- MySQL

### Tools

- Postman
- MySQL Workbench
- Git
- GitHub
- VS Code / IntelliJ IDEA

---

## Project Structure

```bash
Meal-Management-System/
│
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/pranta/MealManagement/
│   │   │   │   ├── Controller/
│   │   │   │   ├── Service/
│   │   │   │   ├── Repository/
│   │   │   │   ├── Entity/
│   │   │   │   ├── Dto/
│   │   │   │   ├── Security/
│   │   │   │   ├── Config/
│   │   │   │   └── Exception/
│   │   │   │
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   │
│   │   └── test/
│   │
│   └── pom.xml
│
├── frontend/
│   ├── src/
│   │   ├── api/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── routes/
│   │   └── App.tsx
│   │
│   ├── package.json
│   └── vite.config.ts
│
└── README.md