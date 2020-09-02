// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExResponseBeautyList;
import org.l2j.gameserver.network.GameClient;

public class RequestShowBeautyList extends ClientPacket
{
    private int _type;
    
    public void readImpl() {
        this._type = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        ((GameClient)this.client).sendPacket(new ExResponseBeautyList(activeChar, this._type));
    }
}
