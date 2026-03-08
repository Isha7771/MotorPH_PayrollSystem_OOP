package com.mycompany.oopmotorph.employee.model;

public class RegularEmployee extends EmployeeRecord {

    @Override
    public String getEmploymentType() {
        return "Regular";
    }

    @Override
    public double computeGrossPay(double totalHours) {
        double formulaBasedGross = getFormulaSemiMonthlyGross();
        if (formulaBasedGross > 0) return formulaBasedGross;

        double attendanceBasedGross = getAttendanceBasedGross(totalHours);
        if (attendanceBasedGross > 0) return attendanceBasedGross;

        if (getGrossSemiMonthlyRate() > 0) return round2(getGrossSemiMonthlyRate());
        return 0.0;
    }
}
