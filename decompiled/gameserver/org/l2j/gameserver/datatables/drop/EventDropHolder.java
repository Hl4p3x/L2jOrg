// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.datatables.drop;

import org.l2j.gameserver.enums.DropType;
import io.github.joealisson.primitive.IntCollection;
import org.l2j.gameserver.model.holders.DropHolder;

public class EventDropHolder extends DropHolder
{
    private final int minLevel;
    private final int maxLevel;
    private final IntCollection monsterIds;
    
    public EventDropHolder(final int itemId, final long min, final long max, final double chance, final int minLevel, final int maxLevel, final IntCollection monsterIds) {
        super(null, itemId, min, max, chance);
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.monsterIds = monsterIds;
    }
    
    public int getMinLevel() {
        return this.minLevel;
    }
    
    public int getMaxLevel() {
        return this.maxLevel;
    }
    
    public boolean hasMonster(final int id) {
        return this.monsterIds.contains(id);
    }
    
    public boolean checkLevel(final int level) {
        return this.minLevel <= level && this.maxLevel >= level;
    }
}
