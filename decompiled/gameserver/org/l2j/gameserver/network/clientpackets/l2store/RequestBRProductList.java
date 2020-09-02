// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.l2store;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.engine.item.shop.l2store.L2StoreProduct;
import java.util.Collection;
import org.l2j.gameserver.network.serverpackets.store.ExBRProductList;
import org.l2j.gameserver.engine.item.shop.L2Store;
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
                    player.sendPacket(new ExBRProductList(player, 0, L2Store.getInstance().getPrimeItems().values()));
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
