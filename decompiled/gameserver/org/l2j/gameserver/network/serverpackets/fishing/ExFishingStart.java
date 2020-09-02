// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.fishing;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExFishingStart extends ServerPacket
{
    private final Player _player;
    private final int _fishType;
    private final int _baitType;
    private final ILocational _baitLocation;
    
    public ExFishingStart(final Player player, final int fishType, final int baitType, final ILocational baitLocation) {
        this._player = player;
        this._fishType = fishType;
        this._baitType = baitType;
        this._baitLocation = baitLocation;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_FISHING_START);
        this.writeInt(this._player.getObjectId());
        this.writeByte((byte)this._fishType);
        this.writeInt(this._baitLocation.getX());
        this.writeInt(this._baitLocation.getY());
        this.writeInt(this._baitLocation.getZ());
        this.writeByte((byte)this._baitType);
    }
}
