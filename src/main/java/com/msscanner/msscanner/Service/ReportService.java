package com.msscanner.msscanner.Service;


import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.apache.logging.log4j.spi.LoggerRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class ReportService {


    private final LibraryService libraryService;
    private final RestService restDiscoveryService;
    private final GreedyService greedyService;
    @Autowired
    private final ResourceService resourceService;


    private final ESBService esbService;

     //processBuilder.command("cmd.exe", "/c", "docker scan --json ostock/organization-service:0.0.1-SNAPSHOT > gatewayserver-output.json");

    private String cmd = "cmd.exe";
    private String driveName = "/c";
    private String synkCommand = "docker scan --json ostock/organization-service:0.0.1-SNAPSHOT > gatewayserver-output.json";

    private String dataReading = "gatewayserver-output.json";


    private Double weight_APIGateway = Double.valueOf(0),
            weight_NetworkCommunication = Double.valueOf(0),weight_AccessControl = Double.valueOf(0),
            weight_ImproperDependency = Double.valueOf(0),weight_HardcodedEndpointsScore = Double.valueOf(0),weight_ImproperMSUtilization = Double.valueOf(0),weight_Caching = Double.valueOf(0),weight_ESBScore = Double.valueOf(0),
            weigth_SynkScanning =1.00;


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
        System.out.println(i);
        Boolean resource = false;
        test = test.substring(micro.get(2), micro.get(3));
        Boolean con = test.toLowerCase().contains("gatewayserver");
        List<String> resourcePaths = resourceService.getResourcePaths(request.getPathToCompiledMicroservices());
        for (String path : resourcePaths) {
            if (path.contains("gatewayserver")){
                resource = true;
                break;
            }
        }
        if (con || resource)
        {
            risk_1 = Double.valueOf(0);
        }
        else {
            severity = Double.valueOf(Major);
            frequency = Double.valueOf(Double.valueOf(responseContext.getRestEntityContexts().size()));
            System.out.println(responseContext.getRestEntityContexts().size());
            risk_1 = severity * getLikelihood(frequency) * frequency;
            weight_APIGateway = frequency;
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
                    return risk_2 = 0.00;
                }
                else {
                    severity = Double.valueOf(Significant);
                    frequency = Double.valueOf(Double.valueOf(responseContext.getRestEntityContexts().size()));
                     risk_2 = severity*frequency*getLikelihood(frequency);
                     weight_NetworkCommunication = Double.valueOf(Double.valueOf(responseContext.getRestEntityContexts().size()));
                    return risk_2;
                }
            } else properties = null;

        }
        return 0.00;
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
                     frequency = Double.valueOf(Double.valueOf(responseContext.getRestEntityContexts().size()));
                     risk_3 = severity*frequency*getLikelihood(frequency);
                     weight_AccessControl = Double.valueOf(Double.valueOf(responseContext.getRestEntityContexts().size()));
                     return risk_3;
                 }

            }
        }

        return 0.00;
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
                weight_ImproperDependency = Double.valueOf(entry.getValue().getCount());
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
        weight_HardcodedEndpointsScore = Double.valueOf(hardCodedEndpointsContext.getTotalHardcodedEndpoints());
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
        weight_ImproperMSUtilization = 0.00;
        MicroservicesGreedyContext greedyService1 = greedyService.getGreedyMicroservices(request);
        if(greedyService1.getGreedyMicroservices().size() > 0)
        {
            return risk_6 = 0.00;
        } else {
            frequency = Double.valueOf(greedyService1.getGreedyMicroservices().size());
            weight_ImproperMSUtilization = frequency;
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
        weight_Caching = 0.00;
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
                    weight_Caching = Double.valueOf(Float.valueOf(responseContext.getRestEntityContexts().size()));
                    return risk_7;
                }
                else {
                    return 0.00;
                }

            }
        }
        return 0.00;
    }

    public Double getESBScore(RequestContext request) {
        ResponseContext responseContext = restDiscoveryService.generateResponseContext(request);
        double risk_8;
        weight_ESBScore = 0.00;
        if (esbService.getESBContext(request).getCandidateESBs().size() > 0)
        {
            severity = Double.valueOf(Float.valueOf(Major));
            frequency = Double.valueOf(esbService.getESBContext(request).getCandidateESBs().size());
            risk_8 = severity * getLikelihood(frequency) * frequency;
            weight_ESBScore = Double.valueOf(esbService.getESBContext(request).getCandidateESBs().size());
            return risk_8;
        }
        else {
            return 0.00;
        }

    }
    public Double getTotalRiskScore(RequestContext request) throws Exception {

//        System.out.println(weight_APIGateway);
//        System.out.println(weight_ImproperMSUtilization);
//        System.out.println(weight_AccessControl);
        double total_risk = 0.00;

//        long curr = System.currentTimeMillis();
//        ResponseContext responseContext = restDiscoveryService.generateResponseContext(request);
//        long now = System.currentTimeMillis();
//        times.put("Bytecode Analysis", now - curr);

        double risk_APIGateway = getAPIGatewayResponse(request);

        double risk_network = getNetworkCommunication(request);

        double risk_AccessControl = getAccessControl(request);

        double risk_ImproperDependency = getImproperDependency(request);

        double risk_HardcodedEndpointsScore = getHardcodedEndpointsScore(request);

        double risk_ImproperMSUtilization = getImproperMSUtilization(request);

        double risk_Caching = getCaching(request);

        double risk_ESB = getESBScore(request);

        double risk_synkScanning = getContainerVulnerabilities();


        double total_weight = weight_APIGateway + weight_NetworkCommunication +  + weight_AccessControl + weight_ImproperDependency + weight_HardcodedEndpointsScore +
                weight_Caching + weight_ImproperMSUtilization + weight_ESBScore + weigth_SynkScanning;

        if (total_weight <= 0.00)
        {
            total_risk = 0.00;
        }
        else {
            total_risk = ((risk_APIGateway * weight_APIGateway) + (risk_network * weight_NetworkCommunication) + (risk_AccessControl * weight_AccessControl) +
                    (risk_ImproperDependency + weight_ImproperDependency) + (risk_HardcodedEndpointsScore * weight_HardcodedEndpointsScore) +
                    (risk_Caching * weight_Caching) + (risk_ImproperMSUtilization + weight_ImproperMSUtilization) + (risk_ESB * weight_ESBScore) + (risk_synkScanning * weigth_SynkScanning)) / total_weight;

            return total_risk;
        }

//
//        String risk = "The risk score: "+ " "+  weight_APIGateway+ " " + weight_ImproperMSUtilization + " " + weight_AccessControl;
        return null;
    }

    public Double getContainerVulnerabilities() throws IOException, InterruptedException {
        Double synkRisk = null;
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(cmd, driveName, synkCommand);
        //processBuilder.start();

        //TimeUnit.MINUTES.sleep(1);
        //wait(100);
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<LinkedHashMap> dockerScanOutput =  objectMapper.readValue(new File(dataReading),ArrayList.class);
        // Mapping
        Map<String, LinkedHashMap<String,LinkedHashMap<String,ArrayList<LinkedHashMap<String,String>>>>> vulmap = dockerScanOutput.get(0);
        String baseImage = vulmap.get("docker").get("baseImageRemediation").get("advice").get(0).get("message");
        String lines[] = baseImage.split("\\r?\\n");
        String out [] = lines[1].split(" ");
        Map<String, Double> result = new HashMap<>();
        result.put("total", Double.valueOf(out[2]));
        result.put("critical", Double.valueOf(out[17]));
        result.put("high", Double.valueOf(out[19]));
        result.put("medium", Double.valueOf(out[21]));
        result.put("low", Double.valueOf(out[23]));
        if(Double.valueOf(out[19]) > 0 && Double.valueOf(out[17]) >0)
        {
            if(Double.valueOf(out[23]) > 10)
            {
                severity = Double.valueOf(Significant);
                likelihood = getLikelihood(Double.valueOf(out[19]) + Double.valueOf(out[17]));
                synkRisk = severity * likelihood;
            }
        } else if (Double.valueOf(out[23])> 20) {
            severity = Double.valueOf(Major);
            likelihood = getLikelihood(Double.valueOf(out[2]));
            synkRisk = severity * likelihood;
        }
        else {
            synkRisk =0.00;
        }
        return synkRisk;

    }


}
