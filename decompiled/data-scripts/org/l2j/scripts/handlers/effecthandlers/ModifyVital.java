// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class ModifyVital extends AbstractEffect
{
    private final int hp;
    private final int mp;
    private final int cp;
    
    private ModifyVital(final StatsSet params) {
        this.hp = params.getInt("hp", 0);
        this.mp = params.getInt("mp", 0);
        this.cp = params.getInt("cp", 0);
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (effected.isDead()) {
            return;
        }
        if (GameUtils.isPlayer((WorldObject)effector) && GameUtils.isPlayer((WorldObject)effected) && effected.isAffected(EffectFlag.DUELIST_FURY) && !effector.isAffected(EffectFlag.DUELIST_FURY)) {
            return;
        }
        effected.setCurrentCp((double)Math.max(this.cp, 0));
        effected.setCurrentHp((double)Math.max(this.hp, 0));
        effected.setCurrentMp((double)Math.max(this.mp, 0));
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new ModifyVital(data);
        }
        
        public String effectName() {
            return "vital-modify";
        }
    }
}
