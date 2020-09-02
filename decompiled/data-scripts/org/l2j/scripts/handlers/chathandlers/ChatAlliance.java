// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.chathandlers;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.CreatureSay;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.handler.IChatHandler;

public final class ChatAlliance implements IChatHandler
{
    private static final ChatType[] CHAT_TYPES;
    
    public void handleChat(final ChatType type, final Player player, final String target, final String text) {
        if (player.getAllyId() == 0) {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_IN_AN_ALLIANCE);
            return;
        }
        player.getClan().broadcastToOnlineAllyMembers((ServerPacket)new CreatureSay(player, type, text));
    }
    
    public ChatType[] getChatTypeList() {
        return ChatAlliance.CHAT_TYPES;
    }
    
    static {
        CHAT_TYPES = new ChatType[] { ChatType.ALLIANCE };
    }
}
