package com.msscanner.msscanner.jparser.component.context;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.msscanner.msscanner.jparser.component.Component;
import com.msscanner.msscanner.jparser.visitor.IComponentVisitor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.stream.Collectors;


@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AnalysisContext extends JSSAContext {

    public AnalysisContext filterByClass(String name) {
        setClassNames(this.classNames.stream().filter(x -> x.equals(name)).collect(Collectors.toList()));
        this.setClassesAndInterfaces(this.getClassesAndInterfaces().stream()
                .filter(x -> x.getInstanceName().equals(name)).collect(Collectors.toList()));
        return this;
    }

    public Component getClassByName(String name) {
        for (Component c : classes) {
            if (c.asClassComponent().getClassName().equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }

    public Component getMethodByName(String name) {
        for (Component m : methods) {
            if (m.asMethodInfoComponent().getMethodName().equalsIgnoreCase(name)) {
                return m;
            }
        }
        return null;
    }

    @Override
    public void accept(IComponentVisitor visitor) {
        visitor.visit(this);
    }
}

