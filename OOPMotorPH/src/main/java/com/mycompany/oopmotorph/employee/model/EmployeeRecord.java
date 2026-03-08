package com.mycompany.oopmotorph.employee.model;

import java.time.LocalDate;
import java.util.Locale;

public abstract class EmployeeRecord {

    private String employeeNo;
    private String lastName;
    private String firstName;
    private String email;

    private LocalDate birthday;
    private String address;
    private String phoneNumber;

    private String sssNo;
    private String philhealthNo;
    private String tinNo;
    private String pagibigNo;

    private String status;
    private String position;
    private String immediateSupervisor;

    private double basicSalary;
    private double riceSubsidy;
    private double phoneAllowance;
    private double clothingAllowance;
    private double grossSemiMonthlyRate;
    private double hourlyRate;

    public abstract String getEmploymentType();

    public abstract double computeGrossPay(double totalHours);

    public String getEmployeeNo() { return employeeNo; }
    public void setEmployeeNo(String employeeNo) { this.employeeNo = clean(employeeNo); }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = clean(lastName); }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = clean(firstName); }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = clean(email); }

    public LocalDate getBirthday() { return birthday; }
    public void setBirthday(LocalDate birthday) { this.birthday = birthday; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = clean(address); }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = clean(phoneNumber); }

    public String getSssNo() { return sssNo; }
    public void setSssNo(String sssNo) { this.sssNo = clean(sssNo); }

    public String getPhilhealthNo() { return philhealthNo; }
    public void setPhilhealthNo(String philhealthNo) { this.philhealthNo = clean(philhealthNo); }

    public String getTinNo() { return tinNo; }
    public void setTinNo(String tinNo) { this.tinNo = clean(tinNo); }

    public String getPagibigNo() { return pagibigNo; }
    public void setPagibigNo(String pagibigNo) { this.pagibigNo = clean(pagibigNo); }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = clean(status); }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = clean(position); }

    public String getImmediateSupervisor() { return immediateSupervisor; }
    public void setImmediateSupervisor(String immediateSupervisor) { this.immediateSupervisor = clean(immediateSupervisor); }

    public double getBasicSalary() { return basicSalary; }
    public void setBasicSalary(double basicSalary) { this.basicSalary = safeMoney(basicSalary); }

    public double getRiceSubsidy() { return riceSubsidy; }
    public void setRiceSubsidy(double riceSubsidy) { this.riceSubsidy = safeMoney(riceSubsidy); }

    public double getPhoneAllowance() { return phoneAllowance; }
    public void setPhoneAllowance(double phoneAllowance) { this.phoneAllowance = safeMoney(phoneAllowance); }

    public double getClothingAllowance() { return clothingAllowance; }
    public void setClothingAllowance(double clothingAllowance) { this.clothingAllowance = safeMoney(clothingAllowance); }

    public double getGrossSemiMonthlyRate() { return grossSemiMonthlyRate; }
    public void setGrossSemiMonthlyRate(double grossSemiMonthlyRate) { this.grossSemiMonthlyRate = safeMoney(grossSemiMonthlyRate); }

    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = safeMoney(hourlyRate); }

    public String getFullName() {
        String ln = clean(lastName);
        String fn = clean(firstName);
        if (ln.isEmpty() && fn.isEmpty()) return "";
        if (ln.isEmpty()) return fn;
        if (fn.isEmpty()) return ln;
        return ln + ", " + fn;
    }

    public boolean isRegular() {
        return getEmploymentType().toLowerCase(Locale.ROOT).contains("regular");
    }

    public double getTotalAllowances() {
        return round2(riceSubsidy + phoneAllowance + clothingAllowance);
    }

    public double getMonthlyGrossSalary() {
        double salary = basicSalary;
        if (salary <= 0 && grossSemiMonthlyRate > 0) {
            salary = grossSemiMonthlyRate * 2.0;
        }
        return round2(salary + getTotalAllowances());
    }

    public double getSemiMonthlyAllowances() {
        return round2(getTotalAllowances() / 2.0);
    }

    public double getFormulaSemiMonthlyGross() {
        if (grossSemiMonthlyRate > 0 && getTotalAllowances() <= 0) {
            return round2(grossSemiMonthlyRate);
        }
        double halfSalary = basicSalary > 0 ? (basicSalary / 2.0) : grossSemiMonthlyRate;
        return round2(halfSalary + getSemiMonthlyAllowances());
    }

    public double getAttendanceBasedGross(double totalHours) {
        if (hourlyRate <= 0 || totalHours <= 0) return 0.0;
        return round2(hourlyRate * totalHours);
    }

    protected static String clean(String s) {
        return s == null ? "" : s.trim();
    }

    protected static double safeMoney(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value) || value < 0) return 0.0;
        return value;
    }

    protected static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
