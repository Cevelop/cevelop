package com.cevelop.tdd.refactorings;

import com.cevelop.tdd.helpers.IdHelper.RefactoringId;

import ch.hsr.ifs.iltis.core.core.functional.functions.Function;

import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoringContribution;


public abstract class AbstractTddRefactoringContribution extends CRefactoringContribution<RefactoringId> {

    @Override
    protected Function<String, RefactoringId> getFromStringMethod() {
        return RefactoringId::of;
    }
}
