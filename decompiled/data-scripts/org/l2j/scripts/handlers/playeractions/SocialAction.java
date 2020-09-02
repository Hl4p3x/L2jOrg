// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.playeractions;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.serverpackets.ExAskCoupleAction;
import org.l2j.gameserver.ai.NextAction;
import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.taskmanager.AttackStanceTaskManager;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerSocialAction;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.data.xml.model.ActionData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IPlayerActionHandler;

public final class SocialAction implements IPlayerActionHandler
{
    public void useAction(final Player player, final ActionData action, final boolean ctrlPressed, final boolean shiftPressed) {
        switch (action.getOptionId()) {
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 28:
            case 29: {
                this.useSocial(player, action.getOptionId());
                break;
            }
            case 30: {
                if (this.useSocial(player, action.getOptionId())) {
                    player.broadcastInfo();
                    break;
                }
                break;
            }
            case 16:
            case 17:
            case 18: {
                this.useCoupleSocial(player, action.getOptionId());
                break;
            }
        }
    }
    
    private boolean useSocial(final Player activeChar, final int id) {
        if (activeChar.isFishing()) {
            activeChar.sendPacket(SystemMessageId.YOU_CANNOT_DO_THAT_WHILE_FISHING_SCREEN);
            return false;
        }
        if (activeChar.canMakeSocialAction()) {
            activeChar.broadcastPacket((ServerPacket)new org.l2j.gameserver.network.serverpackets.SocialAction(activeChar.getObjectId(), id));
            EventDispatcher.getInstance().notifyEventAsync((IBaseEvent)new OnPlayerSocialAction(activeChar, id), new ListenersContainer[] { (ListenersContainer)activeChar });
        }
        return true;
    }
    
    private void scheduleDeny(final Player player) {
        if (player != null) {
            player.sendPacket(SystemMessageId.THE_COUPLE_ACTION_WAS_DENIED);
            player.onTransactionResponse();
        }
    }
    
