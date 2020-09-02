// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.transform;

import java.util.HashMap;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.model.StatsSet;
import java.util.Map;

public final class TransformLevelData
{
    private final int _level;
    private final double _levelMod;
    private Map<Integer, Double> _stats;
    
    public TransformLevelData(final StatsSet set) {
        this._level = set.getInt("val");
        this._levelMod = set.getDouble("levelMod");
        this.addStats(Stat.MAX_HP, set.getDouble("hp"));
        this.addStats(Stat.MAX_MP, set.getDouble("mp"));
        this.addStats(Stat.MAX_CP, set.getDouble("cp"));
        this.addStats(Stat.REGENERATE_HP_RATE, set.getDouble("hpRegen"));
        this.addStats(Stat.REGENERATE_MP_RATE, set.getDouble("mpRegen"));
        this.addStats(Stat.REGENERATE_CP_RATE, set.getDouble("cpRegen"));
    }
    
    private void addStats(final Stat stat, final double val) {
        if (this._stats == null) {
            this._stats = new HashMap<Integer, Double>();
        }
        this._stats.put(stat.ordinal(), val);
    }
    
    public double getStats(final Stat stat, final double defaultValue) {
        return (this._stats == null) ? defaultValue : this._stats.getOrDefault(stat.ordinal(), defaultValue);
    }
    
    public int getLevel() {
        return this._level;
    }
    
    public double getLevelMod() {
        return this._levelMod;
    }
}
