package com.cevelop.templator.plugin.view.interfaces;

import java.util.List;

import com.cevelop.templator.plugin.view.components.Connection;


public interface INode {

    List<Connection> getConnections();

    boolean isRoot();
}
