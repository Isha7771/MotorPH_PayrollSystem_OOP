MotorPH Payroll System (OOP)
Project Overview

The MotorPH Payroll System is a Java-based application developed using Object-Oriented Programming (OOP) principles. The system automates key HR and payroll processes for MotorPH, including employee record management, attendance tracking, payroll computation, and payslip generation.

The system was developed as part of the MO-IT110 Object-Oriented Programming course and demonstrates the application of OOP concepts such as encapsulation, modular design, and separation of responsibilities.

**System Features**
1. User Authentication
    - Login system with role-based access
    - Different dashboards for different system users
2. Employee Management
    - Add, view, and manage employee records
    - HR dashboard for employee administration
3. Attendance Tracking
    - Track employee attendance and working hours
    - Process time logs and attendance records
4. Payroll Processing
    - Calculate employee salaries
    - Process payroll deductions
    - Generate payroll summaries
5. Payslip Generation
    - Generate employee payslips
    - Display payroll information for employees
6. Leave and Overtime Management
    - Submit and process leave requests
    - Submit overtime requests for approval
7. IT Support Tickets
    - Employees can submit IT support requests
    - IT support module for ticket handling

**Technologies Used**
    - Java
    - Java Swing (GUI)
    - Object-Oriented Programming (OOP)
    - CSV files for data storage
    - GitHub for version control

**System Architecture**
The system follows a modular OOP architecture that separates responsibilities into different packages and classes.

**Core Modules**
  - Employee Management
  - Attendance Management
  - Payroll Processing
  - Payslip Generation
  - Authentication System
  - Leave Management
  - Overtime Management
  - IT Support Ticket System

**Layer Structure**

GUI Layer → User Interfaces and Dashboards
Service Layer → Business Logic Processing
Repository Layer → Data access and CSV handling
Model Layer → Data structures and objects

**Project Structure** 

```OOPMotorPH
├── pom.xml
├── src/main/java/com/mycompany/oopmotorph
│   ├── app
│   │   ├── AppContext.java
│   │   └── AppLauncher.java
│   │
│   ├── auth
│   │   ├── AuthService.java
│   │   ├── CsvUserRepository.java
│   │   ├── Role.java
│   │   ├── RoleMapper.java
│   │   ├── User.java
│   │   ├── UserAccountAdminService.java
│   │   ├── UserCsvBootstrapper.java
│   │   ├── UserProvisioningService.java
│   │   └── UserRepository.java
│   │
│   ├── common
│   │   ├── CsvPaths.java
│   │   ├── CsvUtils.java
│   │   └── ValidationResult.java
│   │
│   ├── employee
│   │   ├── model
│   │   ├── repository
│   │   └── ui
│   │
│   ├── attendance
│   │   ├── model
│   │   ├── repository
│   │   └── service
│   │
│   ├── timesheet
│   │   ├── model
│   │   ├── repository
│   │   └── service
│   │
│   ├── deductions
│   │   ├── model
│   │   ├── repository
│   │   └── service
│   │
│   ├── payroll
│   │   ├── model
│   │   ├── repository
│   │   └── service
│   │
│   ├── leave
│   │   ├── model
│   │   ├── repository
│   │   └── service
│   │
│   ├── overtime
│   │   ├── model
│   │   ├── repository
│   │   └── service
│   │
│   ├── it
│   │   ├── model
│   │   ├── repository
│   │   ├── service
│   │   └── ui
│   │
│   ├── hr
│   │   ├── service
│   │   └── ui
│   │
│   ├── payrollstaff
│   │   └── ui
│   │
│   ├── supervisor
│   │   └── ui
│   │
│   ├── requests
│   │   └── model
│   │
│   └── ui
│       ├── BaseDashboardFrame.java
│       ├── LoginFrame.java
│       └── RoleSelectionFrame.java
│
└── Data
    ├── Attendance.csv
    ├── DataTimeLogs.csv
    ├── EmployeeData.csv
    ├── ITTickets.csv
    ├── Leave.csv
    ├── MotorPH Users.csv
    ├── Overtime.csv
    ├── Pag-ibigContribution.csv
    ├── PayslipDetails.csv
    ├── PayslipDisputes.csv
    ├── Payslips.csv
    ├── PhilhealthContribution.csv
    ├── SSSContribution.csv
    ├── WitholdingTax.csv
    └── users.csv
  ```  
**How to Run the System**
1. Clone the repository
2. git clone https://github.com/Isha7771/MotorPH_PayrollSystem_OOP.git
3. Open the project in NetBeans or any Java IDE
4. Compile and run the project
5. Start the system through the Login interface

**Testing**
Internal Testing

Internal smoke testing was conducted to verify core features including:
  - Login authentication
  - Employee record management
  - Attendance processing
  - Payroll calculations
  - Payslip generation

**External QA Testing**



**Developers**

Group 30

MO-IT110 Object-Oriented Programming
