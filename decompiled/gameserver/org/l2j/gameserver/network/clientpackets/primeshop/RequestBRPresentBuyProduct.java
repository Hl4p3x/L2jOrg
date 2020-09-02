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
import org.l2j.gameserver.engine.mail.MailEngine;
import org.l2j.gameserver.model.primeshop.PrimeShopItem;
import org.l2j.gameserver.model.item.container.Attachment;
import org.l2j.gameserver.data.database.data.MailData;
import org.l2j.gameserver.enums.MailType;
import org.l2j.gameserver.network.serverpackets.primeshop.ExBRGamePoint;
import org.l2j.gameserver.data.xml.impl.PrimeShopData;
import org.l2j.gameserver.model.actor.request.AbstractRequest;
import org.l2j.gameserver.model.actor.request.PrimeShopRequest;
import org.l2j.gameserver.network.serverpackets.primeshop.ExBRBuyProduct;
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
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        final int receiverId = PlayerNameTable.getInstance().getIdByName(this._charName);
        if (receiverId <= 0) {
            activeChar.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.INVENTORY_FULL0));
            return;
        }
        if (activeChar.hasItemRequest() || activeChar.hasRequest(PrimeShopRequest.class, (Class<? extends AbstractRequest>[])new Class[0])) {
            activeChar.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.INVENTORY_FULL));
            return;
        }
        try {
            activeChar.addRequest(new PrimeShopRequest(activeChar));
            final PrimeShopProduct item = PrimeShopData.getInstance().getItem(this.productId);
            if (RequestBuyProduct.validatePlayer(item, this.count, activeChar) && this.processPayment(activeChar, item, this.count)) {
                ((GameClient)this.client).sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.SUCCESS));
                ((GameClient)this.client).sendPacket(new ExBRGamePoint());
                final MailData mail = MailData.of(receiverId, this._mailTitle, this._mailBody, MailType.PRIME_SHOP_GIFT);
                final Attachment attachement = new Attachment(mail.getSender(), mail.getId());
                for (final PrimeShopItem subItem : item.getItems()) {
                    attachement.addItem("Prime Shop Gift", subItem.getId(), subItem.getCount() * this.count, activeChar, this);
                }
                mail.attach(attachement);
                MailEngine.getInstance().sendMail(mail);
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
