package com.msscanner.msscanner.jparser.factory.container;

import com.msscanner.msscanner.jparser.factory.container.impl.ClassComponentFactory;
import com.msscanner.msscanner.jparser.factory.container.impl.InterfaceComponentFactory;
import com.msscanner.msscanner.jparser.factory.container.impl.ModuleComponentFactory;
import com.msscanner.msscanner.jparser.model.ContainerType;
@Deprecated
public class ComponentFactoryProducer {

    public static AbstractContainerFactory getFactory(ContainerType coi) {
        switch(coi) {
            case CLASS: return ClassComponentFactory.getInstance();
            case INTERFACE: return InterfaceComponentFactory.getInstance();
            case MODULE: return ModuleComponentFactory.getInstance();
            default: return null;
        }
    }

}
