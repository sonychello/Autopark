package org.example.autopark.model;

import java.sql.Timestamp;

public record JournalRecord(Integer id, Timestamp time_out, Timestamp time_in, int auto_id, int route_id) {
}
