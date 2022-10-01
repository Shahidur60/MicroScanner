package com.msscanner.msscanner.discovery.context;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@AllArgsConstructor
public class RequestContext {
    private String pathToCompiledMicroservices;
    private String organizationPath;
    private String outputPath;
}
