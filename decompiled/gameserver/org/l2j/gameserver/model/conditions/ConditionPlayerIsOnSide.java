// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.enums.CastleSide;

public class ConditionPlayerIsOnSide extends Condition
{
    private final CastleSide _side;
    
    public ConditionPlayerIsOnSide(final CastleSide side) {
        this._side = side;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        return GameUtils.isPlayer(effector) && effector.getActingPlayer().getPlayerSide() == this._side;
    }
}
