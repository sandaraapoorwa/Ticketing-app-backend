package com.BackEnd.Ticketing.service;

import com.BackEnd.Ticketing.Logger.TicketLoggingService;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class TicketPoolService {
    private final Queue<String> ticketPool = new LinkedList<>();
    private final Set<String> availableTickets = new HashSet<>();
    private final Lock lock = new ReentrantLock();

    private final Condition ticketPoolSpaceAvailableCondition = lock.newCondition();
    private final Condition ticketAvailableCondition = lock.newCondition();

    private TicketLoggingService ticketLoggingService;

    private int ticketsReleased = 0; // Counter for released tickets
    private int maxTicketsAllowed;
    private int customerRetrievalRate;
    private int ticketReleaseRate;
    
    public void configure(int maxTicketsAllowed, int maxTicketCapacity, int customerRetrievalRate, int ticketReleaseRate) {
        this.maxTicketsAllowed = maxTicketsAllowed;
        this.customerRetrievalRate = customerRetrievalRate;
        this.ticketReleaseRate = ticketReleaseRate;

        // Initialize the pool as empty, allowing vendors to fill it
        this.ticketPool.clear();
        this.availableTickets.clear();
        this.ticketsReleased = 0; // Reset released ticket counter
    }

    public synchronized String retrieveTicket(String customer, long retrievalRate) {
        lock.lock();
        try {
            while (ticketPool.isEmpty()) {
                try {
                    ticketAvailableCondition.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            // Add a delay based on retrievalRate before retrieving the ticket
            Thread.sleep(retrievalRate);

            String ticket = ticketPool.poll();
            availableTickets.remove(ticket);
            ticketPoolSpaceAvailableCondition.signal();

            return ticket;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } finally {
            lock.unlock();
        }
    }

    public String addTicket(String vendorIdStr, long releaseRate) {
        lock.lock();
        try {
            if (ticketsReleased >= maxTicketsAllowed) {
                System.out.println("Vendor " + vendorIdStr + " stopping. Total ticket limit reached.");
                return null;
            }

            while (ticketPool.size() >= maxTicketsAllowed) {
                try {
                    ticketPoolSpaceAvailableCondition.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }

            ticketsReleased++;
            String ticket = "Ticket" + ticketsReleased;
            ticketPool.offer(ticket);
            ticketAvailableCondition.signal();

            Thread.sleep(releaseRate);  // Simulate ticket release rate

            return ticket;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } finally {
            lock.unlock();
        }
    }

    public void shutdown() {
        lock.lock();
        try {
            ticketPool.clear();
            availableTickets.clear();
        } finally {
            lock.unlock();
        }
    }

    public int getTotalTickets() {
        return ticketsReleased;
    }

    public int getMaxTicketsAllowed() {
        return maxTicketsAllowed;
    }

    public long getTicketReleaseRate() {
        return ticketReleaseRate;
    }

    public String getTicketPoolStatus() {
        return "Tickets left: " + ticketPool.size() + " | Tickets released: " + ticketsReleased + "/" + maxTicketsAllowed;
    }

    public long getCustomerRetrievalRate() {
        return customerRetrievalRate;
    }

    public int getTicketsReleased() {
        return ticketsReleased;
    }
}
