package com.cevelop.conanator.preferences;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.databinding.viewers.ViewerSupport;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.cevelop.conanator.Activator;
import com.cevelop.conanator.models.ConanProfile;
import com.cevelop.conanator.models.Entry;
import com.cevelop.conanator.models.Line;
import com.cevelop.conanator.models.ProfileSection;
import com.cevelop.conanator.utility.NamedSectionParser;


public class ProfileListViewer {

    public enum Section {
        settings, options, scopes, env,
    }

    private ListViewer                 viewer;
    private WritableList<ConanProfile> currentInput;
    private List<ConanProfile>         defaultInput     = new ArrayList<>();
    private List<ConanProfile>         profilesToUpdate = new ArrayList<>();
    private List<ConanProfile>         profilesToAdd    = new ArrayList<>();
    private List<ConanProfile>         profilesToDelete = new ArrayList<>();
    private Button                     editButton;
    private Button                     deleteButton;
    private static final String        PROFILES_FOLDER  = System.getProperty("user.home") + "/.conan/profiles/";
    private Shell                      shell;
    private boolean                    presentsDefault;
    private Object                     defaultProfile;

    public ProfileListViewer(Shell shell, Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        container.setLayout(new GridLayout(2, false));

        createViewer(container);
        createButtons(container);

        performDefaults();
    }

    private void createViewer(Composite parent) {
        viewer = new ListViewer(parent);
        viewer.getList().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        viewer.addSelectionChangedListener(e -> updateButtonStates());
        currentInput = loadProfiles();
        ViewerSupport.bind(viewer, currentInput, BeanProperties.values(new String[] { "name" }));
        for (ConanProfile p : currentInput)
            defaultInput.add(p.clone());
    }

