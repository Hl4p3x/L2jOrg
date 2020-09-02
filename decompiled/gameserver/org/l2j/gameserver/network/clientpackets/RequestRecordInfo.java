// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.UserInfo;
import org.l2j.gameserver.network.GameClient;

public class RequestRecordInfo extends ClientPacket
{
    public void readImpl() {
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        ((GameClient)this.client).sendPacket(new UserInfo(activeChar));
        final Player player;
        Creature obj;
        World.getInstance().forEachVisibleObject(activeChar, WorldObject.class, object -> {
            if (object.isVisibleFor(player)) {
                object.sendInfo(player);
                if (GameUtils.isCreature(object)) {
                    obj = object;
                    if (obj.getAI() != null) {
                        obj.getAI().describeStateToPlayer(player);
                    }
                }
            }
        });
    }
}
