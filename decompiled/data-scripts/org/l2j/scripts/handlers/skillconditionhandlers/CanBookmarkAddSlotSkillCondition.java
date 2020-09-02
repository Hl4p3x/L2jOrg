// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import org.w3c.dom.Node;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import java.util.Objects;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillCondition;

public class CanBookmarkAddSlotSkillCondition implements SkillCondition
{
    public final int slots;
    
    private CanBookmarkAddSlotSkillCondition(final int slots) {
        this.slots = slots;
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        final Player player = caster.getActingPlayer();
        if (Objects.isNull(player)) {
            return false;
        }
        if (player.getBookMarkSlot() + this.slots > 18) {
            player.sendPacket(SystemMessageId.YOUR_NUMBER_OF_MY_TELEPORTS_SLOTS_HAS_REACHED_ITS_MAXIMUM_LIMIT);
            return false;
        }
        return true;
    }
    
    public static final class Factory extends SkillConditionFactory
    {
        public SkillCondition create(final Node xmlNode) {
            return (SkillCondition)new CanBookmarkAddSlotSkillCondition(this.parseInt(xmlNode.getAttributes(), "slots"));
        }
        
        public String conditionName() {
            return "can-add-bookmark-slot";
        }
    }
}
