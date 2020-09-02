// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class Ex2ndPasswordCheck extends ServerPacket
{
    public static final int PASSWORD_NEW = 0;
    public static final int PASSWORD_PROMPT = 1;
    public static final int PASSWORD_OK = 2;
    private final int _windowType;
    
    public Ex2ndPasswordCheck(final int windowType) {
        this._windowType = windowType;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_2ND_PASSWORD_CHECK);
        this.writeInt(this._windowType);
        this.writeInt(0);
    }
}
