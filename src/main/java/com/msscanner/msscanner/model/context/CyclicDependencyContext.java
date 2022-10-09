package com.msscanner.msscanner.model.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor @AllArgsConstructor
@Data public class CyclicDependencyContext {
    List<DependencyContext> cycles;
}
