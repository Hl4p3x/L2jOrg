// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.chathandlers;

import org.l2j.gameserver.network.serverpackets.CreatureSay;
import org.l2j.gameserver.network.SystemMessageId;
import java.util.Objects;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.handler.IChatHandler;

public final class ChatClan implements IChatHandler
{
    private static final ChatType[] CHAT_TYPES;
    
    public void handleChat(final ChatType type, final Player player, final String target, final String text) {
        if (Objects.isNull(player.getClan())) {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_IN_A_CLAN);
            return;
        }
        player.getClan().broadcastCSToOnlineMembers(new CreatureSay(player, type, text), player);
    }
    
    public ChatType[] getChatTypeList() {
        return ChatClan.CHAT_TYPES;
    }
    
    static {
        CHAT_TYPES = new ChatType[] { ChatType.CLAN };
    }
}
