// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.taskmanager.AttackStanceTaskManager;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionPlayerCanSwitchSubclass extends Condition
{
    private final int _subIndex;
    
    public ConditionPlayerCanSwitchSubclass(final int subIndex) {
        this._subIndex = subIndex;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        boolean canSwitchSub = true;
        final Player player = effector.getActingPlayer();
        if (player == null || player.isAlikeDead()) {
            canSwitchSub = false;
        }
        else if ((this._subIndex != 0 && player.getSubClasses().get(this._subIndex) == null) || player.getClassIndex() == this._subIndex) {
            canSwitchSub = false;
        }
        else if (!player.isInventoryUnder90(true)) {
            player.sendPacket(SystemMessageId.A_SUBCLASS_CANNOT_BE_CREATED_OR_CHANGED_BECAUSE_YOU_HAVE_EXCEEDED_YOUR_INVENTORY_LIMIT);
            canSwitchSub = false;
        }
        else if (player.getWeightPenalty() >= 2) {
            player.sendPacket(SystemMessageId.A_SUBCLASS_CANNOT_BE_CREATED_OR_CHANGED_WHILE_YOU_ARE_OVER_YOUR_WEIGHT_LIMIT);
            canSwitchSub = false;
        }
        else if (player.isOnEvent()) {
            player.sendMessage("You cannot change your subclass while registered in an event.");
            canSwitchSub = false;
        }
        else if (player.isAllSkillsDisabled()) {
            canSwitchSub = false;
        }
        else if (player.isAffected(EffectFlag.MUTED)) {
            canSwitchSub = false;
            player.sendPacket(SystemMessageId.YOU_CANNOT_CHANGE_THE_CLASS_BECAUSE_OF_IDENTITY_CRISIS);
        }
        else if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(player) || player.getPvpFlag() > 0 || player.isInInstance() || player.isTransformed() || player.isMounted()) {
            canSwitchSub = false;
        }
        return canSwitchSub;
    }
}
