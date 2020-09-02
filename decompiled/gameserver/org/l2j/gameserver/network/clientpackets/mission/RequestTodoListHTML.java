// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.mission;

import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestTodoListHTML extends ClientPacket
{
    private int _tab;
    private String _linkName;
    
    public void readImpl() {
        this._tab = this.readByte();
        this._linkName = this.readString();
    }
    
    public void runImpl() {
    }
}
