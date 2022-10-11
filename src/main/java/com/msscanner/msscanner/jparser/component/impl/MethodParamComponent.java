package com.msscanner.msscanner.jparser.component.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.msscanner.msscanner.jparser.component.Component;
import com.msscanner.msscanner.jparser.visitor.IComponentVisitor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MethodParamComponent extends Component {

   // @JsonIgnore
    private Class<?> type;

    private AnnotationComponent annotation;
    @JsonProperty(value = "parameter_type")
    private String parameterType;
    @JsonProperty(value = "parameter_name")
    private String parameterName;

    public MethodParamComponent(Object x) {
        this.type = x.getClass();
    }

    @Override
    public void accept(IComponentVisitor visitor) {

    }
}
