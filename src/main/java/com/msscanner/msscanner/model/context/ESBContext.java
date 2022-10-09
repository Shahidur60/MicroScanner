package com.msscanner.msscanner.model.context;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data public class ESBContext {
    List<MicroserviceContext> candidateESBs;

    public ESBContext(){
        this.candidateESBs = new ArrayList<>();
    }
}
