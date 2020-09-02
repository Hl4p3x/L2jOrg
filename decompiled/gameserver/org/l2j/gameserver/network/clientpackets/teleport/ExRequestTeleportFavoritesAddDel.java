// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.teleport;

import org.l2j.gameserver.data.xml.model.TeleportData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.data.xml.impl.TeleportEngine;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class ExRequestTeleportFavoritesAddDel extends ClientPacket
{
    private boolean activate;
    private int teleportId;
    
    @Override
    protected void readImpl() {
        this.activate = this.readBoolean();
        this.teleportId = this.readInt();
    }
    
    @Override
    protected void runImpl() {
        final Player player;
        TeleportEngine.getInstance().getInfo(this.teleportId).ifPresent(teleport -> {
            player = ((GameClient)this.client).getPlayer();
            if (this.activate) {
                player.addTeleportFavorite(this.teleportId);
            }
            else {
                player.removeTeleportFavorite(this.teleportId);
            }
        });
    }
}
