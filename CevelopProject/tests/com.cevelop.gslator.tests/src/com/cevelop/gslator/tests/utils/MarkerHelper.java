package com.cevelop.gslator.tests.utils;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IMarker;


public class MarkerHelper {

    public static List<IMarker> sortedMarkers(IMarker[] markers) {
        List<IMarker> list = Arrays.asList(markers);
        list.sort((arg0, arg1) -> {
            int nr0 = arg0.getAttribute(IMarker.CHAR_START, -1);
            int nr1 = arg1.getAttribute(IMarker.CHAR_START, -1);
            if (nr0 == nr1) return 0;
            return nr0 < nr1 ? -1 : 1;
        });
        return list;
    }
}
