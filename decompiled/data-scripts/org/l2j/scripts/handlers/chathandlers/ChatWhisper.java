// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.chathandlers;

import org.l2j.gameserver.network.serverpackets.CreatureSay;
import org.l2j.gameserver.model.BlockList;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ChatSettings;
import java.util.Objects;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.world.World;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.handler.IChatHandler;

public final class ChatWhisper implements IChatHandler
{
    private static final ChatType[] CHAT_TYPES;
    
    public void handleChat(final ChatType type, final Player player, final String target, final String text) {
        if (Util.isNullOrEmpty((CharSequence)target)) {
            return;
        }
        final Player receiver = World.getInstance().findPlayer(target);
        if (receiver != null && !receiver.isSilenceMode(player.getObjectId())) {
            if (receiver.isChatBanned()) {
                player.sendPacket(SystemMessageId.THAT_PERSON_IS_IN_MESSAGE_REFUSAL_MODE);
                return;
            }
            if (Objects.isNull(receiver.getClient())) {
                player.sendMessage("Player is in offline mode.");
                return;
            }
            final ChatSettings chatSettings = (ChatSettings)Configurator.getSettings((Class)ChatSettings.class);
            final int levelRequired = chatSettings.whisperChatLevel();
            if (player.getLevel() < levelRequired && !player.getWhisperers().contains(receiver.getObjectId()) && !player.canOverrideCond(PcCondOverride.CHAT_CONDITIONS)) {
                player.sendPacket(new ServerPacket[] { (ServerPacket)SystemMessage.getSystemMessage(SystemMessageId.USERS_LV_S1_OR_LOWER_CAN_RESPOND_TO_A_WHISPER_BUT_CANNOT_INITIATE_IT).addInt(levelRequired) });
                return;
            }
            if (!BlockList.isBlocked(receiver, player)) {
                if (chatSettings.silenceModeExclude() && player.isSilenceMode()) {
                    player.addSilenceModeExcluded(receiver.getObjectId());
                }
                receiver.getWhisperers().add(player.getObjectId());
                receiver.sendPacket(new ServerPacket[] { (ServerPacket)new CreatureSay(player, receiver, player.getAppearance().getVisibleName(), type, text) });
                player.sendPacket(new ServerPacket[] { (ServerPacket)new CreatureSay(player, receiver, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, receiver.getAppearance().getVisibleName()), type, text) });
            }
            else {
                player.sendPacket(SystemMessageId.THAT_PERSON_IS_IN_MESSAGE_REFUSAL_MODE);
            }
        }
        else {
            player.sendPacket(SystemMessageId.THAT_PLAYER_IS_NOT_ONLINE);
        }
    }
    
    public ChatType[] getChatTypeList() {
        return ChatWhisper.CHAT_TYPES;
    }
    
    static {
        CHAT_TYPES = new ChatType[] { ChatType.WHISPER };
    }
}
