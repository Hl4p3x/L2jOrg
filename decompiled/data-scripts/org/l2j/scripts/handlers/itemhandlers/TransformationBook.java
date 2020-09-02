// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.itemhandlers;

import org.l2j.gameserver.network.serverpackets.costume.ExChooseCostumeItem;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.handler.IItemHandler;

public class TransformationBook implements IItemHandler
{
    public boolean useItem(final Playable playable, final Item item, final boolean forceUse) {
        playable.sendPacket(new ServerPacket[] { (ServerPacket)new ExChooseCostumeItem(item.getId()) });
        return true;
    }
}
