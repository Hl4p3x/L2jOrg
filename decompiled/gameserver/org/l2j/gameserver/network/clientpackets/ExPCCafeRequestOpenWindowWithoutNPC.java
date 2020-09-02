// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.GameClient;

public class ExPCCafeRequestOpenWindowWithoutNPC extends ClientPacket
{
    public void readImpl() {
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar != null && Config.PC_CAFE_ENABLED) {
            final NpcHtmlMessage html = new NpcHtmlMessage();
            html.setFile(activeChar, "data/html/pccafe.htm");
            activeChar.sendPacket(html);
        }
    }
}
