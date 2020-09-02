// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.voicedcommandhandlers;

import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IVoicedCommandHandler;

public class CastleVCmd implements IVoicedCommandHandler
{
    private static final String[] VOICED_COMMANDS;
    
    public boolean useVoicedCommand(final String command, final Player activeChar, final String params) {
        switch (command) {
            case "opendoors": {
                if (!params.equals("castle")) {
                    activeChar.sendMessage("Only Castle doors can be open.");
                    return false;
                }
                if (!activeChar.isClanLeader()) {
                    activeChar.sendPacket(SystemMessageId.ONLY_THE_CLAN_LEADER_MAY_ISSUE_COMMANDS);
                    return false;
                }
                final Door door = (Door)activeChar.getTarget();
                if (door == null) {
                    activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
                    return false;
                }
                final Castle castle = CastleManager.getInstance().getCastleById(activeChar.getClan().getCastleId());
                if (castle == null) {
                    activeChar.sendMessage("Your clan does not own a castle.");
                    return false;
                }
                if (castle.getSiege().isInProgress()) {
                    activeChar.sendPacket(SystemMessageId.THE_CASTLE_GATES_CANNOT_BE_OPENED_AND_CLOSED_DURING_A_SIEGE);
                    return false;
                }
                if (castle.checkIfInZone((ILocational)door)) {
                    activeChar.sendPacket(SystemMessageId.THE_GATE_IS_BEING_OPENED);
                    door.openMe();
                    break;
                }
                break;
            }
            case "closedoors": {
                if (!params.equals("castle")) {
                    activeChar.sendMessage("Only Castle doors can be closed.");
                    return false;
                }
                if (!activeChar.isClanLeader()) {
                    activeChar.sendPacket(SystemMessageId.ONLY_THE_CLAN_LEADER_MAY_ISSUE_COMMANDS);
                    return false;
                }
                final Door door2 = (Door)activeChar.getTarget();
                if (door2 == null) {
                    activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
                    return false;
                }
                final Castle castle2 = CastleManager.getInstance().getCastleById(activeChar.getClan().getCastleId());
                if (castle2 == null) {
                    activeChar.sendMessage("Your clan does not own a castle.");
                    return false;
                }
                if (castle2.getSiege().isInProgress()) {
                    activeChar.sendPacket(SystemMessageId.THE_CASTLE_GATES_CANNOT_BE_OPENED_AND_CLOSED_DURING_A_SIEGE);
                    return false;
                }
                if (castle2.checkIfInZone((ILocational)door2)) {
                    activeChar.sendMessage("The gate is being closed.");
                    door2.closeMe();
                    break;
                }
                break;
            }
            case "ridewyvern": {
                if (activeChar.isClanLeader() && activeChar.getClan().getCastleId() > 0) {
                    activeChar.mount(12621, 0, true);
                    break;
                }
                break;
            }
        }
        return true;
    }
    
    public String[] getVoicedCommandList() {
        return CastleVCmd.VOICED_COMMANDS;
    }
    
    static {
        VOICED_COMMANDS = new String[] { "opendoors", "closedoors", "ridewyvern" };
    }
}
