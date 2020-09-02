// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionTargetMyPartyExceptMe extends Condition
{
    private final boolean _val;
    
    public ConditionTargetMyPartyExceptMe(final boolean val) {
        this._val = val;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        boolean isPartyMember = true;
        final Player player = effector.getActingPlayer();
        if (player == null || !GameUtils.isPlayer(effected)) {
            isPartyMember = false;
        }
        else if (player == effected) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_USE_THIS_ON_YOURSELF);
            isPartyMember = false;
        }
        else if (!player.isInParty() || !player.getParty().equals(effected.getParty())) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
            sm.addSkillName(skill);
            player.sendPacket(sm);
            isPartyMember = false;
        }
        return this._val == isPartyMember;
    }
}
