// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.StatsSet;

public interface ConditionFactory
{
    ICondition create(final StatsSet data);
    
    String conditionName();
}
