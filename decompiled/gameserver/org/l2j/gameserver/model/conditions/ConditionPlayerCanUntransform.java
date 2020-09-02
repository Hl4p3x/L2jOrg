// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionPlayerCanUntransform extends Condition
{
    private final boolean _val;
    
    public ConditionPlayerCanUntransform(final boolean val) {
        this._val = val;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        boolean canUntransform = true;
        final Player player = effector.getActingPlayer();
        if (player == null) {
            canUntransform = false;
        }
        else if (player.isAlikeDead()) {
            canUntransform = false;
        }
        else if (player.isFlyingMounted() && !player.isInsideZone(ZoneType.LANDING)) {
            player.sendPacket(SystemMessageId.YOU_ARE_TOO_HIGH_TO_PERFORM_THIS_ACTION_PLEASE_LOWER_YOUR_ALTITUDE_AND_TRY_AGAIN);
            canUntransform = false;
        }
        return this._val == canUntransform;
    }
}
