package com.msscanner.msscanner.Service;


import com.msscanner.msscanner.discovery.context.RequestContext;
import com.msscanner.msscanner.discovery.context.ResponseContext;
import com.msscanner.msscanner.discovery.context.RestEntityContext;
import com.msscanner.msscanner.discovery.model.RestEntity;
import com.msscanner.msscanner.discovery.service.ResourceService;
import com.msscanner.msscanner.model.SharedLibrary;
import com.msscanner.msscanner.model.context.ESBContext;
import com.msscanner.msscanner.model.context.HardCodedEndpointsContext;
import com.msscanner.msscanner.model.context.MicroservicesGreedyContext;
import com.msscanner.msscanner.model.context.SharedLibraryContext;
import com.msscanner.msscanner.model.hardcodedEndpoint.HardcodedEndpoint;
import com.msscanner.msscanner.model.hardcodedEndpoint.HardcodedEndpointType;
import javassist.CtClass;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

@Service
public class ReportService {


    private final LibraryService libraryService;
    private final RestService restDiscoveryService;
    private final GreedyService greedyService;
    @Autowired
    private final ResourceService resourceService;
    private final ESBService esbService;

    public ReportService(LibraryService libraryService, RestService restDiscoveryService, GreedyService greedyService, ResourceService resourceService, ESBService esbService) {
        this.libraryService = libraryService;
        this.restDiscoveryService = restDiscoveryService;
        this.greedyService = greedyService;
        this.resourceService = resourceService;
        this.esbService = esbService;
    }

    Double severity, frequency, likelihood;
    Integer Minimal = 25 , Significant = 50, Major = 75, Catastrophe = 100;

    public Double getAPIGatewayResponse(@RequestBody RequestContext request) throws Exception
    {
        ResponseContext responseContext = restDiscoveryService.generateResponseContext(request);
        Double risk_1;
        SharedLibraryContext sharedLibraryContext = libraryService.getSharedLibraries(request);
        String test = sharedLibraryContext.getSharedLibraries().toString();
        System.out.println(test);

        List<Integer> micro = new ArrayList<Integer>();
        String yy = null;
        int i=1;
        while (true){

            //System.out.pt StringUtils.ordinalIndexOf("Java Language", "[", 2)

            int start = StringUtils.ordinalIndexOf(test,"[",i)+1;
            int end = StringUtils.ordinalIndexOf(test,"]",i)-1;
            if(start < -1 || end < -1)
            {
                break;
            }
            micro.add(start);
            micro.add(end);
            i++;
        }

        test = test.substring(micro.get(2), micro.get(3));
        Boolean con = test.toLowerCase().contains("gatewayserver");
        if (con)
        {
            risk_1 = Double.valueOf(0);
        }
        else {
            severity = Double.valueOf(Float.valueOf(Major));
            frequency = Double.valueOf(Float.valueOf(responseContext.getRestEntityContexts().size()));
            risk_1 = severity * getLikelihood(frequency) * frequency;
        }
        return risk_1;
    }

    public Double getLikelihood(Double frequency)
    {
        if(frequency > 10)
        {
            likelihood = Double.valueOf(Float.valueOf(1));
            return likelihood;   
        } else if (frequency >= 5 && frequency < 10) {
            likelihood = 0.50;
            return likelihood;
        }else {
            likelihood = 0.10;
            return likelihood;
        }
    }

    public Double getNetworkCommunication(@RequestBody RequestContext request)
    {
        Double risk_2;
        ResponseContext responseContext = restDiscoveryService.generateResponseContext(request);
        List<String> resourcePaths = resourceService.getResourcePaths(request.getPathToCompiledMicroservices());

        for (String path : resourcePaths) {

            Set<Properties> propertiesSet = resourceService.getProperties(path, request.getOrganizationPath());

            Properties properties;
            if (propertiesSet.size() > 0) {
                String restMethods = propertiesSet.iterator().next().getProperty("cors-allowed-methods").toString();
                if (restMethods.contains("GET") && restMethods.contains("DELETE") && restMethods.contains("POST") && restMethods.contains("PUT"))
                {
                    return risk_2 = Double.valueOf(0);
                }
                else {
                    severity = Double.valueOf(Significant);
                    frequency = Double.valueOf(Float.valueOf(responseContext.getRestEntityContexts().size()));
                    return risk_2 = severity*frequency*getLikelihood(frequency);
                }
            } else properties = null;

        }
        return null;
    }

    public Double getAccessControl(@RequestBody RequestContext request)
    {
        Double risk_3;
        ResponseContext responseContext = restDiscoveryService.generateResponseContext(request);
        List<String> resourcePaths = resourceService.getResourcePaths(request.getPathToCompiledMicroservices());

        for (String path : resourcePaths) {
            List<CtClass> ctClasses = resourceService.getCtClasses(path, request.getOrganizationPath());

            Set<Properties> propertiesSet = resourceService.getProperties(path, request.getOrganizationPath());

            Properties properties;
            if (propertiesSet.size() > 0) {
                properties = propertiesSet.iterator().next();

                 if(properties.getProperty("keycloak" ).equals(""))
                 {
                     severity = Double.valueOf(Significant);
                     frequency = Double.valueOf(Float.valueOf(responseContext.getRestEntityContexts().size()));
                     risk_3 = severity*frequency*getLikelihood(frequency);
                     return risk_3;
                 }

            }
        }

        return null;
    }

