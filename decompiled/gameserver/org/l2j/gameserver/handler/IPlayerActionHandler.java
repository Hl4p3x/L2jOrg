// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.handler;

import org.l2j.gameserver.data.xml.model.ActionData;
import org.l2j.gameserver.model.actor.instance.Player;

public interface IPlayerActionHandler
{
    void useAction(final Player player, final ActionData action, final boolean ctrlPressed, final boolean shiftPressed);
}
