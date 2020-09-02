// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.item;

public enum ItemPacketType
{
    HEADER, 
    LIST;
    
    public int clientId() {
        return 1 + this.ordinal();
    }
}
