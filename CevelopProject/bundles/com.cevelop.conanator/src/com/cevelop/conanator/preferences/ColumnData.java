package com.cevelop.conanator.preferences;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.eclipse.jface.viewers.ColumnLabelProvider;


public class ColumnData<T> {

    public final ColumnLabelProvider labelProvider;

    public ColumnData(String title, int width, Function<T, String> getter, BiConsumer<T, String> setter, Predicate<T> canEdit) {
        this(null, title, width, getter, setter, canEdit);
    }

    public final String                title;
    public final int                   width;
    public final Function<T, String>   getter;
    public final BiConsumer<T, String> setter;
    public final Predicate<T>          canEdit;

    public ColumnData(ColumnLabelProvider labelProvider, String title, int width, Function<T, String> getter, BiConsumer<T, String> setter,
                      Predicate<T> canEdit) {
        this.labelProvider = labelProvider;
        this.title = title;
        this.width = width;
        this.setter = setter;
        this.getter = getter;
        this.canEdit = canEdit;
    }
}
