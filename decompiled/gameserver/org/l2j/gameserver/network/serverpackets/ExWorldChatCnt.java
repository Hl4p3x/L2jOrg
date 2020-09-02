// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ChatSettings;
import org.l2j.gameserver.model.actor.instance.Player;

public class ExWorldChatCnt extends ServerPacket
{
    private final int worldChatCount;
    
    public ExWorldChatCnt(final Player activeChar) {
        this.worldChatCount = (this.canUseWorldChat(activeChar) ? Math.max(activeChar.getWorldChatPoints() - activeChar.getWorldChatUsed(), 0) : 0);
    }
    
    private boolean canUseWorldChat(final Player activeChar) {
        return activeChar.getLevel() >= ((ChatSettings)Configurator.getSettings((Class)ChatSettings.class)).worldChatMinLevel() || activeChar.getVipTier() > 0;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_WORLDCHAT_CNT);
        this.writeInt(this.worldChatCount);
    }
}
