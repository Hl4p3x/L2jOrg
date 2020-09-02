// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

public class RequestPledgeExtendedInfo extends ClientPacket
{
    private String _name;
    
    public void readImpl() {
        this._name = this.readString();
    }
    
    public void runImpl() {
    }
}
