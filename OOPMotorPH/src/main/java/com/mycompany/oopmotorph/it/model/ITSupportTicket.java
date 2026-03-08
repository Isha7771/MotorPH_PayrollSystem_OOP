package com.mycompany.oopmotorph.it.model;

public class ITSupportTicket {
    private String ticketId;
    private String employeeNo;
    private String employeeName;
    private String category;
    private String description;
    private ITTicketStatus status = ITTicketStatus.OPEN;
    private String assignedTo;
    private String createdDate;
    private String resolvedNotes;

    public String getTicketId() { return ticketId; }
    public void setTicketId(String ticketId) { this.ticketId = clean(ticketId); }
    public String getEmployeeNo() { return employeeNo; }
    public void setEmployeeNo(String employeeNo) { this.employeeNo = clean(employeeNo); }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = clean(employeeName); }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = clean(category); }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = clean(description); }
    public ITTicketStatus getStatus() { return status; }
    public void setStatus(ITTicketStatus status) { this.status = status == null ? ITTicketStatus.OPEN : status; }
    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = clean(assignedTo); }
    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = clean(createdDate); }
    public String getResolvedNotes() { return resolvedNotes; }
    public void setResolvedNotes(String resolvedNotes) { this.resolvedNotes = clean(resolvedNotes); }

    public boolean matchesRequester(String employeeNo) {
        return this.employeeNo != null && this.employeeNo.equalsIgnoreCase(clean(employeeNo));
    }

    private String clean(String s) { return s == null ? "" : s.trim(); }
}
