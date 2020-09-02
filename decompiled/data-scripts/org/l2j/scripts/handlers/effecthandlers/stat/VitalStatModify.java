// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers.stat;

import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.model.StatsSet;

public class VitalStatModify extends AbstractStatEffect
{
    private final boolean heal;
    
    private VitalStatModify(final StatsSet params) {
        super(params, (Stat)params.getEnum("stat", (Class)Stat.class));
        this.heal = params.getBoolean("heal", false);
    }
    
    public void continuousInstant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (this.heal) {
            ThreadPool.schedule(() -> {
                switch (this.mode) {
                    case DIFF: {
                        this.instantDiff(effected);
                        break;
                    }
                    case PER: {
                        this.instantPercent(effected);
                        break;
                    }
                }
            }, 100L);
        }
    }
    
    private void instantDiff(final Creature effected) {
        switch (this.addStat) {
            case MAX_CP: {
                effected.setCurrentCp(effected.getCurrentCp() + this.power);
                break;
            }
            case MAX_HP: {
                effected.setCurrentHp(effected.getCurrentHp() + this.power);
                break;
            }
            case MAX_MP: {
                effected.setCurrentMp(effected.getCurrentMp() + this.power);
                break;
            }
        }
    }
    
    private void instantPercent(final Creature effected) {
        final double percent = this.power / 100.0;
        switch (this.mulStat) {
            case MAX_CP: {
                effected.setCurrentCp(effected.getCurrentCp() + effected.getMaxCp() * percent);
                break;
            }
            case MAX_HP: {
                effected.setCurrentHp(effected.getCurrentHp() + effected.getMaxHp() * percent);
                break;
            }
            case MAX_MP: {
                effected.setCurrentMp(effected.getCurrentMp() + effected.getMaxMp() * percent);
                break;
            }
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new VitalStatModify(data);
        }
        
        public String effectName() {
            return "vital-stat-modify";
        }
    }
}
