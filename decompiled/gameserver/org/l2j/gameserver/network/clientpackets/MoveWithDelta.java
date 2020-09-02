// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

public class MoveWithDelta extends ClientPacket
{
    private int _dx;
    private int _dy;
    private int _dz;
    
    public void readImpl() {
        this._dx = this.readInt();
        this._dy = this.readInt();
        this._dz = this.readInt();
    }
    
    public void runImpl() {
    }
}
