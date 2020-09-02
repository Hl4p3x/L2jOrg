// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExUISetting;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.GameClient;

public class RequestKeyMapping extends ClientPacket
{
    public void readImpl() {
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        if (Config.STORE_UI_SETTINGS) {
            ((GameClient)this.client).sendPacket(new ExUISetting(activeChar));
        }
    }
}
