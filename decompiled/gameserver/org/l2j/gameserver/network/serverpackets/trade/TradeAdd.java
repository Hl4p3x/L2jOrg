// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.trade;

import org.l2j.gameserver.model.TradeItem;
import org.l2j.gameserver.network.serverpackets.AbstractItemPacket;

public abstract class TradeAdd extends AbstractItemPacket
{
    private final int type;
    private final TradeItem item;
    
    protected TradeAdd(final int type, final TradeItem item) {
        this.type = type;
        this.item = item;
    }
    
    protected void writeItemAdd() {
        this.writeByte(this.type);
        if (this.type == 2) {
            this.writeInt(1);
        }
        this.writeInt(1);
        this.writeItem(this.item);
    }
}
