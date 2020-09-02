// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.instance.SiegeFlag;
import org.l2j.gameserver.model.actor.instance.Defender;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import java.util.Objects;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class Fear extends AbstractEffect
{
    private static final int FEAR_RANGE = 500;
    
    private Fear() {
    }
    
    public long getEffectFlags() {
        return EffectFlag.FEAR.getMask();
    }
    
    public boolean canStart(final Creature effector, final Creature effected, final Skill skill) {
        return !Objects.isNull(effected) && !effected.isRaid() && (GameUtils.isPlayer((WorldObject)effected) || GameUtils.isSummon((WorldObject)effected) || (GameUtils.isAttackable((WorldObject)effected) && !(effected instanceof Defender) && !(effected instanceof SiegeFlag) && effected.getTemplate().getRace() != Race.SIEGE_WEAPON));
    }
    
    public int getTicks() {
        return 5;
    }
    
    public boolean onActionTime(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        this.fearAction(null, effected);
        return false;
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        effected.getAI().notifyEvent(CtrlEvent.EVT_AFRAID);
        this.fearAction(effector, effected);
    }
    
    public void onExit(final Creature effector, final Creature effected, final Skill skill) {
        if (!GameUtils.isPlayer((WorldObject)effected)) {
            effected.getAI().notifyEvent(CtrlEvent.EVT_THINK);
        }
    }
    
    private void fearAction(final Creature effector, final Creature effected) {
        final double radians = Math.toRadians((effector != null) ? MathUtil.calculateAngleFrom((ILocational)effector, (ILocational)effected) : MathUtil.convertHeadingToDegree(effected.getHeading()));
        final int posX = (int)(effected.getX() + 500.0 * Math.cos(radians));
        final int posY = (int)(effected.getY() + 500.0 * Math.sin(radians));
        final int posZ = effected.getZ();
        final Location destination = GeoEngine.getInstance().canMoveToTargetLoc(effected.getX(), effected.getY(), effected.getZ(), posX, posY, posZ, effected.getInstanceWorld());
        effected.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Object[] { destination });
    }
    
    public static class Factory implements SkillEffectFactory
    {
        private static final Fear INSTANCE;
        
        public AbstractEffect create(final StatsSet data) {
            return Factory.INSTANCE;
        }
        
        public String effectName() {
            return "Fear";
        }
        
        static {
            INSTANCE = new Fear();
        }
    }
}
