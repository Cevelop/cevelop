package com.cevelop.ctylechecker.ui;

import org.eclipse.swt.widgets.Composite;

import com.cevelop.ctylechecker.service.ICtylecheckerRegistry;
import com.cevelop.ctylechecker.service.types.CtylecheckerRuntime;


public class AbstractCtylecheckerComposite extends Composite {

    ICtylecheckerRegistry registry = CtylecheckerRuntime.getInstance().getRegistry();

    public AbstractCtylecheckerComposite(Composite parent, int style) {
        super(parent, style);
    }

    protected ICtylecheckerRegistry getRegistry() {
        return registry;
    }

}
