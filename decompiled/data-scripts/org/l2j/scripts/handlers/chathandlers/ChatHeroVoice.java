// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.chathandlers;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.BlockList;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.serverpackets.CreatureSay;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.handler.IChatHandler;

public final class ChatHeroVoice implements IChatHandler
{
    private static final ChatType[] CHAT_TYPES;
    
    public void handleChat(final ChatType type, final Player player, final String target, final String text) {
        if (!player.isHero() && !player.canOverrideCond(PcCondOverride.CHAT_CONDITIONS)) {
            player.sendPacket(SystemMessageId.ONLY_HEROES_CAN_ENTER_THE_HERO_CHANNEL);
            return;
        }
        if (!player.getFloodProtectors().getHeroVoice().tryPerformAction("hero voice")) {
            player.sendMessage("Action failed. Heroes are only able to speak in the global channel once every 10 seconds.");
            return;
        }
        final CreatureSay cs = new CreatureSay(player, type, text);
        final ServerPacket serverPacket;
        World.getInstance().forEachPlayer(receiver -> {
            if (!BlockList.isBlocked(receiver, player)) {
                receiver.sendPacket(new ServerPacket[] { serverPacket });
            }
        });
    }
    
    public ChatType[] getChatTypeList() {
        return ChatHeroVoice.CHAT_TYPES;
    }
    
    static {
        CHAT_TYPES = new ChatType[] { ChatType.HERO_VOICE };
    }
}
