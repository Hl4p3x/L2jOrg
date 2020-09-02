// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.instancemanager.PetitionManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminPetition implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        int petitionId = -1;
        try {
            petitionId = Integer.parseInt(command.split(" ")[1]);
        }
        catch (Exception ex) {}
        if (command.equals("admin_view_petitions")) {
            PetitionManager.getInstance().sendPendingPetitionList(activeChar);
        }
        else if (command.startsWith("admin_view_petition")) {
            PetitionManager.getInstance().viewPetition(activeChar, petitionId);
        }
        else if (command.startsWith("admin_accept_petition")) {
            if (PetitionManager.getInstance().isPlayerInConsultation(activeChar)) {
                activeChar.sendPacket(SystemMessageId.YOU_MAY_ONLY_SUBMIT_ONE_PETITION_ACTIVE_AT_A_TIME);
                return true;
            }
            if (PetitionManager.getInstance().isPetitionInProcess(petitionId)) {
                activeChar.sendPacket(SystemMessageId.YOUR_PETITION_IS_BEING_PROCESSED);
                return true;
            }
            if (!PetitionManager.getInstance().acceptPetition(activeChar, petitionId)) {
                activeChar.sendPacket(SystemMessageId.NOT_UNDER_PETITION_CONSULTATION);
            }
        }
        else if (command.startsWith("admin_reject_petition")) {
            if (!PetitionManager.getInstance().rejectPetition(activeChar, petitionId)) {
                activeChar.sendPacket(SystemMessageId.FAILED_TO_CANCEL_PETITION_PLEASE_TRY_AGAIN_LATER);
            }
            PetitionManager.getInstance().sendPendingPetitionList(activeChar);
        }
        else if (command.equals("admin_reset_petitions")) {
            if (PetitionManager.getInstance().isPetitionInProcess()) {
                activeChar.sendPacket(SystemMessageId.YOUR_PETITION_IS_BEING_PROCESSED);
                return false;
            }
            PetitionManager.getInstance().clearPendingPetitions();
            PetitionManager.getInstance().sendPendingPetitionList(activeChar);
        }
        else if (command.startsWith("admin_force_peti")) {
            try {
                final WorldObject targetChar = activeChar.getTarget();
                if (!GameUtils.isPlayer(targetChar)) {
                    activeChar.sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
                    return false;
                }
                final Player targetPlayer = (Player)targetChar;
                final String val = command.substring(15);
                petitionId = PetitionManager.getInstance().submitPetition(targetPlayer, val, 9);
                PetitionManager.getInstance().acceptPetition(activeChar, petitionId);
            }
            catch (StringIndexOutOfBoundsException e) {
                BuilderUtil.sendSysMessage(activeChar, "Usage: //force_peti text");
                return false;
            }
        }
        return true;
    }
    
    public String[] getAdminCommandList() {
        return AdminPetition.ADMIN_COMMANDS;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_view_petitions", "admin_view_petition", "admin_accept_petition", "admin_reject_petition", "admin_reset_petitions", "admin_force_peti" };
    }
}
