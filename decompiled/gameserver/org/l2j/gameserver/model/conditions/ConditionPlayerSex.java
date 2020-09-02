// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionPlayerSex extends Condition
{
    private static final ConditionPlayerSex MALE;
    private static final ConditionPlayerSex FEMALE;
    public final int _sex;
    
    private ConditionPlayerSex(final int sex) {
        this._sex = sex;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        return effector.getActingPlayer() != null && (effector.getActingPlayer().getAppearance().isFemale() ? 1 : 0) == this._sex;
    }
    
    public static ConditionPlayerSex of(final int sex) {
        return (sex == 0) ? ConditionPlayerSex.MALE : ConditionPlayerSex.FEMALE;
    }
    
    static {
        MALE = new ConditionPlayerSex(0);
        FEMALE = new ConditionPlayerSex(1);
    }
}
