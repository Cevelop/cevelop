package com.cevelop.aliextor.wizard.helper;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.cevelop.aliextor.ast.ASTHelper;
import com.cevelop.aliextor.ast.ASTHelper.Type;
import com.cevelop.aliextor.ast.BaseASTVisitor;
import com.cevelop.aliextor.ast.FindQualifiedName;


public class TemplateNameHelper {

    private HashMap<Pair<String, Integer>, ArrayList<Pair<String, Integer>>> namesWithTempParam;
    private ArrayList<Pair<String, Integer>>                                 orderingOfNames;
    private ArrayList<DummyTableItem>                                        listOfDummyTableItems;
    private String                                                           selectedNode;

    public TemplateNameHelper(ArrayList<IASTNode> names, IASTNode selectedNode) {
        namesWithTempParam = new HashMap<>();
        orderingOfNames = new ArrayList<>();
        listOfDummyTableItems = new ArrayList<>();
        if (names != null) {
            fillHashMap(names);
        }
        this.selectedNode = selectedNode != null ? selectedNode.toString() : null;
    }

    private void fillHashMap(ArrayList<IASTNode> names) {
        HashMap<Pair<String, Integer>, ArrayList<String>> namesWithTempParam = new HashMap<>();
        for (int i = ignoreFirstName(names); i < names.size(); i++) {
            IASTNode temp = names.get(i);
            ArrayList<IASTNode> qualiName = new ArrayList<>();
            while (temp != null) {
                qualiName.add(temp);
                if (++i < names.size()) {
                    temp = names.get(i);
                } else {
                    break;
                }
            }
            String name = createQualifiedName(qualiName);
            Pair<String, Integer> pair = Pair.of(name, countOccurrence(name));

            orderingOfNames.add(pair);
            namesWithTempParam.put(pair, findTemplateParameters(names.get(i - 1)));
        }
        convertNamesWithTemplateParam(namesWithTempParam);
    }

    private void convertNamesWithTemplateParam(HashMap<Pair<String, Integer>, ArrayList<String>> namesWithTempParam) {
        for (Pair<String, Integer> pair : orderingOfNames) {
            ArrayList<String> paramsAsString = namesWithTempParam.get(pair);
            ArrayList<Pair<String, Integer>> list = new ArrayList<>();
            this.namesWithTempParam.put(pair, list);
            if (paramsAsString != null) {
                addEntry(pair, paramsAsString, list);
            }
        }
    }

    private void addEntry(Pair<String, Integer> pair, ArrayList<String> paramsAsString, ArrayList<Pair<String, Integer>> list) {
        for (int j = 0; j < paramsAsString.size(); j++) {
            int i = orderingOfNames.indexOf(pair) + 1 + j;
            if (i < orderingOfNames.size()) {
                Pair<String, Integer> entry = orderingOfNames.get(i);
                if (entry.getLeft().contentEquals(paramsAsString.get(j))) {
                    list.add(entry);
                }
            }
        }
    }

    private int ignoreFirstName(ArrayList<IASTNode> list) {
        int index = 0;
        IASTNode node = list.get(index++);
        for (; node != null; index++) {
            node = list.get(index);
        }
        return index;
    }

    private String createQualifiedName(ArrayList<IASTNode> list) {
        if (!list.isEmpty()) {
            StringBuilder string = new StringBuilder(list.get(0).getRawSignature());
            for (int i = 1; i < list.size(); i++) {
                string.append("::" + list.get(i).getRawSignature());
            }
            return string.toString();
        } else {
            return null;
        }
    }

    private int countOccurrence(String name) {
        int i;
        for (i = 0; orderingOfNames.contains(Pair.of(name, i)); i++) {
            ;
        }
        return i;
    }

    private ArrayList<String> findTemplateParameters(IASTNode name) {
        if (ASTHelper.isType(name, Type.CPPASTName)) {
            ArrayList<String> list = new ArrayList<>();
            IASTNode parent = name.getParent();
            if (ASTHelper.isType(parent, Type.ICPPASTTemplateId)) {
                BaseASTVisitor visitor;
                ICPPASTTemplateId templateId = (ICPPASTTemplateId) parent;
                IASTNode args[] = templateId.getTemplateArguments();
                for (IASTNode node : args) {
                    visitor = new FindQualifiedName(null);
                    node.accept(visitor);
                    list.add(createQualifiedName(visitor.getOccurrences()));
                }
            }
            return list.isEmpty() ? null : list;
        } else {
            return null;
        }
    }

    public void addTableValues(Table table) {
        for (Pair<String, Integer> pair : orderingOfNames) {
            createTableItem(table, pair);
        }
    }

    private void createTableItem(Table table, Pair<String, Integer> pair) {
        createTableItem(table, pair, null);
    }

    private void createTableItem(Table table, Pair<String, Integer> pair, DummyTableItem dummy) {
        TableItem item = new TableItem(table, SWT.NONE);
        int i = 1;
        item.setChecked(dummy != null ? dummy.isChecked() : false);
        item.setText(i++, pair.getLeft());
        item.setData(Pair.of(pair, namesWithTempParam.get(pair)));

        Pair<String, Integer> type = getTypeIfIsParameter(table, pair);
        item.setText(i++, type != null ? type.getLeft() : "-");

        item.setText(i++, pair.getLeft());

        DummyTableItem dummyItem = createDummyTableItem(item);
        if (listOfDummyTableItems.size() != orderingOfNames.size()) {
            listOfDummyTableItems.add(dummyItem);
        }
    }

