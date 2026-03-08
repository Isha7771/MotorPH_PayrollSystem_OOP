/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.leave.model;

import com.mycompany.oopmotorph.requests.model.AbstractRequest;

public class LeaveRequest extends AbstractRequest {

    private final String dateRequest;
    private final LeaveType leaveType;
    private final String dateFiled;
    private final String startDate;
    private final String endDate;

    private LeaveStatus status;

    public LeaveRequest(String requestId,
                        String employeeName,
                        String dateRequest,
                        LeaveType leaveType,
                        String dateFiled,
                        String startDate,
                        String endDate,
                        LeaveStatus status) {

        super(requestId, employeeName);

        this.dateRequest = dateRequest;
        this.leaveType = leaveType;
        this.dateFiled = dateFiled;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = (status == null) ? LeaveStatus.PENDING : status;
    }

    public String getDateRequest() { return dateRequest; }
    public LeaveType getLeaveType() { return leaveType; }
    public String getDateFiled() { return dateFiled; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }

    public LeaveStatus getStatus() { return status; }

    public boolean isPending() {
        return status == LeaveStatus.PENDING;
    }

    public void setStatus(LeaveStatus newStatus) {
        if (newStatus == null) return;

        // ✅ Tightening: only allow changes from PENDING
        if (this.status != LeaveStatus.PENDING && newStatus != this.status) {
            throw new IllegalStateException("Leave request status can no longer be changed (already " + this.status + ").");
        }

        // Only allow valid terminal statuses
        if (newStatus != LeaveStatus.PENDING
                && newStatus != LeaveStatus.APPROVED
                && newStatus != LeaveStatus.REJECTED) {
            throw new IllegalArgumentException("Invalid LeaveStatus: " + newStatus);
        }

        this.status = newStatus;
    }
}