package com.BackEnd.Ticketing.config;

import com.BackEnd.Ticketing.service.CustomerService;
import com.BackEnd.Ticketing.service.TicketPoolService;
import com.BackEnd.Ticketing.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TicketingConfig {

    private final VendorService vendorService;
    private final CustomerService customerService;
    private final TicketPoolService ticketPoolService;
    private volatile boolean systemRunning = false;
    private volatile boolean isConfigured = false;

    @Autowired
    public TicketingConfig(VendorService vendorService, CustomerService customerService, TicketPoolService ticketPoolService) {
        this.vendorService = vendorService;
        this.customerService = customerService;
        this.ticketPoolService = ticketPoolService;
    }

    public synchronized String configureSystem(int totalTickets, int ticketReleaseRate, int customerRetrievalRate,
                                               int maxTicketCapacity, int numberOfVendors, int numberOfCustomers) {
        if (systemRunning) {
            return "Cannot configure while the system is running. Please stop the system first.";
        }
        ticketPoolService.configure(totalTickets, maxTicketCapacity, customerRetrievalRate, ticketReleaseRate);
        vendorService.initializeVendors(numberOfVendors);
        customerService.initializeCustomers(numberOfCustomers);

        isConfigured = true;
        return "System configured successfully. Call /start to begin operations.";
    }

    public synchronized String startSystem() {
        if (systemRunning) {
            return "System is already running.";
        }

        if (!isConfigured) {
            return "System is not configured. Please configure first.";
        }

        try {
            // Restart the services from their current state.
            vendorService.startVendorThreads();
            customerService.startCustomerThreads();
            systemRunning = true;

            return "System started successfully. Vendors and customers are now active.";
        } catch (Exception e) {
            return "Failed to start system: " + e.getMessage();
        }
    }


    public synchronized String stopSystem() {
        if (!systemRunning) {
            return "System is already stopped.";
        }

        try {
            // Indicate that the system is no longer running
            systemRunning = false;

            // Shut down the services and stop threads
            ticketPoolService.shutdown();   // Ensure proper shutdown of ticket pool
            vendorService.stopVendorThreads(); // Gracefully stop vendor threads
            customerService.stopCustomerThreads(); // Gracefully stop customer threads

            // Optionally, perform any final cleanup operations, like closing resources
            isConfigured = false;

            // Return success message
            return "System stopped successfully. All threads have been terminated.";
        } catch (Exception e) {
            // Log the error and return failure message
            e.printStackTrace();
            return "Failed to stop system: " + e.getMessage();
        }
    }



    public boolean isConfigured() {
        return isConfigured;
    }

    public boolean isSystemRunning() {
        return systemRunning;
    }
}
