// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import java.util.Iterator;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Objects;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import java.util.HashMap;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;
import java.util.Map;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class ServitorShare extends AbstractEffect
{
    private final Map<Stat, Float> sharedStats;
    
    private ServitorShare(final StatsSet params) {
        if (params.contains("type")) {
            this.sharedStats = Map.of((Stat)params.getEnum("type", (Class)Stat.class), params.getFloat("power") / 100.0f);
        }
        else {
            this.sharedStats = new HashMap<Stat, Float>();
            StatsSet set;
            params.getSet().forEach((key, value) -> {
                if (key.startsWith("stat")) {
                    set = value;
                    this.sharedStats.put((Stat)set.getEnum("type", (Class)Stat.class), set.getFloat("power") / 100.0f);
                }
            });
        }
    }
    
    public boolean canPump(final Creature effector, final Creature effected, final Skill skill) {
        return GameUtils.isSummon((WorldObject)effected);
    }
    
    public void pump(final Creature effected, final Skill skill) {
        final Player owner = effected.getActingPlayer();
        if (Objects.nonNull(owner)) {
            for (final Map.Entry<Stat, Float> stats : this.sharedStats.entrySet()) {
                effected.getStats().mergeAdd((Stat)stats.getKey(), owner.getStats().getValue((Stat)stats.getKey()) * stats.getValue());
            }
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new ServitorShare(data);
        }
        
        public String effectName() {
            return "servitor-share";
        }
    }
}
