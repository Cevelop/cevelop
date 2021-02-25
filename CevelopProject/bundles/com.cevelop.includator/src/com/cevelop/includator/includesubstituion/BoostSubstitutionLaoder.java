package com.cevelop.includator.includesubstituion;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.core.model.IIncludeReference;
import org.eclipse.core.runtime.IPath;

import com.cevelop.includator.resources.IncludatorProject;


public class BoostSubstitutionLaoder extends IncludeSubstitutionLoader {

    private final List<String> boostIncludePaths;

    public BoostSubstitutionLaoder(IncludatorProject project) {
        super(project);
        boostIncludePaths = new ArrayList<>();
        initBosstIncludePath();
    }

    private void initBosstIncludePath() {
        for (IIncludeReference curIncludePath : project.getIncludeReferences()) {
            IPath potentialBoostPath = curIncludePath.getPath().append("boost");
            File potentialFolder = new File(potentialBoostPath.toOSString());
            if (potentialFolder.exists() && potentialFolder.isDirectory()) {
                boostIncludePaths.add(potentialBoostPath.toOSString());
                // do not break here since there could be different places containing boost header (e.g. cute project that contains boost headers)
            }
        }
    }

    public boolean isBoostPresent() {
        return !boostIncludePaths.isEmpty();
    }

    @Override
    public Map<String, WeightedObject<String>> getSubstitutions() {
        return new HashMap<>();
    }

    @Override
    public Map<String, WeightedObject<String>> getExceptions() {
        HashMap<String, WeightedObject<String>> exceptions = new HashMap<>();
        addFunctionHppException(exceptions);
        return exceptions;
    }

    private void addFunctionHppException(HashMap<String, WeightedObject<String>> exceptions) {
        for (String curPath : boostIncludePaths) {

            String functionHppPath = curPath + File.separator + "function.hpp";
            File functionHppFile = new File(functionHppPath);
            if (!functionHppFile.exists()) {
                return;
            }
            String functionTemplateHppPath = curPath + File.separator + "function" + File.separator + "function_template.hpp";
            exceptions.put(functionTemplateHppPath, new WeightedObject<>(0, functionHppPath));
        }
    }
}
