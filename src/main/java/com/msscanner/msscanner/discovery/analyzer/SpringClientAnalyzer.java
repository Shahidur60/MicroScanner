package com.msscanner.msscanner.discovery.analyzer;

import com.msscanner.msscanner.discovery.dataflow.DataFlowException;
import com.msscanner.msscanner.discovery.dataflow.LocalVariableScanner;
import com.msscanner.msscanner.discovery.instruction.InstructionInfo;
import com.msscanner.msscanner.discovery.instruction.InstructionScanner;
import com.msscanner.msscanner.discovery.instruction.StringStackElement;
import com.msscanner.msscanner.discovery.model.HttpMethod;
import com.msscanner.msscanner.discovery.model.RestEntity;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


@Component
@Slf4j
public class SpringClientAnalyzer {

    @AllArgsConstructor
    @ToString
    private static class RestTemplateMethod {
        String restTemplateMethod;
        HttpMethod httpMethod;
        int numberOfParams;
    }

    private static final RestTemplateMethod[] restTemplateMethods = {
            new RestTemplateMethod("getForObject", HttpMethod.GET, 2),
            new RestTemplateMethod("getForEntity", HttpMethod.GET, 2),
            new RestTemplateMethod("exchange", HttpMethod.GET, 3),
            new RestTemplateMethod("postForObject", HttpMethod.POST, 3),
            new RestTemplateMethod("postForEntity", HttpMethod.POST, 3),
            new RestTemplateMethod("delete", HttpMethod.DELETE, 1),
    };

    private static final String restTemplateClass = "org.springframework.web.client.RestTemplate";

    public List<RestEntity> getRestEntity(CtClass ctClass, Properties properties) {
        List<RestEntity> restEntities = new ArrayList<>();

        for (CtMethod ctMethod : ctClass.getMethods()) {

            // get instructions
            List<InstructionInfo> instructions = InstructionScanner.scan(ctMethod);

            // find caller index
            int index = 0;
            RestTemplateMethod foundMethod = null;

            for (RestTemplateMethod targetMethod : restTemplateMethods) {
                try {
                    index = LocalVariableScanner.findIndexForMethodCall(instructions, restTemplateClass + "." + targetMethod.restTemplateMethod);
                    foundMethod = targetMethod;
                    break;
                } catch (DataFlowException ignore) {
                }
            }

            // not a rest entity
            if (foundMethod == null) {
                continue;
            }

            // determine HTTP method for exchange
            if (foundMethod.restTemplateMethod.equals("exchange")) {
                foundMethod.httpMethod = LocalVariableScanner.peakHttpMethodForExchange(instructions, index);
                log.info(ctMethod.getLongName() + " exchange type " + foundMethod.httpMethod.toString());
            }

            // find url
            try {
                List<StringStackElement> stringStackElements = LocalVariableScanner.peekParamForMethodCall(
                        instructions, index, foundMethod.numberOfParams);

                // find field values defined by @value annotation
                for (StringStackElement stringStackElement : stringStackElements) {
                    if (stringStackElement.getType() == StringStackElement.StringStackElementType.FIELD) {
                        String simpleFieldName = stringStackElement.getValue().replace(ctClass.getName() + ".", "");

                        CtField ctField = ctClass.getField(simpleFieldName);

                        String propertyName = Helper.getFieldAnnotationValue(ctField);
                        String propertyValue = null;

                        if (propertyName != null && properties != null) {
                            propertyValue = properties.getProperty(propertyName);
                            propertyValue = Helper.removeEnclosedQuotations(propertyValue);
                            propertyValue = Helper.removeEnclosedSingleQuotations(propertyValue);
                        }

                        if (propertyValue != null) {
                            stringStackElement.setType(StringStackElement.StringStackElementType.CONSTANT);
                            stringStackElement.setValue(propertyValue);
                        } else {
                            throw new DataFlowException("Can not resolve field value for " + stringStackElement.getValue());
                        }
                    }
                }

                // build the url from stack elements
                String url = StringStackElement.mergeStackElements(stringStackElements);

                // create rest entity
                RestEntity restEntity = new RestEntity();
                restEntity.setClient(true);
                restEntity.setHttpMethod(foundMethod.httpMethod);
                restEntity.setUrl(url);

                // add class and method signatures
                restEntity.setClassName(ctClass.getName());
                restEntity.setMethodName(ctMethod.getName());
                restEntity.setReturnType(Helper.getReturnType(ctMethod));

                restEntities.add(restEntity);

            } catch (DataFlowException | NotFoundException e) {
                log.error(e.toString());
            }
        }
        return restEntities;
    }
}
