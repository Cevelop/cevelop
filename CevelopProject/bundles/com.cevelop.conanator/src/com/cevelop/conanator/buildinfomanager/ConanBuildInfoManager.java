package com.cevelop.conanator.buildinfomanager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IResourceInfo;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.core.resources.IProject;
import org.osgi.service.prefs.BackingStoreException;

import com.cevelop.conanator.utility.NamedSectionParser;


public class ConanBuildInfoManager {

    private enum Section {
        includedirs, libdirs, libs, defines, cflags, cppflags, sharedlinkflags, exelinkflags
    }

    private static final String                 FLAGS_DELIMITER = " ";
    private static final Map<Section, IdHolder> sectionInfo     = ((Supplier<Map<Section, IdHolder>>) (() -> {
                                                                    Map<Section, IdHolder> sectionInfo = new HashMap<>();

                                                                    String cppCompilerId = "cdt.managedbuild.tool.gnu.cpp.compiler";

                                                                    sectionInfo.put(Section.includedirs, new IdHolder(cppCompilerId,
                                                                            "gnu.cpp.compiler.option.include.paths"));

                                                                    sectionInfo.put(Section.defines, new IdHolder(cppCompilerId,
                                                                            "gnu.cpp.compiler.option.preprocessor.def"));

                                                                    sectionInfo.put(Section.cppflags, new IdHolder(cppCompilerId,
                                                                            "gnu.cpp.compiler.option.other.other"));

                                                                    sectionInfo.put(Section.cflags, new IdHolder(
                                                                            "cdt.managedbuild.tool.gnu.c.compiler",
                                                                            "gnu.c.compiler.option.misc.other"));

                                                                    String linkerId = "cdt.managedbuild.tool.gnu.cpp.linker";

                                                                    sectionInfo.put(Section.libdirs, new IdHolder(linkerId,
                                                                            "gnu.cpp.link.option.paths"));

                                                                    sectionInfo.put(Section.libs, new IdHolder(linkerId, "gnu.cpp.link.option.libs"));

                                                                    sectionInfo.put(Section.exelinkflags, new IdHolder(linkerId,
                                                                            "gnu.cpp.link.option.flags"));

                                                                    sectionInfo.put(Section.sharedlinkflags, new IdHolder(linkerId,
                                                                            "gnu.cpp.link.option.flags"));

                                                                    return sectionInfo;
                                                                })).get();

    public static void install(IProject project, File file) throws FileNotFoundException, IOException, BuildException, BackingStoreException {
        NamedSectionParser<Section> parser = new NamedSectionParser<>(file);
        IManagedBuildInfo buildInfo = ManagedBuildManager.getBuildInfo(project);
        IConfiguration config = buildInfo.getDefaultConfiguration();
        IToolChain toolChain = config.getToolChain();
        IResourceInfo resourceInfo = config.getResourceInfos()[0];

        for (Section section : Section.values()) {
            List<String> newConanValues = parser.getSection(section);
            if (newConanValues == null) {
                newConanValues = new ArrayList<>();
            }
            updateOption(project, toolChain, resourceInfo, section, newConanValues);
        }

        ManagedBuildManager.saveBuildInfo(project, true);
        CCorePlugin.getIndexManager().reindex(CoreModel.getDefault().create(project));
    }

    private static void updateOption(IProject project, IToolChain toolChain, IResourceInfo resourceInfo, Section section, List<String> newConanValues)
            throws BuildException, BackingStoreException {
        IdHolder holder = sectionInfo.get(section);
        ITool tool = toolChain.getToolsBySuperClassId(holder.toolId)[0];
        IOption option = tool.getOptionBySuperClassId(holder.optionId);

        ConanBuildInfoPrefStore prefStore = new ConanBuildInfoPrefStore(project);
        Collection<String> storedBuildInfo = prefStore.getStoredBuildInfo(section.name());
        if (option.getBasicValueType() == IOption.STRING_LIST) {
            Collection<String> optionValues = new ArrayList<>(Arrays.asList(option.getBasicStringListValue()));
            optionValues.removeAll(storedBuildInfo);
            optionValues.addAll(newConanValues);
            ManagedBuildManager.setOption(resourceInfo, tool, option, optionValues.toArray(new String[optionValues.size()]));
        } else {
            String[] split = option.getStringValue().split(FLAGS_DELIMITER);
            Collection<String> optionValues = new ArrayList<>(Arrays.asList(split));
            optionValues.removeAll(storedBuildInfo);
            optionValues.addAll(newConanValues);
            ManagedBuildManager.setOption(resourceInfo, tool, option, String.join(FLAGS_DELIMITER, optionValues));
        }

        prefStore.storeBuildInfo(section.name(), newConanValues);
    }

    private static class IdHolder {

        public String toolId;
        public String optionId;

        public IdHolder(String toolId, String optionId) {
            this.toolId = toolId;
            this.optionId = optionId;
        }
    }
}
