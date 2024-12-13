package com.BackEnd.Ticketing.config;

public class ConfigRequest {
    private int totalTickets;
    private int ticketReleaseRate;
    private int customerRetrievalRate;
    private int maxTicketCapacity;
    private int numberOfVendors;
    private int numberOfCustomers;

    // Getters and setters
    public int getTotalTickets() { return totalTickets; }
    public void setTotalTickets(int totalTickets) { this.totalTickets = totalTickets; }

    public int getTicketReleaseRate() { return ticketReleaseRate; }
    public void setTicketReleaseRate(int ticketReleaseRate) { this.ticketReleaseRate = ticketReleaseRate; }

    public int getCustomerRetrievalRate() { return customerRetrievalRate; }
    public void setCustomerRetrievalRate(int customerRetrievalRate) { this.customerRetrievalRate = customerRetrievalRate; }

    public int getMaxTicketCapacity() { return maxTicketCapacity; }
    public void setMaxTicketCapacity(int maxTicketCapacity) { this.maxTicketCapacity = maxTicketCapacity; }

    public int getNumberOfVendors() { return numberOfVendors; }
    public void setNumberOfVendors(int numberOfVendors) { this.numberOfVendors = numberOfVendors; }

    public int getNumberOfCustomers() { return numberOfCustomers; }
    public void setNumberOfCustomers(int numberOfCustomers) { this.numberOfCustomers = numberOfCustomers; }
}
