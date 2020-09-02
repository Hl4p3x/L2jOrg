// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.AskJoinPledge;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.GameClient;

public class RequestClanAskJoinByName extends ClientPacket
{
    private String _playerName;
    private int _pledgeType;
    
    public void readImpl() {
        this._playerName = this.readString();
        this._pledgeType = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null || activeChar.getClan() == null) {
            return;
        }
        final Player invitedPlayer = World.getInstance().findPlayer(this._playerName);
        if (!activeChar.getClan().checkClanJoinCondition(activeChar, invitedPlayer, this._pledgeType)) {
            return;
        }
        if (!activeChar.getRequest().setRequest(invitedPlayer, this)) {
            return;
        }
        invitedPlayer.sendPacket(new AskJoinPledge(activeChar, this._pledgeType, activeChar.getClan().getName()));
    }
    
    public int getPledgeType() {
        return this._pledgeType;
    }
}
