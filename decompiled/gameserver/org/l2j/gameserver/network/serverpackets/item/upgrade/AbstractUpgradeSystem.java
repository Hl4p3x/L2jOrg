// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.item.upgrade;

import org.l2j.gameserver.model.holders.ItemHolder;
import java.util.Collection;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public abstract class AbstractUpgradeSystem extends ServerPacket
{
    protected void writeMaterial(final Collection<ItemHolder> materials) {
        this.writeInt(materials.size());
        materials.stream().mapToInt(ItemHolder::getId).forEach(x$0 -> this.writeInt(x$0));
        this.writeInt(materials.size());
        materials.forEach(i -> this.writeInt(5));
    }
}
