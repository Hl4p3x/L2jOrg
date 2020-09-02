// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.chathandlers;

import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.instancemanager.PetitionManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.handler.IChatHandler;

public final class ChatPetition implements IChatHandler
{
    private static final ChatType[] CHAT_TYPES;
    
    public void handleChat(final ChatType type, final Player player, final String target, final String text) {
        if (!PetitionManager.getInstance().isPlayerInConsultation(player)) {
            player.sendPacket(SystemMessageId.YOU_ARE_CURRENTLY_NOT_IN_A_PETITION_CHAT);
            return;
        }
        PetitionManager.getInstance().sendActivePetitionMessage(player, text);
    }
    
    public ChatType[] getChatTypeList() {
        return ChatPetition.CHAT_TYPES;
    }
    
    static {
        CHAT_TYPES = new ChatType[] { ChatType.PETITION_PLAYER, ChatType.PETITION_GM };
    }
}
