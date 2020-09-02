// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import java.util.Iterator;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import java.util.HashMap;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.TraitType;
import java.util.Map;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class AttackTrait extends AbstractEffect
{
    private final Map<TraitType, Float> attackTraits;
    
    private AttackTrait(final StatsSet params) {
        if (params.contains("type")) {
            this.attackTraits = Map.of((TraitType)params.getEnum("type", (Class)TraitType.class), params.getFloat("power") / 100.0f);
        }
        else {
            this.attackTraits = new HashMap<TraitType, Float>();
            StatsSet set;
            params.getSet().forEach((key, value) -> {
                if (key.startsWith("trait")) {
                    set = value;
                    this.attackTraits.put((TraitType)set.getEnum("type", (Class)TraitType.class), set.getFloat("power") / 100.0f);
                }
            });
        }
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        for (final Map.Entry<TraitType, Float> trait : this.attackTraits.entrySet()) {
            effected.getStats().mergeAttackTrait((TraitType)trait.getKey(), (float)trait.getValue());
        }
    }
    
    public void onExit(final Creature effector, final Creature effected, final Skill skill) {
        for (final Map.Entry<TraitType, Float> trait : this.attackTraits.entrySet()) {
            effected.getStats().removeAttackTrait((TraitType)trait.getKey(), (float)trait.getValue());
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new AttackTrait(data);
        }
        
        public String effectName() {
            return "attack-trait";
        }
    }
}
