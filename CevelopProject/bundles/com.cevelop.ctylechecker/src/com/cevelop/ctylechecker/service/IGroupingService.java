package com.cevelop.ctylechecker.service;

import com.cevelop.ctylechecker.domain.IGrouping;


public interface IGroupingService {

    IGrouping createGroup(String pName, Boolean pEnabled);
}
