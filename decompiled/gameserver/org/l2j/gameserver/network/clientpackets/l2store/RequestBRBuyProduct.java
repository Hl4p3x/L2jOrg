// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.l2store;

import java.util.Iterator;
import org.l2j.gameserver.engine.item.shop.l2store.L2StoreProduct;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExBRNewIconCashBtnWnd;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.L2StoreDAO;
import org.l2j.gameserver.network.serverpackets.store.ExBRGamePoint;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.item.shop.l2store.L2StoreItem;
import org.l2j.gameserver.engine.item.shop.L2Store;
import org.l2j.gameserver.network.serverpackets.store.ExBRBuyProduct;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.request.AbstractRequest;
import org.l2j.gameserver.model.actor.request.L2StoreRequest;
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
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        if (player.hasItemRequest() || player.hasRequest(L2StoreRequest.class, (Class<? extends AbstractRequest>[])new Class[0])) {
            player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.SERVER_ERROR));
            return;
        }
        try {
            player.addRequest(new L2StoreRequest(player));
            final L2StoreProduct item = L2Store.getInstance().getItem(this.productId);
            if (this.validatePlayer(item, this.count, player) && this.processPayment(player, item, this.count)) {
                for (final L2StoreItem subItem : item.getItems()) {
                    player.addItem("PrimeShop", subItem.getId(), subItem.getCount() * this.count, player, true);
                }
                ((GameClient)this.client).sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.SUCCESS));
                ((GameClient)this.client).sendPacket(new ExBRGamePoint());
                ((L2StoreDAO)DatabaseAccess.getDAO((Class)L2StoreDAO.class)).addHistory(this.productId, this.count, player.getAccountName());
                if (item.isVipGift()) {
                    ((GameClient)this.client).sendPacket(ExBRNewIconCashBtnWnd.NOT_SHOW);
                }
            }
        }
        finally {
            player.removeRequest(L2StoreRequest.class);
        }
    }
}
