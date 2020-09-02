// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class DamageBlock extends AbstractEffect
{
    private final boolean blockHp;
    private final boolean blockMp;
    
    private DamageBlock(final StatsSet params) {
        this.blockHp = params.getBoolean("block-hp");
        this.blockMp = params.getBoolean("block-mp");
    }
    
    public long getEffectFlags() {
        int mask = 0;
        if (this.blockHp) {
            mask = (int)((long)mask | EffectFlag.HP_BLOCK.getMask());
        }
        if (this.blockMp) {
            mask = (int)((long)mask | EffectFlag.MP_BLOCK.getMask());
        }
        return mask;
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new DamageBlock(data);
        }
        
        public String effectName() {
            return "damage-block";
        }
    }
}
