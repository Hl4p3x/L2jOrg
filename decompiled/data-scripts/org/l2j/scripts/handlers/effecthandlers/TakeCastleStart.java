// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import java.util.Objects;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class TakeCastleStart extends AbstractEffect
{
    private TakeCastleStart() {
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (!GameUtils.isPlayer((WorldObject)effector)) {
            return;
        }
        final Castle castle = CastleManager.getInstance().getCastle((ILocational)effected);
        if (Objects.nonNull(castle) && castle.getSiege().isInProgress()) {
            castle.getSiege().announceToPlayer((SystemMessage)SystemMessage.getSystemMessage(SystemMessageId.THE_OPPOSING_CLAN_HAS_STARTED_S1).addSkillName(skill.getId()), false);
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        private static final TakeCastleStart INSTANCE;
        
        public AbstractEffect create(final StatsSet data) {
            return Factory.INSTANCE;
        }
        
        public String effectName() {
            return "TakeCastleStart";
        }
        
        static {
            INSTANCE = new TakeCastleStart();
        }
    }
}
