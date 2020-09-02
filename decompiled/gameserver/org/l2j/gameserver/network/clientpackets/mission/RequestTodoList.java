// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.mission;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.mission.ExOneDayReceiveRewardList;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestTodoList extends ClientPacket
{
    private int _tab;
    private boolean _showAllLevels;
    
    public void readImpl() {
        this._tab = this.readByte();
        this._showAllLevels = (this.readByte() == 1);
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        switch (this._tab) {
            case 9: {
                player.sendPacket(new ExOneDayReceiveRewardList(player, true));
                break;
            }
        }
    }
}
