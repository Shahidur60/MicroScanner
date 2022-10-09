package com.msscanner.msscanner.model.greedy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MicroserviceMetric {
    private String path;
    private int staticFileCount;
    private int entityFileCount;
}
