package com.msscanner.msscanner.jparser.component.context;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.msscanner.msscanner.jparser.component.Component;
import com.msscanner.msscanner.jparser.component.impl.ClassComponent;
import com.msscanner.msscanner.jparser.component.impl.DirectoryComponent;
import com.msscanner.msscanner.jparser.component.impl.InterfaceComponent;
import com.msscanner.msscanner.jparser.component.impl.ModuleComponent;
import com.msscanner.msscanner.jparser.serializer.ModulePackageMapSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public abstract class JSSAContext extends Component {

    @JsonIgnore
    protected List<ClassOrInterfaceDeclaration> classOrInterfaceDeclarations;
    @JsonIgnore
    protected List<MethodDeclaration> methodDeclarations;
    @JsonIgnore
    protected DirectoryComponent directoryGraph;

    @JsonProperty(value = "module_package_map")
    @JsonSerialize(using = ModulePackageMapSerializer.class)
    protected Map<ModuleComponent, String> packageMap;

    @JsonProperty(value = "succeeded")
    protected boolean succeeded = false;
    @JsonProperty(value = "root_path")
    protected String rootPath;

    @JsonProperty(value = "class_names")
    protected List<String> classNames;
    @JsonProperty(value = "interface_names")
    protected List<String> interfaceNames;

    @JsonProperty(value = "containers")
    protected List<Component> classesAndInterfaces;
    protected List<ClassComponent> classes;
    protected List<InterfaceComponent> interfaces;
    protected List<ModuleComponent> modules;
    protected List<Component> methods;

}
