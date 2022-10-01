package com.msscanner.msscanner.discovery.context;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class ResponseContext {
    private RequestContext request;
    private List<RestEntityContext> restEntityContexts = new ArrayList<>();
    private RestFlowContext restFlowContext;
}
