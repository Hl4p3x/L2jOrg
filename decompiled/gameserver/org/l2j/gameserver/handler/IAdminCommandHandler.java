// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.handler;

import org.l2j.gameserver.model.actor.instance.Player;

public interface IAdminCommandHandler
{
    boolean useAdminCommand(final String command, final Player activeChar);
    
    String[] getAdminCommandList();
}
