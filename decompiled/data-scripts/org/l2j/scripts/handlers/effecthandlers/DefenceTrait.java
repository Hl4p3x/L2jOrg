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

public final class DefenceTrait extends AbstractEffect
{
    private final Map<TraitType, Float> defenceTraits;
    
    private DefenceTrait(final StatsSet params) {
        if (params.contains("type")) {
            this.defenceTraits = Map.of((TraitType)params.getEnum("type", (Class)TraitType.class), params.getFloat("power") / 100.0f);
        }
        else {
            this.defenceTraits = new HashMap<TraitType, Float>();
            StatsSet set;
            params.getSet().forEach((key, value) -> {
                if (key.startsWith("trait")) {
                    set = value;
                    this.defenceTraits.put((TraitType)set.getEnum("type", (Class)TraitType.class), set.getFloat("power") / 100.0f);
                }
            });
        }
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        for (final Map.Entry<TraitType, Float> trait : this.defenceTraits.entrySet()) {
            if (trait.getValue() < 1.0f) {
                effected.getStats().mergeDefenceTrait((TraitType)trait.getKey(), (float)trait.getValue());
            }
            else {
                effected.getStats().mergeInvulnerableTrait((TraitType)trait.getKey());
            }
        }
    }
    
    public void onExit(final Creature effector, final Creature effected, final Skill skill) {
        for (final Map.Entry<TraitType, Float> trait : this.defenceTraits.entrySet()) {
            if (trait.getValue() < 1.0f) {
                effected.getStats().removeDefenceTrait((TraitType)trait.getKey(), (float)trait.getValue());
            }
            else {
                effected.getStats().removeInvulnerableTrait((TraitType)trait.getKey());
            }
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new DefenceTrait(data);
        }
        
        public String effectName() {
            return "defence-trait";
        }
    }
}
