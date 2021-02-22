package com.cevelop.constificator.core.deciders.decision;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.cdt.core.dom.ast.IASTNode;

import com.cevelop.constificator.core.util.type.Truelean;


public class DecisionFactory {

    public static Decision makeDecision(Class<? extends Decision> type, IASTNode node, Truelean decision) {
        Decision instance = null;

        try {
            Constructor<? extends Decision> ctor = type.getConstructor(IASTNode.class);
            instance = ctor.newInstance(node);
            instance.decide(decision);
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            e.printStackTrace();
        }

        return instance;
    }

}
