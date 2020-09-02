// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.enums.CastleSide;
import org.l2j.gameserver.model.Location;

public class CastleSpawnHolder extends Location
{
    private final int _npcId;
    private final CastleSide _side;
    
    public CastleSpawnHolder(final int npcId, final CastleSide side, final int x, final int y, final int z, final int heading) {
        super(x, y, z, heading);
        this._npcId = npcId;
        this._side = side;
    }
    
    public final int getNpcId() {
        return this._npcId;
    }
    
    public final CastleSide getSide() {
        return this._side;
    }
}
