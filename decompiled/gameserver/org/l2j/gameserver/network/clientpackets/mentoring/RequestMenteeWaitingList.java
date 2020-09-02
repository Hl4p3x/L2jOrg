// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.mentoring;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.mentoring.ListMenteeWaiting;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestMenteeWaitingList extends ClientPacket
{
    private int _page;
    private int _minLevel;
    private int _maxLevel;
    
    public void readImpl() {
        this._page = this.readInt();
        this._minLevel = this.readInt();
        this._maxLevel = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        ((GameClient)this.client).sendPacket(new ListMenteeWaiting(this._page, this._minLevel, this._maxLevel));
    }
}
