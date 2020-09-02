// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;

public final class ExSendSelectedQuestZoneID extends ClientPacket
{
    private int _questZoneId;
    
    public void readImpl() {
        this._questZoneId = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        activeChar.setQuestZoneId(this._questZoneId);
    }
}
