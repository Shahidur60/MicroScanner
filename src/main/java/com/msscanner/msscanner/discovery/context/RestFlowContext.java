package com.msscanner.msscanner.discovery.context;

import com.msscanner.msscanner.discovery.model.RestFlow;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;


@Getter
@ToString
public class RestFlowContext {
    private List<RestFlow> restFlows = new ArrayList<>();
}
