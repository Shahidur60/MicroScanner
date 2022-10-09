package com.msscanner.msscanner.model.context;

import com.msscanner.msscanner.model.SharedPersistency;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data public class SharedPersistencyContext {
    List<SharedPersistency> sharedPersistencies;

    public SharedPersistencyContext(){
        this.sharedPersistencies = new ArrayList<>();
    }
}
