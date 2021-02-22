package com.cevelop.aliextor.tests.tests;

import java.util.ArrayList;
import java.util.Properties;

import org.eclipse.ltk.core.refactoring.RefactoringContext;

import com.cevelop.aliextor.wizard.helper.Pair;


public class AliasTemplateTest extends SimpleRefactoringTest {

    private boolean                          userWantsTemplateAlias;
    private ArrayList<Pair<String, Integer>> selectedNames;

    @Override
    protected void configureTest(Properties properties) {
        super.configureTest(properties);
        selectedNames = new ArrayList<>();

        userWantsTemplateAlias = Boolean.parseBoolean(properties.getProperty("userWantsTemplateAlias", "false"));
        String name = "";
        for (int i = 0; !name.contentEquals("null"); i++) {
            name = properties.getProperty("userSelected" + i, "null");
            if (name.contentEquals("null")) {
                break;
            }
            selectedNames.add(Pair.of(name, occurrence(name, properties.getProperty("occurence" + i, "null"))));
        }
    }

    private int occurrence(String name, String occurrence) {
        return occurrence.contentEquals("null") ? countOccurrence(name) : Integer.parseInt(occurrence);
    }

    private int countOccurrence(String name) {
        int i;
        for (i = 0; selectedNames.contains(Pair.of(name, i)); i++);
        return i;
    }

    @Override
    protected void simulateUserInput(RefactoringContext context) {
        super.simulateUserInput(context);

        refactoring.setUserWantsTemplateAlias(userWantsTemplateAlias);
        refactoring.setSelectedNamesInRefactoring(selectedNames);
    }

}
