// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.chathandlers;

import org.l2j.gameserver.handler.IVoicedCommandHandler;
import org.l2j.gameserver.handler.VoicedCommandHandler;
import java.util.StringTokenizer;
import java.util.function.Consumer;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.BlockList;
import java.util.Objects;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.serverpackets.CreatureSay;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ChatSettings;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.handler.IChatHandler;

public final class ChatGeneral implements IChatHandler
{
    private static final int CHAT_RANGE = 1250;
    private static final ChatType[] CHAT_TYPES;
    
    public void handleChat(final ChatType type, final Player player, final String params, final String text) {
        final boolean vcd_used = this.checkUseVoicedCommand(player, params, text);
        if (!vcd_used) {
            final int levelRequired = ((ChatSettings)Configurator.getSettings((Class)ChatSettings.class)).generalChatLevel();
            if (player.getLevel() < levelRequired && !player.canOverrideCond(PcCondOverride.CHAT_CONDITIONS)) {
                player.sendPacket(new ServerPacket[] { (ServerPacket)SystemMessage.getSystemMessage(SystemMessageId.GENERAL_CHAT_CANNOT_BE_USED_BY_USERS_LV_S1_OR_LOWER).addInt(levelRequired) });
                return;
            }
            final CreatureSay cs = new CreatureSay(player, type, text);
            final World instance = World.getInstance();
            final int n = 1250;
            final CreatureSay obj = cs;
            Objects.requireNonNull(obj);
            instance.forEachPlayerInRange((WorldObject)player, n, (Consumer)obj::sendTo, receiver -> !BlockList.isBlocked(receiver, player));
            player.sendPacket(new ServerPacket[] { (ServerPacket)cs });
        }
    }
    
    private boolean checkUseVoicedCommand(final Player activeChar, String params, final String text) {
        if (text.startsWith(".")) {
            final StringTokenizer st = new StringTokenizer(text);
            String command;
            if (st.countTokens() > 1) {
                command = st.nextToken().substring(1);
                params = text.substring(command.length() + 2);
            }
            else {
                command = text.substring(1);
            }
            final IVoicedCommandHandler vch = VoicedCommandHandler.getInstance().getHandler(command);
            if (Objects.nonNull(vch)) {
                return vch.useVoicedCommand(command, activeChar, params);
            }
        }
        return false;
    }
    
    public ChatType[] getChatTypeList() {
        return ChatGeneral.CHAT_TYPES;
    }
    
    static {
        CHAT_TYPES = new ChatType[] { ChatType.GENERAL };
    }
}
