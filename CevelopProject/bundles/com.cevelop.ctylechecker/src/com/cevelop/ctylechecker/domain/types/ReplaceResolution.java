package com.cevelop.ctylechecker.domain.types;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cevelop.ctylechecker.domain.ISingleExpression;


public class ReplaceResolution extends AbstractRenameResolution {

    public ReplaceResolution(ISingleExpression expression) {
        super(expression);
    }

    @Override
    public String transform(String pName) {
        Pattern pattern = Pattern.compile(expression.getExpression());
        Matcher matcher = pattern.matcher(pName);
        String[] arguments = expression.getArgument().split(",");
        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                if ((i - 1) < arguments.length) {
                    String replacementText = arguments[i - 1];
                    if (!replacementText.isEmpty()) {
                        pName = pName.replace(matcher.group(i), arguments[i - 1]);
                    }
                }
            }
        }
        return pName;
    }
}
