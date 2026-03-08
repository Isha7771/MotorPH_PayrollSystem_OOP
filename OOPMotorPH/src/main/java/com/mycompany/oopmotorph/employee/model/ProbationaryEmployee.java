package com.mycompany.oopmotorph.employee.model;

public class ProbationaryEmployee extends EmployeeRecord {

    @Override
    public String getEmploymentType() {
        return "Probationary";
    }

    @Override
    public double computeGrossPay(double totalHours) {
        double attendanceBasedGross = getAttendanceBasedGross(totalHours);
        if (attendanceBasedGross > 0) return attendanceBasedGross;

        double formulaBasedGross = getFormulaSemiMonthlyGross();
        if (formulaBasedGross > 0) return formulaBasedGross;

        if (getGrossSemiMonthlyRate() > 0) return round2(getGrossSemiMonthlyRate());
        return 0.0;
    }
}
