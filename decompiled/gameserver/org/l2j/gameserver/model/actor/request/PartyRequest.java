// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.request;

import java.util.Objects;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.actor.instance.Player;

public class PartyRequest extends AbstractRequest
{
    private final Player _targetPlayer;
    private final Party _party;
    
    public PartyRequest(final Player activeChar, final Player targetPlayer, final Party party) {
        super(activeChar);
        Objects.requireNonNull(targetPlayer);
        Objects.requireNonNull(party);
        this._targetPlayer = targetPlayer;
        this._party = party;
    }
    
    public Player getTargetPlayer() {
        return this._targetPlayer;
    }
    
    public Party getParty() {
        return this._party;
    }
    
    @Override
    public boolean isUsing(final int objectId) {
        return false;
    }
    
    @Override
    public void onTimeout() {
        super.onTimeout();
        this.getPlayer().removeRequest(this.getClass());
        this._targetPlayer.removeRequest(this.getClass());
    }
}
