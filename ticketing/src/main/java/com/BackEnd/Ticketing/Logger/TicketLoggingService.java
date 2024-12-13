package com.BackEnd.Ticketing.Logger;

import com.BackEnd.Ticketing.repository.TicketLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TicketLoggingService {

    private static final Logger logger = LoggerFactory.getLogger(TicketLoggingService.class);

    @Autowired
    private TicketLogRepository ticketLogRepository;

    public void logTicket(String ticketId, String customerId, String vendorId, String status) {
        try {
            TicketLog ticketLog = new TicketLog();
            ticketLog.setTicketId(ticketId);
            ticketLog.setCustomerName(customerId);
            ticketLog.setVendorName(vendorId);  // This is where the correct vendor ID is set
            ticketLog.setStatus(status);
            ticketLog.setTimestamp(LocalDateTime.now().toString());

            ticketLogRepository.save(ticketLog);
            logger.info("Logged ticket: {}", ticketLog);
        } catch (Exception e) {
            logger.error("Error logging ticket: {}", e.getMessage(), e);
        }
    }

    public List<TicketLog> getAllLogs() {
        return ticketLogRepository.findAll();
    }
}
