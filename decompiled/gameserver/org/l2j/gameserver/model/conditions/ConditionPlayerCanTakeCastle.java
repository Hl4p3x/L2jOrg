// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionPlayerCanTakeCastle extends Condition
{
    private final boolean _val;
    
    public ConditionPlayerCanTakeCastle(final boolean val) {
        this._val = val;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        if (!GameUtils.isPlayer(effector)) {
            return !this._val;
        }
        final Player player = effector.getActingPlayer();
        boolean canTakeCastle = true;
        if (player.isAlikeDead() || !player.isClanLeader()) {
            canTakeCastle = false;
        }
        final Castle castle = CastleManager.getInstance().getCastle(player);
        if (castle == null || castle.getId() <= 0 || !castle.getSiege().isInProgress() || castle.getSiege().getAttackerClan(player.getClan()) == null) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
            sm.addSkillName(skill);
            player.sendPacket(sm);
            canTakeCastle = false;
        }
        else if (!castle.getArtefacts().contains(effected)) {
            player.sendPacket(SystemMessageId.INVALID_TARGET);
            canTakeCastle = false;
        }
        else if (!GameUtils.checkIfInRange(skill.getCastRange(), player, effected, true)) {
            player.sendPacket(SystemMessageId.THE_DISTANCE_IS_TOO_FAR_AND_SO_THE_CASTING_HAS_BEEN_CANCELLED);
            canTakeCastle = false;
        }
        return this._val == canTakeCastle;
    }
}
