// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class Ex2ndPasswordVerify extends ServerPacket
{
    public static final int PASSWORD_OK = 0;
    public static final int PASSWORD_WRONG = 1;
    public static final int PASSWORD_BAN = 2;
    private final int _wrongTentatives;
    private final int _mode;
    
    public Ex2ndPasswordVerify(final int mode, final int wrongTentatives) {
        this._mode = mode;
        this._wrongTentatives = wrongTentatives;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_2ND_PASSWORD_VERIFY);
        this.writeInt(this._mode);
        this.writeInt(this._wrongTentatives);
    }
}
