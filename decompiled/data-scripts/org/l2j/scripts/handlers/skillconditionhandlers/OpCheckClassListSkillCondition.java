// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.Arrays;
import java.util.Objects;
import java.util.Collections;
import org.w3c.dom.Node;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.enums.SkillConditionAffectType;
import org.l2j.gameserver.model.base.ClassId;
import java.util.Set;
import org.l2j.gameserver.engine.skill.api.SkillCondition;

public class OpCheckClassListSkillCondition implements SkillCondition
{
    public final Set<ClassId> classIds;
    public final SkillConditionAffectType affectType;
    
    private OpCheckClassListSkillCondition(final Set<ClassId> classIds, final SkillConditionAffectType affect) {
        this.classIds = classIds;
        this.affectType = affect;
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        boolean b = false;
        switch (this.affectType) {
            case CASTER: {
                b = (GameUtils.isPlayer((WorldObject)caster) && this.classIds.contains(caster.getActingPlayer().getClassId()));
                break;
            }
            case TARGET: {
                b = (GameUtils.isPlayer(target) && this.classIds.contains(target.getActingPlayer().getClassId()));
                break;
            }
            default: {
                b = false;
                break;
            }
        }
        return b;
    }
    
    public static final class Factory extends SkillConditionFactory
    {
        public SkillCondition create(final Node xmlNode) {
            final SkillConditionAffectType affect = (SkillConditionAffectType)this.parseEnum(xmlNode.getAttributes(), (Class)SkillConditionAffectType.class, "affect");
            final Node listNode = xmlNode.getFirstChild();
            Set<ClassId> classIds = Collections.emptySet();
            if (Objects.nonNull(listNode)) {
                classIds = Arrays.stream(listNode.getTextContent().split(" ")).map((Function<? super String, ?>)ClassId::valueOf).collect((Collector<? super Object, ?, Set<ClassId>>)Collectors.toSet());
            }
            return (SkillCondition)new OpCheckClassListSkillCondition(classIds, affect);
        }
        
        public String conditionName() {
            return "class";
        }
    }
}
