package com.msscanner.msscanner.model.context;

import com.msscanner.msscanner.model.SharedLibrary;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Data public class SharedLibraryContext {
    Map<String, SharedLibrary> sharedLibraries;

    public SharedLibraryContext(){
        this.sharedLibraries = new HashMap<>();
    }

    public void addSharedLibrary(SharedLibrary sharedLibrary){
        this.sharedLibraries.put(sharedLibrary.getLibrary(), sharedLibrary);
    }

    public SharedLibrary getOrDefault(String library){
        return this.sharedLibraries.getOrDefault(library, new SharedLibrary(library));
    }
}
