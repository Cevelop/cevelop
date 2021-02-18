package com.cevelop.clonewar.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;

import com.cevelop.clonewar.transformation.ETTPFunctionTransform;
import com.cevelop.clonewar.transformation.ETTPTypeTransform;
import com.cevelop.clonewar.transformation.Transform;


/**
 * Factory to create the input pages for a specific transformation type. This
 * way, the wizard doesn't need to manually create the input pages with checking
 * the transformation type.
 *
 * @author ythrier(at)hsr.ch
 */
public class PageFactory {

    private Map<Class<?>, List<UserInputWizardPage>> registry_ = new HashMap<>();

    /**
     * Create the page factory.
     */
    public PageFactory() {
        putPagesForExtractTTPFunction();
        putPagesForExtractTTPType();
    }

    /**
     * Insert the pages for the extract typename template parameter in the
     * registry.
     */
    private void putPagesForExtractTTPType() {
        List<UserInputWizardPage> ttpTypePages = createPageList();
        ttpTypePages.add(new ETTPTypeSelectionWizardPage());
        register(ETTPTypeTransform.class, ttpTypePages);
    }

    /**
     * Returns the input pages for a given transformation.
     *
     * @param transformation
     * Transformation for which the factory should create input
     * pages.
     * @return Input pages for the transformation.
     */
    public List<UserInputWizardPage> createPagesFor(Transform transformation) {
        if (transformation == null) {
            return new ArrayList<>();
        }
        return registry_.get(transformation.getClass());
    }

    /**
     * Insert the pages for the extract typename template parameter in the
     * registry.
     */
    private void putPagesForExtractTTPFunction() {
        List<UserInputWizardPage> ttpFunctionPages = createPageList();
        // ttpFunctionPages.add(new
        // ExtractSelectionWizardPage(Messages.EXTRACTION_PAGE_NAME,false));
        ttpFunctionPages.add(new ETTPFunctionSelectionWizardPage());
        register(ETTPFunctionTransform.class, ttpFunctionPages);
    }

    /**
     * Factory method to create a list of {@link UserInputWizardPage}s.
     *
     * @return List for input pages.
     */
    private ArrayList<UserInputWizardPage> createPageList() {
        return new ArrayList<>();
    }

    /**
     * Register a list in the registry.
     *
     * @param key
     * Key to register the pages with.
     * @param pageList
     * List of the pages to register.
     */
    private void register(Class<?> key, List<UserInputWizardPage> pageList) {
        registry_.put(key, pageList);
    }
}
