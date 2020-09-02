// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExRpItemLink;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.World;

public class RequestExRqItemLink extends ClientPacket
{
    private int _objectId;
    
    public void readImpl() {
        this._objectId = this.readInt();
    }
    
    public void runImpl() {
        final WorldObject object = World.getInstance().findObject(this._objectId);
        if (GameUtils.isItem(object)) {
            final Item item = (Item)object;
            if (item.isPublished()) {
                ((GameClient)this.client).sendPacket(new ExRpItemLink(item));
            }
        }
    }
}