    public Double getImproperDependency(@RequestBody RequestContext request) throws Exception
    {
        Double risk_4;
        severity = 0.00;
        SharedLibraryContext sharedLibraryContext = libraryService.getSharedLibraries(request);
        Map<String, SharedLibrary> sharedLibraries = sharedLibraryContext.getSharedLibraries();
        for (Map.Entry<String, SharedLibrary> entry : sharedLibraries.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue().getCount());

            if (entry.getValue().getCount() > 0)
            {

                frequency = Double.valueOf(entry.getValue().getCount());
                if(Objects.equals(getLikelihood(frequency), Double.valueOf(1)))
                {
                    severity = Double.valueOf(Major);
                } else if (getLikelihood(frequency) == 0.50) {
                    severity = Double.valueOf(Significant);
                }else {
                    severity = Double.valueOf(Minimal);
                }
                risk_4 = severity*frequency*getLikelihood(frequency);
                return risk_4;
            }
        }
        return null;
    }

    public Double getHardcodedEndpointsScore(@RequestBody RequestContext request){
        Double risk_5;
        Integer count = 0;
        HardCodedEndpointsContext hardCodedEndpointsContext = new HardCodedEndpointsContext();
        ResponseContext responseContext = restDiscoveryService.generateResponseContext(request);
        for(RestEntityContext restEntityContext : responseContext.getRestEntityContexts()){
            for(RestEntity restEntity : restEntityContext.getRestEntities()){
                if(restEntity.isClient()){
                    String url = restEntity.getUrl();
                    if(url.matches(".*:[0-9]{1,5}.*")){
                        hardCodedEndpointsContext.addHardcodedEndpoint(new HardcodedEndpoint(restEntity, HardcodedEndpointType.PORT));
                    }

                    if(url.matches(".*[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}.*")){
                        hardCodedEndpointsContext.addHardcodedEndpoint(new HardcodedEndpoint(restEntity, HardcodedEndpointType.IP));
                    }
                }
            }
        }
        if (hardCodedEndpointsContext.getTotalHardcodedEndpoints() > 0)
        {

            ResponseContext responseContext1 = restDiscoveryService.generateResponseContext(request);

            System.out.println(responseContext1.getRestEntityContexts());
            for (RestEntityContext restEntityList: responseContext1.getRestEntityContexts())
            {
                for (RestEntity restEntities: restEntityList.getRestEntities())
                {
                    count++;
                }
            }
        }

        System.out.println(count);
        frequency = Double.valueOf(hardCodedEndpointsContext.getTotalHardcodedEndpoints());
        Double newLike = Double.valueOf(0);
        if (count == 0)
        {
            count = 1;
        }else {
            newLike = frequency / count;
        }

        if (newLike >= 0.50 && newLike < 0.75)
        {
            severity = Double.valueOf(Major);
        }
        else if(newLike < 0.50){
            severity = Double.valueOf(Minimal);
        }else {
            severity = Double.valueOf(Catastrophe);
        }
        risk_5 = severity*frequency*newLike;
            return risk_5;
    }

    public Double getImproperMSUtilization(RequestContext request) {
        Double risk_6;
        severity = 0.00;
        MicroservicesGreedyContext greedyService1 = greedyService.getGreedyMicroservices(request);
        if(greedyService1.getGreedyMicroservices().size() > 0)
        {
            return risk_6 = 0.00;
        } else {
            frequency = Double.valueOf(greedyService1.getGreedyMicroservices().size());
            if(Objects.equals(getLikelihood(frequency), 1.0))
            {
                severity = Double.valueOf(Major);
            } else if (getLikelihood(frequency) == 0.50) {
                severity = Double.valueOf(Significant);
            }else {
                severity = Double.valueOf(Minimal);
            }
            risk_6 = severity*frequency*getLikelihood(frequency);
            return risk_6;
        }
    }

    public Double getCaching(RequestContext request) {
        ResponseContext responseContext = restDiscoveryService.generateResponseContext(request);
        double risk_7;
        List<String> resourcePaths = resourceService.getResourcePaths(request.getPathToCompiledMicroservices());
        for (String path : resourcePaths) {
            List<CtClass> ctClasses = resourceService.getCtClasses(path, request.getOrganizationPath());

            Set<Properties> propertiesSet = resourceService.getProperties(path, request.getOrganizationPath());

            Properties properties;
            if (propertiesSet.size() > 0) {
                properties = propertiesSet.iterator().next();

                if(properties.getProperty("realm" ).equals(""))
                {
                    severity = Double.valueOf(Float.valueOf(Major));
                    frequency = Double.valueOf(Float.valueOf(responseContext.getRestEntityContexts().size()));
                    risk_7 = severity * getLikelihood(frequency) * frequency;
                    return risk_7;
                }
                else {
                    return 0.00;
                }

            }
        }
        return null;
    }

    public Double getESBScore(RequestContext request) {
        ResponseContext responseContext = restDiscoveryService.generateResponseContext(request);
        double risk_8;
        if (esbService.getESBContext(request).getCandidateESBs().size() > 0)
        {
            severity = Double.valueOf(Float.valueOf(Major));
            frequency = Double.valueOf(esbService.getESBContext(request).getCandidateESBs().size());
            risk_8 = severity * getLikelihood(frequency) * frequency;
            return risk_8;
        }
        else {
            return 0.00;
        }

    }
}
