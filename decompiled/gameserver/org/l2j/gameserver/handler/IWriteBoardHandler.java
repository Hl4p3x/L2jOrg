// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.handler;

import org.l2j.gameserver.model.actor.instance.Player;

public interface IWriteBoardHandler extends IParseBoardHandler
{
    boolean writeCommunityBoardCommand(final Player player, final String arg1, final String arg2, final String arg3, final String arg4, final String arg5);
}
