// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.friend;

import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.util.function.IntFunction;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import java.util.Objects;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public final class RequestFriendList extends ClientPacket
{
    public void readImpl() {
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (Objects.isNull(player)) {
            return;
        }
        player.sendPacket(SystemMessageId.FRIENDS_LIST);
        player.sendPacket((ServerPacket[])player.getFriendList().stream().mapToObj((IntFunction<?>)this::statusMessage).toArray(SystemMessage[]::new));
        player.sendPacket(SystemMessageId.SEPARATOR_EQUALS);
    }
    
    private SystemMessage statusMessage(final int friendId) {
        final Player friend = World.getInstance().findPlayer(friendId);
        SystemMessage sm;
        if (Objects.nonNull(friend)) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CURRENTLY_ONLINE).addString(friend.getName());
        }
        else {
            sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CURRENTLY_OFFLINE).addString(PlayerNameTable.getInstance().getNameById(friendId));
        }
        return sm;
    }
}
