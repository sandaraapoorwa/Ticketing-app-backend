package com.BackEnd.Ticketing.repository;

import com.BackEnd.Ticketing.Logger.TicketLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketLogRepository extends JpaRepository<TicketLog, String> {

    // Additional query methods can be defined here, if needed
}
