// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExBrLoadEventTopRankers;
import org.l2j.gameserver.network.GameClient;

public class BrEventRankerList extends ClientPacket
{
    private int _eventId;
    private int _day;
    private int _ranking;
    
    public void readImpl() {
        this._eventId = this.readInt();
        this._day = this.readInt();
        this._ranking = this.readInt();
    }
    
    public void runImpl() {
        final int count = 0;
        final int bestScore = 0;
        final int myScore = 0;
        ((GameClient)this.client).sendPacket(new ExBrLoadEventTopRankers(this._eventId, this._day, 0, 0, 0));
    }
}
