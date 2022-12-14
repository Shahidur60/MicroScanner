package com.msscanner.msscanner.Service;

import com.msscanner.msscanner.discovery.context.RequestContext;
import com.msscanner.msscanner.discovery.context.ResponseContext;
import com.msscanner.msscanner.discovery.service.RestDiscoveryService;
import org.springframework.stereotype.Service;

@Service
public class RestService {

    private final RestDiscoveryService restDiscoveryService;

    private ResponseContext cache = null;

    public RestService(RestDiscoveryService restDiscoveryService){
        this.restDiscoveryService = restDiscoveryService;
    }

    public ResponseContext generateResponseContext(RequestContext requestContext){
        if(this.cache == null){
            this.cache = restDiscoveryService.generateResponseContext(requestContext);
        }

        return this.cache;
    }

}
