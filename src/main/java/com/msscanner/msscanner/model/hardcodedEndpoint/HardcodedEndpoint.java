package com.msscanner.msscanner.model.hardcodedEndpoint;

import com.msscanner.msscanner.discovery.model.RestEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
public class HardcodedEndpoint {
    private RestEntity restEntity;
    private HardcodedEndpointType type;
}
