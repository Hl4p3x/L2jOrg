// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.playeractions;

import org.l2j.gameserver.model.WorldObject;
import java.util.Objects;
import org.l2j.gameserver.data.xml.model.ActionData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IPlayerActionHandler;

public class ActionHandler implements IPlayerActionHandler
{
    public void useAction(final Player player, final ActionData action, final boolean ctrlPressed, final boolean shiftPressed) {
        final WorldObject target = player.getTarget();
        if (Objects.nonNull(target) && target != player) {
            if (ctrlPressed) {
                target.onForcedAttack(player);
            }
            else if (shiftPressed) {
                target.onActionShift(player);
            }
            else {
                target.onAction(player);
            }
        }
    }
}
