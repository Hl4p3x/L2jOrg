// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.item.upgrade;

import org.l2j.gameserver.model.holders.ItemHolder;
import java.util.Collection;
import java.util.Collections;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExShowUpgradeSystem extends AbstractUpgradeSystem
{
    @Override
    protected void writeImpl(final GameClient client) throws Exception {
        this.writeId(ServerExPacketId.EX_SHOW_UPGRADE_SYSTEM);
        this.writeShort(1);
        this.writeShort(100);
        this.writeMaterial((Collection<ItemHolder>)Collections.emptyList());
    }
}
