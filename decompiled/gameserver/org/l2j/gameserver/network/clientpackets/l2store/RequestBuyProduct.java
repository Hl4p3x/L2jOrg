// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.l2store;

import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.L2StoreDAO;
import java.util.Calendar;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.network.serverpackets.store.ExBRBuyProduct;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.util.Objects;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.engine.item.shop.l2store.L2StoreProduct;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public abstract class RequestBuyProduct extends ClientPacket
{
    private static final int HERO_COINS = 23805;
    
    protected boolean validatePlayer(final L2StoreProduct product, final int count, final Player player) {
        final long currentTime = System.currentTimeMillis() / 1000L;
        if (Objects.isNull(product)) {
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
        return !this.hasBoughtMaxAmount(product, count, player);
    }
    
    private boolean hasBoughtMaxAmount(final L2StoreProduct product, final int count, final Player player) {
        if (product.getRestrictionAmount() > 0) {
            if (count > product.getRestrictionAmount()) {
                player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.INCORRECT_COUNT));
                return true;
            }
            final int boughtAmount = this.getBoughtAmount(player, product);
            if (boughtAmount >= product.getRestrictionAmount()) {
                player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.ALREADY_BOUGHT));
                return true;
            }
            if (boughtAmount + count > product.getRestrictionAmount()) {
                player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.INCORRECT_COUNT));
                return true;
            }
        }
        return false;
    }
    
    private int getBoughtAmount(final Player player, final L2StoreProduct product) {
        int n = 0;
        switch (product.getRestrictionPeriod()) {
            case DAY: {
                n = ((L2StoreDAO)DatabaseAccess.getDAO((Class)L2StoreDAO.class)).countBoughtProductToday(player.getAccountName(), product.getId());
                break;
            }
            case MONTH: {
                n = ((L2StoreDAO)DatabaseAccess.getDAO((Class)L2StoreDAO.class)).countBoughtProductInDays(player.getAccountName(), product.getId(), 30);
                break;
            }
            case EVER: {
                n = ((L2StoreDAO)DatabaseAccess.getDAO((Class)L2StoreDAO.class)).countBoughtProduct(player.getAccountName(), product.getId());
                break;
            }
            default: {
                throw new IncompatibleClassChangeError();
            }
        }
        return n;
    }
    
    protected int validatePaymentId(final L2StoreProduct item) {
        int n = 0;
        switch (item.getPaymentType()) {
            case 0: {
                n = 0;
                break;
            }
            case 1: {
                n = 57;
                break;
            }
            case 2: {
                n = 23805;
                break;
            }
            default: {
                n = -1;
                break;
            }
        }
        return n;
    }
    
    protected boolean processPayment(final Player player, final L2StoreProduct item, final int count) {
        final int price = Math.multiplyExact(item.getPrice(), count);
        final int paymentId = this.validatePaymentId(item);
        if (paymentId < 0) {
            player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.LACK_OF_POINT));
            return false;
        }
        if (paymentId > 0) {
            if (!player.destroyItemByItemId("PrimeShop", paymentId, price, player, true)) {
                player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.LACK_OF_POINT));
                return false;
            }
        }
        else {
            if (player.getNCoins() < price) {
                player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.LACK_OF_POINT));
                player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_NCOIN);
                return false;
            }
            if (price > 0) {
                player.updateNCoins(-price);
                player.updateVipPoints(price);
            }
        }
        return true;
    }
}
