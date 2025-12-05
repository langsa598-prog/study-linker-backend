package com.study.service.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BasicStatResponse {
    private String label;
    private Long value;
}


