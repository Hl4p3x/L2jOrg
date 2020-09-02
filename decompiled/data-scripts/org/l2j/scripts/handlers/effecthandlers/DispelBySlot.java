// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.skills.BuffInfo;
import java.util.Objects;
import java.util.Collection;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.EffectType;
import java.util.HashMap;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.skills.AbnormalType;
import java.util.Map;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class DispelBySlot extends AbstractEffect
{
    private final Map<AbnormalType, Short> dispelAbnormals;
    
    private DispelBySlot(final StatsSet params) {
        if (params.contains("type")) {
            this.dispelAbnormals = Map.of((AbnormalType)params.getEnum("type", (Class)AbnormalType.class), params.getShort("power"));
        }
        else {
            this.dispelAbnormals = new HashMap<AbnormalType, Short>();
            StatsSet set;
            params.getSet().forEach((key, value) -> {
                if (key.startsWith("abnormal")) {
                    set = value;
                    this.dispelAbnormals.put((AbnormalType)set.getEnum("type", (Class)AbnormalType.class), set.getShort("power"));
                }
            });
        }
    }
    
    public EffectType getEffectType() {
        return EffectType.DISPEL_BY_SLOT;
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (this.dispelAbnormals.isEmpty()) {
            return;
        }
        if (effected.getEffectList().hasAbnormalType((Collection)this.dispelAbnormals.keySet())) {
            final Short transformToDispel = this.dispelAbnormals.get(AbnormalType.TRANSFORM);
            if (Objects.nonNull(transformToDispel) && (transformToDispel == effected.getTransformationId() || transformToDispel < 0)) {
                effected.stopTransformation(true);
            }
            Short abnormalLevel;
            effected.getEffectList().stopEffects(info -> {
                if (info.isAbnormalType(AbnormalType.TRANSFORM)) {
                    return false;
                }
                else {
                    abnormalLevel = this.dispelAbnormals.get(info.getSkill().getAbnormalType());
                    return abnormalLevel != null && (abnormalLevel < 0 || abnormalLevel >= info.getSkill().getAbnormalLvl());
                }
            }, true, true);
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new DispelBySlot(data);
        }
        
        public String effectName() {
            return "dispel";
        }
    }
}
