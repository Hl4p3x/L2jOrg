// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.handler;

import org.l2j.gameserver.model.actor.instance.Player;
import java.util.StringTokenizer;

public interface IParseBoardHandler
{
    boolean parseCommunityBoardCommand(final String command, final StringTokenizer tokens, final Player player);
    
    String[] getCommunityBoardCommands();
    
    default String name() {
        return this.getClass().getSimpleName().replace("Board", "");
    }
}
