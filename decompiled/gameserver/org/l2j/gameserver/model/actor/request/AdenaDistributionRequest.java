// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.request;

import org.l2j.gameserver.network.serverpackets.adenadistribution.ExDivideAdenaCancel;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.util.List;
import org.l2j.gameserver.model.actor.instance.Player;

public class AdenaDistributionRequest extends AbstractRequest
{
    private final Player _distributor;
    private final List<Player> _players;
    private final int _adenaObjectId;
    private final long _adenaCount;
    
    public AdenaDistributionRequest(final Player activeChar, final Player distributor, final List<Player> players, final int adenaObjectId, final long adenaCount) {
        super(activeChar);
        this._distributor = distributor;
        this._adenaObjectId = adenaObjectId;
        this._players = players;
        this._adenaCount = adenaCount;
    }
    
    public Player getDistributor() {
        return this._distributor;
    }
    
    public List<Player> getPlayers() {
        return this._players;
    }
    
    public int getAdenaObjectId() {
        return this._adenaObjectId;
    }
    
    public long getAdenaCount() {
        return this._adenaCount;
    }
    
    @Override
    public boolean isUsingItem(final int objectId) {
        return objectId == this._adenaObjectId;
    }
    
    @Override
    public void onTimeout() {
        super.onTimeout();
        this._players.forEach(p -> {
            p.removeRequest(AdenaDistributionRequest.class);
            p.sendPacket(ExDivideAdenaCancel.STATIC_PACKET);
        });
    }
}
