// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.ensoul;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.ensoul.EnsoulOption;
import org.l2j.gameserver.model.ensoul.EnsoulStone;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ensoul.ExEnsoulResult;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.data.xml.impl.EnsoulData;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.taskmanager.AttackStanceTaskManager;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestItemEnsoul extends ClientPacket
{
    private static final Logger LOGGER;
    private int _itemObjectId;
    private EnsoulItemOption[] _options;
    
    public void readImpl() {
        this._itemObjectId = this.readInt();
        final int options = this.readByte();
        if (options > 0 && options <= 3) {
            this._options = new EnsoulItemOption[options];
            for (int i = 0; i < options; ++i) {
                final int type = this.readByte();
                final int position = this.readByte();
                final int soulCrystalObjectId = this.readInt();
                final int soulCrystalOption = this.readInt();
                if (position > 0 && position < 3 && (type == 1 || type == 2)) {
                    this._options[i] = new EnsoulItemOption(type, position, soulCrystalObjectId, soulCrystalOption);
                }
            }
        }
    }
    
    public void runImpl() {
        if (this._options == null) {
            return;
        }
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        if (player.getPrivateStoreType() != PrivateStoreType.NONE) {
            player.sendPacket(SystemMessageId.RUNE_INSERTION_IS_IMPOSSIBLE_WHEN_PRIVATE_STORE_AND_WORKSHOP_ARE_OPENED);
            return;
        }
        if (player.hasAbnormalType(AbnormalType.FREEZING)) {
            player.sendPacket(SystemMessageId.RUNE_INSERTION_IS_IMPOSSIBLE_WHILE_IN_FROZEN_STATE);
        }
        else {
            if (player.isDead()) {
                player.sendPacket(SystemMessageId.RUNE_INSERTION_IS_IMPOSSIBLE_IF_THE_CHARACTER_IS_DEAD);
                return;
            }
            if (player.getActiveTradeList() != null || player.hasItemRequest()) {
                player.sendPacket(SystemMessageId.RUNE_INSERTION_IS_IMPOSSIBLE_DURING_EXCHANGE);
                return;
            }
            if (player.hasAbnormalType(AbnormalType.PARALYZE)) {
                player.sendPacket(SystemMessageId.RUNE_INSERTION_IS_IMPOSSIBLE_WHILE_PETRIFIED);
                return;
            }
            if (player.isFishing()) {
                player.sendPacket(SystemMessageId.RUNE_INSERTION_IS_IMPOSSIBLE_DURING_FISHING);
                return;
            }
            if (player.isSitting()) {
                player.sendPacket(SystemMessageId.RUNE_INSERTION_IS_IMPOSSIBLE_WHILE_SITTING);
                return;
            }
            if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(player)) {
                player.sendPacket(SystemMessageId.RUNE_INSERTION_IS_IMPOSSIBLE_WHILE_IN_COMBAT);
                return;
            }
        }
        final Item item = player.getInventory().getItemByObjectId(this._itemObjectId);
        if (item == null) {
            RequestItemEnsoul.LOGGER.warn("Player: {} attempting to ensoul item without having it!", (Object)player);
            return;
        }
        if (!item.isEquipable()) {
            RequestItemEnsoul.LOGGER.warn("Player: {} attempting to ensoul non equippable item: {}!", (Object)player, (Object)item);
            return;
        }
        if (!item.isWeapon()) {
            RequestItemEnsoul.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;Lorg/l2j/gameserver/model/item/instance/Item;)Ljava/lang/String;, player, item));
            return;
        }
        if (item.isCommonItem()) {
            RequestItemEnsoul.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;Lorg/l2j/gameserver/model/item/instance/Item;)Ljava/lang/String;, player, item));
            return;
        }
        if (item.isHeroItem()) {
            RequestItemEnsoul.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;Lorg/l2j/gameserver/model/item/instance/Item;)Ljava/lang/String;, player, item));
            return;
        }
        if (this._options == null || this._options.length == 0) {
            RequestItemEnsoul.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;)Ljava/lang/String;, player));
            return;
        }
        int success = 0;
        final InventoryUpdate iu = new InventoryUpdate();
        for (final EnsoulItemOption itemOption : this._options) {
            final int position = itemOption.getPosition() - 1;
            final Item soulCrystal = player.getInventory().getItemByObjectId(itemOption.getSoulCrystalObjectId());
            Label_0770: {
                if (soulCrystal == null) {
                    player.sendPacket(SystemMessageId.THE_RUNE_DOES_NOT_FIT);
                }
                else {
                    final EnsoulStone stone = EnsoulData.getInstance().getStone(soulCrystal.getId());
                    if (stone != null) {
                        if (!stone.getOptions().contains(itemOption.getSoulCrystalOption())) {
                            RequestItemEnsoul.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;)Ljava/lang/String;, player));
                        }
                        else {
                            final EnsoulOption option = EnsoulData.getInstance().getOption(itemOption.getSoulCrystalOption());
                            if (option == null) {
                                RequestItemEnsoul.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;)Ljava/lang/String;, player));
                            }
                            else {
                                ItemHolder fee;
                                if (itemOption.getType() == 1) {
                                    fee = EnsoulData.getInstance().getEnsoulFee(item.getTemplate().getCrystalType(), position);
                                    if ((itemOption.getPosition() == 1 || itemOption.getPosition() == 2) && item.getSpecialAbility(position) != null) {
                                        fee = EnsoulData.getInstance().getResoulFee(item.getTemplate().getCrystalType(), position);
                                    }
                                }
                                else {
                                    if (itemOption.getType() != 2) {
                                        RequestItemEnsoul.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;I)Ljava/lang/String;, player, itemOption.getType()));
                                        break Label_0770;
                                    }
                                    fee = EnsoulData.getInstance().getEnsoulFee(item.getTemplate().getCrystalType(), position);
                                    if (itemOption.getPosition() == 1 && item.getAdditionalSpecialAbility(position) != null) {
                                        fee = EnsoulData.getInstance().getResoulFee(item.getTemplate().getCrystalType(), position);
                                    }
                                }
                                if (fee == null) {
                                    RequestItemEnsoul.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;)Ljava/lang/String;, player));
                                }
                                else {
                                    final Item gemStones = player.getInventory().getItemByItemId(fee.getId());
                                    if (gemStones != null) {
                                        if (gemStones.getCount() >= fee.getCount()) {
                                            if (player.destroyItem("EnsoulOption", soulCrystal, 1L, player, true) && player.destroyItem("EnsoulOption", gemStones, fee.getCount(), player, true)) {
                                                item.addSpecialAbility(option, position, stone.getSlotType(), true);
                                                success = 1;
                                            }
                                            iu.addModifiedItem(soulCrystal);
                                            iu.addModifiedItem(gemStones);
                                            iu.addModifiedItem(item);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        player.sendInventoryUpdate(iu);
        if (item.isEquipped()) {
            item.applySpecialAbilities();
        }
        player.sendPacket(new ExEnsoulResult(success, item));
        item.updateDatabase(true);
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestItemEnsoul.class);
    }
    
    static class EnsoulItemOption
    {
        private final int _type;
        private final int _position;
        private final int _soulCrystalObjectId;
        private final int _soulCrystalOption;
        
        EnsoulItemOption(final int type, final int position, final int soulCrystalObjectId, final int soulCrystalOption) {
            this._type = type;
            this._position = position;
            this._soulCrystalObjectId = soulCrystalObjectId;
            this._soulCrystalOption = soulCrystalOption;
        }
        
        public int getType() {
            return this._type;
        }
        
        public int getPosition() {
            return this._position;
        }
        
        public int getSoulCrystalObjectId() {
            return this._soulCrystalObjectId;
        }
        
        public int getSoulCrystalOption() {
            return this._soulCrystalOption;
        }
    }
}
