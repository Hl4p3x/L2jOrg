// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.ArrayList;
import org.l2j.gameserver.model.interfaces.ILocational;
import java.util.List;

public class ExShowTerritory extends ServerPacket
{
    private final int _minZ;
    private final int _maxZ;
    private final List<ILocational> _vertices;
    
    public ExShowTerritory(final int minZ, final int maxZ) {
        this._vertices = new ArrayList<ILocational>();
        this._minZ = minZ;
        this._maxZ = maxZ;
    }
    
    public void addVertice(final ILocational loc) {
        this._vertices.add(loc);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SHOW_TERRITORY);
        this.writeInt(this._vertices.size());
        this.writeInt(this._minZ);
        this.writeInt(this._maxZ);
        for (final ILocational loc : this._vertices) {
            this.writeInt(loc.getX());
            this.writeInt(loc.getY());
        }
    }
}
