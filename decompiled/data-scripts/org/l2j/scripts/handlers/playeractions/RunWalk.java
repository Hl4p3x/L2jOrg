// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.playeractions;

import org.l2j.gameserver.data.xml.model.ActionData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IPlayerActionHandler;

public final class RunWalk implements IPlayerActionHandler
{
    public void useAction(final Player player, final ActionData action, final boolean ctrlPressed, final boolean shiftPressed) {
        if (player.isRunning()) {
            player.setWalking();
        }
        else {
            player.setRunning();
        }
    }
}
