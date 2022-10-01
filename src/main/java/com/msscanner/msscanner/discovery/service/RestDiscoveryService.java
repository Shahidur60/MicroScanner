package com.msscanner.msscanner.discovery.service;

import com.msscanner.msscanner.discovery.context.RequestContext;
import com.msscanner.msscanner.discovery.context.ResponseContext;
import com.msscanner.msscanner.discovery.context.RestEntityContext;
import com.msscanner.msscanner.discovery.context.RestFlowContext;
import com.msscanner.msscanner.discovery.graph.GVGenerator;
import javassist.CtClass;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;
import java.util.Set;


@AllArgsConstructor
@Service
public class RestDiscoveryService {

    @Autowired
    private final ResourceService resourceService;

    @Autowired
    private final RestEntityService restEntityService;

    @Autowired
    private final RestFlowService restFlowService;

    public ResponseContext generateResponseContext(RequestContext request) {
        ResponseContext responseContext = new ResponseContext();
        responseContext.setRequest(request);

        List<String> resourcePaths = resourceService.getResourcePaths(request.getPathToCompiledMicroservices());
        for (String path : resourcePaths) {
            List<CtClass> ctClasses = resourceService.getCtClasses(path, request.getOrganizationPath());

            Set<Properties> propertiesSet = resourceService.getProperties(path, request.getOrganizationPath());
            Properties properties;
            if (propertiesSet.size() > 0) {
                properties = propertiesSet.iterator().next();
            } else properties = null;

            // print the properties for debug
            // Helper.dumpProperties(properties, path);

            RestEntityContext restEntityContext = restEntityService.getRestEntityContext(ctClasses, path, null, properties);
            responseContext.getRestEntityContexts().add(restEntityContext);
        }

        RestFlowContext restFlowContext = restFlowService.getRestFlowContext(responseContext.getRestEntityContexts());
        responseContext.setRestFlowContext(restFlowContext);

        if (!request.getOutputPath().isEmpty()) {
            GVGenerator.generate(responseContext);
        }

        return responseContext;
    }

    // rest entities for single jar
    public RestEntityContext generateRestEntityContext(RequestContext request, String serviceDNS) {
        List<CtClass> ctClasses = resourceService.getCtClasses(request.getPathToCompiledMicroservices(), request.getOrganizationPath());

        Set<Properties> propertiesSet = resourceService.getProperties(request.getPathToCompiledMicroservices(), request.getOrganizationPath());
        Properties properties;
        if (propertiesSet.size() > 0) {
            properties = propertiesSet.iterator().next();
        } else properties = null;

        return restEntityService.getRestEntityContext(ctClasses, request.getPathToCompiledMicroservices(), serviceDNS, properties);
    }
}
