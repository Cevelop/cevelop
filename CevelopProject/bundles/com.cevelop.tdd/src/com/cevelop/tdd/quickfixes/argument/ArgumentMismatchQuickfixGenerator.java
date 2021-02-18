/*******************************************************************************
 * Copyright (c) 2011, IFS Institute for Software, HSR Rapperswil,
 * Switzerland, http://ifs.hsr.ch
 *
 * Permission to use, copy, and/or distribute this software for any
 * purpose without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *******************************************************************************/
package com.cevelop.tdd.quickfixes.argument;

import org.eclipse.cdt.ui.CDTSharedImages;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.core.resources.IMarker;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;

import com.cevelop.tdd.Activator;
import com.cevelop.tdd.infos.ArgumentMismatchInfo;

import ch.hsr.ifs.iltis.cpp.core.resources.info.MarkerInfo;


public class ArgumentMismatchQuickfixGenerator implements IMarkerResolutionGenerator2 {

    private static IMarkerResolution[] MARKER_RESOLUTION_TYPE          = new IMarkerResolution[] {};
    public static final String         ASSIGN_SEPARATOR                = "==";
    public static final String         CANDIDATE_SEPARATOR             = ":candidate ";
    public static final String         PARAMETERS                      = "targetParameters" + ASSIGN_SEPARATOR;
    public static final String         ADD_ARGUMENTS                   = "addArguments" + ASSIGN_SEPARATOR;
    public static final String         REMOVE_ARGUMENTS                = "removeArguments" + ASSIGN_SEPARATOR;
    public static final String         SEPARATOR                       = "°";
    private static final String        IMG_OBJS_CORRECTION_REMOVE_PATH = "obj16/remove_correction.gif";

    @Override
    public IMarkerResolution[] getResolutions(IMarker marker) {
        MutableList<IMarkerResolution> result = Lists.mutable.empty();
        ArgumentMismatchInfo info = MarkerInfo.fromCodanProblemMarker(ArgumentMismatchInfo::new, marker);
        String[] candidates = info.candidates.split(CANDIDATE_SEPARATOR);
        String funcName = info.argumentName;
        for (int i = 0; i < candidates.length; i++) {
            final int candidateNr = i;
            String message = getMessage(funcName, candidates[i]);
            if (!message.isEmpty()) {
                ArgumentMismatchQuickfix quickFix = getQuickFix(message);
                quickFix.configure(info.copy().also(c -> c.candidateNr = candidateNr));
                result.add(quickFix);
            }
        }
        return result.toArray(MARKER_RESOLUTION_TYPE);
    }

    private ArgumentMismatchQuickfix getQuickFix(String message) {
        Image image;
        if (message.startsWith(Messages.ArgumentMismatchQuickfixGenerator_Add)) {
            image = CDTSharedImages.getImage(CDTSharedImages.IMG_OBJS_CORRECTION_ADD);
        } else {
            image = Activator.getImageDescriptor(IMG_OBJS_CORRECTION_REMOVE_PATH).createImage();
        }
        return new ArgumentMismatchQuickfix(message, image);
    }

    private String getMessage(String function, String candidate) {
        String[] args = candidate.split(SEPARATOR);
        if (args.length < 2 || !(args[0].startsWith(ADD_ARGUMENTS) || (args[0].startsWith(REMOVE_ARGUMENTS))) //
            || !args[1].startsWith(PARAMETERS)) {
            return "";
        }

        return NLS.bind(Messages.ArgumentMismatchQuickfixGenerator_Placeholder, //
                new String[] { args[0].startsWith(ADD_ARGUMENTS) ? Messages.ArgumentMismatchQuickfixGenerator_Add
                                                                 : Messages.ArgumentMismatchQuickfixGenerator_Remove, //
                               getArgument(args[0], 1), function + "(" + getArgument(args[1], 1) + ")" });
    }

    private String getArgument(String argument, int index) {
        String[] arguments = argument.split(ASSIGN_SEPARATOR);
        return (arguments.length > index) ? arguments[index] : "";
    }

    @Override
    public boolean hasResolutions(IMarker marker) {
        return getResolutions(marker).length > 0;
    }

}
