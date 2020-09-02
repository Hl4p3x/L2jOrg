// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.network.serverpackets.ValidateLocation;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.FlyToLocation;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class TeleportToTarget extends AbstractEffect
{
    private TeleportToTarget() {
    }
    
    public EffectType getEffectType() {
        return EffectType.TELEPORT_TO_TARGET;
    }
    
    public boolean canStart(final Creature effector, final Creature effected, final Skill skill) {
        return effected != null && GeoEngine.getInstance().canSeeTarget((WorldObject)effected, (WorldObject)effector);
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        final int px = effected.getX();
        final int py = effected.getY();
        double ph = MathUtil.convertHeadingToDegree(effected.getHeading());
        ph += 180.0;
        if (ph > 360.0) {
            ph -= 360.0;
        }
        ph = 3.141592653589793 * ph / 180.0;
        final int x = (int)(px + 25.0 * Math.cos(ph));
        final int y = (int)(py + 25.0 * Math.sin(ph));
        final int z = effected.getZ();
        final Location loc = GeoEngine.getInstance().canMoveToTargetLoc(effector.getX(), effector.getY(), effector.getZ(), x, y, z, effector.getInstanceWorld());
        effector.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
        effector.broadcastPacket((ServerPacket)new FlyToLocation(effector, loc.getX(), loc.getY(), loc.getZ(), FlyToLocation.FlyType.DUMMY));
        effector.abortAttack();
        effector.abortCast();
        effector.setXYZ((ILocational)loc);
        effector.broadcastPacket((ServerPacket)new ValidateLocation((WorldObject)effector));
        effected.revalidateZone(true);
    }
    
    public static class Factory implements SkillEffectFactory
    {
        private static final TeleportToTarget INSTANCE;
        
        public AbstractEffect create(final StatsSet data) {
            return Factory.INSTANCE;
        }
        
        public String effectName() {
            return "TeleportToTarget";
        }
        
        static {
            INSTANCE = new TeleportToTarget();
        }
    }
}
