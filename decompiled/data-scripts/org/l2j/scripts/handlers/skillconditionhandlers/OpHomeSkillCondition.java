// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import org.w3c.dom.Node;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.data.xml.impl.ClanHallManager;
import org.l2j.gameserver.instancemanager.CastleManager;
import java.util.Objects;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.enums.ResidenceType;
import org.l2j.gameserver.engine.skill.api.SkillCondition;

public class OpHomeSkillCondition implements SkillCondition
{
    public final ResidenceType type;
    
    private OpHomeSkillCondition(final ResidenceType type) {
        this.type = type;
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        if (GameUtils.isPlayer((WorldObject)caster)) {
            final Clan clan = caster.getActingPlayer().getClan();
            if (Objects.nonNull(clan)) {
                boolean b = false;
                switch (this.type) {
                    case CASTLE: {
                        b = Objects.nonNull(CastleManager.getInstance().getCastleByOwner(clan));
                        break;
                    }
                    case CLANHALL: {
                        b = Objects.nonNull(ClanHallManager.getInstance().getClanHallByClan(clan));
                        break;
                    }
                    default: {
                        b = false;
                        break;
                    }
                }
                return b;
            }
        }
        return false;
    }
    
    public static final class Factory extends SkillConditionFactory
    {
        public SkillCondition create(final Node xmlNode) {
            return (SkillCondition)new OpHomeSkillCondition((ResidenceType)this.parseEnum(xmlNode.getAttributes(), (Class)ResidenceType.class, "Type"));
        }
        
        public String conditionName() {
            return "residence";
        }
    }
}
