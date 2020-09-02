// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.enums.FenceState;
import org.l2j.gameserver.model.actor.instance.Fence;

public class ExColosseumFenceInfo extends ServerPacket
{
    private final int _objId;
    private final int _x;
    private final int _y;
    private final int _z;
    private final int _width;
    private final int _length;
    private final int _clientState;
    
    public ExColosseumFenceInfo(final Fence fence) {
        this(fence.getObjectId(), fence.getX(), fence.getY(), fence.getZ(), fence.getWidth(), fence.getLength(), fence.getState());
    }
    
    public ExColosseumFenceInfo(final int objId, final double x, final double y, final double z, final int width, final int length, final FenceState state) {
        this._objId = objId;
        this._x = (int)x;
        this._y = (int)y;
        this._z = (int)z;
        this._width = width;
        this._length = length;
        this._clientState = state.getClientId();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_COLOSSEUM_FENCE_INFO);
        this.writeInt(this._objId);
        this.writeInt(this._clientState);
        this.writeInt(this._x);
        this.writeInt(this._y);
        this.writeInt(this._z);
        this.writeInt(this._width);
        this.writeInt(this._length);
    }
}
