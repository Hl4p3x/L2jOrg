// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.model.buylist.ProductList;
import org.l2j.gameserver.network.serverpackets.ExBuySellList;
import org.l2j.gameserver.network.serverpackets.BuyList;
import org.l2j.gameserver.enums.TaxType;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.data.xml.impl.BuyListData;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;

public class Merchant extends Folk
{
    public Merchant(final NpcTemplate template) {
        super(template);
        this.setInstanceType(InstanceType.L2MerchantInstance);
    }
    
    @Override
    public boolean isAutoAttackable(final Creature attacker) {
        return GameUtils.isMonster(attacker) || super.isAutoAttackable(attacker);
    }
    
    @Override
    public String getHtmlPath(final int npcId, final int val) {
        String pom;
        if (val == 0) {
            pom = Integer.toString(npcId);
        }
        else {
            pom = invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, npcId, val);
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, pom);
    }
    
    public final void showBuyWindow(final Player player, final int val) {
        this.showBuyWindow(player, val, true);
    }
    
    public final void showBuyWindow(final Player player, final int val, final boolean applyCastleTax) {
        final ProductList buyList = BuyListData.getInstance().getBuyList(val);
        if (buyList == null) {
            Creature.LOGGER.warn(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, val));
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (!buyList.isNpcAllowed(this.getId())) {
            Creature.LOGGER.warn(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, val, this.getId()));
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        player.setInventoryBlockingStatus(true);
        player.sendPacket(new BuyList(buyList, player, applyCastleTax ? this.getCastleTaxRate(TaxType.BUY) : 0.0));
        player.sendPacket(new ExBuySellList(player, false, applyCastleTax ? this.getCastleTaxRate(TaxType.SELL) : 0.0));
    }
}