    private void createButtons(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new FillLayout(SWT.VERTICAL));
        container.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false));

        createButton(container, "New...", this::addProfile);
        editButton = createButton(container, "Edit...", this::editSelected);
        deleteButton = createButton(container, "Delete", this::deleteSelected);
        updateButtonStates();
    }

    private Button createButton(Composite container, String label, Runnable actionHandler) {
        Button button = new Button(container, SWT.PUSH);
        button.setText(label);
        button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                actionHandler.run();
            }
        });
        return button;
    }

    protected void updateButtonStates() {
        editButton.setEnabled(!viewer.getSelection().isEmpty());
        deleteButton.setEnabled(!viewer.getSelection().isEmpty());
    }

    private void addProfile() {
        InputDialog dialog = new InputDialog(shell, "Create New Profile", "Please enter the name of the new profile.\n" +
                                                                          "The profile will be located in\n" +
                                                                          "the default location as specified by Conan.", "profile", result -> {
                                                                              String errorMsg = null;
                                                                              if (result.isEmpty()) {
                                                                                  errorMsg = "Profile name is required.";
                                                                              } else if (!(isValidName(result))) {
                                                                                  errorMsg = "Not a valid name.\n" +
                                                                                             "Profile name can contain only these characters: [a-zA-Z_0-9-.]";
                                                                              } else if (containsProfile(result)) {
                                                                                  errorMsg =
                                                                                           "The profile already exists. Please choose a different name.";
                                                                              }
                                                                              return errorMsg;
                                                                          });
        if (dialog.open() == Window.OK) {
            String name = dialog.getValue();
            ConanProfile profile = new ConanProfile(name, new File(PROFILES_FOLDER + name));
            for (Section section : Section.values()) {
                profile.getSections().add(new ProfileSection(section.name()));
            }
            currentInput.add(profile);
            profilesToAdd.add(profile);
            profilesToUpdate.add(profile);
            presentsDefault = false;
        }
    }

    private boolean isValidName(String name) {
        return Pattern.compile("[\\w-.]+").matcher(name).matches();
    }

    private boolean containsProfile(String name) {
        for (ConanProfile p : currentInput) {
            if (p.getName().equals(name)) return true;
        }
        return false;
    }

    private void editSelected() {
        IStructuredSelection selection = viewer.getStructuredSelection();
        ConanProfile profile = currentInput.get(currentInput.indexOf(selection.getFirstElement()));
        ConanProfile clone = profile.clone();
        EditProfileDialog dialog = new EditProfileDialog(shell, clone, new ArrayList<>(currentInput));

        if (dialog.open() == Window.OK) {
            currentInput.set(currentInput.indexOf(profile), clone);
            profilesToUpdate.add(clone);
            presentsDefault = false;
        }
    }

    private void deleteSelected() {
        @SuppressWarnings("unchecked")
        List<ConanProfile> toDelete = viewer.getStructuredSelection().toList();
        for (ConanProfile profile : toDelete) {
            if (profile.getName().equals(defaultProfile)) {
                MessageDialog dialog = new MessageDialog(shell, "Warning: Deleting Default Profile", null,
                        "You are about to delete your current default profile.\n Are you sure?", MessageDialog.WARNING, 0, "OK", "Cancel");
                if (dialog.open() != 0) {
                    break;
                }
            }
            currentInput.remove(profile);
            profilesToDelete.add(profile);
        }
        presentsDefault = false;
    }

    private WritableList<ConanProfile> loadProfiles() {
        WritableList<ConanProfile> profiles = new WritableList<>();
        File[] files = new File(PROFILES_FOLDER).listFiles();

        if (files != null) {
            for (File file : files) {
                ConanProfile profile = new ConanProfile(file.getName(), file);
                loadProfile(profile);
                profiles.add(profile);
            }
        }

        return profiles;
    }

    private void loadProfile(ConanProfile profile) {
        if (profile.getFile().exists()) {
            try {
                NamedSectionParser<Section> parser = new NamedSectionParser<>(profile.getFile());

                Pattern include = Pattern.compile("include\\((\\S+)\\)");
                Matcher m;
                List<String> namelessSection = parser.getNamelessSection();
                if (namelessSection != null) {
                    List<Entry> variables = new ArrayList<>();
                    for (String line : namelessSection) {
                        if ((m = include.matcher(line)).matches()) {
                            profile.getDependencies().add(m.group(1));
                        } else {
                            String[] pair = line.split("=", 2);
                            variables.add(new Entry(pair[0], pair[1]));
                        }
                    }
                    profile.setVariables(variables);
                }
                List<ProfileSection> sections = new ArrayList<>();
                for (Section section : Section.values()) {
                    List<String> values = parser.getSection(section);
                    if (values != null) {
                        ProfileSection profileSection = new ProfileSection(section.name(), values);
                        sections.add(profileSection);
                    }
                }
                profile.setSections(sections);
                List<String> values = parser.getSection("build_requires");
                if (values != null) {
                    List<Line> buildRequires = new ArrayList<>();
                    for (String s : values)
                        buildRequires.add(new Line(s));
                    profile.setBuildRequires(buildRequires);
                }
            } catch (IOException e) {
                Activator.log(e);
            }
        }
    }

    public void performOk() {
        if (presentsDefault) return;

        File profilesFolder = new File(PROFILES_FOLDER);
        if (!profilesFolder.exists()) profilesFolder.mkdirs();

        for (ConanProfile profile : profilesToAdd) {
            File file = new File(PROFILES_FOLDER + profile.getName());
            profile.setFile(file);
            try {
                file.createNewFile();
            } catch (IOException e) {
                Activator.log(e);
            }
        }
        profilesToAdd.clear();

        for (ConanProfile profile : profilesToUpdate) {
            try {
                NamedSectionParser<Section> parser = new NamedSectionParser<>(profile.getFile());
                updateNamelessSection(profile, parser);
                updateSections(profile, parser);
                parser.save();
            } catch (IOException e) {
                e.printStackTrace();
                Activator.log(e);
            }
        }
        profilesToUpdate.clear();

        for (ConanProfile profile : profilesToDelete) {
            profile.getFile().delete();
        }
        profilesToDelete.clear();

        defaultInput = new ArrayList<>();
        for (ConanProfile p : currentInput)
            defaultInput.add(p.clone());
        presentsDefault = true;
    }

    private void updateNamelessSection(ConanProfile profile, NamedSectionParser<Section> parser) {
        List<String> namelessSection = new ArrayList<>();
        for (String dep : profile.getDependencies())
            namelessSection.add("include(" + dep + ")");
        List<Entry> variables = profile.getVariables();
        for (Entry entry : variables)
            namelessSection.add(String.join("=", entry.getKey(), entry.getValue()));
        parser.setNamelessSection(namelessSection);
    }

    private void updateSections(ConanProfile profile, NamedSectionParser<Section> parser) {
        List<Line> buildRequires = profile.getBuildRequires();
        List<String> lines = new ArrayList<>();
        for (Line l : buildRequires)
            lines.add(l.getValue());
        parser.setSection("build_requires", lines);
        for (ProfileSection section : profile.getSections()) {
            List<String> updatedSection = section.getEntries().stream().map(entry -> String.join("=", entry.getKey(), entry.getValue())).collect(
                    Collectors.toList());
            parser.setSection(section.getName(), updatedSection);
        }
    }

    public void performDefaults() {
        if (presentsDefault) return;

        currentInput.clear();
        for (ConanProfile p : defaultInput)
            currentInput.add(p.clone());

        profilesToAdd.clear();
        profilesToUpdate.clear();
        profilesToDelete.clear();

        presentsDefault = true;
        updateButtonStates();

    }

    public void subscribe(IListChangeListener<ConanProfile> listener) {
        currentInput.addListChangeListener(listener);
    }

    public String[] getProfileNames() {
        return currentInput.stream().map(p -> p.getName()).toArray(String[]::new);
    }

    public void setDefaultProfile(String profile) {
        this.defaultProfile = profile;
    }
}
