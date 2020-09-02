// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import java.util.Iterator;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import java.util.List;

public class ConditionPlayerServitorNpcId extends Condition
{
    private final List<Integer> _npcIds;
    
    public ConditionPlayerServitorNpcId(final List<Integer> npcIds) {
        if (npcIds.size() == 1 && npcIds.get(0) == 0) {
            this._npcIds = null;
        }
        else {
            this._npcIds = npcIds;
        }
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        if (effector.getActingPlayer() == null || !effector.getActingPlayer().hasSummon()) {
            return false;
        }
        if (this._npcIds == null) {
            return true;
        }
        for (final Summon summon : effector.getServitors().values()) {
            if (this._npcIds.contains(summon.getId())) {
                return true;
            }
        }
        return false;
    }
}
