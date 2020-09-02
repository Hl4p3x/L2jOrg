// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public class ShortBuffStatusUpdate extends ServerPacket
{
    public static final ShortBuffStatusUpdate RESET_SHORT_BUFF;
    private final int _skillId;
    private final int _skillLvl;
    private final int _skillSubLvl;
    private final int _duration;
    
    public ShortBuffStatusUpdate(final int skillId, final int skillLvl, final int skillSubLvl, final int duration) {
        this._skillId = skillId;
        this._skillLvl = skillLvl;
        this._skillSubLvl = skillSubLvl;
        this._duration = duration;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.SHORT_BUFF_STATUS_UPDATE);
        this.writeInt(this._skillId);
        this.writeShort((short)this._skillLvl);
        this.writeShort((short)this._skillSubLvl);
        this.writeInt(this._duration);
    }
    
    static {
        RESET_SHORT_BUFF = new ShortBuffStatusUpdate(0, 0, 0, 0);
    }
}
