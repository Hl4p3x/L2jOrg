// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.shuttle;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Shuttle;
import org.l2j.gameserver.world.World;
import java.util.Objects;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestShuttleGetOn extends ClientPacket
{
    private int x;
    private int y;
    private int z;
    
    public void readImpl() {
        this.readInt();
        this.x = this.readInt();
        this.y = this.readInt();
        this.z = this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (Objects.isNull(player)) {
            return;
        }
        final Player player2;
        World.getInstance().forEachVisibleObjectInRange(player, Shuttle.class, 1000, shuttle -> {
            shuttle.addPassenger(player2);
            player2.getInVehiclePosition().setXYZ(this.x, this.y, this.z);
        });
    }
}
