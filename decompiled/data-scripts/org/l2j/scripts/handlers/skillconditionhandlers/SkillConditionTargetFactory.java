// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.w3c.dom.Node;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;

public class SkillConditionTargetFactory extends SkillConditionFactory
{
    public SkillCondition create(final Node xmlNode) {
        final Node child = xmlNode.getFirstChild();
        final String nodeName = child.getNodeName();
        SkillCondition skillCondition = null;
        switch (nodeName) {
            case "door": {
                skillCondition = this.doorTargetCondition(child);
                break;
            }
            case "npc": {
                skillCondition = this.npcTargetCondition(child);
                break;
            }
            case "party": {
                skillCondition = this.partyTargetCondition(child);
                break;
            }
            case "race": {
                skillCondition = this.raceTargetCondition(child);
                break;
            }
            default: {
                skillCondition = null;
                break;
            }
        }
        return skillCondition;
    }
    
    private SkillCondition raceTargetCondition(final Node raceNode) {
        return (SkillCondition)new TargetRaceSkillCondition((Race)this.parseEnum(raceNode.getAttributes(), (Class)Race.class, "name"));
    }
    
    private SkillCondition partyTargetCondition(final Node partyNode) {
        return (SkillCondition)new TargetMyPartySkillCondition(this.parseBoolean(partyNode.getAttributes(), "include-caster"));
    }
    
    private SkillCondition npcTargetCondition(final Node npcNode) {
        return (SkillCondition)new OpTargetNpcSkillCondition(this.parseIntSet(npcNode.getFirstChild()));
    }
    
    private SkillCondition doorTargetCondition(final Node doorNode) {
        return (SkillCondition)new OpTargetDoorSkillCondition(this.parseIntSet(doorNode.getFirstChild()));
    }
    
    public String conditionName() {
        return "target";
    }
}
