// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class FocusSouls extends AbstractEffect
{
    private final int power;
    
    private FocusSouls(final StatsSet params) {
        this.power = params.getInt("power", 0);
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (!GameUtils.isPlayer((WorldObject)effected) || effected.isAlikeDead()) {
            return;
        }
        final Player target = effected.getActingPlayer();
        final int maxSouls = (int)target.getStats().getValue(Stat.MAX_SOULS, 0.0);
        if (maxSouls > 0) {
            final int amount = this.power;
            if (target.getChargedSouls() < maxSouls) {
                final int count = (target.getChargedSouls() + amount <= maxSouls) ? amount : (maxSouls - target.getChargedSouls());
                target.increaseSouls(count);
            }
            else {
                target.sendPacket(SystemMessageId.SOUL_CANNOT_BE_INCREASED_ANYMORE);
            }
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new FocusSouls(data);
        }
        
        public String effectName() {
            return "FocusSouls";
        }
    }
}
