// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.friend;

import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import java.util.Objects;
import org.l2j.gameserver.world.World;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.IntFunction;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.List;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

abstract class AbstractFriendListPacket extends ServerPacket
{
    protected final List<FriendInfo> info;
    
    AbstractFriendListPacket(final Player player) {
        this.info = player.getFriendList().stream().mapToObj((IntFunction<?>)this::friendInfo).collect((Collector<? super Object, ?, List<FriendInfo>>)Collectors.toList());
    }
    
    private FriendInfo friendInfo(final int friendId) {
        final Player friend = World.getInstance().findPlayer(friendId);
        if (Objects.nonNull(friend)) {
            return new FriendInfo(friendId, friend.getName(), true, friend.getLevel(), friend.getClassId().getId());
        }
        return new FriendInfo(friendId, ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).findFriendData(friendId));
    }
}
