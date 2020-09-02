// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionPlayerHasFreeTeleportBookmarkSlots extends Condition
{
    private final int _teleportBookmarkSlots;
    
    public ConditionPlayerHasFreeTeleportBookmarkSlots(final int teleportBookmarkSlots) {
        this._teleportBookmarkSlots = teleportBookmarkSlots;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        final Player player = effector.getActingPlayer();
        if (player == null) {
            return false;
        }
        if (player.getBookMarkSlot() + this._teleportBookmarkSlots > 18) {
            player.sendPacket(SystemMessageId.YOUR_NUMBER_OF_MY_TELEPORTS_SLOTS_HAS_REACHED_ITS_MAXIMUM_LIMIT);
            return false;
        }
        return true;
    }
}
