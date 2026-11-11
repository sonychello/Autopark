package org.example.autopark.model;

import org.postgresql.util.PGInterval;

public record RecordTimeAndAuto(PGInterval time, String num) {
}
