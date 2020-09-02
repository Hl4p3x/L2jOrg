// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import org.w3c.dom.Node;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.entity.ClanHall;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.data.xml.impl.ClanHallManager;
import java.util.Objects;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import io.github.joealisson.primitive.IntSet;
import org.l2j.gameserver.engine.skill.api.SkillCondition;

public class OpCheckResidenceSkillCondition implements SkillCondition
{
    public final IntSet residencesId;
    public final boolean isWithin;
    
    private OpCheckResidenceSkillCondition(final IntSet ids, final boolean isWithin) {
        this.residencesId = ids;
        this.isWithin = isWithin;
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        if (GameUtils.isPlayer((WorldObject)caster)) {
            final Clan clan = caster.getActingPlayer().getClan();
            if (Objects.nonNull(clan)) {
                final ClanHall clanHall = ClanHallManager.getInstance().getClanHallByClan(clan);
                if (Objects.nonNull(clanHall)) {
                    return this.isWithin == this.residencesId.contains(clanHall.getId());
                }
            }
        }
        return false;
    }
    
    public static final class Factory extends SkillConditionFactory
    {
        public SkillCondition create(final Node xmlNode) {
            final Boolean isWithin = this.parseBoolean(xmlNode.getAttributes(), "is-within");
            final IntSet ids = this.parseIntSet(xmlNode.getFirstChild());
            return (SkillCondition)new OpCheckResidenceSkillCondition(ids, isWithin);
        }
        
        public String conditionName() {
            return "check-residence";
        }
    }
}
