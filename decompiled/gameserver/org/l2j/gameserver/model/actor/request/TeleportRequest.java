// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.request;

import org.l2j.gameserver.model.actor.instance.Player;

public class TeleportRequest extends AbstractRequest
{
    private final int teleportId;
    
    public TeleportRequest(final Player player, final int teleportId) {
        super(player);
        this.teleportId = teleportId;
    }
    
    @Override
    public boolean isUsing(final int objectId) {
        return false;
    }
    
    public int getTeleportId() {
        return this.teleportId;
    }
}
