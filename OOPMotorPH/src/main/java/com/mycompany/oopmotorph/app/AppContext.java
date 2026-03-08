package com.mycompany.oopmotorph.app;

import com.mycompany.oopmotorph.attendance.repository.AttendanceCsvRepository;
import com.mycompany.oopmotorph.attendance.repository.AttendanceRepository;
import com.mycompany.oopmotorph.attendance.service.AttendanceService;
import com.mycompany.oopmotorph.auth.*;
import com.mycompany.oopmotorph.common.CsvPaths;
import com.mycompany.oopmotorph.deductions.service.*;
import com.mycompany.oopmotorph.employee.repository.EmployeeCsvRepository;
import com.mycompany.oopmotorph.employee.repository.EmployeeRepository;
import com.mycompany.oopmotorph.hr.service.HrEmployeeManagementService;
import com.mycompany.oopmotorph.it.repository.ITTicketCsvRepository;
import com.mycompany.oopmotorph.it.repository.ITTicketRepository;
import com.mycompany.oopmotorph.it.service.ITSupportService;
import com.mycompany.oopmotorph.leave.repository.LeaveCsvRepository;
import com.mycompany.oopmotorph.leave.repository.LeaveRepository;
import com.mycompany.oopmotorph.leave.service.LeaveService;
import com.mycompany.oopmotorph.overtime.repository.OvertimeCsvRepository;
import com.mycompany.oopmotorph.overtime.repository.OvertimeRepository;
import com.mycompany.oopmotorph.overtime.service.OvertimeService;
import com.mycompany.oopmotorph.payroll.repository.*;
import com.mycompany.oopmotorph.payroll.service.EmployeePayslipService;
import com.mycompany.oopmotorph.payroll.service.PayslipDistributionService;
import com.mycompany.oopmotorph.payrollstaff.service.PayrollAttendanceService;
import com.mycompany.oopmotorph.payrollstaff.service.PayrollProcessingService;
import com.mycompany.oopmotorph.timesheet.repository.TimeLogCsvRepository;
import com.mycompany.oopmotorph.timesheet.repository.TimeLogRepository;
import com.mycompany.oopmotorph.timesheet.service.TimeLogService;

public class AppContext {
    private final UserRepository userRepository;
    private final AuthService authService;
    private final UserAccountAdminService userAccountAdminService;
    private final ITTicketRepository itTicketRepository;
    private final ITSupportService itSupportService;
    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveRepository leaveRepository;
    private final OvertimeRepository overtimeRepository;
    private final PayslipRepository payslipRepository;
    private final PayslipDetailsRepository payslipDetailsRepository;
    private final PayslipDisputeRepository payslipDisputeRepository;
    private final TimeLogRepository timeLogRepository;
    private final AttendanceService attendanceService;
    private final LeaveService leaveService;
    private final OvertimeService overtimeService;
    private final EmployeePayslipService employeePayslipService;
    private final PayslipDistributionService payslipDistributionService;
    private final TimeLogService timeLogService;
    private final HrEmployeeManagementService hrEmployeeManagementService;
    private final PayrollAttendanceService payrollAttendanceService;
    private final PayrollProcessingService payrollProcessingService;

