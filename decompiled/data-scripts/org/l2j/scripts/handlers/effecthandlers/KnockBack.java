// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.network.serverpackets.ValidateLocation;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import java.util.Objects;
import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.network.serverpackets.FlyToLocation;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class KnockBack extends AbstractEffect
{
    private final int power;
    private final int speed;
    private final int delay;
    private final int animationSpeed;
    private final boolean knockDown;
    private final FlyToLocation.FlyType type;
    
    private KnockBack(final StatsSet params) {
        this.power = params.getInt("power", 50);
        this.speed = params.getInt("speed", 0);
        this.delay = params.getInt("delay", 0);
        this.animationSpeed = params.getInt("animationSpeed", 0);
        this.knockDown = params.getBoolean("knock-down", false);
        this.type = (FlyToLocation.FlyType)params.getEnum("type", (Class)FlyToLocation.FlyType.class, (Enum)(this.knockDown ? FlyToLocation.FlyType.PUSH_DOWN_HORIZONTAL : FlyToLocation.FlyType.PUSH_HORIZONTAL));
    }
    
    public boolean calcSuccess(final Creature effector, final Creature effected, final Skill skill) {
        return this.knockDown || Formulas.calcProbability(100.0, effector, effected, skill);
    }
    
    public boolean isInstant() {
        return !this.knockDown;
    }
    
    public EffectType getEffectType() {
        return EffectType.KNOCK;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (!this.knockDown) {
            this.knockBack(effector, effected);
        }
    }
    
    public void continuousInstant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (this.knockDown) {
            this.knockBack(effector, effected);
        }
    }
    
    public void onExit(final Creature effector, final Creature effected, final Skill skill) {
        if (!GameUtils.isPlayer((WorldObject)effected)) {
            effected.getAI().notifyEvent(CtrlEvent.EVT_THINK);
        }
    }
    
    private void knockBack(final Creature effector, final Creature effected) {
        if (Objects.isNull(effected) || effected.isRaid()) {
            return;
        }
        final double radians = Math.toRadians(MathUtil.calculateAngleFrom((ILocational)effector, (ILocational)effected));
        final int x = (int)(effected.getX() + this.power * Math.cos(radians));
        final int y = (int)(effected.getY() + this.power * Math.sin(radians));
        final int z = effected.getZ();
        final Location loc = GeoEngine.getInstance().canMoveToTargetLoc(effected.getX(), effected.getY(), effected.getZ(), x, y, z, effected.getInstanceWorld());
        effected.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
        effected.broadcastPacket((ServerPacket)new FlyToLocation(effected, (ILocational)loc, this.type, this.speed, this.delay, this.animationSpeed));
        if (this.knockDown) {
            effected.setHeading(MathUtil.calculateHeadingFrom((ILocational)effected, (ILocational)effector));
        }
        effected.setXYZ((ILocational)loc);
        effected.broadcastPacket((ServerPacket)new ValidateLocation((WorldObject)effected));
        effected.revalidateZone(true);
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new KnockBack(data);
        }
        
        public String effectName() {
            return "knockback";
        }
    }
}
