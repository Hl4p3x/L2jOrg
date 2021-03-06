// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.HennaEquipList;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.GameClient;

public final class RequestHennaItemList extends ClientPacket
{
    private int _unknown;
    
    public void readImpl() {
        this._unknown = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar != null) {
            activeChar.sendPacket(new HennaEquipList(activeChar));
        }
    }
}
