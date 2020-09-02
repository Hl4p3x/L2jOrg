// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.chathandlers;

import java.util.Set;
import org.l2j.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.commons.util.Util;
import java.util.function.Consumer;
import java.util.Objects;
import org.l2j.gameserver.network.serverpackets.CreatureSay;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.handler.IChatHandler;

public class ChatPartyMatchRoom implements IChatHandler
{
    private static final ChatType[] CHAT_TYPES;
    
    public void handleChat(final ChatType type, final Player player, final String target, final String text) {
        final CreatureSay cs;
        final ServerPacket obj;
        final Iterable iterable;
        Util.doIfNonNull((Object)player.getMatchingRoom(), room -> {
            cs = new CreatureSay(player, type, text);
            room.getMembers();
            Objects.requireNonNull((CreatureSay)obj);
            iterable.forEach(obj::sendTo);
        });
    }
    
    public ChatType[] getChatTypeList() {
        return ChatPartyMatchRoom.CHAT_TYPES;
    }
    
    static {
        CHAT_TYPES = new ChatType[] { ChatType.PARTYMATCH_ROOM };
    }
}
