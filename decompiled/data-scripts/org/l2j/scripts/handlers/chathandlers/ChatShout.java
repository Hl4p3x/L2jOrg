// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.chathandlers;

import org.l2j.gameserver.model.BlockList;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.world.MapRegionManager;
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

public final class ChatShout implements IChatHandler
{
    private static final ChatType[] CHAT_TYPES;
    
    public void handleChat(final ChatType type, final Player player, final String target, final String text) {
        final ChatSettings chatSettings = (ChatSettings)Configurator.getSettings((Class)ChatSettings.class);
        final int levelRequired = chatSettings.shoutChatLevel();
        if (player.getLevel() < levelRequired && !player.canOverrideCond(PcCondOverride.CHAT_CONDITIONS)) {
            player.sendPacket(new ServerPacket[] { (ServerPacket)SystemMessage.getSystemMessage(SystemMessageId.SHOUT_CHAT_CANNOT_BE_USED_BY_USERS_LV_S1_OR_LOWER).addInt(levelRequired) });
            return;
        }
        final CreatureSay cs = new CreatureSay(player, type, text);
        if (chatSettings.defaultGlobalChat().equalsIgnoreCase("ON") || (chatSettings.defaultGlobalChat().equalsIgnoreCase("GM") && player.canOverrideCond(PcCondOverride.CHAT_CONDITIONS))) {
            final int region = MapRegionManager.getInstance().getMapRegionLocId((WorldObject)player);
            final int n;
            final ServerPacket serverPacket;
            World.getInstance().forEachPlayer(receiver -> {
                if (n == MapRegionManager.getInstance().getMapRegionLocId(receiver) && !BlockList.isBlocked((Player)receiver, player) && ((Player)receiver).getInstanceId() == player.getInstanceId()) {
                    ((Player)receiver).sendPacket(new ServerPacket[] { serverPacket });
                }
            });
        }
        else if (chatSettings.defaultGlobalChat().equalsIgnoreCase("global")) {
            if (!player.canOverrideCond(PcCondOverride.CHAT_CONDITIONS) && !player.getFloodProtectors().getGlobalChat().tryPerformAction("global chat")) {
                player.sendMessage("Do not spam shout channel.");
                return;
            }
            final ServerPacket serverPacket2;
            World.getInstance().forEachPlayer(receiver -> {
                if (!BlockList.isBlocked(receiver, player)) {
                    receiver.sendPacket(new ServerPacket[] { serverPacket2 });
                }
            });
        }
    }
    
    public ChatType[] getChatTypeList() {
        return ChatShout.CHAT_TYPES;
    }
    
    static {
        CHAT_TYPES = new ChatType[] { ChatType.SHOUT };
    }
}
