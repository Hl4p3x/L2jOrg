// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

public class RequestFortressMapInfo extends ClientPacket
{
    private int _fortressId;
    
    public void readImpl() {
        this._fortressId = this.readInt();
    }
    
    public void runImpl() {
    }
}
