// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.handler;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.enums.ChatType;

public interface IChatHandler
{
    void handleChat(final ChatType type, final Player player, final String target, final String text);
    
    ChatType[] getChatTypeList();
}
