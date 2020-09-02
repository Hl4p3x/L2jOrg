// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.mentoring;

import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestMenteeAdd extends ClientPacket
{
    private String _target;
    
    public void readImpl() {
        this._target = this.readString();
    }
    
    public void runImpl() {
    }
}
