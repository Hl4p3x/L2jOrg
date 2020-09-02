// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.playeractions;

import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.data.xml.model.ActionData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IPlayerActionHandler;

public final class PetMove implements IPlayerActionHandler
{
    public void useAction(final Player player, final ActionData action, final boolean ctrlPressed, final boolean shiftPressed) {
        if (!GameUtils.isPet((WorldObject)player.getPet())) {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_A_PET);
            return;
        }
        final Pet pet = player.getPet();
        if (pet.isUncontrollable()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_USE_YOUR_PET_WHEN_ITS_HUNGER_GAUGE_IS_AT_0);
        }
        else if (pet.isBetrayed()) {
            player.sendPacket(SystemMessageId.YOUR_SERVITOR_IS_UNRESPONSIVE_AND_WILL_NOT_OBEY_ANY_ORDERS);
        }
        else if (player.getTarget() != null && pet != player.getTarget() && !pet.isMovementDisabled()) {
            pet.setFollowStatus(false);
            pet.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Object[] { player.getTarget().getLocation() });
        }
    }
}
