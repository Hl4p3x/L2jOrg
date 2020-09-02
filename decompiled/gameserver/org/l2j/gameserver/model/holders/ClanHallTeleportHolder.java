// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.model.Location;

public class ClanHallTeleportHolder extends Location
{
    private final NpcStringId _npcStringId;
    private final int _minFunctionLevel;
    private final int _cost;
    
    public ClanHallTeleportHolder(final int npcStringId, final int x, final int y, final int z, final int minFunctionLevel, final int cost) {
        super(x, y, z);
        this._npcStringId = NpcStringId.getNpcStringId(npcStringId);
        this._minFunctionLevel = minFunctionLevel;
        this._cost = cost;
    }
    
    public final NpcStringId getNpcStringId() {
        return this._npcStringId;
    }
    
    public final int getMinFunctionLevel() {
        return this._minFunctionLevel;
    }
    
    public final int getCost() {
        return this._cost;
    }
}
