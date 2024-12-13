package com.BackEnd.Ticketing.service;

import com.BackEnd.Ticketing.Logger.TicketLoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService {

    private final List<Thread> customerThreads = new ArrayList<>();
    private int numberOfCustomers;
    private final TicketPoolService ticketPoolService;
    private final VendorService vendorService;
    private final TicketLoggingService ticketLoggingService;
    private final WebSocketService webSocketService;
    private volatile boolean running = true;

    @Autowired
    public CustomerService(TicketPoolService ticketPoolService, VendorService vendorService,
                           TicketLoggingService ticketLoggingService, WebSocketService webSocketService) {
        this.ticketPoolService = ticketPoolService;
        this.vendorService = vendorService;
        this.ticketLoggingService = ticketLoggingService;
        this.webSocketService = webSocketService;
    }

    public void initializeCustomers(int numberOfCustomers) {
        if (numberOfCustomers <= 0) {
            throw new IllegalArgumentException("Number of customers must be greater than 0");
        }
        this.numberOfCustomers = numberOfCustomers;
    }

    public void startCustomerThreads() {
        if (!customerThreads.isEmpty()) {
            throw new IllegalStateException("Customer threads are already running");
        }

        running = true;
        for (int i = 1; i <= numberOfCustomers; i++) {
            final int customerId = i;
            Thread customerThread = new Thread(() -> {
                while (running) {
                    String customer = "Customer" + customerId;
                    long retrievalRate = ticketPoolService.getCustomerRetrievalRate();
                    String ticket = ticketPoolService.retrieveTicket(customer, retrievalRate);
                    if (ticket != null) {
                        String vendorId = getVendorIdForCustomer();
                        ticketLoggingService.logTicket(ticket, customer, vendorId, "Retrieved");
                        System.out.println(customer + " retrieved ticket: " + ticket);
                        webSocketService.sendMessage(customer + " retrieved ticket: " + ticket);
                    } else if (ticketPoolService.getTicketsReleased() >= ticketPoolService.getMaxTicketsAllowed()) {
                        break;
                    }
                }
            });
            customerThreads.add(customerThread);
            customerThread.start();
        }
    }





    private int vendorIndex = 0;

    private String getVendorIdForCustomer() {
        // Assuming vendorService.getNumberOfVendors() returns the total number of vendors
        vendorIndex = (int) ((vendorIndex + 1) % vendorService.getNumberOfVendors());
        return "Vendor" + (vendorIndex + 1);
    }

    public void stopCustomerThreads() {
        running = false;
        for (Thread thread : customerThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public boolean areCustomerThreadsRunning() {
        return running;
    }
}
