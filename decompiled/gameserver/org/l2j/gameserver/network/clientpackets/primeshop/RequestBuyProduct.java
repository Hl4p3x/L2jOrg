// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.primeshop;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.PrimeShopDAO;
import java.util.Calendar;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.network.serverpackets.primeshop.ExBRBuyProduct;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.primeshop.PrimeShopProduct;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public abstract class RequestBuyProduct extends ClientPacket
{
    private static final int HERO_COINS = 23805;
    
    protected static boolean validatePlayer(final PrimeShopProduct product, final int count, final Player player) {
        final long currentTime = System.currentTimeMillis() / 1000L;
        if (product == null) {
            player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.INVALID_PRODUCT));
            GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.getName()));
            return false;
        }
        if (count < 1 || count > 99) {
            GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, player.getName(), count));
            player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.INCORRECT_COUNT));
            return false;
        }
        if ((product.getMinLevel() > 0 && product.getMinLevel() > player.getLevel()) || (product.getMaxLevel() > 0 && product.getMaxLevel() < player.getLevel())) {
            player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.INVALID_LEVEL));
            return false;
        }
        if ((product.getMinBirthday() > 0 && product.getMinBirthday() > player.getBirthdays()) || (product.getMaxBirthday() > 0 && product.getMaxBirthday() < player.getBirthdays())) {
            player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.INVALID_DATE_CREATION));
            return false;
        }
        if ((Calendar.getInstance().get(7) & product.getDaysOfWeek()) == 0x0) {
            player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.NOT_DAY_OF_WEEK));
            return false;
        }
        if (product.getVipTier() > player.getVipTier()) {
            player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.SOLD_OUT));
            return false;
        }
        if (product.getStartSale() > 1 && product.getStartSale() > currentTime) {
            player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.BEFORE_SALE_DATE));
            return false;
        }
        if (product.getEndSale() > 1 && product.getEndSale() < currentTime) {
            player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.AFTER_SALE_DATE));
            return false;
        }
        final int weight = product.getWeight() * count;
        final long slots = product.getCount() * count;
        if (!player.getInventory().validateWeight(weight)) {
            player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.INVENTROY_OVERFLOW));
            return false;
        }
        if (!player.getInventory().validateCapacity(slots)) {
            player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.INVENTORY_FULL));
            return false;
        }
        if (product.getRestrictionDay() > 0 && ((PrimeShopDAO)DatabaseAccess.getDAO((Class)PrimeShopDAO.class)).countBougthItemToday(player.getObjectId(), product.getId()) >= product.getRestrictionDay()) {
            player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.ALREADY_BOUGHT));
            return false;
        }
        return true;
    }
    
    protected static int validatePaymentId(final Player player, final PrimeShopProduct item, final long amount) {
        switch (item.getPaymentType()) {
            case 0: {
                return 0;
            }
            case 1: {
                return 57;
            }
            case 2: {
                return 23805;
            }
            default: {
                return -1;
            }
        }
    }
    
    protected boolean processPayment(final Player activeChar, final PrimeShopProduct item, final int count) {
        final int price = item.getPrice() * count;
        final int paymentId = validatePaymentId(activeChar, item, price);
        if (paymentId < 0) {
            activeChar.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.LACK_OF_POINT));
            return false;
        }
        if (paymentId > 0) {
            if (!activeChar.destroyItemByItemId(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, item.getId()), paymentId, (long)price, (WorldObject)activeChar, true)) {
                activeChar.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.LACK_OF_POINT));
                return false;
            }
        }
        else {
            if (activeChar.getNCoins() < price) {
                activeChar.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.LACK_OF_POINT));
                return false;
            }
            if (price > 0) {
                activeChar.updateNCoins(-price);
                activeChar.updateVipPoints(price);
            }
        }
        return true;
    }
}
