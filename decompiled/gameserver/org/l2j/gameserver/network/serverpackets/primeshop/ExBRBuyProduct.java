// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.primeshop;

import org.l2j.gameserver.model.interfaces.IIdentifiable;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExBRBuyProduct extends ServerPacket
{
    private final int _reply;
    
    public ExBRBuyProduct(final ExBrProductReplyType type) {
        this._reply = type.getId();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_BR_BUY_PRODUCT_ACK);
        this.writeInt(this._reply);
    }
    
    public enum ExBrProductReplyType implements IIdentifiable
    {
        SUCCESS(1), 
        LACK_OF_POINT(-1), 
        INVALID_PRODUCT(-2), 
        INCORRECT_COUNT(-3), 
        INVENTROY_OVERFLOW(-4), 
        CLOSED_PRODUCT(-5), 
        SERVER_ERROR(-6), 
        BEFORE_SALE_DATE(-7), 
        AFTER_SALE_DATE(-8), 
        INVENTORY_FULL0(-9), 
        INVALID_ITEM(-10), 
        INVENTORY_FULL(-11), 
        NOT_DAY_OF_WEEK(-12), 
        NOT_TIME_OF_DAY(-13), 
        SOLD_OUT(-14), 
        RECIPIENT_OR_CHARACTER_DELETED(-17), 
        CANNOT_SEND_EMAIL_TO_YOURSELF(-18), 
        EMAIL_LIMIT_REACHED(-19), 
        EMAIL_OTHER_LIMIT_REACHED(-20), 
        CANNOT_SEND_EMAIL_BLOCKED(-21), 
        MAXIMUM_QUANTITY_REACHED(-22), 
        NOT_A_GIFT(-23), 
        INVALID_LEVEL(-24), 
        NOT_ENOUGH_ADENA(-25), 
        NOT_ENOUGH_HERO_COIN(-26), 
        INVALID_DATE_CREATION(-27), 
        ALREADY_BOUGHT(-28);
        
        private final int _id;
        
        private ExBrProductReplyType(final int id) {
            this._id = id;
        }
        
        @Override
        public int getId() {
            return this._id;
        }
    }
}
