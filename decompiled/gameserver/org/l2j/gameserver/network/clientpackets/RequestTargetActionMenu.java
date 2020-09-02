// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.GameClient;

public class RequestTargetActionMenu extends ClientPacket
{
    private int _objectId;
    
    public void readImpl() {
        this._objectId = this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        final Player player2;
        Util.doIfNonNull((Object)World.getInstance().findVisibleObject(player, this._objectId), target -> {
            if (target.isTargetable()) {
                player2.setTarget(target);
            }
        });
    }
}
