// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionPlayerIsHero extends Condition
{
    private static final ConditionPlayerIsHero HERO;
    private static final ConditionPlayerIsHero NO_HERO;
    public final boolean isHero;
    
    private ConditionPlayerIsHero(final boolean isHero) {
        this.isHero = isHero;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        return effector.getActingPlayer() != null && effector.getActingPlayer().isHero() == this.isHero;
    }
    
    public static ConditionPlayerIsHero of(final boolean hero) {
        return hero ? ConditionPlayerIsHero.HERO : ConditionPlayerIsHero.NO_HERO;
    }
    
    static {
        HERO = new ConditionPlayerIsHero(true);
        NO_HERO = new ConditionPlayerIsHero(false);
    }
}
