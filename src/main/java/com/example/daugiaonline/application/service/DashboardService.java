package com.example.daugiaonline.application.service;

import com.example.daugiaonline.application.dto.DashboardDto;

import java.time.LocalDate;

public interface DashboardService {
    DashboardDto getDashboardStats(LocalDate startDate, LocalDate endDate);
}
