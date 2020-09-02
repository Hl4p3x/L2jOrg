// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.conditions;

import org.l2j.gameserver.model.conditions.ConditionFactory;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.conditions.ICondition;

public class NpcLevelCondition implements ICondition
{
    private final int _minLevel;
    private final int _maxLevel;
    
    private NpcLevelCondition(final StatsSet params) {
        this._minLevel = params.getInt("minLevel");
        this._maxLevel = params.getInt("maxLevel");
    }
    
    public boolean test(final Creature creature, final WorldObject object) {
        return GameUtils.isNpc(object) && ((Creature)object).getLevel() >= this._minLevel && ((Creature)object).getLevel() < this._maxLevel;
    }
    
    public static class Factory implements ConditionFactory
    {
        public ICondition create(final StatsSet data) {
            return (ICondition)new NpcLevelCondition(data);
        }
        
        public String conditionName() {
            return "NpcLevel";
        }
    }
}
