package com.cevelop.clonewar.tests.tests;

import java.util.List;
import java.util.Properties;

import org.eclipse.ltk.core.refactoring.RefactoringContext;

import com.cevelop.clonewar.transformation.configuration.TransformConfiguration;
import com.cevelop.clonewar.transformation.util.TypeInformation;


public class ExtractFunctionTemplateTest extends AbstractCloneWarTest {

    protected final String PARAMETER_BEFORE_RETURN_KEY = "parameterBeforeReturn";
    protected boolean      parameterBeforeReturn;

    @Override
    protected void configureTest(Properties properties) {
        parameterBeforeReturn = Boolean.parseBoolean(properties.getProperty(PARAMETER_BEFORE_RETURN_KEY, "false"));
        super.configureTest(properties);
    }

    @Override
    protected void simulateUserInput(RefactoringContext context) {
        TransformConfiguration config = refactoring.getTransformation().getConfig();
        if (parameterBeforeReturn) {
            List<TypeInformation> types = config.getAllTypesOrdered();
            int i = 0;
            for (TypeInformation type : types) {
                if (config.hasReturnTypeAction(type)) {
                    type.setOrderId(types.size() - 1);
                } else {
                    type.setOrderId(i++);
                }
            }
        }
        super.simulateUserInput(context);
    }
}
