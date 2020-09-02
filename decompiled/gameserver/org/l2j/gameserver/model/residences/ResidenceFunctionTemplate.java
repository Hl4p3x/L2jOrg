// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.residences;

import org.l2j.gameserver.model.StatsSet;
import java.time.Duration;
import org.l2j.gameserver.model.holders.ItemHolder;

public class ResidenceFunctionTemplate
{
    private final int _id;
    private final int _level;
    private final ResidenceFunctionType _type;
    private final ItemHolder _cost;
    private final Duration _duration;
    private final double _value;
    
    public ResidenceFunctionTemplate(final StatsSet set) {
        this._id = set.getInt("id");
        this._level = set.getInt("level");
        this._type = set.getEnum("type", ResidenceFunctionType.class, ResidenceFunctionType.NONE);
        this._cost = new ItemHolder(set.getInt("costId"), set.getLong("costCount"));
        this._duration = set.getDuration("duration");
        this._value = set.getDouble("value", 0.0);
    }
    
    public int getId() {
        return this._id;
    }
    
    public int getLevel() {
        return this._level;
    }
    
    public ResidenceFunctionType getType() {
        return this._type;
    }
    
    public ItemHolder getCost() {
        return this._cost;
    }
    
    public Duration getDuration() {
        return this._duration;
    }
    
    public long getDurationAsDays() {
        return this._duration.toDays();
    }
    
    public double getValue() {
        return this._value;
    }
}
