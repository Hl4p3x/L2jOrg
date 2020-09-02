// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillCondition;

public class TargetMyPartySkillCondition implements SkillCondition
{
    public final boolean includeCaster;
    
    protected TargetMyPartySkillCondition(final boolean includeCaster) {
        this.includeCaster = includeCaster;
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        if (!GameUtils.isPlayer(target)) {
            return false;
        }
        final Party party = caster.getParty();
        final Party targetParty = target.getActingPlayer().getParty();
        return (party == null) ? (this.includeCaster && caster == target) : (this.includeCaster ? (party == targetParty) : (party == targetParty && caster != target));
    }
}
