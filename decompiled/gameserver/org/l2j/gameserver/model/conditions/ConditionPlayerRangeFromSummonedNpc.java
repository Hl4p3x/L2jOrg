// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.world.World;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionPlayerRangeFromSummonedNpc extends Condition
{
    private final int[] npcIds;
    private final int radius;
    private final boolean _val;
    
    public ConditionPlayerRangeFromSummonedNpc(final int[] npcIds, final int radius, final boolean val) {
        this.npcIds = npcIds;
        this.radius = radius;
        this._val = val;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        boolean existNpc = false;
        if (!Util.isNullOrEmpty(this.npcIds) && this.radius > 0) {
            existNpc = World.getInstance().hasAnyVisibleObjectInRange(effector, Npc.class, this.radius, npc -> Util.contains(this.npcIds, npc.getId()) && effector == npc.getSummoner());
        }
        return existNpc == this._val;
    }
}
