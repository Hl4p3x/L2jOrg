// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.targethandlers.affectobject;

import org.l2j.gameserver.model.skills.targets.AffectObject;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.handler.IAffectObjectHandler;

public class NotFriendPc implements IAffectObjectHandler
{
    public boolean checkAffectedObject(final Creature activeChar, final Creature target) {
        if (!GameUtils.isPlayer((WorldObject)target)) {
            return false;
        }
        if (activeChar == target) {
            return false;
        }
        final Player player = activeChar.getActingPlayer();
        final Player targetPlayer = target.getActingPlayer();
        if (player == null) {
            return target.isAutoAttackable(activeChar);
        }
        if (player == targetPlayer) {
            return false;
        }
        if (target.isInsidePeaceZone((WorldObject)player) && !player.getAccessLevel().allowPeaceAttack()) {
            return false;
        }
        final Party party = player.getParty();
        final Party targetParty = targetPlayer.getParty();
        if (party != null && targetParty != null && party.getLeaderObjectId() == targetParty.getLeaderObjectId()) {
            return false;
        }
        if (activeChar.isInsideZone(ZoneType.PVP) && target.isInsideZone(ZoneType.PVP)) {
            return true;
        }
        if (player.isInDuel() && targetPlayer.isInDuel() && player.getDuelId() == targetPlayer.getDuelId()) {
            return true;
        }
        if (player.isInOlympiadMode() && targetPlayer.isInOlympiadMode() && player.getOlympiadGameId() == targetPlayer.getOlympiadGameId()) {
            return true;
        }
        final Clan clan = player.getClan();
        final Clan targetClan = targetPlayer.getClan();
        if (clan != null) {
            if (clan == targetClan) {
                return false;
            }
            if (targetClan != null && clan.isAtWarWith(targetClan) && targetClan.isAtWarWith(clan)) {
                return true;
            }
        }
        if (player.getAllyId() != 0 && player.getAllyId() == targetPlayer.getAllyId()) {
            return false;
        }
        if (target.isInsideZone(ZoneType.SIEGE)) {
            return !player.isSiegeFriend((WorldObject)targetPlayer);
        }
        return target.getActingPlayer().getPvpFlag() > 0 || target.getActingPlayer().getReputation() < 0;
    }
    
    public Enum<AffectObject> getAffectObjectType() {
        return (Enum<AffectObject>)AffectObject.NOT_FRIEND_PC;
    }
}
