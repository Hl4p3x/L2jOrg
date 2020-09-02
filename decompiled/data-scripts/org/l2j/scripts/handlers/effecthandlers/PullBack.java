// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.network.serverpackets.ValidateLocation;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import java.util.Objects;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.network.serverpackets.FlyToLocation;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class PullBack extends AbstractEffect
{
    private final int speed;
    private final int delay;
    private final int animationSpeed;
    private final FlyToLocation.FlyType type;
    
    private PullBack(final StatsSet params) {
        this.speed = params.getInt("power", 0);
        this.delay = params.getInt("delay", this.speed);
        this.animationSpeed = params.getInt("animationSpeed", 0);
        this.type = (FlyToLocation.FlyType)params.getEnum("type", (Class)FlyToLocation.FlyType.class, (Enum)FlyToLocation.FlyType.WARP_FORWARD);
    }
    
    public boolean calcSuccess(final Creature effector, final Creature effected, final Skill skill) {
        return Formulas.calcProbability(100.0, effector, effected, skill);
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (Objects.isNull(effected) || effected.isRaid()) {
            return;
        }
        if (effected.isDebuffBlocked()) {
            return;
        }
        if (!GameUtils.isPlayable((WorldObject)effected) && !GameUtils.isMonster((WorldObject)effected)) {
            return;
        }
        if (GeoEngine.getInstance().canMoveToTarget(effected.getX(), effected.getY(), effected.getZ(), effector.getX(), effector.getY(), effector.getZ(), effector.getInstanceWorld())) {
            effected.broadcastPacket((ServerPacket)new FlyToLocation(effected, (ILocational)effector, this.type, this.speed, this.delay, this.animationSpeed));
            effected.setXYZ((ILocational)effector);
            effected.broadcastPacket((ServerPacket)new ValidateLocation((WorldObject)effected));
            effected.revalidateZone(true);
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new PullBack(data);
        }
        
        public String effectName() {
            return "pull-back";
        }
    }
}
