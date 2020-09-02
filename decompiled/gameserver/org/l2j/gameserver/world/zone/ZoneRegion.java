// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.world.zone;

import org.l2j.gameserver.model.WorldObject;
import java.util.Iterator;
import org.l2j.gameserver.world.zone.type.PeaceZone;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.IntMap;

public class ZoneRegion
{
    private final IntMap<Zone> zones;
    
    public ZoneRegion() {
        this.zones = (IntMap<Zone>)new CHashIntMap();
    }
    
    public IntMap<Zone> getZones() {
        return this.zones;
    }
    
    public void revalidateZones(final Creature creature) {
        if (creature.isTeleporting()) {
            return;
        }
        this.zones.values().forEach(z -> z.revalidateInZone(creature));
    }
    
    public void removeFromZones(final Creature creature) {
        this.zones.values().forEach(z -> z.removeCreature(creature));
    }
    
    public boolean checkEffectRangeInsidePeaceZone(final Skill skill, final int x, final int y, final int z) {
        final int range = skill.getEffectRange();
        final int up = y + range;
        final int down = y - range;
        final int left = x + range;
        final int right = x - range;
        for (final Zone e : this.zones.values()) {
            if (e instanceof PeaceZone) {
                if (e.isInsideZone(x, up, z)) {
                    return false;
                }
                if (e.isInsideZone(x, down, z)) {
                    return false;
                }
                if (e.isInsideZone(left, y, z)) {
                    return false;
                }
                if (e.isInsideZone(right, y, z)) {
                    return false;
                }
                if (e.isInsideZone(x, y, z)) {
                    return false;
                }
                continue;
            }
        }
        return true;
    }
    
    public void onDeath(final Creature creature) {
        this.zones.values().stream().filter(z -> z.isInsideZone(creature)).forEach(z -> z.onDieInside(creature));
    }
    
    public void onRevive(final Creature creature) {
        this.zones.values().stream().filter(z -> z.isInsideZone(creature)).forEach(z -> z.onReviveInside(creature));
    }
}
