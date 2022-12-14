package com.msscanner.msscanner.Service;

import com.msscanner.msscanner.jparser.component.Component;
import com.msscanner.msscanner.jparser.component.context.AnalysisContext;
import com.msscanner.msscanner.jparser.component.impl.AnnotationComponent;
import com.msscanner.msscanner.jparser.component.impl.ClassComponent;
import com.msscanner.msscanner.jparser.component.impl.MethodInfoComponent;
import com.msscanner.msscanner.jparser.model.AnnotationValuePair;
import com.msscanner.msscanner.model.context.APIContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class APIService {

    public List<APIContext> getAPIs(String path){
        List<APIContext> apis = new ArrayList<>();

        AnalysisContext analysisContext = JParserService.createContextFromPath(path);
        List<ClassComponent> classes = analysisContext.getClasses();
        classes.forEach(clazz -> {
            List<AnnotationComponent> annotationComponents = clazz.getAnnotations().stream().map(Component::asAnnotationComponent).collect(Collectors.toList());
            for(AnnotationComponent annotationComponent : annotationComponents) {
                String annotation = annotationComponent.getAsString();
                if (annotation.matches("@RequestMapping|@RestController")) {
                    String classLevelPath = "";
                    if(annotationComponent.getAnnotationValue() != null){
                        if(!annotationComponent.getAnnotationValue().equals("RestController")){
                            classLevelPath = annotationComponent.getAnnotationValue();
                        }
                    }
                    List<MethodInfoComponent> methods = clazz.getMethods().stream().map(Component::asMethodInfoComponent).collect(Collectors.toList());
                    for(MethodInfoComponent method : methods){
                        List<AnnotationComponent> methodAnnotationComponents = method.getAnnotations().stream().map(Component::asAnnotationComponent).collect(Collectors.toList());
                        for(AnnotationComponent methodAnnotationComponent : methodAnnotationComponents) {
                            if (methodAnnotationComponent.getAsString().contains("Mapping")) {
                                String methodLevelPath = null;
                                if(methodAnnotationComponent.getAnnotationValue() != null){
                                    methodLevelPath =  methodAnnotationComponent.getAnnotationValue().endsWith("Mapping") ? "" : methodAnnotationComponent.getAnnotationValue();
                                }

                                if(methodLevelPath == null) {
                                    List<AnnotationValuePair> annotationValuePairList = methodAnnotationComponent.getAnnotationValuePairList();
                                    for (AnnotationValuePair valuePair : annotationValuePairList) {
                                        if (valuePair.getKey().equals("path")) {
                                            String apiPath = (classLevelPath + valuePair.getValue()).replace("\"", "");
                                            apis.add(new APIContext(apiPath));
                                        }
                                    }
                                } else {
                                    String apiPath = (classLevelPath + methodLevelPath).replace("\"", "");
                                    apis.add(new APIContext(apiPath));
                                }
                            }
                        }
                    }
                }
            }
        });

        return apis;
    }

    public boolean isVersioned(String api){
        return api.matches("/?api/v[0-9]+.*");
    }
}
