package com.BackEnd.Ticketing.controller;
import com.BackEnd.Ticketing.Logger.TicketLog;
import com.BackEnd.Ticketing.Logger.TicketLoggingService;
import com.BackEnd.Ticketing.config.ConfigRequest;
import com.BackEnd.Ticketing.config.TicketingConfig;
import com.BackEnd.Ticketing.service.CustomerService;
import com.BackEnd.Ticketing.service.TicketPoolService;
import com.BackEnd.Ticketing.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/ticketing")
public class TicketingController {
    @Autowired
    private TicketingConfig ticketingConfig;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private TicketPoolService ticketPoolService;

    @Autowired
    private TicketLoggingService ticketLoggingService;

    @PostMapping("/config")
    public ResponseEntity<String> configureSystem(@RequestBody ConfigRequest configRequest) {
        try {
            String response = ticketingConfig.configureSystem(
                    configRequest.getTotalTickets(),
                    configRequest.getTicketReleaseRate(),
                    configRequest.getCustomerRetrievalRate(),
                    configRequest.getMaxTicketCapacity(),
                    configRequest.getNumberOfVendors(),
                    configRequest.getNumberOfCustomers()
            );
            return ResponseEntity.ok(response); // Respond with OK status and the result
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to configure system: " + e.getMessage());
        }
    }

    @PostMapping("/start")
    public String startSystem() {
        return ticketingConfig.startSystem();
    }

    @PostMapping("/stop")
    public String stopSystem() {
        return ticketingConfig.stopSystem();
    }

    @GetMapping("/status")
    public String showStatus() {
        return ticketPoolService.getTicketPoolStatus();
    }

    @GetMapping("/logs")
    public List<TicketLog> getLogs() {
        return ticketLoggingService.getAllLogs();
    }
}
