package com.cevelop.clonewar.view.util;

import org.eclipse.swt.widgets.TableItem;

import com.cevelop.clonewar.transformation.action.TransformAction;
import com.cevelop.clonewar.transformation.util.TypeInformation;


/**
 * Helper class for {@link TableItem} data with {@link TypeInformation} and
 * {@link TransformAction}.
 *
 * @author ythrier(at)hsr.ch
 */
public class TypePair {

    private TypeInformation typeInfo_;
    private TransformAction transformAction_;

    /**
     * Create the type pair.
     *
     * @param typeInfo
     * Type info.
     * @param transformAction
     * Transform action.
     */
    public TypePair(TypeInformation typeInfo, TransformAction transformAction) {
        this.typeInfo_ = typeInfo;
        this.transformAction_ = transformAction;
    }

    /**
     * Get the type info.
     *
     * @return Type info.
     */
    public TypeInformation getTypeInfo() {
        return typeInfo_;
    }

    /**
     * Get the transform action.
     *
     * @return Transform action.
     */
    public TransformAction getAction() {
        return transformAction_;
    }
}
