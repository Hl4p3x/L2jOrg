// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import java.util.Iterator;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import io.github.joealisson.primitive.IntList;

public class ConditionPlayerInsideZoneId extends Condition
{
    public final IntList zones;
    
    public ConditionPlayerInsideZoneId(final IntList zones) {
        this.zones = zones;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        if (effector.getActingPlayer() == null) {
            return false;
        }
        for (final Zone zone : ZoneManager.getInstance().getZones(effector)) {
            if (this.zones.contains(zone.getId())) {
                return true;
            }
        }
        return false;
    }
}
