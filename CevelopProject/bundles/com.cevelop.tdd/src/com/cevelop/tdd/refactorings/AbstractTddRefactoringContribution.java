package com.cevelop.tdd.refactorings;

import ch.hsr.ifs.iltis.core.functional.functions.Function;

import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoringContribution;

import com.cevelop.tdd.helpers.IdHelper.RefactoringId;


public abstract class AbstractTddRefactoringContribution extends CRefactoringContribution<RefactoringId> {

    @Override
    protected Function<String, RefactoringId> getFromStringMethod() {
        return RefactoringId::of;
    }
}
