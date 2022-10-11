package com.msscanner.msscanner.Service;

import com.msscanner.msscanner.discovery.context.RequestContext;
import com.msscanner.msscanner.jparser.component.Component;
import com.msscanner.msscanner.jparser.component.context.AnalysisContext;
import com.msscanner.msscanner.jparser.component.impl.AnnotationComponent;
import com.msscanner.msscanner.jparser.component.impl.ClassComponent;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EntityService {

    public List<String> getAllEntities(RequestContext request){

        List<String> entities = new ArrayList<>();

        AnalysisContext analysisContext = JParserService.createContextFromPath(request.getPathToCompiledMicroservices());
        List<ClassComponent> classes = analysisContext.getClasses();
        classes.forEach(clazz -> {
            List<AnnotationComponent> annotationComponents = clazz.getAnnotations().stream().map(Component::asAnnotationComponent).collect(Collectors.toList());
            for(AnnotationComponent annotationComponent : annotationComponents) {
                String annotation = annotationComponent.getAsString();
                if (annotation.matches("@Entity")) {
                    // Entity
                    entities.add(clazz.getClassName());
                }
            }
        });

        return entities;
    }

    public List<String> getEntitiesPerJar(RequestContext request, String path){

        Set<String> entities = new HashSet<>();

        String basePath = request.getPathToCompiledMicroservices();

        // convert windows path to linux style
        basePath = basePath.replace("\\\\", "/");

        if(!basePath.endsWith("/")){
            basePath = basePath.concat("/");
        }

        String remainder = path.substring(basePath.length());
        String folder = remainder.substring(0, remainder.indexOf('/'));

        String newPath = basePath.concat(folder).concat("/");

        AnalysisContext analysisContext = JParserService.createContextFromPath(newPath);
        List<ClassComponent> classes = analysisContext.getClasses();
        classes.forEach(clazz -> {

            if(clazz.getPackageName().contains("entity")){
                entities.add(clazz.getClassName());
            }

            List<AnnotationComponent> annotationComponents = clazz.getAnnotations().stream().map(Component::asAnnotationComponent).collect(Collectors.toList());
            for(AnnotationComponent annotationComponent : annotationComponents) {
                String annotation = annotationComponent.getAsString();
                if (annotation.matches("@Entity") || annotation.matches("@Document")) {
                    // Entity
                    entities.add(clazz.getClassName());
                }
            }
        });

        return new ArrayList<>(entities);
    }

    public List<String> getEntitiesPerFolder(String path){

        Set<String> entities = new HashSet<>();
        AnalysisContext analysisContext = JParserService.createContextFromPath(path);
        List<ClassComponent> classes = analysisContext.getClasses();
        classes.forEach(clazz -> {

            if(clazz.getPackageName().contains("entity")){
                entities.add(clazz.getClassName());
            }

            List<AnnotationComponent> annotationComponents = clazz.getAnnotations().stream().map(Component::asAnnotationComponent).collect(Collectors.toList());
            for(AnnotationComponent annotationComponent : annotationComponents) {
                String annotation = annotationComponent.getAsString();
                if (annotation.matches("@Entity") || annotation.matches("@Document")) {
                    // Entity
                    entities.add(clazz.getClassName());
                }
            }
        });

        return new ArrayList<>(entities);
    }
}
