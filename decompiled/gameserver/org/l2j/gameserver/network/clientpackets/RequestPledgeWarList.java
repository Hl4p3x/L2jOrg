// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.PledgeReceiveWarList;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.GameClient;

public final class RequestPledgeWarList extends ClientPacket
{
    private int _page;
    private int _tab;
    
    public void readImpl() {
        this._page = this.readInt();
        this._tab = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        if (activeChar.getClan() == null) {
            return;
        }
        activeChar.sendPacket(new PledgeReceiveWarList(activeChar.getClan(), this._tab));
    }
}
