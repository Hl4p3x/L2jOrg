// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionTargetCheckCrtEffect extends Condition
{
    private final boolean _isCrtEffect;
    
    public ConditionTargetCheckCrtEffect(final boolean isCrtEffect) {
        this._isCrtEffect = isCrtEffect;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        return !GameUtils.isNpc(effected) || ((Npc)effected).getTemplate().canBeCrt() == this._isCrtEffect;
    }
}
