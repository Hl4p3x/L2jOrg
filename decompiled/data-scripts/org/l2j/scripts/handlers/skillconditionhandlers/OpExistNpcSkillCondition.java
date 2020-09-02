// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import io.github.joealisson.primitive.IntSet;
import org.l2j.gameserver.engine.skill.api.SkillCondition;

public class OpExistNpcSkillCondition implements SkillCondition
{
    public final IntSet npcIds;
    public final int range;
    public final boolean isAround;
    
    private OpExistNpcSkillCondition(final IntSet npcs, final int range, final boolean around) {
        this.npcIds = npcs;
        this.range = range;
        this.isAround = around;
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        return this.isAround == World.getInstance().hasAnyVisibleObjectInRange((WorldObject)caster, (Class)Npc.class, this.range, npc -> this.npcIds.contains(npc.getId()));
    }
    
    public static final class Factory extends SkillConditionFactory
    {
        public SkillCondition create(final Node xmlNode) {
            final NamedNodeMap attr = xmlNode.getAttributes();
            final IntSet npcs = this.parseIntSet(xmlNode.getFirstChild());
            return (SkillCondition)new OpExistNpcSkillCondition(npcs, this.parseInt(attr, "range"), this.parseBoolean(attr, "around"));
        }
        
        public String conditionName() {
            return "exists-npc";
        }
    }
}
