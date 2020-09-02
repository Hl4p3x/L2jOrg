// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.network.serverpackets.ExChangeMpCost;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.util.function.BiFunction;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillType;
import org.l2j.gameserver.model.effects.AbstractEffect;

public class MagicMpCost extends AbstractEffect
{
    private final SkillType magicType;
    private final double power;
    private final boolean anyType;
    
    private MagicMpCost(final StatsSet params) {
        this.anyType = params.getBoolean("any-type");
        this.magicType = (SkillType)params.getEnum("type", (Class)SkillType.class);
        this.power = params.getDouble("power", 0.0);
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (this.anyType) {
            SkillType.forEach(type -> this.onStart(effected, type));
        }
        else {
            this.onStart(effected, this.magicType);
        }
    }
    
    protected void onStart(final Creature effected, final SkillType type) {
        effected.getStats().mergeMpConsumeTypeValue(type, this.power / 100.0 + 1.0, (BiFunction)MathUtil::mul);
        effected.sendPacket(new ServerPacket[] { (ServerPacket)new ExChangeMpCost(this.power, type.ordinal()) });
    }
    
    public void onExit(final Creature effector, final Creature effected, final Skill skill) {
        if (this.anyType) {
            SkillType.forEach(type -> this.onExit(effected, type));
        }
        else {
            this.onExit(effected, this.magicType);
        }
    }
    
    protected void onExit(final Creature effected, final SkillType type) {
        effected.getStats().mergeMpConsumeTypeValue(type, this.power / 100.0 + 1.0, (BiFunction)MathUtil::div);
        effected.sendPacket(new ServerPacket[] { (ServerPacket)new ExChangeMpCost(-this.power, type.ordinal()) });
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new MagicMpCost(data);
        }
        
        public String effectName() {
            return "magic-cost";
        }
    }
}
