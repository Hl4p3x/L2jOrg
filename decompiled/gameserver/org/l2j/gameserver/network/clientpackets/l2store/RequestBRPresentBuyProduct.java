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
import org.l2j.gameserver.engine.mail.MailEngine;
import org.l2j.gameserver.engine.item.shop.l2store.L2StoreItem;
import org.l2j.gameserver.model.item.container.Attachment;
import org.l2j.gameserver.data.database.data.MailData;
import org.l2j.gameserver.enums.MailType;
import org.l2j.gameserver.network.serverpackets.store.ExBRGamePoint;
import org.l2j.gameserver.engine.item.shop.L2Store;
import org.l2j.gameserver.model.actor.request.AbstractRequest;
import org.l2j.gameserver.model.actor.request.L2StoreRequest;
import org.l2j.gameserver.network.serverpackets.store.ExBRBuyProduct;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.network.GameClient;

public final class RequestBRPresentBuyProduct extends RequestBuyProduct
{
    private int productId;
    private int count;
    private String _charName;
    private String _mailTitle;
    private String _mailBody;
    
    public void readImpl() {
        this.productId = this.readInt();
        this.count = this.readInt();
        this._charName = this.readString();
        this._mailTitle = this.readString();
        this._mailBody = this.readString();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        final int receiverId = PlayerNameTable.getInstance().getIdByName(this._charName);
        if (receiverId <= 0) {
            player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.INVENTORY_FULL0));
            return;
        }
        if (player.hasItemRequest() || player.hasRequest(L2StoreRequest.class, (Class<? extends AbstractRequest>[])new Class[0])) {
            player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.INVENTORY_FULL));
            return;
        }
        try {
            player.addRequest(new L2StoreRequest(player));
            final L2StoreProduct item = L2Store.getInstance().getItem(this.productId);
            if (this.validatePlayer(item, this.count, player) && this.processPayment(player, item, this.count)) {
                ((GameClient)this.client).sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.SUCCESS));
                ((GameClient)this.client).sendPacket(new ExBRGamePoint());
                final MailData mail = MailData.of(receiverId, this._mailTitle, this._mailBody, MailType.PRIME_SHOP_GIFT);
                final Attachment attachement = new Attachment(mail.getSender(), mail.getId());
                for (final L2StoreItem subItem : item.getItems()) {
                    attachement.addItem("Prime Shop Gift", subItem.getId(), subItem.getCount() * this.count, player, this);
                }
                mail.attach(attachement);
                MailEngine.getInstance().sendMail(mail);
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
