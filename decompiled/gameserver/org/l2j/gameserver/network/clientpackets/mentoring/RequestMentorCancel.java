// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.mentoring;

import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestMentorCancel extends ClientPacket
{
    private int _confirmed;
    private String _name;
    
    public void readImpl() {
        this._confirmed = this.readInt();
        this._name = this.readString();
    }
    
    public void runImpl() {
    }
}
