// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.function.Predicate;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillCondition;

public class OpEncumberedSkillCondition implements SkillCondition
{
    public final int slotsPercent;
    public final int weightPercent;
    
    private OpEncumberedSkillCondition(final int slots, final int weight) {
        this.slotsPercent = slots;
        this.weightPercent = weight;
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        if (!GameUtils.isPlayer((WorldObject)caster)) {
            return false;
        }
        final Player player = caster.getActingPlayer();
        final int currentSlotsPercent = this.calcPercent(player.getInventoryLimit(), player.getInventory().getSize(item -> !item.isQuestItem(), new Predicate[0]));
        final int currentWeightPercent = this.calcPercent(player.getMaxLoad(), player.getCurrentLoad());
        return currentSlotsPercent >= this.slotsPercent && currentWeightPercent >= this.weightPercent;
    }
    
    private int calcPercent(final int max, final int current) {
        return 100 - current * 100 / max;
    }
    
    public static final class Factory extends SkillConditionFactory
    {
        public SkillCondition create(final Node xmlNode) {
            final NamedNodeMap attr = xmlNode.getAttributes();
            return (SkillCondition)new OpEncumberedSkillCondition(this.parseInt(attr, "slots"), this.parseInt(attr, "weight"));
        }
        
        public String conditionName() {
            return "encumbered";
        }
    }
}
