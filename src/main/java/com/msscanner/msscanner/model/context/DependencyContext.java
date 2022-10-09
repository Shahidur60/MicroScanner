package com.msscanner.msscanner.model.context;

import com.msscanner.msscanner.model.Pair;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor @AllArgsConstructor
@Data public class DependencyContext {
    List<String> microservices;
    List<Pair> edges;
}
