/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.overtime.model;

import com.mycompany.oopmotorph.requests.model.AbstractRequest;

public class OvertimeRequest extends AbstractRequest {

    private final String date;
    private final String hours;
    private final String reason;

    private OvertimeStatus status;

    public OvertimeRequest(String requestId,
                           String employeeName,
                           String date,
                           String hours,
                           OvertimeStatus status,
                           String reason) {

        super(requestId, employeeName);

        this.date = date;
        this.hours = hours;
        this.reason = reason;
        this.status = (status == null) ? OvertimeStatus.PENDING : status;
    }

    public String getDate() { return date; }
    public String getHours() { return hours; }
    public String getReason() { return reason; }

    public OvertimeStatus getStatus() { return status; }

    public boolean isPending() {
        return status == OvertimeStatus.PENDING;
    }

    public void setStatus(OvertimeStatus newStatus) {
        if (newStatus == null) return;

        // ✅ Tightening: only allow changes from PENDING
        if (this.status != OvertimeStatus.PENDING && newStatus != this.status) {
            throw new IllegalStateException("Overtime request status can no longer be changed (already " + this.status + ").");
        }

        if (newStatus != OvertimeStatus.PENDING
                && newStatus != OvertimeStatus.APPROVED
                && newStatus != OvertimeStatus.REJECTED) {
            throw new IllegalArgumentException("Invalid OvertimeStatus: " + newStatus);
        }

        this.status = newStatus;
    }
}