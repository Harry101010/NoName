package aptech.proj_NN_group2.model.entity.saleman;

import java.time.LocalDateTime;

public class IssueNote {
    private int noteId;
    private String customerName;
    private LocalDateTime createDate;
    private String status;

    public IssueNote() {}

    // Getters và Setters
    public int getNoteId() { return noteId; }
    public void setNoteId(int noteId) { this.noteId = noteId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}