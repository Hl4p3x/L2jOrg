// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionPlayerCallPc extends Condition
{
    private final boolean _val;
    
    public ConditionPlayerCallPc(final boolean val) {
        this._val = val;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        boolean canCallPlayer = true;
        final Player player = effector.getActingPlayer();
        if (player == null) {
            canCallPlayer = false;
        }
        else if (player.isInOlympiadMode()) {
            player.sendPacket(SystemMessageId.YOU_MAY_NOT_SUMMON_FROM_YOUR_CURRENT_LOCATION);
            canCallPlayer = false;
        }
        else if (player.inObserverMode()) {
            canCallPlayer = false;
        }
        else if (player.isInsideZone(ZoneType.NO_SUMMON_FRIEND) || player.isInsideZone(ZoneType.JAIL) || player.isFlyingMounted()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_USE_SUMMONING_OR_TELEPORTING_IN_THIS_AREA);
            canCallPlayer = false;
        }
        return this._val == canCallPlayer;
    }
}
