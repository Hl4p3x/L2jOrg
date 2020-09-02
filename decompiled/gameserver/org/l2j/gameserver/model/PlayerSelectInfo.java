// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.slf4j.LoggerFactory;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.data.database.data.ItemVariationData;
import java.util.Objects;
import java.util.Iterator;
import org.l2j.gameserver.data.database.dao.ItemDAO;
import org.l2j.gameserver.model.punishment.PunishmentType;
import org.l2j.gameserver.model.punishment.PunishmentAffect;
import org.l2j.gameserver.instancemanager.PunishmentManager;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.PlayerVariablesDAO;
import org.l2j.gameserver.data.database.data.PlayerData;
import org.l2j.gameserver.data.database.data.PlayerVariableData;
import org.l2j.gameserver.data.database.data.ItemData;
import org.l2j.gameserver.enums.InventorySlot;
import java.util.EnumMap;
import org.slf4j.Logger;

public class PlayerSelectInfo
{
    private static final Logger LOGGER;
    private final EnumMap<InventorySlot, ItemData> paperdoll;
    private final PlayerVariableData _vars;
    private final PlayerData data;
    private final long banExpireTime;
    private VariationInstance _augmentation;
    
    public PlayerSelectInfo(final PlayerData data) {
        this.paperdoll = new EnumMap<InventorySlot, ItemData>(InventorySlot.class);
        this.data = data;
        this.restoreVisibleInventory();
        this.restoreAugmentation();
        this._vars = ((PlayerVariablesDAO)DatabaseAccess.getDAO((Class)PlayerVariablesDAO.class)).findById(data.getCharId());
        this.banExpireTime = PunishmentManager.getInstance().getPunishmentExpiration(data.getCharId(), PunishmentAffect.CHARACTER, PunishmentType.BAN);
    }
    
    private void restoreVisibleInventory() {
        for (final ItemData itemData : ((ItemDAO)DatabaseAccess.getDAO((Class)ItemDAO.class)).findEquipedItemsByOwner(this.data.getCharId())) {
            this.paperdoll.put(InventorySlot.fromId(itemData.getLocData()), itemData);
        }
    }
    
    private void restoreAugmentation() {
        final ItemData weapon = this.paperdoll.get(InventorySlot.RIGHT_HAND);
        if (Objects.nonNull(weapon)) {
            final ItemVariationData itemVariation = ((ItemDAO)DatabaseAccess.getDAO((Class)ItemDAO.class)).findItemVariationByItemId(weapon.getObjectId());
            if (Objects.nonNull(itemVariation)) {
                try {
                    final int mineralId = itemVariation.getMineralId();
                    final int option1 = itemVariation.getOption1();
                    final int option2 = itemVariation.getOption2();
                    if (option1 != -1 && option2 != -1) {
                        this._augmentation = new VariationInstance(mineralId, option1, option2);
                    }
                }
                catch (Exception e) {
                    PlayerSelectInfo.LOGGER.warn("Could not restore augmentation info", (Throwable)e);
                }
            }
        }
    }
    
    public int getObjectId() {
        return this.data.getCharId();
    }
    
    public int getAccessLevel() {
        return this.data.getAccessLevel();
    }
    
    public long getLastAccess() {
        return this.data.getLastAccess();
    }
    
    public int getFace() {
        return this._vars.getVisualFaceId();
    }
    
    public int getHairColor() {
        return this._vars.getVisualHairColorId();
    }
    
    public int getHairStyle() {
        return this._vars.getVisualHairId();
    }
    
    public String getName() {
        return this.data.getName();
    }
    
    public int getPaperdollObjectId(final InventorySlot slot) {
        return Util.zeroIfNullOrElse((Object)this.paperdoll.get(slot), ItemData::getObjectId);
    }
    
    public int getPaperdollItemId(final InventorySlot slot) {
        return Util.zeroIfNullOrElse((Object)this.paperdoll.get(slot), ItemData::getItemId);
    }
    
    public int getEnchantEffect(final InventorySlot slot) {
        return Util.zeroIfNullOrElse((Object)this.paperdoll.get(slot), ItemData::getEnchantLevel);
    }
    
    public VariationInstance getAugmentation() {
        return this._augmentation;
    }
    
    public int getAugmentationOption1() {
        return Objects.nonNull(this._augmentation) ? this._augmentation.getOption1Id() : 0;
    }
    
    public int getAugmentationOption2() {
        return Objects.nonNull(this._augmentation) ? this._augmentation.getOption2Id() : 0;
    }
    
    public boolean isHairAccessoryEnabled() {
        return this._vars.isHairAccessoryEnabled();
    }
    
    public int getVitalityItemsUsed() {
        return this._vars.getVitalityItemsUsed();
    }
    
    public PlayerData getData() {
        return this.data;
    }
    
    public int getRemainBanExpireTime() {
        final long diff = this.banExpireTime - System.currentTimeMillis();
        return (diff > 0L) ? ((int)(diff / 1000L)) : 0;
    }
    
    public void setDeleteTime(final long deleteTime) {
        this.data.setDeleteTime(deleteTime);
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)PlayerSelectInfo.class);
    }
}
