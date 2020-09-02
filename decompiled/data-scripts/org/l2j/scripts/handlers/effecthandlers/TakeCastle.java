// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.enums.CastleSide;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class TakeCastle extends AbstractEffect
{
    private final CastleSide side;
    
    private TakeCastle(final StatsSet params) {
        this.side = (CastleSide)params.getEnum("side", (Class)CastleSide.class);
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (!GameUtils.isPlayer((WorldObject)effector)) {
            return;
        }
        final Castle castle = CastleManager.getInstance().getCastle((ILocational)effector);
        castle.engrave(effector.getClan(), (WorldObject)effected, this.side);
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new TakeCastle(data);
        }
        
        public String effectName() {
            return "take-castle";
        }
    }
}
