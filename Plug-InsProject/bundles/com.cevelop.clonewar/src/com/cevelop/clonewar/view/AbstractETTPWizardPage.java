package com.cevelop.clonewar.view;

import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.widgets.Composite;

import com.cevelop.clonewar.refactorings.CloneWarRefactoring;
import com.cevelop.clonewar.transformation.Transform;
import com.cevelop.clonewar.transformation.configuration.TransformConfiguration;


/**
 * Base wizard page from the clonewar refactoring.
 *
 * @author ythrier(at)hsr.ch
 */
public abstract class AbstractETTPWizardPage extends UserInputWizardPage {

    private TransformConfiguration config_;
    private Composite              page_;

    /**
     * Create the wizard page.
     *
     * @param name
     * Name of the page.
     */
    public AbstractETTPWizardPage(String name) {
        super(name);
    }

    /**
     * Get the transformation.
     *
     * @return Transformation.
     */
    private Transform getTransformation() {
        return getCWRefactoring().getTransformation();
    }

    /**
     * Get the clonewar refactoring.
     *
     * @return Refactoring.
     */
    private CloneWarRefactoring getCWRefactoring() {
        return (CloneWarRefactoring) getRefactoring();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createControl(Composite parent) {
        this.config_ = getTransformation().getConfig();
        this.page_ = createPageComposite(parent);
        setControl(page_);
        createComponents();
    }

    /**
     * Get the page composite.
     *
     * @return Page composite.
     */
    protected Composite getPage() {
        return page_;
    }

    /**
     * Return the config.
     *
     * @return Configuration.
     */
    protected TransformConfiguration getConfig() {
        return config_;
    }

    /**
     * Create the components and add them to the page composite.
     */
    protected abstract void createComponents();

    /**
     * Create the page composite in which all components are added.
     *
     * @param parent
     * Parent composite.
     * @return Composite created.
     */
    protected abstract Composite createPageComposite(Composite parent);
}
