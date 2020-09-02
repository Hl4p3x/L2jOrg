// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;

public final class ExRequestAutoFish extends ClientPacket
{
    private boolean start;
    
    public void readImpl() {
        this.start = this.readBoolean();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (this.start) {
            player.getFishing().startFishing();
        }
        else {
            player.getFishing().stopFishing();
        }
    }
}
