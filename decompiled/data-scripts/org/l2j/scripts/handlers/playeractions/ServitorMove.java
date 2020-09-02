// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.playeractions;

import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.data.xml.model.ActionData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IPlayerActionHandler;

public final class ServitorMove implements IPlayerActionHandler
{
    public void useAction(final Player player, final ActionData action, final boolean ctrlPressed, final boolean shiftPressed) {
        if (!player.hasServitors()) {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_A_SERVITOR);
            return;
        }
        if (player.getTarget() != null) {
            player.getServitors().values().stream().filter(s -> s != player.getTarget() && !((Summon)s).isMovementDisabled()).forEach(s -> {
                if (s.isBetrayed()) {
                    player.sendPacket(SystemMessageId.YOUR_SERVITOR_IS_UNRESPONSIVE_AND_WILL_NOT_OBEY_ANY_ORDERS);
                }
                else {
                    s.setFollowStatus(false);
                    s.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Object[] { player.getTarget().getLocation() });
                }
            });
        }
    }
}
