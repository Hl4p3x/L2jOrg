// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.friend;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.friend.ExFriendDetailInfo;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestFriendDetailInfo extends ClientPacket
{
    private String _name;
    
    public void readImpl() {
        this._name = this.readString();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player != null) {
            ((GameClient)this.client).sendPacket(new ExFriendDetailInfo(player, this._name));
        }
    }
}
