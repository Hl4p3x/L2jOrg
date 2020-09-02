// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.interfaces.ILocational;
import java.util.Objects;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class CallParty extends AbstractEffect
{
    private CallParty() {
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        final Party party = effector.getParty();
        if (Objects.isNull(party)) {
            return;
        }
        party.getMembers().stream().filter(partyMember -> effector != partyMember && CallPc.checkSummonTargetStatus(partyMember, (Creature)effector.getActingPlayer())).forEach(partyMember -> partyMember.teleToLocation((ILocational)effector, true));
    }
    
    public static class Factory implements SkillEffectFactory
    {
        private static final CallParty INSTANCE;
        
        public AbstractEffect create(final StatsSet data) {
            return Factory.INSTANCE;
        }
        
        public String effectName() {
            return "CallParty";
        }
        
        static {
            INSTANCE = new CallParty();
        }
    }
}
