package com.msscanner.msscanner.model.context;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@Data public class UnversionedAPIContext {
    Set<String> unversionedAPIs;
    int count;

    public UnversionedAPIContext(){
        this.unversionedAPIs = new HashSet<>();
        this.count = 0;
    }

    public UnversionedAPIContext(Set<String> unversionedAPIs){
        this.unversionedAPIs = unversionedAPIs;
        this.count = unversionedAPIs.size();
    }

    public void addAPIContext(APIContext apiContext){
        this.unversionedAPIs.add(apiContext.getPath());
        this.count++;
    }
}
