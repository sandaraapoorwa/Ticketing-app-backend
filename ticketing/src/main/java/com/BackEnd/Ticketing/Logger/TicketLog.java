package com.BackEnd.Ticketing.Logger;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class TicketLog {

    @Id
    private String ticketId; // Unique ID for the ticket
    private String customerId; // Name of the customer
    private String vendorId; // Name of the vendor
    private String status; // Status of the ticket (e.g., "Completed")
    private String timestamp; // Timestamp when the log was created

    // Getters and Setters
    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerName(String customerId) {
        this.customerId = customerId;
    }

    public String getVendorName() {
        return vendorId;
    }

    public void setVendorName(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "TicketLog{" +
                "ticketId='" + ticketId + '\'' +
                ", customerName='" + customerId + '\'' +
                ", vendorName='" + vendorId + '\'' +
                ", status='" + status + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
