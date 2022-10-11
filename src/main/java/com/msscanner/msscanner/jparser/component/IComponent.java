package com.msscanner.msscanner.jparser.component;

import com.msscanner.msscanner.jparser.visitor.IComponentVisitor;

public interface IComponent {

    void accept(IComponentVisitor visitor);

}