    private AppContext(UserRepository userRepository, AuthService authService,
                       UserAccountAdminService userAccountAdminService,
                       ITTicketRepository itTicketRepository, ITSupportService itSupportService,
                       EmployeeRepository employeeRepository, AttendanceRepository attendanceRepository,
                       LeaveRepository leaveRepository, OvertimeRepository overtimeRepository,
                       PayslipRepository payslipRepository, PayslipDetailsRepository payslipDetailsRepository,
                       PayslipDisputeRepository payslipDisputeRepository, TimeLogRepository timeLogRepository,
                       AttendanceService attendanceService, LeaveService leaveService,
                       OvertimeService overtimeService, EmployeePayslipService employeePayslipService,
                       PayslipDistributionService payslipDistributionService, TimeLogService timeLogService,
                       HrEmployeeManagementService hrEmployeeManagementService,
                       PayrollAttendanceService payrollAttendanceService,
                       PayrollProcessingService payrollProcessingService) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.userAccountAdminService = userAccountAdminService;
        this.itTicketRepository = itTicketRepository;
        this.itSupportService = itSupportService;
        this.employeeRepository = employeeRepository;
        this.attendanceRepository = attendanceRepository;
        this.leaveRepository = leaveRepository;
        this.overtimeRepository = overtimeRepository;
        this.payslipRepository = payslipRepository;
        this.payslipDetailsRepository = payslipDetailsRepository;
        this.payslipDisputeRepository = payslipDisputeRepository;
        this.timeLogRepository = timeLogRepository;
        this.attendanceService = attendanceService;
        this.leaveService = leaveService;
        this.overtimeService = overtimeService;
        this.employeePayslipService = employeePayslipService;
        this.payslipDistributionService = payslipDistributionService;
        this.timeLogService = timeLogService;
        this.hrEmployeeManagementService = hrEmployeeManagementService;
        this.payrollAttendanceService = payrollAttendanceService;
        this.payrollProcessingService = payrollProcessingService;
    }

    public static AppContext createDefault() {
        UserRepository userRepository = new CsvUserRepository(CsvPaths.usersCsv());
        AuthService authService = new AuthService(userRepository);
        UserAccountAdminService userAccountAdminService = new UserAccountAdminService(userRepository);
        ITTicketRepository itTicketRepository = new ITTicketCsvRepository(CsvPaths.itTicketsCsv());
        ITSupportService itSupportService = new ITSupportService(itTicketRepository);
        EmployeeRepository employeeRepository = new EmployeeCsvRepository(CsvPaths.employeeDataCsv());
        AttendanceRepository attendanceRepository = new AttendanceCsvRepository(CsvPaths.attendanceCsv());
        LeaveRepository leaveRepository = new LeaveCsvRepository(CsvPaths.leaveCsv());
        OvertimeRepository overtimeRepository = new OvertimeCsvRepository(CsvPaths.overtimeCsv());
        PayslipRepository payslipRepository = new PayslipCsvRepository(CsvPaths.payslipsCsv());
        PayslipDetailsRepository payslipDetailsRepository = new PayslipDetailsCsvRepository(CsvPaths.payslipDetailsCsv());
        PayslipDisputeRepository payslipDisputeRepository = new PayslipDisputeCsvRepository(CsvPaths.payslipDisputesCsv());
        TimeLogRepository timeLogRepository = new TimeLogCsvRepository(CsvPaths.timeLogsCsv());
        AttendanceService attendanceService = new AttendanceService(attendanceRepository);
        LeaveService leaveService = new LeaveService(leaveRepository);
        OvertimeService overtimeService = new OvertimeService(overtimeRepository);
        EmployeePayslipService employeePayslipService = new EmployeePayslipService((PayslipCsvRepository) payslipRepository, (PayslipDisputeCsvRepository) payslipDisputeRepository);
        PayslipDistributionService payslipDistributionService = new PayslipDistributionService((PayslipCsvRepository) payslipRepository);
        TimeLogService timeLogService = new TimeLogService(timeLogRepository);
        HrEmployeeManagementService hrEmployeeManagementService = new HrEmployeeManagementService((EmployeeCsvRepository) employeeRepository);
        PayrollAttendanceService payrollAttendanceService = new PayrollAttendanceService(attendanceService);
        PayrollProcessingService payrollProcessingService = PayrollProcessingService.createDefault(
                payrollAttendanceService,
                new SssCalculator(), new PhilHealthCalculator(), new PagibigCalculator(), new WithholdingTaxCalculator()
        );
        return new AppContext(userRepository, authService, userAccountAdminService, itTicketRepository,
                itSupportService, employeeRepository, attendanceRepository, leaveRepository,
                overtimeRepository, payslipRepository, payslipDetailsRepository, payslipDisputeRepository,
                timeLogRepository, attendanceService, leaveService, overtimeService,
                employeePayslipService, payslipDistributionService, timeLogService,
                hrEmployeeManagementService, payrollAttendanceService, payrollProcessingService);
    }

    public UserRepository getUserRepository() { return userRepository; }
    public AuthService getAuthService() { return authService; }
    public UserAccountAdminService getUserAccountAdminService() { return userAccountAdminService; }
    public ITTicketRepository getItTicketRepository() { return itTicketRepository; }
    public ITSupportService getItSupportService() { return itSupportService; }
    public EmployeeRepository getEmployeeRepository() { return employeeRepository; }
    public AttendanceService getAttendanceService() { return attendanceService; }
    public LeaveService getLeaveService() { return leaveService; }
    public OvertimeService getOvertimeService() { return overtimeService; }
    public EmployeePayslipService getEmployeePayslipService() { return employeePayslipService; }
    public PayslipDetailsRepository getPayslipDetailsRepository() { return payslipDetailsRepository; }
    public PayslipDistributionService getPayslipDistributionService() { return payslipDistributionService; }
    public TimeLogService getTimeLogService() { return timeLogService; }
    public HrEmployeeManagementService getHrEmployeeManagementService() { return hrEmployeeManagementService; }
    public PayrollAttendanceService getPayrollAttendanceService() { return payrollAttendanceService; }
    public PayrollProcessingService getPayrollProcessingService() { return payrollProcessingService; }
}
