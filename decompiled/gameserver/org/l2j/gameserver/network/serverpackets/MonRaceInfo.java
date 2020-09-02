// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.Npc;

public class MonRaceInfo extends ServerPacket
{
    private final int _unknown1;
    private final int _unknown2;
    private final Npc[] _monsters;
    private final int[][] _speeds;
    
    public MonRaceInfo(final int unknown1, final int unknown2, final Npc[] monsters, final int[][] speeds) {
        this._unknown1 = unknown1;
        this._unknown2 = unknown2;
        this._monsters = monsters;
        this._speeds = speeds;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.MONRACE_INFO);
        this.writeInt(this._unknown1);
        this.writeInt(this._unknown2);
        this.writeInt(8);
        for (int i = 0; i < 8; ++i) {
            this.writeInt(this._monsters[i].getObjectId());
            this.writeInt(this._monsters[i].getTemplate().getId() + 1000000);
            this.writeInt(14107);
            this.writeInt(181875 + 58 * (7 - i));
            this.writeInt(-3566);
            this.writeInt(12080);
            this.writeInt(181875 + 58 * (7 - i));
            this.writeInt(-3566);
            this.writeDouble(this._monsters[i].getTemplate().getfCollisionHeight());
            this.writeDouble(this._monsters[i].getTemplate().getfCollisionRadius());
            this.writeInt(120);
            for (int j = 0; j < 20; ++j) {
                if (this._unknown1 == 0) {
                    this.writeByte((byte)this._speeds[i][j]);
                }
                else {
                    this.writeByte((byte)0);
                }
            }
        }
    }
}
