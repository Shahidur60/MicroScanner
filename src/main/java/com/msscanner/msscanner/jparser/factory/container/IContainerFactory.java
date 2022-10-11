package com.msscanner.msscanner.jparser.factory.container;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.msscanner.msscanner.jparser.component.Component;
import com.msscanner.msscanner.jparser.component.impl.ModuleComponent;

public interface IContainerFactory {

    Component createComponent(ModuleComponent parent, ClassOrInterfaceDeclaration cls, CompilationUnit unit);

}
