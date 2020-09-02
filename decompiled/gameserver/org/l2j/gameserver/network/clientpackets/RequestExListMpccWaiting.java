// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExListMpccWaiting;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.GameClient;

public class RequestExListMpccWaiting extends ClientPacket
{
    private int _page;
    private int _location;
    private int _level;
    
    public void readImpl() {
        this._page = this.readInt();
        this._location = this.readInt();
        this._level = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        activeChar.sendPacket(new ExListMpccWaiting(this._page, this._location, this._level));
    }
}
