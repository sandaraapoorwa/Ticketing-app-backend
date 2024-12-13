package com.BackEnd.Ticketing.service;

import com.BackEnd.Ticketing.Logger.TicketLoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VendorService {

    private final List<Thread> vendorThreads = new ArrayList<>();
    private int numberOfVendors;
    private final TicketPoolService ticketPoolService;
    private final TicketLoggingService ticketLoggingService;
    private final WebSocketService webSocketService;
    private volatile boolean running = true;

    @Autowired
    public VendorService(TicketPoolService ticketPoolService, TicketLoggingService ticketLoggingService,
                         WebSocketService webSocketService) {
        this.ticketPoolService = ticketPoolService;
        this.ticketLoggingService = ticketLoggingService;
        this.webSocketService = webSocketService;
    }

    public void initializeVendors(int numberOfVendors) {
        if (numberOfVendors <= 0) {
            throw new IllegalArgumentException("Number of vendors must be greater than 0");
        }
        this.numberOfVendors = numberOfVendors;
    }
    public void startVendorThreads() {
        // Ensure vendor threads are not already running
        if (!vendorThreads.isEmpty()) {
            throw new IllegalStateException("Vendor threads are already running");
        }

        running = true;

        for (int i = 1; i <= numberOfVendors; i++) {
            final int vendorId = i;

            // Creating a new thread for each vendor, giving it a stage name!
            Thread vendorThread = new Thread(() -> {
                while (running) {
                    try {
                        String vendorIdStr = "Vendor" + vendorId;
                        long releaseRate = ticketPoolService.getTicketReleaseRate(); // Get the vendor's release rate
                        String ticket = ticketPoolService.addTicket(vendorIdStr, releaseRate);
                        if (ticket != null) {
                            ticketLoggingService.logTicket(ticket, null, vendorIdStr, "Released");
                            System.out.println(" Vendor " + vendorId + " just dropped a ticket: " + ticket + "");
                            webSocketService.sendMessage("Vendor " + vendorId + " just dropped a ticket: " + ticket);
                        } else {
                            System.out.println("Vendor " + vendorId + " couldn't release a ticket. Exiting performance.");
                            webSocketService.sendMessage("Vendor " + vendorId + " couldn't release a ticket. Exiting performance.");
                            break;
                        }
                        System.out.println("Vendor " + vendorId + " waiting for the next ticket");
                        webSocketService.sendMessage("Vendor " + vendorId + " waiting for the next ticket");
                        Thread.sleep(releaseRate);

                    } catch (InterruptedException e) {
                        System.out.println("Vendor " + vendorId + " interrupted.");
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception e) {
                        System.err.println(" An error occurred during Vendor " + vendorId + "'s performance: " + e.getMessage());
                        e.printStackTrace();
                        break;
                    }
                }
                System.out.println(" Vendor " + vendorId + " exits the stage.");
            });
            vendorThreads.add(vendorThread);
            vendorThread.start();
        }
    }

    public void stopVendorThreads() {
        running = false;
        for (Thread thread : vendorThreads) {
            thread.interrupt();
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        vendorThreads.clear();
        System.out.println("Vendor threads stopped.");
        webSocketService.sendMessage("Vendor Threads Stopped");
    }

    public double getNumberOfVendors() {
        return numberOfVendors;
    }
}
