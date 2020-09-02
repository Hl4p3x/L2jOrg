// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.pvpbook;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.pvpbook.ExKillerLocation;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.GameClient;
import java.util.Objects;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class ExRequestKillerLocation extends ClientPacket
{
    private String killerName;
    
    @Override
    protected void readImpl() throws Exception {
        this.killerName = this.readSizedString();
    }
    
    @Override
    protected void runImpl() {
        final Player killer = World.getInstance().findPlayer(this.killerName);
        if (Objects.isNull(killer)) {
            ((GameClient)this.client).sendPacket(SystemMessageId.CANNOT_LOCATE_THE_SELECTED_FOE_THE_FOE_IS_NOT_ONLINE);
        }
        else {
            final Player player = ((GameClient)this.client).getPlayer();
            if (player.getRevengeUsableLocation() > 0 && (player.getRevengeUsableLocation() == 5 || player.reduceAdena("Killer Location", 50000L, player, true))) {
                ((GameClient)this.client).sendPacket(new ExKillerLocation(killer));
                player.useRevengeLocation();
            }
        }
    }
}
