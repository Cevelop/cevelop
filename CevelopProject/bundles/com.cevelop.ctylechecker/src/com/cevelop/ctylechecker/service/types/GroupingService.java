package com.cevelop.ctylechecker.service.types;

import com.cevelop.ctylechecker.domain.IGrouping;
import com.cevelop.ctylechecker.domain.types.Grouping;
import com.cevelop.ctylechecker.service.IGroupingService;


public class GroupingService implements IGroupingService {

    @Override
    public IGrouping createGroup(String pName, Boolean pEnabled) {
        return new Grouping(pName, pEnabled);
    }

}
