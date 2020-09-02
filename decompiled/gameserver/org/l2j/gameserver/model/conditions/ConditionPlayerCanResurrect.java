// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.entity.Siege;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.instancemanager.SiegeManager;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionPlayerCanResurrect extends Condition
{
    private final boolean _val;
    
    public ConditionPlayerCanResurrect(final boolean val) {
        this._val = val;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        if (skill.getAffectRange() > 0) {
            return true;
        }
        if (effected == null) {
            return false;
        }
        boolean canResurrect = true;
        if (GameUtils.isPlayer(effected)) {
            final Player player = effected.getActingPlayer();
            if (!player.isDead()) {
                canResurrect = false;
                if (GameUtils.isPlayer(effector)) {
                    final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
                    msg.addSkillName(skill);
                    effector.sendPacket(msg);
                }
            }
            else if (player.isResurrectionBlocked()) {
                canResurrect = false;
                if (GameUtils.isPlayer(effector)) {
                    effector.sendPacket(SystemMessageId.REJECT_RESURRECTION);
                }
            }
            else if (player.isReviveRequested()) {
                canResurrect = false;
                if (GameUtils.isPlayer(effector)) {
                    effector.sendPacket(SystemMessageId.RESURRECTION_HAS_ALREADY_BEEN_PROPOSED);
                }
            }
            else if (skill.getId() != 2393) {
                final Siege siege = SiegeManager.getInstance().getSiege(player);
                if (siege != null && siege.isInProgress()) {
                    final Clan clan = player.getClan();
                    if (clan == null) {
                        canResurrect = false;
                        if (GameUtils.isPlayer(effector)) {
                            effector.sendPacket(SystemMessageId.IT_IS_NOT_POSSIBLE_TO_RESURRECT_IN_BATTLEGROUNDS_WHERE_A_SIEGE_WAR_IS_TAKING_PLACE);
                        }
                    }
                    else if (siege.checkIsDefender(clan) && siege.getControlTowerCount() == 0) {
                        canResurrect = false;
                        if (GameUtils.isPlayer(effector)) {
                            effector.sendPacket(SystemMessageId.THE_GUARDIAN_TOWER_HAS_BEEN_DESTROYED_AND_RESURRECTION_IS_NOT_POSSIBLE);
                        }
                    }
                    else if (siege.checkIsAttacker(clan) && siege.getAttackerClan(clan).getNumFlags() == 0) {
                        canResurrect = false;
                        if (GameUtils.isPlayer(effector)) {
                            effector.sendPacket(SystemMessageId.IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE);
                        }
                    }
                    else {
                        canResurrect = false;
                        if (GameUtils.isPlayer(effector)) {
                            effector.sendPacket(SystemMessageId.IT_IS_NOT_POSSIBLE_TO_RESURRECT_IN_BATTLEGROUNDS_WHERE_A_SIEGE_WAR_IS_TAKING_PLACE);
                        }
                    }
                }
            }
        }
        else if (GameUtils.isSummon(effected)) {
            final Summon summon = (Summon)effected;
            final Player player2 = summon.getOwner();
            if (!summon.isDead()) {
                canResurrect = false;
                if (GameUtils.isPlayer(effector)) {
                    final SystemMessage msg2 = SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
                    msg2.addSkillName(skill);
                    effector.sendPacket(msg2);
                }
            }
            else if (summon.isResurrectionBlocked()) {
                canResurrect = false;
                if (GameUtils.isPlayer(effector)) {
                    effector.sendPacket(SystemMessageId.REJECT_RESURRECTION);
                }
            }
            else if (player2 != null && player2.isRevivingPet()) {
                canResurrect = false;
                if (GameUtils.isPlayer(effector)) {
                    effector.sendPacket(SystemMessageId.RESURRECTION_HAS_ALREADY_BEEN_PROPOSED);
                }
            }
        }
        return this._val == canResurrect;
    }
}
