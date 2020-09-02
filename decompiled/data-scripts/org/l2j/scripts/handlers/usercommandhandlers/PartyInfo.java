// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.usercommandhandlers;

import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IUserCommandHandler;

public class PartyInfo implements IUserCommandHandler
{
    private static final int[] COMMAND_IDS;
    
    public boolean useUserCommand(final int id, final Player player) {
        if (id != PartyInfo.COMMAND_IDS[0]) {
            return false;
        }
        player.sendPacket(SystemMessageId.PARTY_INFORMATION);
        if (player.isInParty()) {
            final Party party = player.getParty();
            switch (party.getDistributionType()) {
                case FINDERS_KEEPERS: {
                    player.sendPacket(SystemMessageId.LOOTING_METHOD_FINDERS_KEEPERS);
                    break;
                }
                case RANDOM: {
                    player.sendPacket(SystemMessageId.LOOTING_METHOD_RANDOM);
                    break;
                }
                case RANDOM_INCLUDING_SPOIL: {
                    player.sendPacket(SystemMessageId.LOOTING_METHOD_RANDOM_INCLUDING_SPOIL);
                    break;
                }
                case BY_TURN: {
                    player.sendPacket(SystemMessageId.LOOTING_METHOD_BY_TURN);
                    break;
                }
                case BY_TURN_INCLUDING_SPOIL: {
                    player.sendPacket(SystemMessageId.LOOTING_METHOD_BY_TURN_INCLUDING_SPOIL);
                    break;
                }
            }
        }
        player.sendPacket(SystemMessageId.SEPARATOR_EQUALS);
        return true;
    }
    
    public int[] getUserCommandList() {
        return PartyInfo.COMMAND_IDS;
    }
    
    static {
        COMMAND_IDS = new int[] { 81 };
    }
}
