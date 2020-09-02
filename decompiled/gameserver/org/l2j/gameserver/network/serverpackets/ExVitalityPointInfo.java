// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExVitalityPointInfo extends ServerPacket
{
    private final int _vitalityPoints;
    
    public ExVitalityPointInfo(final int vitPoints) {
        this._vitalityPoints = vitPoints;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_VITALITY_POINT_INFO);
        this.writeInt(this._vitalityPoints);
    }
}
