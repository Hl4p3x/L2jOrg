// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.playeractions;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.data.xml.model.ActionData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IPlayerActionHandler;

public final class TacticalSignUse implements IPlayerActionHandler
{
    public void useAction(final Player player, final ActionData action, final boolean ctrlPressed, final boolean shiftPressed) {
        if (!player.isInParty() || !GameUtils.isCreature(player.getTarget())) {
            player.sendPacket(new ServerPacket[] { (ServerPacket)ActionFailed.STATIC_PACKET });
            return;
        }
        player.getParty().addTacticalSign(player, action.getOptionId(), (Creature)player.getTarget());
    }
}
