// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.teleporter;

import java.util.ArrayList;
import java.util.Collections;
import org.l2j.gameserver.model.StatsSet;
import java.util.List;
import org.l2j.gameserver.model.Location;

public class TeleportLocation extends Location
{
    private final int _id;
    private final String _name;
    private final int _npcStringId;
    private final int _questZoneId;
    private final int _feeId;
    private final long _feeCount;
    private final List<Integer> _castleId;
    
    public TeleportLocation(final int id, final StatsSet set) {
        super(set);
        this._id = id;
        this._name = set.getString("name", null);
        this._npcStringId = set.getInt("npcStringId", -1);
        this._questZoneId = set.getInt("questZoneId", 0);
        this._feeId = set.getInt("feeId", 57);
        this._feeCount = set.getLong("feeCount", 0L);
        final String castleIds = set.getString("castleId", "");
        if (castleIds.isEmpty()) {
            this._castleId = Collections.emptyList();
        }
        else if (!castleIds.contains(";")) {
            this._castleId = Collections.singletonList(Integer.parseInt(castleIds));
        }
        else {
            this._castleId = new ArrayList<Integer>();
            for (final String castleId : castleIds.split(";")) {
                this._castleId.add(Integer.parseInt(castleId));
            }
        }
    }
    
    public int getId() {
        return this._id;
    }
    
    public String getName() {
        return this._name;
    }
    
    public int getNpcStringId() {
        return this._npcStringId;
    }
    
    public int getQuestZoneId() {
        return this._questZoneId;
    }
    
    public int getFeeId() {
        return this._feeId;
    }
    
    public long getFeeCount() {
        return this._feeCount;
    }
    
    public List<Integer> getCastleId() {
        return this._castleId;
    }
}
