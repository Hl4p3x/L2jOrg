// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.PetitionDAO;
import org.l2j.gameserver.network.GameClient;

public class RequestPetitionFeedback extends ClientPacket
{
    private int _rate;
    private String _message;
    
    public void readImpl() {
        this.readInt();
        this._rate = this.readInt();
        this._message = this.readString();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null || player.getLastPetitionGmName() == null) {
            return;
        }
        if (this._rate > 4 || this._rate < 0) {
            return;
        }
        ((PetitionDAO)DatabaseAccess.getDAO((Class)PetitionDAO.class)).saveFeedback(player.getName(), player.getLastPetitionGmName(), this._rate, this._message, System.currentTimeMillis());
    }
}
