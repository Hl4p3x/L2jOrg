// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionPlayerHasFreeSummonPoints extends Condition
{
    private final int _summonPoints;
    
    public ConditionPlayerHasFreeSummonPoints(final int summonPoints) {
        this._summonPoints = summonPoints;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        final Player player = effector.getActingPlayer();
        if (player == null) {
            return false;
        }
        boolean canSummon = true;
        if (this._summonPoints == 0 && player.hasServitors()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_USE_THE_S1_SKILL_DUE_TO_INSUFFICIENT_SUMMON_POINTS);
            canSummon = false;
        }
        else if (player.getSummonPoints() + this._summonPoints > player.getMaxSummonPoints()) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_CANNOT_USE_THE_S1_SKILL_DUE_TO_INSUFFICIENT_SUMMON_POINTS);
            sm.addSkillName(skill);
            player.sendPacket(sm);
            canSummon = false;
        }
        return canSummon;
    }
}
