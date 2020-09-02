// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.friend;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.serverpackets.friend.FriendRemove;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import java.util.Objects;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public final class RequestFriendDel extends ClientPacket
{
    private String name;
    
    public void readImpl() {
        this.name = this.readString();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (Objects.isNull(activeChar)) {
            return;
        }
        final int id = PlayerNameTable.getInstance().getIdByName(this.name);
        if (id == -1) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_NOT_ON_YOUR_FRIEND_LIST);
            sm.addString(this.name);
            activeChar.sendPacket(sm);
            return;
        }
        if (!activeChar.getFriendList().contains(id)) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_NOT_ON_YOUR_FRIEND_LIST);
            sm.addString(this.name);
            activeChar.sendPacket(sm);
            return;
        }
        ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).deleteFriendship(activeChar.getObjectId(), id);
        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_REMOVED_FROM_YOUR_FRIENDS_LIST);
        sm.addString(this.name);
        activeChar.sendPacket(sm);
        activeChar.getFriendList().remove(id);
        activeChar.sendPacket(new FriendRemove(this.name, 1));
        final Player player = World.getInstance().findPlayer(this.name);
        if (player != null) {
            player.getFriendList().remove(activeChar.getObjectId());
            player.sendPacket(new FriendRemove(activeChar.getName(), 1));
        }
    }
}