    private Pair<String, Integer> getTypeIfIsParameter(Table table, Pair<String, Integer> pair) {
        TableItem items[] = table.getItems();
        for (int i = items.length - 1; i >= 0; i--) {
            Pair<Pair<String, Integer>, ArrayList<Pair<String, Integer>>> entry = getDataOf(items[i]);
            ArrayList<Pair<String, Integer>> paramListAsStrings = namesWithTempParam.get(entry.getLeft());
            if (paramListAsStrings != null && paramListAsStrings.contains(pair)) {
                return entry.getLeft();
            }
        }
        return null;
    }

    public void setItemsNew(Table table, TableItem item) {
        ArrayList<Pair<String, Integer>> parameters = getDataOf(item).getRight();
        if (item.getChecked()) {
            removeItemsIfNecessary(table, parameters);
        } else {
            addItemsIfNecessary(table);
        }
    }

    private void addItemsIfNecessary(Table table) {
        saveSelectionsInTableItems(table);
        table.removeAll();
        TableItem newItem = null;
        int i = 0;
        for (DummyTableItem tableItem : listOfDummyTableItems) {
            createTableItem(table, tableItem.getData().getLeft(), tableItem);
            newItem = table.getItem(i++);
        }
        if (newItem != null) {
            removeItemsIfNecessary(table, getDataOf(newItem).getRight());
        }
    }

    protected void saveSelectionsInTableItems(Table table) {
        removeSelectionsFromDummyTableItems();
        for (TableItem tableItem : table.getItems()) {
            if (tableItem.getChecked()) {
                for (DummyTableItem dummyItem : listOfDummyTableItems) {
                    if (dummyItem.equalsTableItem(tableItem)) {
                        dummyItem.setChecked(tableItem.getChecked());
                    }
                }
            }
        }
    }

    private void removeSelectionsFromDummyTableItems() {
        for (DummyTableItem dummyItem : listOfDummyTableItems) {
            dummyItem.setChecked(false);
        }
    }

    private DummyTableItem createDummyTableItem(TableItem item) {
        DummyTableItem dummyItem = new DummyTableItem();
        int i = 1;
        dummyItem.setChecked(item.getChecked());
        dummyItem.setText(i, item.getText(i++));
        dummyItem.setText(i, item.getText(i++));
        dummyItem.setText(i, item.getText(i));
        dummyItem.setData(getDataOf(item));
        return dummyItem;
    }

    private void removeItemsIfNecessary(Table table, ArrayList<Pair<String, Integer>> templateParams) {
        if (templateParams.isEmpty()) {
            return;
        }
        for (Pair<String, Integer> pair : templateParams) {
            Pair<Pair<String, Integer>, ArrayList<Pair<String, Integer>>> entry = getDataOf(findItem(table, pair));
            if (entry != null) {
                removeItemsIfNecessary(table, entry.getRight());
                for (int i = 0; i < table.getItemCount(); i++) {
                    Pair<Pair<String, Integer>, ArrayList<Pair<String, Integer>>> tempEntry = getDataOf(table.getItem(i));
                    if (tempEntry != null && tempEntry.getLeft().equals(pair)) {
                        table.remove(i);
                    }
                }
            }
        }
    }

    private TableItem findItem(Table table, Pair<String, Integer> pair) {
        for (int i = 0; i < table.getItemCount(); i++) {
            Pair<Pair<String, Integer>, ArrayList<Pair<String, Integer>>> entry = getDataOf(table.getItem(i));
            if (entry.getLeft().equals(pair)) {
                return table.getItem(i);
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Pair<Pair<String, Integer>, ArrayList<Pair<String, Integer>>> getDataOf(TableItem item) {
        return item != null ? (Pair<Pair<String, Integer>, ArrayList<Pair<String, Integer>>>) item.getData() : null;
    }

    public String getAliasTemplate(ArrayList<Pair<String, Integer>> selectedNames, String aliasname) {
        if (selectedNames.isEmpty()) {
            return "Alias:\n\t";
        }
        StringBuilder prefix = createPrefix(selectedNames, aliasname);
        StringBuilder type = new StringBuilder(selectedNode);
        int i = selectedNames.size() > 1 ? 1 : 0;
        for (Pair<String, Integer> pair : selectedNames) {
            int indexBegin = getIndexOf(type, pair.getLeft(), pair.getRight());
            int indexEnd = indexBegin + pair.getLeft().length();
            type.replace(indexBegin, indexEnd, i == 0 ? "T" : "T" + i++);
            removeParameters(type, indexBegin + 1);
        }
        prefix.append(type);
        return prefix.toString();
    }

    private void removeParameters(StringBuilder type, int indexBegin) {
        if (type.charAt(indexBegin) != '<') {
            return;
        }
        int begin = indexBegin;
        int end = indexBegin + 1;
        for (int i = begin + 1; i < type.length(); i++) {
            if (type.charAt(i) == '>') {
                end = i;
                break;
            }
        }
        type.replace(begin, end, "");
    }

    private int getIndexOf(StringBuilder sb, String left, Integer right) {
        int index = selectedNode.indexOf('<');
        for (int i = 0; i <= right; i++) {
            int temp = sb.indexOf(left, index + 1);
            if (temp < sb.length() && temp != -1) {
                index = temp;
            }
        }
        return index;
    }

    private StringBuilder createPrefix(ArrayList<Pair<String, Integer>> selectedNames, String aliasName) {
        StringBuilder sb = new StringBuilder("Alias:\n\ttemplate<typename T");
        if (selectedNames.size() > 1) {
            int i = 1;
            sb.append(i++);
            for (; i <= selectedNames.size(); i++) {
                sb.append(", typename T" + i);
            }
        }
        sb.append(">\n\tusing " + aliasName + " = ");
        return sb;
    }

}
