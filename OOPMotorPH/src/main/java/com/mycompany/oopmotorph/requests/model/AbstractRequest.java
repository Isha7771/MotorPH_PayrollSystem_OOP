/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.requests.model;

public abstract class AbstractRequest {

    protected final String requestId;
    protected final String employeeName;

    protected AbstractRequest(String requestId, String employeeName) {
        this.requestId = req(requestId, "Request ID");
        this.employeeName = req(employeeName, "Employee name");
    }

    public String getRequestId() {
        return requestId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    private static String req(String s, String field) {
        String v = safe(s);
        if (v.isEmpty()) throw new IllegalArgumentException(field + " is required.");
        return v;
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
