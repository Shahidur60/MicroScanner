package com.msscanner.msscanner.jparser.factory.container.impl;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.msscanner.msscanner.jparser.component.Component;
import com.msscanner.msscanner.jparser.component.impl.InterfaceComponent;
import com.msscanner.msscanner.jparser.component.impl.ModuleComponent;
import com.msscanner.msscanner.jparser.factory.container.AbstractContainerFactory;
import com.msscanner.msscanner.jparser.model.ContainerType;
import com.msscanner.msscanner.jparser.model.InstanceType;
import com.msscanner.msscanner.jparser.model.LanguageFileType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode
public class InterfaceComponentFactory extends AbstractContainerFactory {

    private static InterfaceComponentFactory INSTANCE;

    public final ContainerType TYPE = ContainerType.INTERFACE;

    private InterfaceComponentFactory() {
    }

    public static InterfaceComponentFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InterfaceComponentFactory();
        }
        return INSTANCE;
    }

    @Override
    public Component createComponent(ModuleComponent parent, ClassOrInterfaceDeclaration cls, CompilationUnit unit) {
        InterfaceComponent output = new InterfaceComponent();
        List<Component> annotations = initAnnotations(output, cls);
        output.setAnalysisUnit(unit);
        output.setAnnotations(annotations);
        output.setContainerType(ContainerType.CLASS);
        output.setCls(cls);
        output.setCompilationUnit(unit);
        output.setId(getId());
        output.setInstanceName(cls.getNameAsString() + "::InterfaceComponent");
        output.setInstanceType(InstanceType.CLASSCOMPONENT);
        output.setMethodDeclarations(cls.getMethods());
        output.setContainerName(cls.getNameAsString());
        output.setPackageName("N/A"); // TODO: Set package name
        output.setParent(parent);
        output.setStereotype(createStereotype(cls));
        output.setId(getId());
        output.setRawSource(cls.toString());
        output.setPath(parent.getPath() + "/" + cls.getNameAsString() + "."
                + LanguageFileType.fromString(parent.getLanguage().toLowerCase()).asString()); //TODO: Use appropriate directory separater for OS
        List<Component> methods = createMethods(cls, output);
        List<Component> constructors = createConstructors(cls, output);
        output.setMethods(methods);
        List<Component> subComponents = new ArrayList<>();
        subComponents.addAll(methods);
        subComponents.addAll(constructors);
        subComponents.addAll(annotations);
        output.setSubComponents(subComponents);
        return output;
    }

}