    private void useCoupleSocial(final Player player, final int id) {
        if (player == null) {
            return;
        }
        final WorldObject target = player.getTarget();
        if (target == null) {
            player.sendPacket(SystemMessageId.INVALID_TARGET);
            return;
        }
        if (!GameUtils.isPlayer(target)) {
            player.sendPacket(SystemMessageId.INVALID_TARGET);
            return;
        }
        final int distance = (int)MathUtil.calculateDistance2D((ILocational)player, (ILocational)target);
        if (distance > 125 || distance < 15 || player.getObjectId() == target.getObjectId()) {
            player.sendPacket(SystemMessageId.THE_REQUEST_CANNOT_BE_COMPLETED_BECAUSE_THE_TARGET_DOES_NOT_MEET_LOCATION_REQUIREMENTS);
            return;
        }
        if (player.isInStoreMode() || player.isCrafting()) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_IN_PRIVATE_STORE_MODE_OR_IN_A_BATTLE_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
            sm.addPcName(player);
            player.sendPacket(new ServerPacket[] { (ServerPacket)sm });
            return;
        }
        if (player.isInCombat() || player.isInDuel() || AttackStanceTaskManager.getInstance().hasAttackStanceTask((Creature)player)) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_IN_A_BATTLE_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
            sm.addPcName(player);
            player.sendPacket(new ServerPacket[] { (ServerPacket)sm });
            return;
        }
        if (player.isFishing()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_DO_THAT_WHILE_FISHING_SCREEN);
            return;
        }
        if (player.getReputation() < 0) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_IN_A_CHAOTIC_STATE_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
            sm.addPcName(player);
            player.sendPacket(new ServerPacket[] { (ServerPacket)sm });
            return;
        }
        if (player.isInOlympiadMode()) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_PARTICIPATING_IN_THE_OLYMPIAD_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
            sm.addPcName(player);
            player.sendPacket(new ServerPacket[] { (ServerPacket)sm });
            return;
        }
        if (player.isInSiege()) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_IN_A_CASTLE_SIEGE_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
            sm.addPcName(player);
            player.sendPacket(new ServerPacket[] { (ServerPacket)sm });
            return;
        }
        if (player.isInHideoutSiege()) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_PARTICIPATING_IN_A_CLAN_HALL_SIEGE_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
            sm.addPcName(player);
            player.sendPacket(new ServerPacket[] { (ServerPacket)sm });
        }
        if (player.isMounted() || player.isFlyingMounted() || player.isInBoat()) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_RIDING_A_SHIP_STEED_OR_STRIDER_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
            sm.addPcName(player);
            player.sendPacket(new ServerPacket[] { (ServerPacket)sm });
            return;
        }
        if (player.isTransformed()) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_CURRENTLY_TRANSFORMING_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
            sm.addPcName(player);
            player.sendPacket(new ServerPacket[] { (ServerPacket)sm });
            return;
        }
        if (player.isAlikeDead()) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_CURRENTLY_DEAD_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
            sm.addPcName(player);
            player.sendPacket(new ServerPacket[] { (ServerPacket)sm });
            return;
        }
        final Player partner = target.getActingPlayer();
        if (partner.isInStoreMode() || partner.isCrafting()) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_IN_PRIVATE_STORE_MODE_OR_IN_A_BATTLE_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
            sm.addPcName(partner);
            player.sendPacket(new ServerPacket[] { (ServerPacket)sm });
            return;
        }
        if (partner.isInCombat() || partner.isInDuel() || AttackStanceTaskManager.getInstance().hasAttackStanceTask((Creature)partner)) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_IN_A_BATTLE_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
            sm.addPcName(partner);
            player.sendPacket(new ServerPacket[] { (ServerPacket)sm });
            return;
        }
        if (partner.getMultiSociaAction() > 0) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_ALREADY_PARTICIPATING_IN_A_COUPLE_ACTION_AND_CANNOT_BE_REQUESTED_FOR_ANOTHER_COUPLE_ACTION);
            sm.addPcName(partner);
            player.sendPacket(new ServerPacket[] { (ServerPacket)sm });
            return;
        }
        if (partner.isFishing()) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_FISHING_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
            sm.addPcName(partner);
            player.sendPacket(new ServerPacket[] { (ServerPacket)sm });
            return;
        }
        if (partner.getReputation() < 0) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_IN_A_CHAOTIC_STATE_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
            sm.addPcName(partner);
            player.sendPacket(new ServerPacket[] { (ServerPacket)sm });
            return;
        }
        if (partner.isInOlympiadMode()) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_PARTICIPATING_IN_THE_OLYMPIAD_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
            sm.addPcName(partner);
            player.sendPacket(new ServerPacket[] { (ServerPacket)sm });
            return;
        }
        if (partner.isInHideoutSiege()) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_PARTICIPATING_IN_A_CLAN_HALL_SIEGE_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
            sm.addPcName(partner);
            player.sendPacket(new ServerPacket[] { (ServerPacket)sm });
            return;
        }
        if (partner.isInSiege()) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_IN_A_CASTLE_SIEGE_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
            sm.addPcName(partner);
            player.sendPacket(new ServerPacket[] { (ServerPacket)sm });
            return;
        }
        if (partner.isMounted() || partner.isFlyingMounted() || partner.isInBoat()) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_RIDING_A_SHIP_STEED_OR_STRIDER_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
            sm.addPcName(partner);
            player.sendPacket(new ServerPacket[] { (ServerPacket)sm });
            return;
        }
        if (partner.isTeleporting()) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_CURRENTLY_TELEPORTING_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
            sm.addPcName(partner);
            player.sendPacket(new ServerPacket[] { (ServerPacket)sm });
            return;
        }
        if (partner.isTransformed()) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_CURRENTLY_TRANSFORMING_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
            sm.addPcName(partner);
            player.sendPacket(new ServerPacket[] { (ServerPacket)sm });
            return;
        }
        if (partner.isAlikeDead()) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_CURRENTLY_DEAD_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
            sm.addPcName(partner);
            player.sendPacket(new ServerPacket[] { (ServerPacket)sm });
            return;
        }
        if (player.isAllSkillsDisabled() || partner.isAllSkillsDisabled()) {
            player.sendPacket(SystemMessageId.THE_COUPLE_ACTION_WAS_CANCELLED);
            return;
        }
        player.setMultiSocialAction(id, partner.getObjectId());
        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_REQUESTED_A_COUPLE_ACTION_WITH_C1);
        sm.addPcName(partner);
        player.sendPacket(new ServerPacket[] { (ServerPacket)sm });
        if (player.getAI().getIntention() != CtrlIntention.AI_INTENTION_IDLE || partner.getAI().getIntention() != CtrlIntention.AI_INTENTION_IDLE) {
            final NextAction nextAction = new NextAction(CtrlEvent.EVT_ARRIVED, CtrlIntention.AI_INTENTION_MOVE_TO, () -> partner.sendPacket(new ServerPacket[] { (ServerPacket)new ExAskCoupleAction(player.getObjectId(), id) }));
            player.getAI().setNextAction(nextAction);
            return;
        }
        if (player.isCastingNow()) {
            final NextAction nextAction = new NextAction(CtrlEvent.EVT_FINISH_CASTING, CtrlIntention.AI_INTENTION_CAST, () -> partner.sendPacket(new ServerPacket[] { (ServerPacket)new ExAskCoupleAction(player.getObjectId(), id) }));
            player.getAI().setNextAction(nextAction);
            return;
        }
        partner.sendPacket(new ServerPacket[] { (ServerPacket)new ExAskCoupleAction(player.getObjectId(), id) });
    }
}
