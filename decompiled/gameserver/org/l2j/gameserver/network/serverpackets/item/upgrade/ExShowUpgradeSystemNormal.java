// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.item.upgrade;

import org.l2j.gameserver.model.holders.ItemHolder;
import java.util.Collection;
import java.util.Collections;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.api.item.UpgradeType;

public class ExShowUpgradeSystemNormal extends AbstractUpgradeSystem
{
    private final UpgradeType type;
    
    public ExShowUpgradeSystemNormal(final UpgradeType type) {
        this.type = type;
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SHOW_UPGRADE_SYSTEM_NORMAL);
        this.writeShort(1);
        this.writeShort(this.type.ordinal());
        this.writeShort(100);
        this.writeMaterial((Collection<ItemHolder>)Collections.emptyList());
    }
}
