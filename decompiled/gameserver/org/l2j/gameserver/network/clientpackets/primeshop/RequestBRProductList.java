// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.primeshop;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.primeshop.PrimeShopProduct;
import java.util.Collection;
import org.l2j.gameserver.network.serverpackets.primeshop.ExBRProductList;
import org.l2j.gameserver.data.xml.impl.PrimeShopData;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public final class RequestBRProductList extends ClientPacket
{
    private static final Logger LOGGER;
    private int _type;
    
    public void readImpl() {
        this._type = this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player != null) {
            switch (this._type) {
                case 0: {
                    player.sendPacket(new ExBRProductList(player, 0, PrimeShopData.getInstance().getPrimeItems().values()));
                    break;
                }
                case 1: {
                    break;
                }
                case 2: {
                    break;
                }
                default: {
                    RequestBRProductList.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;I)Ljava/lang/String;, player, this._type));
                    break;
                }
            }
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestBRProductList.class);
    }
}
