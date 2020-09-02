// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.commons.util.CommonUtil;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.enums.Race;

public class ConditionPlayerRace extends Condition
{
    private final Race[] _races;
    
    public ConditionPlayerRace(final Race[] races) {
        this._races = races;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        return GameUtils.isPlayer(effector) && CommonUtil.contains((Object[])this._races, (Object)effector.getActingPlayer().getRace());
    }
}
