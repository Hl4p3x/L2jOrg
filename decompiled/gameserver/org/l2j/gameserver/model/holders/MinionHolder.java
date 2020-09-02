// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

import java.time.Duration;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.interfaces.IIdentifiable;

public class MinionHolder implements IIdentifiable
{
    private final int _id;
    private final int _count;
    private final long _respawnTime;
    private final int _weightPoint;
    
    public MinionHolder(final StatsSet set) {
        this._id = set.getInt("id");
        this._count = set.getInt("count", 1);
        this._respawnTime = set.getDuration("respawnTime", Duration.ofSeconds(0L)).getSeconds() * 1000L;
        this._weightPoint = set.getInt("weightPoint", 0);
    }
    
    public MinionHolder(final int id, final int count, final long respawnTime, final int weightPoint) {
        this._id = id;
        this._count = count;
        this._respawnTime = respawnTime;
        this._weightPoint = weightPoint;
    }
    
    @Override
    public int getId() {
        return this._id;
    }
    
    public int getCount() {
        return this._count;
    }
    
    public long getRespawnTime() {
        return this._respawnTime;
    }
    
    public int getWeightPoint() {
        return this._weightPoint;
    }
}
