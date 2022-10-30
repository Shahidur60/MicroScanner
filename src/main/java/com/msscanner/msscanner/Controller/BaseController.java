package com.msscanner.msscanner.Controller;


import com.msscanner.msscanner.Service.*;
import com.msscanner.msscanner.discovery.context.RequestContext;
import com.msscanner.msscanner.discovery.context.ResponseContext;
import com.msscanner.msscanner.discovery.context.RestEntityContext;
import com.msscanner.msscanner.discovery.context.RestFlowContext;
import com.msscanner.msscanner.discovery.model.RestEntity;
import com.msscanner.msscanner.discovery.model.RestFlow;
import com.msscanner.msscanner.discovery.service.ResourceService;
import com.msscanner.msscanner.model.SharedIntimacy;
import com.msscanner.msscanner.model.SharedLibrary;
import com.msscanner.msscanner.model.context.*;
import com.msscanner.msscanner.model.hardcodedEndpoint.HardcodedEndpoint;
import com.msscanner.msscanner.model.hardcodedEndpoint.HardcodedEndpointType;
import com.msscanner.msscanner.util.FileUtil;
import javassist.CtClass;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BaseController {

    private final APIService apiService;
    private final LibraryService libraryService;
    private final PersistencyService persistencyService;
    private final ESBService esbService;
    private final TooManyStandardsService tooManyStandardsService;
    private final RestService restDiscoveryService;
    private final CyclicDependencyService cyclicDependencyService;
    private final EntityService entityService;
    private final WrongCutsService wrongCutsService;
    private final GreedyService greedyService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private FileUtil fileUtil;

    @RequestMapping(value = "/base", method = RequestMethod.GET)
    public String populateCourse(){
        return "Hello World!!!!!";
    }

//    @CrossOrigin(origins = "*")
//    @RequestMapping(path = "/noAPIGateway", method = RequestMethod.POST, produces = "application/json; charset=UTF-8", consumes = {"text/plain", "application/*"})
//    public boolean getNoAPIGateway(){
//        return true;
//    }

//   evaluateAPIGateway
@CrossOrigin(origins = "*")
@RequestMapping(path = "/", method = RequestMethod.POST, produces = "application/json; charset=UTF-8", consumes = {"text/plain", "application/*"})
public String getHandshake(){
    return "Hello from [NoseController]";
}

    @CrossOrigin(origins = "*")
    @RequestMapping(path = "/report", method = RequestMethod.POST, produces = "application/json; charset=UTF-8", consumes = {"text/plain", "application/*"})
    public ApplicationSmellsContext getReport(@RequestBody RequestContext request) throws Exception{
        ApplicationSmellsContext context = new ApplicationSmellsContext();
        Map<String, Long> times = new HashMap<>();

        long curr = System.currentTimeMillis();
        ResponseContext responseContext = restDiscoveryService.generateResponseContext(request);
        long now = System.currentTimeMillis();
        times.put("Bytecode Analysis", now - curr);

        curr = System.currentTimeMillis();
        context.setUnversionedAPIContext(getApis(request));
        now = System.currentTimeMillis();
        times.put("Unversioned API", now - curr);

        curr = System.currentTimeMillis();
        context.setSharedLibraryContext(getSharedLibraries(request));
        now = System.currentTimeMillis();
        times.put("Shared Library", now - curr);

//        curr = System.currentTimeMillis();
//        context.setWrongCutsContext(getWrongCuts(request));
//        now = System.currentTimeMillis();
//        times.put("Wrong Cuts", now - curr);

        curr = System.currentTimeMillis();
        context.setHardCodedEndpointsContext(getHardcodedEndpoints(request));
        now = System.currentTimeMillis();
        times.put("Hardcoded Enpoints", now - curr);

        curr = System.currentTimeMillis();
        context.setCyclicDependency(getCyclicDependency(request));
        now = System.currentTimeMillis();
        times.put("Cyclic Dependency", now - curr);

        curr = System.currentTimeMillis();
        context.setSharedPersistencyContext(getSharedPersistency(request));
        now = System.currentTimeMillis();
        times.put("Shared Persistency", now - curr);

        curr = System.currentTimeMillis();
        context.setEsbContext(getESBUsage(request));
        now = System.currentTimeMillis();
        times.put("ESB", now - curr);

        curr = System.currentTimeMillis();
        context.setAPIGateway(getNoAPIGateway(request));
        now = System.currentTimeMillis();
        times.put("API Gateway", now - curr);

//        curr = System.currentTimeMillis();
//        context.setInappropriateServiceIntimacyContext(getInappropriateServiceIntimacy(request));
//        now = System.currentTimeMillis();
//        times.put("ISI", now - curr);

        curr = System.currentTimeMillis();
        context.setTooManyStandardsContext(getTooManyStandards(request));
        now = System.currentTimeMillis();
        times.put("Too Many Standards", now - curr);

        curr = System.currentTimeMillis();
        context.setMicroservicesGreedyContext(getMicroservicesGreedy(request));
        now = System.currentTimeMillis();
        times.put("Microservice Greedy", now - curr);

        context.setTimes(times);

        return context;
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(path = "/apis", method = RequestMethod.POST, produces = "application/json; charset=UTF-8", consumes = {"text/plain", "application/*"})
    public UnversionedAPIContext getApis(@RequestBody RequestContext request){
        return new UnversionedAPIContext(apiService.getAPIs(request.getPathToCompiledMicroservices()).stream()
                .map(APIContext::getPath)
                .filter(api -> !apiService.isVersioned(api))
                .collect(Collectors.toSet()));
    }

    @RequestMapping(path = "/rad", method = RequestMethod.POST, produces = "application/json; charset=UTF-8", consumes = {"text/plain", "application/*"})
    public ResponseContext rad(@RequestBody RequestContext request) {
        return restDiscoveryService.generateResponseContext(request);
    }
    @Autowired
    private final ResourceService resourceService;

    @RequestMapping(path = "/prop", method = RequestMethod.GET, produces = "application/json; charset=UTF-8", consumes = {"text/plain", "application/*"})
    public Properties prop(@RequestBody RequestContext request) throws IOException, XmlPullParserException {
        List<String> resourcePaths = resourceService.getResourcePaths(request.getPathToCompiledMicroservices());

        for (String path : resourcePaths) {
            List<CtClass> ctClasses = resourceService.getCtClasses(path, request.getOrganizationPath());

            Set<Properties> propertiesSet = resourceService.getProperties(path, request.getOrganizationPath());
            List<String> fileNames = resourceService.getPomXML(request.getPathToCompiledMicroservices());
            MavenXpp3Reader reader = new MavenXpp3Reader();
            //System.out.println(reader.read(new FileReader(fileNames.get(0))).getDependencies().toString());


           // System.out.println(fileNames.toString());
            Properties properties;
            if (propertiesSet.size() > 0) {
                properties = propertiesSet.iterator().next();

//                 if(properties.getProperty("keycloak" ).toString().equals(""))
//                 {
//                     return Properties;
//                 }
                return properties;
                //JSONObject jsonObject = new JSONObject(properties);
                //return properties.toString();
            } else properties = null;

            // print the properties for debug
            // Helper.dumpProperties(properties, path);
//            JSONObject jsonObject = new JSONObject(properties.toString());
//            return jsonObject;
            System.out.println(properties);

        }


        return null;
    }
    @RequestMapping(path = "/caching", method = RequestMethod.GET, produces = "application/json; charset=UTF-8", consumes = {"text/plain", "application/*"})
    public Double getCaching(@RequestBody RequestContext request) throws IOException, XmlPullParserException {
        return reportService.getCaching(request);
    }

    @RequestMapping(path = "/access", method = RequestMethod.GET, produces = "application/json; charset=UTF-8", consumes = {"text/plain", "application/*"})
    public Double getAccessControl(@RequestBody RequestContext request) throws IOException, XmlPullParserException {
        return reportService.getAccessControl(request);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(path = "/sharedLibraries", method = RequestMethod.POST, produces = "application/json; charset=UTF-8", consumes = {"text/plain", "application/*"})
    public SharedLibraryContext getSharedLibraries(@RequestBody RequestContext request) throws Exception {
        return libraryService.getSharedLibraries(request);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(path = "/getAPIGateway", method = RequestMethod.GET, produces = "application/json; charset=UTF-8", consumes = {"text/plain", "application/*"})
    public Double getAPIGatewayResponse(@RequestBody RequestContext request) throws Exception
    {
        return reportService.getAPIGatewayResponse(request);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(path = "/improperDependency", method = RequestMethod.GET, produces = "application/json; charset=UTF-8", consumes = {"text/plain", "application/*"})
    public Double getImproperDependency(@RequestBody RequestContext request) throws Exception
    {
        return reportService.getImproperDependency(request);
    }



    @CrossOrigin(origins = "*")
    @RequestMapping(path = "/wrongCuts", method = RequestMethod.POST, produces = "application/json; charset=UTF-8", consumes = {"text/plain", "application/*"})
    public WrongCutsContext getWrongCuts(@RequestBody RequestContext request){
        return wrongCutsService.getWrongCuts(request);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(path = "/risk", method = RequestMethod.GET, produces = "application/json; charset=UTF-8", consumes = {"text/plain", "application/*"})
    public Double getRisk(@RequestBody RequestContext request) throws Exception {
        request.setPathToCompiledMicroservices(fileUtil.getMicroservicePath());
        return reportService.getTotalRiskScore(request);
    }

        @CrossOrigin(origins = "*")
    @RequestMapping(path = "/hardcodedEndpoints", method = RequestMethod.POST, produces = "application/json; charset=UTF-8", consumes = {"text/plain", "application/*"})
    public HardCodedEndpointsContext getHardcodedEndpoints(@RequestBody RequestContext request){
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

        return hardCodedEndpointsContext;
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(path = "/gethardcodedEndpoints", method = RequestMethod.GET, produces = "application/json; charset=UTF-8", consumes = {"text/plain", "application/*"})
    public Double getHardcodedEndpointsScore(@RequestBody RequestContext request){

        return reportService.getHardcodedEndpointsScore(request);
    }


        @CrossOrigin(origins = "*")
    @RequestMapping(path = "/netcom", method = RequestMethod.GET, produces = "application/json; charset=UTF-8", consumes = {"text/plain", "application/*"})
    public Double getNetworkCommunication(@RequestBody RequestContext request)
    {
        return reportService.getNetworkCommunication(request);
    }


    @CrossOrigin(origins = "*")
    @RequestMapping(path = "/cyclicDependency", method = RequestMethod.POST, produces = "application/json; charset=UTF-8", consumes = {"text/plain", "application/*"})
    public boolean getCyclicDependency(@RequestBody RequestContext request){
        return cyclicDependencyService.getCyclicDependencies(request);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(path = "/sharedPersistency", method = RequestMethod.POST, produces = "application/json; charset=UTF-8", consumes = {"text/plain", "application/*"})
    public SharedPersistencyContext getSharedPersistency(@RequestBody RequestContext request){
        return persistencyService.getSharedPersistency(request);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(path = "/esbUsage", method = RequestMethod.POST, produces = "application/json; charset=UTF-8", consumes = {"text/plain", "application/*"})
    public ESBContext getESBUsage(@RequestBody RequestContext request) {
        return esbService.getESBContext(request);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(path = "/getESB", method = RequestMethod.GET, produces = "application/json; charset=UTF-8", consumes = {"text/plain", "application/*"})
    public Double getESBScore(@RequestBody RequestContext request) {

        return reportService.getESBScore(request);

//        return esbService.getESBContext(request);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(path = "/noAPIGateway", method = RequestMethod.POST, produces = "application/json; charset=UTF-8", consumes = {"text/plain", "application/*"})
    public boolean getNoAPIGateway(@RequestBody RequestContext request){

        // Get all connections
        ResponseContext responseContext = restDiscoveryService.generateResponseContext(request);

        return responseContext.getRestEntityContexts().size() >= 50;
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(path = "/inappropriateServiceIntimacy", method = RequestMethod.POST, produces = "application/json; charset=UTF-8", consumes = {"text/plain", "application/*"})
    public InappropriateServiceIntimacyContext getInappropriateServiceIntimacy(@RequestBody RequestContext request){

        InappropriateServiceIntimacyContext inappropriateServiceIntimacyContext = new InappropriateServiceIntimacyContext();
        ResponseContext responseContext = restDiscoveryService.generateResponseContext(request);

        for(RestFlow restFlow : responseContext.getRestFlowContext().getRestFlows()){
            String jarA = restFlow.getResourcePath();
            for(RestEntity entity : restFlow.getServers()){
                String jarB = entity.getResourcePath();

                // Get two sets of entity objects
                List<String> entitiesA = entityService.getEntitiesPerJar(request, jarA);
                List<String> entitiesB = entityService.getEntitiesPerJar(request, jarB);

                Set<String> result = entitiesA.stream()
                        .distinct()
                        .filter(entitiesB::contains)
                        .collect(Collectors.toSet());

                // If above a certain threshold (80%) then yes
                double similarity = result.size() * 1.0 / Math.max(entitiesA.size(), entitiesB.size());
                if(similarity > 0.8){
                    inappropriateServiceIntimacyContext.addSharedIntimacy(new SharedIntimacy(restFlow.getResourcePath(), entity.getResourcePath(), similarity));
                }
            }
        }

        return inappropriateServiceIntimacyContext;
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(path = "/tooManyStandards", method = RequestMethod.POST, produces = "application/json; charset=UTF-8", consumes = {"text/plain", "application/*"})
    public TooManyStandardsContext getTooManyStandards(@RequestBody RequestContext request){
        return tooManyStandardsService.getStandards(request);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(path = "/microservicesGreedy", method = RequestMethod.POST, produces = "application/json; charset=UTF-8", consumes = {"text/plain", "application/*"})
    public MicroservicesGreedyContext getMicroservicesGreedy(@RequestBody RequestContext request){
        return greedyService.getGreedyMicroservices(request);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(path = "/getUnproperMSUtilization", method = RequestMethod.GET, produces = "application/json; charset=UTF-8", consumes = {"text/plain", "application/*"})
    public Double getUnproperMSUtilization(@RequestBody RequestContext request){
        return reportService.getImproperMSUtilization(request);
//        return greedyService.getGreedyMicroservices(request);
    }

//    @CrossOrigin(origins = "*")
//    @RequestMapping(path = "/riskScore", method = RequestMethod.GET)
//    public ResponseEntity<?> getRiskScore() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, URISyntaxException {
//        TrustStrategy acceptingTrustStrategy = (x509Certificates, s) -> true;
//        SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
//        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
//        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
//        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
//        requestFactory.setHttpClient(httpClient);
//        RestTemplate restTemplate = new RestTemplate(requestFactory);
//        HttpHeaders headers = new HttpHeaders();
//
//        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//        headers.add("X-Apikeys","accessKey=83a02760539fbcf31e364fe68317e4815cac6695555a163b85e2d551c7da09cb;secretKey=e17d100638dd8dac2dc316453b379f87e3986e0c2630854158cfb7558babbb0d");
//        HttpEntity<?> httpEntity = new HttpEntity<>(headers);
//
//        URI uri = new URI("https://localhost:8834/scans/34");
//        //restTemplate.getForObject("https://localhost:8834/scans/34",httpEntity,String.class);
//        ArrayList response = (ArrayList) restTemplate.exchange("https://localhost:8834/scans/34", HttpMethod.GET, httpEntity, Map.class).getBody().get("vulnerabilities");
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }

}
