package com.mycompany.oopmotorph.it.repository;

import com.mycompany.oopmotorph.it.model.ITSupportTicket;

import java.io.IOException;
import java.util.List;

public interface ITTicketRepository {
    List<ITSupportTicket> findAll() throws IOException;
    void saveAll(List<ITSupportTicket> tickets) throws IOException;
    void append(ITSupportTicket ticket) throws IOException;
    String nextTicketId() throws IOException;
}
