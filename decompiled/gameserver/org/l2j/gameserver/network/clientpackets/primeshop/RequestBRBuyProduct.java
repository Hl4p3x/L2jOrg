// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.primeshop;

import java.util.Iterator;
import org.l2j.gameserver.model.primeshop.PrimeShopProduct;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExBRNewIconCashBtnWnd;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.PrimeShopDAO;
import org.l2j.gameserver.network.serverpackets.primeshop.ExBRGamePoint;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.primeshop.PrimeShopItem;
import org.l2j.gameserver.data.xml.impl.PrimeShopData;
import org.l2j.gameserver.network.serverpackets.primeshop.ExBRBuyProduct;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.request.AbstractRequest;
import org.l2j.gameserver.model.actor.request.PrimeShopRequest;
import org.l2j.gameserver.network.GameClient;

public final class RequestBRBuyProduct extends RequestBuyProduct
{
    private int productId;
    private int count;
    
    public void readImpl() {
        this.productId = this.readInt();
        this.count = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        if (activeChar.hasItemRequest() || activeChar.hasRequest(PrimeShopRequest.class, (Class<? extends AbstractRequest>[])new Class[0])) {
            activeChar.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.SERVER_ERROR));
            return;
        }
        try {
            activeChar.addRequest(new PrimeShopRequest(activeChar));
            final PrimeShopProduct item = PrimeShopData.getInstance().getItem(this.productId);
            if (RequestBuyProduct.validatePlayer(item, this.count, activeChar) && this.processPayment(activeChar, item, this.count)) {
                for (final PrimeShopItem subItem : item.getItems()) {
                    activeChar.addItem("PrimeShop", subItem.getId(), subItem.getCount() * this.count, activeChar, true);
                }
                ((GameClient)this.client).sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.SUCCESS));
                ((GameClient)this.client).sendPacket(new ExBRGamePoint());
                ((PrimeShopDAO)DatabaseAccess.getDAO((Class)PrimeShopDAO.class)).addHistory(this.productId, this.count, activeChar.getObjectId());
                if (item.isVipGift()) {
                    ((GameClient)this.client).sendPacket(ExBRNewIconCashBtnWnd.NOT_SHOW);
                }
            }
        }
        finally {
            activeChar.removeRequest(PrimeShopRequest.class);
        }
    }
}
