// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.PackageSendableList;
import org.l2j.gameserver.network.GameClient;

public class RequestPackageSendableItemList extends ClientPacket
{
    private int _objectId;
    
    public void readImpl() {
        this._objectId = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        ((GameClient)this.client).sendPacket(new PackageSendableList(1, activeChar, this._objectId));
        ((GameClient)this.client).sendPacket(new PackageSendableList(2, activeChar, this._objectId));
    }
}
