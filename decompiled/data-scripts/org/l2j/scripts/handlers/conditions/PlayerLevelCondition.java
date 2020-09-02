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

public class PlayerLevelCondition implements ICondition
{
    private final int _minLevel;
    private final int _maxLevel;
    
    private PlayerLevelCondition(final StatsSet params) {
        this._minLevel = params.getInt("minLevel");
        this._maxLevel = params.getInt("maxLevel");
    }
    
    public boolean test(final Creature creature, final WorldObject object) {
        return GameUtils.isPlayer((WorldObject)creature) && creature.getLevel() >= this._minLevel && creature.getLevel() < this._maxLevel;
    }
    
    public static class Factory implements ConditionFactory
    {
        public ICondition create(final StatsSet data) {
            return (ICondition)new PlayerLevelCondition(data);
        }
        
        public String conditionName() {
            return "PlayerLevel";
        }
    }
}
