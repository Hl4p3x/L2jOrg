// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.RecipeList;
import java.util.List;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.network.serverpackets.RecipeShopMsg;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.data.xml.impl.RecipeData;
import java.util.Arrays;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.taskmanager.AttackStanceTaskManager;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.ManufactureItem;

public final class RequestRecipeShopListSet extends ClientPacket
{
    private static final int BATCH_LENGTH = 12;
    private ManufactureItem[] _items;
    
    public RequestRecipeShopListSet() {
        this._items = null;
    }
    
    public void readImpl() throws InvalidDataPacketException {
        final int count = this.readInt();
        if (count <= 0 || count > Config.MAX_ITEM_IN_PACKET || count * 12 != this.available()) {
            throw new InvalidDataPacketException();
        }
        this._items = new ManufactureItem[count];
        for (int i = 0; i < count; ++i) {
            final int id = this.readInt();
            final long cost = this.readLong();
            if (cost < 0L) {
                this._items = null;
                throw new InvalidDataPacketException();
            }
            this._items[i] = new ManufactureItem(id, cost);
        }
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        if (this._items == null) {
            player.setPrivateStoreType(PrivateStoreType.NONE);
            player.broadcastUserInfo();
            return;
        }
        if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(player) || player.isInDuel()) {
            ((GameClient)this.client).sendPacket(SystemMessageId.WHILE_YOU_ARE_ENGAGED_IN_COMBAT_YOU_CANNOT_OPERATE_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (player.isInsideZone(ZoneType.NO_STORE)) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_CANNOT_OPEN_A_PRIVATE_WORKSHOP_HERE);
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        final List<RecipeList> dwarfRecipes = Arrays.asList(player.getDwarvenRecipeBook());
        final List<RecipeList> commonRecipes = Arrays.asList(player.getCommonRecipeBook());
        player.getManufactureItems().clear();
        for (final ManufactureItem i : this._items) {
            final RecipeList list = RecipeData.getInstance().getRecipeList(i.getRecipeId());
            if (!dwarfRecipes.contains(list) && !commonRecipes.contains(list)) {
                GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, player.getName(), player.getAccountName()));
                return;
            }
            if (i.getCost() > Inventory.MAX_ADENA) {
                GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;J)Ljava/lang/String;, player.getName(), player.getAccountName(), Inventory.MAX_ADENA));
                return;
            }
            player.getManufactureItems().put(i.getRecipeId(), i);
        }
        player.setStoreName(player.hasManufactureShop() ? player.getStoreName() : "");
        player.setPrivateStoreType(PrivateStoreType.MANUFACTURE);
        player.sitDown();
        player.broadcastUserInfo();
        Broadcast.toSelfAndKnownPlayers(player, new RecipeShopMsg(player));
    }
}
