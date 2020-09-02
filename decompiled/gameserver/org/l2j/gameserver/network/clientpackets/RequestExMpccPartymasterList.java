// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.network.serverpackets.ExMPCCPartymasterList;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.Party;
import java.util.function.Predicate;
import java.util.Objects;
import java.util.function.Function;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Set;
import org.l2j.gameserver.enums.MatchingRoomType;
import org.l2j.gameserver.network.GameClient;

public class RequestExMpccPartymasterList extends ClientPacket
{
    public void readImpl() {
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        final MatchingRoom room = activeChar.getMatchingRoom();
        if (room != null && room.getRoomType() == MatchingRoomType.COMMAND_CHANNEL) {
            final Set<String> leadersName = room.getMembers().stream().map((Function<? super Object, ?>)Player::getParty).filter(Objects::nonNull).map((Function<? super Object, ?>)Party::getLeader).map((Function<? super Object, ?>)WorldObject::getName).collect((Collector<? super Object, ?, Set<String>>)Collectors.toSet());
            activeChar.sendPacket(new ExMPCCPartymasterList(leadersName));
        }
    }
}
