package com.msscanner.msscanner.model.context;

import com.msscanner.msscanner.model.greedy.MicroserviceMetric;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data public class MicroservicesGreedyContext {
    List<MicroserviceMetric> microserviceMetrics;
    List<MicroserviceMetric> greedyMicroservices;

    public MicroservicesGreedyContext(){
        this.microserviceMetrics = new ArrayList<>();
        this.greedyMicroservices = new ArrayList<>();
    }

    public void addMetric(MicroserviceMetric microserviceMetric){
        this.microserviceMetrics.add(microserviceMetric);
    }

    public void addGreedyMicroservice(MicroserviceMetric microserviceMetric){
        this.greedyMicroservices.add(microserviceMetric);
    }
}
