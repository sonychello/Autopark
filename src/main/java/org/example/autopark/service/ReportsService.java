package org.example.autopark.service;

import org.example.autopark.model.RecordTimeAndAuto;
import org.example.autopark.repository.ReportsRepository;

import java.util.List;


public class ReportsService {
    private final ReportsRepository reportsRepository;


    public ReportsService(ReportsRepository reportsRepository) {
        this.reportsRepository = reportsRepository;
    }

    public int countAuto(String routeName) {
        return reportsRepository.countAutoOnRoute(routeName);
    }

    public RecordTimeAndAuto getRecordTimeAndAuto(String routeName) {
        return reportsRepository.getRecordTimeAndAuto(routeName);

    }
}
