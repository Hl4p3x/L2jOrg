// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.tasks.player;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.handler.IItemHandler;
import java.util.Iterator;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.List;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.handler.ItemHandler;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;

public class PetFeedTask implements Runnable
{
    private static final Logger LOGGER;
    private final Player _player;
    
    public PetFeedTask(final Player player) {
        this._player = player;
    }
    
    @Override
    public void run() {
        if (this._player != null) {
            try {
                if (!this._player.isMounted() || this._player.getMountNpcId() == 0 || this._player.getPetData(this._player.getMountNpcId()) == null) {
                    this._player.stopFeed();
                    return;
                }
                if (this._player.getCurrentFeed() > this._player.getFeedConsume()) {
                    this._player.setCurrentFeed(this._player.getCurrentFeed() - this._player.getFeedConsume());
                }
                else {
                    this._player.setCurrentFeed(0);
                    this._player.stopFeed();
                    this._player.dismount();
                    this._player.sendPacket(SystemMessageId.YOU_ARE_OUT_OF_FEED_MOUNT_STATUS_CANCELED);
                }
                final List<Integer> foodIds = this._player.getPetData(this._player.getMountNpcId()).getFood();
                if (foodIds.isEmpty()) {
                    return;
                }
                Item food = null;
                for (final int id : foodIds) {
                    food = this._player.getInventory().getItemByItemId(id);
                    if (food != null) {
                        break;
                    }
                }
                if (food != null && this._player.isHungry()) {
                    final IItemHandler handler = ItemHandler.getInstance().getHandler(food.getEtcItem());
                    if (handler != null) {
                        handler.useItem(this._player, food, false);
                        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOUR_PET_WAS_HUNGRY_SO_IT_ATE_S1);
                        sm.addItemName(food.getId());
                        this._player.sendPacket(sm);
                    }
                }
            }
            catch (Exception e) {
                PetFeedTask.LOGGER.error(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this._player.getMountNpcId()), (Throwable)e);
            }
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)PetFeedTask.class);
    }
}
