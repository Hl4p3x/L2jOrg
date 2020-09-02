// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.teleport;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.teleport.ExTeleportFavoritesList;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class ExRequestTeleportFavoritesUIToggle extends ClientPacket
{
    private boolean on;
    
    @Override
    protected void readImpl() {
        this.on = this.readBoolean();
    }
    
    @Override
    protected void runImpl() {
        ((GameClient)this.client).sendPacket(new ExTeleportFavoritesList(this.on));
    }
}
