package com.cevelop.codeanalysator.core.guideline;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.RegistryFactory;

import com.cevelop.codeanalysator.core.helper.CoreIdHelper;


public class GuidelineRegistry {

    private final List<IGuideline> guidelines;

    public GuidelineRegistry() {
        guidelines = new ArrayList<>();
    }

    public void loadExtensions() {
        IExtensionRegistry registry = RegistryFactory.getRegistry();
        IConfigurationElement[] guidelineExtensions = registry.getConfigurationElementsFor(CoreIdHelper.GUIDELINE_EXTENSION_ID);
        for (IConfigurationElement contribution : guidelineExtensions) {
            try {
                Object extension = contribution.createExecutableExtension("impl");
                IGuideline guideline = (IGuideline) extension;
                registerGuideline(guideline);
            } catch (CoreException e) {
                e.printStackTrace();
            }
        }
    }

    private void registerGuideline(IGuideline guideline) {
        boolean alreadyRegistered = getGuidelineById(guideline.getId()).isPresent();
        if (alreadyRegistered) {
            throw new IllegalArgumentException(String.format("guideline already registered. [%s]", guideline.getId()));
        }

        guidelines.add(guideline);
    }

    public List<IGuideline> getGuidelines() {
        return guidelines;
    }

    private Optional<IGuideline> getGuidelineById(String guidelineId) {
        return guidelines.stream() //
                .filter(guideline -> guideline.getId().equals(guidelineId)) //
                .findFirst();
    }
}
