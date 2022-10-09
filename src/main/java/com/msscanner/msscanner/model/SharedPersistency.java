package com.msscanner.msscanner.model;

import com.msscanner.msscanner.model.persistency.DatabaseInstance;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor
@Data public class SharedPersistency {
    String msaA;
    String msaB;
    DatabaseInstance persistency;
}
