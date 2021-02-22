package com.cevelop.includator.ui.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.jface.dialogs.MessageDialog;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.includesubstituion.WeightedObject;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.ui.solutionoperations.SuggestionSolutionOperation;


public class SuggestionTreeElement implements IChildrenProvider<SuggestionTreeElement>, IParentProvider<SuggestionTreeElement> {

    private final String                                                    lable;
    private final LinkedHashMap<SuggestionSolutionOperation, CheckableItem> checkableOperations;
    private final List<SuggestionTreeElement>                               children;
    private SuggestionTreeElement                                           parent;
    private final Suggestion<?>                                             suggestion;

    public SuggestionTreeElement(Suggestion<?> suggestion, String lable) {
        if (lable == null) {
            lable = suggestion.toString();
        }
        this.suggestion = suggestion;
        this.lable = lable;
        checkableOperations = new LinkedHashMap<>();
        children = new ArrayList<>();
        initSuggestionOperations(suggestion);
    }

    private void initSuggestionOperations(Suggestion<?> suggestion) {
        if (suggestion == null) {
            return;
        }
        for (SuggestionSolutionOperation curOperation : suggestion.getSolutionOperations()) {
            checkableOperations.put(curOperation, new CheckableItem(false));
        }
    }

    public void initSuggestionOperationsFromChildren() {
        for (SuggestionTreeElement curChild : children) {
            for (SuggestionSolutionOperation curOperation : curChild.getPossibleOperations()) {
                checkableOperations.put(curOperation, new CheckableItem(false));
            }
        }
    }

    @Override
    public String toString() {
        return lable;
    }

    public boolean isChecked(SuggestionSolutionOperation operation) {
        return checkableOperations.containsKey(operation) ? checkableOperations.get(operation).isChecked() : false;
    }

    public void setChecked(SuggestionSolutionOperation operation, boolean shouldConfirm) {
        if (!checkableOperations.containsKey(operation)) {
            return;
        }
        if (shouldConfirm && !confirmIfBigChange()) {
            return;
        }
        for (Entry<SuggestionSolutionOperation, CheckableItem> curEntry : checkableOperations.entrySet()) {
            boolean newValue = curEntry.getKey().equals(operation);
            curEntry.getValue().setChecked(newValue);
        }
        for (SuggestionTreeElement curChild : children) {
            curChild.setChecked(operation, false);
        }
    }

    /**
     * @return true if value should be changed. False if user does not want to proceed.
     */
    private boolean confirmIfBigChange() {
        if (getSuggestion() != null) {
            return true; // no confirm required for leave-node.
        }
        List<WeightedObject<SuggestionSolutionOperation>> affectedGroups = makeAffectedGroups();
        affectedGroups.remove(0);
        double changes = countChanges(affectedGroups);
        if (changes >= 5) {
            String msg = "This will consolidate many particular selections you made before. Continue?";
            return MessageDialog.openConfirm(IncludatorPlugin.getActiveWorkbenchWindow().getShell(), "Confirm big change", msg);
        }
        return true;
    }

    private double countChanges(List<WeightedObject<SuggestionSolutionOperation>> affectedGroups) {
        double total = 0;
        for (WeightedObject<SuggestionSolutionOperation> cur : affectedGroups) {
            total += cur.weight;
        }
        return total;
    }

    private List<WeightedObject<SuggestionSolutionOperation>> makeAffectedGroups() {
        LinkedHashMap<SuggestionSolutionOperation, WeightedObject<SuggestionSolutionOperation>> groups = new LinkedHashMap<>();
        for (SuggestionSolutionOperation curPossibleOperation : getPossibleOperations()) {
            groups.put(curPossibleOperation, new WeightedObject<>(0, curPossibleOperation));
        }
        addChildrenToOperationGroups(this, groups);
        ArrayList<WeightedObject<SuggestionSolutionOperation>> result = new ArrayList<>(groups.values());
        Collections.sort(result, (o1, o2) -> (int) (o2.weight - o1.weight));
        return result;
    }

    private void addChildrenToOperationGroups(SuggestionTreeElement element,
            LinkedHashMap<SuggestionSolutionOperation, WeightedObject<SuggestionSolutionOperation>> groups) {
        for (SuggestionTreeElement curChild : element.getChildren()) {
            if (curChild.getSuggestion() == null) {
                addChildrenToOperationGroups(curChild, groups);
            } else {
                groups.get(curChild.getSelectedOperation()).weight++;
            }
        }
    }

    public void inheritValueFromChildren() {
        for (SuggestionTreeElement curChild : children) {
            curChild.inheritValueFromChildren();
        }
        for (Entry<SuggestionSolutionOperation, CheckableItem> entry : checkableOperations.entrySet()) {
            inheritValueFromChildren(entry);
        }
    }

    private void inheritValueFromChildren(Entry<SuggestionSolutionOperation, CheckableItem> entry) {
        if (children.isEmpty()) {
            return;
        }
        for (SuggestionTreeElement curChild : children) {
            if (!curChild.isChecked(entry.getKey())) {
                entry.getValue().setChecked(false);
                return;
            }
        }
        entry.getValue().setChecked(true);
    }

    public boolean hasOperation(SuggestionSolutionOperation operation) {
        return checkableOperations.containsKey(operation);
    }

    public void addChild(SuggestionTreeElement child) {
        child.parent = this;
        children.add(child);
    }

    @Override
    public SuggestionTreeElement getParent() {
        return parent;
    }

    @Override
    public boolean hasChildren() {
        return !children.isEmpty();
    }

    @Override
    public SuggestionTreeElement[] getChildren() {
        return children.toArray(new SuggestionTreeElement[children.size()]);
    }

    public Collection<SuggestionSolutionOperation> getPossibleOperations() {
        return checkableOperations.keySet();
    }

    public Suggestion<?> getSuggestion() {
        return suggestion;
    }

    public SuggestionSolutionOperation getSelectedOperation() {
        for (Entry<SuggestionSolutionOperation, CheckableItem> curOperation : checkableOperations.entrySet()) {
            if (curOperation.getValue().isChecked()) {
                return curOperation.getKey();
            }
        }
        return null;
    }
}
