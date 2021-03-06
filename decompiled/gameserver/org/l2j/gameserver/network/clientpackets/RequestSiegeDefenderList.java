// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SiegeDefenderList;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.instancemanager.CastleManager;

public final class RequestSiegeDefenderList extends ClientPacket
{
    private int _castleId;
    
    public void readImpl() {
        this._castleId = this.readInt();
    }
    
    public void runImpl() {
        final Castle castle = CastleManager.getInstance().getCastleById(this._castleId);
        if (castle == null) {
            return;
        }
        ((GameClient)this.client).sendPacket(new SiegeDefenderList(castle));
    }
}
