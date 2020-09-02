// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.playeractions;

import java.util.Iterator;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.data.xml.model.ActionData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IPlayerActionHandler;

public final class UnsummonServitor implements IPlayerActionHandler
{
    public void useAction(final Player player, final ActionData action, final boolean ctrlPressed, final boolean shiftPressed) {
        boolean canUnsummon = true;
        if (player.hasServitors()) {
            for (final Summon s2 : player.getServitors().values()) {
                if (s2.isBetrayed()) {
                    player.sendPacket(SystemMessageId.YOUR_SERVITOR_IS_UNRESPONSIVE_AND_WILL_NOT_OBEY_ANY_ORDERS);
                    canUnsummon = false;
                    break;
                }
                if (s2.isAttackingNow() || s2.isInCombat() || s2.isMovementDisabled()) {
                    player.sendPacket(SystemMessageId.A_SERVITOR_WHOM_IS_ENGAGED_IN_BATTLE_CANNOT_BE_DE_ACTIVATED);
                    canUnsummon = false;
                    break;
                }
            }
            if (canUnsummon) {
                player.getServitors().values().forEach(s -> s.unSummon(player));
            }
        }
        else {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_A_SERVITOR);
        }
    }
}
