package com.cevelop.templator.instancespy.views;

import org.eclipse.cdt.core.dom.ast.ASTTypeUtil;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateArgument;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateInstance;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;


public class TemplateLabelProvider extends LabelProvider implements ITableLabelProvider {

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {
        if (element instanceof ICPPTemplateInstance) {

            ICPPTemplateInstance instance = (ICPPTemplateInstance) element;
            ICPPTemplateArgument[] arguments = instance.getTemplateArguments();
            if (columnIndex == 0) {
                return instance.getName() + ASTTypeUtil.getArgumentListString(arguments, true);
            }
            if (arguments.length < columnIndex) {
                return "";
            }
            return ASTTypeUtil.getType(arguments[columnIndex - 1].getTypeValue());
        }
        return null;
    }

}
