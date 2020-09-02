// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionPlayerCanTransform extends Condition
{
    private final boolean _val;
    
    public ConditionPlayerCanTransform(final boolean val) {
        this._val = val;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        boolean canTransform = true;
        final Player player = effector.getActingPlayer();
        if (player == null || player.isAlikeDead()) {
            canTransform = false;
        }
        else if (player.isSitting()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_TRANSFORM_WHILE_SITTING);
            canTransform = false;
        }
        else if (player.isTransformed()) {
            player.sendPacket(SystemMessageId.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
            canTransform = false;
        }
        else if (player.isInWater()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_POLYMORPH_INTO_THE_DESIRED_FORM_IN_WATER);
            canTransform = false;
        }
        else if (player.isFlyingMounted() || player.isMounted()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_TRANSFORM_WHILE_RIDING_A_PET);
            canTransform = false;
        }
        else if (player.isOnCustomEvent()) {
            player.sendMessage("You cannot transform while registered on an event.");
            canTransform = false;
        }
        return this._val == canTransform;
    }
}
