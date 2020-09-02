// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.CommandChannel;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ListPartyWaiting;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.instancemanager.MatchingRoomManager;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.model.matching.CommandChannelMatchingRoom;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.enums.PartyMatchingRoomLevelType;

public final class RequestPartyMatchConfig extends ClientPacket
{
    private int _page;
    private int _location;
    private PartyMatchingRoomLevelType _type;
    
    public void readImpl() {
        this._page = this.readInt();
        this._location = this.readInt();
        this._type = ((this.readInt() == 0) ? PartyMatchingRoomLevelType.MY_LEVEL_RANGE : PartyMatchingRoomLevelType.ALL);
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        final Party party = activeChar.getParty();
        final CommandChannel cc = (party == null) ? null : party.getCommandChannel();
        if (party != null && cc != null && cc.getLeader() == activeChar) {
            if (activeChar.getMatchingRoom() == null) {
                activeChar.setMatchingRoom(new CommandChannelMatchingRoom(activeChar.getName(), party.getDistributionType().ordinal(), 1, activeChar.getLevel(), 50, activeChar));
            }
        }
        else if (cc != null && cc.getLeader() != activeChar) {
            activeChar.sendPacket(SystemMessageId.THE_COMMAND_CHANNEL_AFFILIATED_PARTY_S_PARTY_MEMBER_CANNOT_USE_THE_MATCHING_SCREEN);
        }
        else if (party != null && party.getLeader() != activeChar) {
            activeChar.sendPacket(SystemMessageId.THE_LIST_OF_PARTY_ROOMS_CAN_ONLY_BE_VIEWED_BY_A_PERSON_WHO_IS_NOT_PART_OF_A_PARTY);
        }
        else {
            MatchingRoomManager.getInstance().addToWaitingList(activeChar);
            activeChar.sendPacket(new ListPartyWaiting(this._type, this._location, this._page, activeChar.getLevel()));
        }
    }
}
