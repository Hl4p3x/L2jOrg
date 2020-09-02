// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.item.instance;

import org.l2j.gameserver.network.serverpackets.AbstractMessagePacket;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.model.item.type.CrystalType;
import org.l2j.gameserver.model.item.type.ActionType;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.model.holders.ItemSkillHolder;
import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.data.xml.impl.AugmentationEngine;
import org.l2j.gameserver.data.xml.impl.EnsoulData;
import java.util.function.Consumer;
import java.util.Collections;
import org.l2j.gameserver.model.options.EnchantOptions;
import org.l2j.gameserver.data.xml.impl.EnchantItemOptionsData;
import org.l2j.gameserver.model.events.impl.item.OnItemTalk;
import org.l2j.gameserver.model.events.impl.item.OnItemBypassEvent;
import java.util.function.Predicate;
import io.github.joealisson.primitive.IntSet;
import io.github.joealisson.primitive.HashIntSet;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.network.serverpackets.SpawnItem;
import org.l2j.gameserver.network.serverpackets.DropItem;
import java.util.Set;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.conditions.Condition;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerItemDrop;
import org.l2j.gameserver.model.Location;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.Config;
import java.util.Collection;
import java.util.Iterator;
import java.sql.PreparedStatement;
import java.sql.Connection;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerAugment;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.item.type.EtcItemType;
import org.l2j.gameserver.model.item.container.WarehouseType;
import org.l2j.gameserver.model.item.Armor;
import org.l2j.gameserver.model.item.Weapon;
import org.l2j.gameserver.model.item.EtcItem;
import org.l2j.gameserver.model.item.type.ItemType;
import org.l2j.gameserver.model.item.EquipableItem;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.util.GMAudit;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.world.WorldRegion;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerItemPickup;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.instancemanager.ItemsOnGroundManager;
import org.l2j.gameserver.instancemanager.SiegeGuardManager;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.GetItem;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.data.database.data.ItemOnGroundData;
import org.l2j.gameserver.idfactory.IdFactory;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Objects;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.enums.InstanceType;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;
import org.l2j.gameserver.model.item.enchant.attribute.AttributeHolder;
import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.model.VariationInstance;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.model.ensoul.EnsoulOption;
import java.util.Map;
import org.l2j.gameserver.model.options.Options;
import java.util.List;
import org.l2j.gameserver.model.DropProtection;
import java.util.concurrent.locks.ReentrantLock;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.slf4j.Logger;
import org.l2j.gameserver.model.WorldObject;

public final class Item extends WorldObject
{
    public static final int ADDED = 1;
    public static final int REMOVED = 3;
    public static final int MODIFIED = 2;
    public static final int[] DEFAULT_ENCHANT_OPTIONS;
    private static final Logger LOGGER;
    private static final Logger LOG_ITEMS;
    private final int itemId;
    private final ItemTemplate template;
    private final ReentrantLock _dbLock;
    private final DropProtection _dropProtection;
    private final List<Options> _enchantOptions;
    private final Map<Integer, EnsoulOption> _ensoulOptions;
    private final Map<Integer, EnsoulOption> _ensoulSpecialOptions;
    private int _ownerId;
    private int _dropperObjectId;
    private long _count;
    private long _initCount;
    private long _time;
    private boolean _decrease;
    private ItemLocation loc;
    private int _locData;
    private int enchantLevel;
    private boolean _wear;
    private VariationInstance _augmentation;
    private long _dropTime;
    private boolean _published;
    private boolean _protected;
    private int _lastChange;
    private boolean _existsInDb;
    private boolean _storedInDb;
    private Map<AttributeType, AttributeHolder> _elementals;
    private ScheduledFuture<?> itemLootShedule;
    private ScheduledFuture<?> _lifeTimeTask;
    
    public Item(final int objectId, final int itemId) {
        super(objectId);
        this._dbLock = new ReentrantLock();
        this._dropProtection = new DropProtection();
        this._enchantOptions = new ArrayList<Options>();
        this._ensoulOptions = new LinkedHashMap<Integer, EnsoulOption>(3);
        this._ensoulSpecialOptions = new LinkedHashMap<Integer, EnsoulOption>(3);
        this._dropperObjectId = 0;
        this._count = 1L;
        this._decrease = false;
        this._augmentation = null;
        this._published = false;
        this._lastChange = 2;
        this._elementals = null;
        this.itemLootShedule = null;
        this.setInstanceType(InstanceType.L2ItemInstance);
        this.template = ItemEngine.getInstance().getTemplate(itemId);
        if (itemId == 0 || Objects.isNull(this.template)) {
            throw new IllegalArgumentException();
        }
        this.itemId = itemId;
        super.setName(this.template.getName());
        this.loc = ItemLocation.VOID;
        this._dropTime = 0L;
        this._time = ((this.template.getTime() == -1L) ? -1L : (System.currentTimeMillis() + this.template.getTime() * 60L * 1000L));
        this.scheduleLifeTimeTask();
    }
    
    public Item(final int objectId, final ItemTemplate template) {
        super(objectId);
        this._dbLock = new ReentrantLock();
        this._dropProtection = new DropProtection();
        this._enchantOptions = new ArrayList<Options>();
        this._ensoulOptions = new LinkedHashMap<Integer, EnsoulOption>(3);
        this._ensoulSpecialOptions = new LinkedHashMap<Integer, EnsoulOption>(3);
        this._dropperObjectId = 0;
        this._count = 1L;
        this._decrease = false;
        this._augmentation = null;
        this._published = false;
        this._lastChange = 2;
        this._elementals = null;
        this.itemLootShedule = null;
        this.setInstanceType(InstanceType.L2ItemInstance);
        this.itemId = template.getId();
        this.template = template;
        if (this.itemId == 0) {
            throw new IllegalArgumentException();
        }
        super.setName(this.template.getName());
        this.loc = ItemLocation.VOID;
        this._time = ((this.template.getTime() == -1L) ? -1L : (System.currentTimeMillis() + this.template.getTime() * 60L * 1000L));
        this.scheduleLifeTimeTask();
    }
    
    public Item(final ResultSet rs) throws SQLException {
        this(rs.getInt("object_id"), ItemEngine.getInstance().getTemplate(rs.getInt("item_id")));
        this._count = rs.getLong("count");
        this._ownerId = rs.getInt("owner_id");
        this.loc = ItemLocation.valueOf(rs.getString("loc"));
        this._locData = rs.getInt("loc_data");
        this.enchantLevel = rs.getInt("enchant_level");
        this._time = rs.getLong("time");
        this._existsInDb = true;
        this._storedInDb = true;
        if (this.isEquipable()) {
            this.restoreAttributes();
            this.restoreSpecialAbilities();
        }
    }
    
    public Item(final int itemId) {
        this(IdFactory.getInstance().getNextId(), itemId);
    }
    
    public Item(final ItemOnGroundData data) {
        this(data.getObjectId(), data.getItemId());
        this.setCount(data.getCount());
        this.setEnchantLevel(data.getEnchantLevel());
        this.setDropTime(data.getDropTime());
        this.setProtected(this.getDropTime() == -1L);
        this.setSpawned(true);
        this.setXYZ(data.getX(), data.getY(), data.getZ());
    }
    
    public final void pickupMe(final Creature character) {
        final WorldRegion oldregion = this.getWorldRegion();
        character.broadcastPacket(new GetItem(this, character.getObjectId()));
        synchronized (this) {
            this.setSpawned(false);
        }
        final Castle castle = CastleManager.getInstance().getCastle(this);
        if (castle != null && SiegeGuardManager.getInstance().getSiegeGuardByItem(castle.getId(), this.getId()) != null) {
            SiegeGuardManager.getInstance().removeTicket(this);
            ItemsOnGroundManager.getInstance().removeObject(this);
        }
        World.getInstance().removeVisibleObject(this, oldregion);
        if (GameUtils.isPlayer(character)) {
            EventDispatcher.getInstance().notifyEventAsync(new OnPlayerItemPickup(character.getActingPlayer(), this), this.getTemplate());
        }
    }
    
    public void setOwnerId(final String process, final int owner_id, final Player creator, final Object reference) {
        this.setOwnerId(owner_id);
        final GeneralSettings generalSettings = (GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class);
        if (generalSettings.logItems() && (!generalSettings.smallLogItems() || this.template.isEquipable() || this.template.getId() == 57)) {
            if (this.enchantLevel > 0) {
                Item.LOG_ITEMS.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;IILjava/lang/String;JLjava/lang/String;Ljava/lang/String;)Ljava/lang/String;, String.valueOf(process), this.getObjectId(), this.enchantLevel, this.template.getName(), this._count, String.valueOf(creator), String.valueOf(reference)));
            }
            else {
                Item.LOG_ITEMS.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;ILjava/lang/String;JLjava/lang/String;Ljava/lang/String;)Ljava/lang/String;, String.valueOf(process), this.getObjectId(), this.template.getName(), this._count, String.valueOf(creator), String.valueOf(reference)));
            }
        }
        if (creator != null && creator.isGM()) {
            String referenceName = "no-reference";
            if (reference instanceof WorldObject) {
                referenceName = ((((WorldObject)reference).getName() != null) ? ((WorldObject)reference).getName() : "no-name");
            }
            else if (reference instanceof String) {
                referenceName = (String)reference;
            }
            final String targetName = (creator.getTarget() != null) ? creator.getTarget().getName() : "no-target";
            if (((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).auditGM()) {
                GMAudit.auditGMAction(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, creator.getName(), creator.getObjectId()), invokedynamic(makeConcatWithConstants:(Ljava/lang/String;ILjava/lang/String;)Ljava/lang/String;, process, this.itemId, this.getName()), targetName, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, referenceName));
            }
        }
    }
    
    public int getOwnerId() {
        return this._ownerId;
    }
    
    public void setOwnerId(final int owner_id) {
        if (owner_id == this._ownerId) {
            return;
        }
        this.removeSkillsFromOwner();
        this._ownerId = owner_id;
        this._storedInDb = false;
        this.giveSkillsToOwner();
    }
    
    public void setItemLocation(final ItemLocation loc, final int loc_data) {
        if (loc == this.loc && loc_data == this._locData) {
            return;
        }
        this.removeSkillsFromOwner();
        this.loc = loc;
        this._locData = loc_data;
        this._storedInDb = false;
        this.giveSkillsToOwner();
    }
    
    public ItemLocation getItemLocation() {
        return this.loc;
    }
    
    public void setItemLocation(final ItemLocation loc) {
        this.setItemLocation(loc, 0);
    }
    
    public long getCount() {
        return this._count;
    }
    
    public void setCount(final long count) {
        if (this._count == count) {
            return;
        }
        this._count = ((count >= -1L) ? count : 0L);
        this._storedInDb = false;
    }
    
    public void changeCount(final String process, final long count, final Player creator, final Object reference) {
        if (count == 0L) {
            return;
        }
        final long old = this._count;
        final long max = (this.itemId == 57) ? Inventory.MAX_ADENA : 2147483647L;
        if (count > 0L && this._count > max - count) {
            this.setCount(max);
        }
        else {
            this.setCount(this._count + count);
        }
        if (this._count < 0L) {
            this.setCount(0L);
        }
        this._storedInDb = false;
        final GeneralSettings generalSettings = (GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class);
        if (generalSettings.logItems() && process != null && (!generalSettings.smallLogItems() || this.template.isEquipable() || this.template.getId() == 57)) {
            if (this.enchantLevel > 0) {
                Item.LOG_ITEMS.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;IILjava/lang/String;JLjava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, String.valueOf(process), this.getObjectId(), this.enchantLevel, this.template.getName(), this._count, String.valueOf(old), String.valueOf(creator), String.valueOf(reference)));
            }
            else {
                Item.LOG_ITEMS.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;ILjava/lang/String;JLjava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, String.valueOf(process), this.getObjectId(), this.template.getName(), this._count, String.valueOf(old), String.valueOf(creator), String.valueOf(reference)));
            }
        }
        if (creator != null && creator.isGM()) {
            String referenceName = "no-reference";
            if (reference instanceof WorldObject) {
                referenceName = ((((WorldObject)reference).getName() != null) ? ((WorldObject)reference).getName() : "no-name");
            }
            else if (reference instanceof String) {
                referenceName = (String)reference;
            }
            final String targetName = (creator.getTarget() != null) ? creator.getTarget().getName() : "no-target";
            if (((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).auditGM()) {
                GMAudit.auditGMAction(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, creator.getName(), creator.getObjectId()), invokedynamic(makeConcatWithConstants:(Ljava/lang/String;IILjava/lang/String;J)Ljava/lang/String;, process, this.itemId, this.getObjectId(), this.getName(), count), targetName, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, referenceName));
            }
        }
    }
    
    public void changeCountWithoutTrace(final long count, final Player creator, final Object reference) {
        this.changeCount(null, count, creator, reference);
    }
    
    public boolean isEnchantable() {
        return (this.loc == ItemLocation.INVENTORY || this.loc == ItemLocation.PAPERDOLL) && this.template.isEnchantable();
    }
    
    public boolean isEquipable() {
        return this.template instanceof EquipableItem;
    }
    
    public boolean isEquipped() {
        return this.loc == ItemLocation.PAPERDOLL || this.loc == ItemLocation.PET_EQUIP;
    }
    
    public int getLocationSlot() {
        return this._locData;
    }
    
    public ItemTemplate getTemplate() {
        return this.template;
    }
    
    public int getCustomType1() {
        return this.template.getType1();
    }
    
    public int getType2() {
        return this.template.getType2();
    }
    
    public long getDropTime() {
        return this._dropTime;
    }
    
    public void setDropTime(final long time) {
        this._dropTime = time;
    }
    
    public ItemType getItemType() {
        return this.template.getItemType();
    }
    
    @Override
    public int getId() {
        return this.itemId;
    }
    
    public int getDisplayId() {
        return this.template.getDisplayId();
    }
    
    public boolean isEtcItem() {
        return this.template instanceof EtcItem;
    }
    
    public boolean isWeapon() {
        return this.template instanceof Weapon;
    }
    
    public boolean isArmor() {
        return this.template instanceof Armor;
    }
    
    public EtcItem getEtcItem() {
        if (this.template instanceof EtcItem) {
            return (EtcItem)this.template;
        }
        return null;
    }
    
    public Weapon getWeaponItem() {
        if (this.template instanceof Weapon) {
            return (Weapon)this.template;
        }
        return null;
    }
    
    public final int getCrystalCount() {
        return this.template.getCrystalCount(this.enchantLevel);
    }
    
    public long getReferencePrice() {
        return this.template.getReferencePrice();
    }
    
    public String getItemName() {
        return this.template.getName();
    }
    
    public int getReuseDelay() {
        return this.template.getReuseDelay();
    }
    
    public int getSharedReuseGroup() {
        return this.template.getSharedReuseGroup();
    }
    
    public int getLastChange() {
        return this._lastChange;
    }
    
    public void setLastChange(final int lastChange) {
        this._lastChange = lastChange;
    }
    
    public boolean isStackable() {
        return this.template.isStackable();
    }
    
    public boolean isDropable() {
        return !this.isAugmented() && this.template.isDropable();
    }
    
    public boolean isDestroyable() {
        return this.template.isDestroyable();
    }
    
    public boolean isTradeable() {
        return !this.isAugmented() && this.template.isTradeable();
    }
    
    public boolean isSellable() {
        return !this.isAugmented() && this.template.isSellable();
    }
    
    public boolean isDepositable(final boolean isPrivateWareHouse) {
        return !this.isEquipped() && this.template.isDepositable() && (isPrivateWareHouse || this.isTradeable());
    }
    
    public boolean isDepositable(final WarehouseType type) {
        if (this.isEquipped() || !this.template.isDepositable()) {
            return false;
        }
        boolean b = false;
        switch (type) {
            case CLAN:
            case CASTLE: {
                b = this.isTradeable();
                break;
            }
            case FREIGHT: {
                b = this.isFreightable();
                break;
            }
            default: {
                b = true;
                break;
            }
        }
        return b;
    }
    
    public boolean isPotion() {
        return this.template.isPotion();
    }
    
    public boolean isElixir() {
        return this.template.isElixir();
    }
    
    public boolean isScroll() {
        return this.template.isScroll();
    }
    
    public boolean isHeroItem() {
        return this.template.isHeroItem();
    }
    
    public boolean isCommonItem() {
        return this.template.isCommon();
    }
    
    public boolean isPvp() {
        return this.template.isPvpItem();
    }
    
    public boolean isAvailable(final Player player, final boolean allowAdena, final boolean allowNonTradeable) {
        final Summon pet = player.getPet();
        return !this.isEquipped() && !this.isQuestItem() && (this.template.getType2() != 4 || this.template.getType1() != 1) && (pet == null || this.getObjectId() != pet.getControlObjectId()) && !player.isProcessingItem(this.getObjectId()) && (allowAdena || this.itemId != 57) && !player.isCastingNow(s -> s.getSkill().getItemConsumeId() != this.itemId) && (allowNonTradeable || (this.isTradeable() && (this.template.getItemType() != EtcItemType.PET_COLLAR || !player.havePetInvItems())));
    }
    
    public int getEnchantLevel() {
        return this.enchantLevel;
    }
    
    public void updateEnchantLevel(final int value) {
        this.setEnchantLevel(this.enchantLevel + value);
    }
    
    public void setEnchantLevel(final int enchantLevel) {
        if (this.enchantLevel == enchantLevel) {
            return;
        }
        this.clearEnchantStats();
        this.enchantLevel = enchantLevel;
        this.applyEnchantStats();
        this._storedInDb = false;
    }
    
    public boolean isEnchanted() {
        return this.enchantLevel > 0;
    }
    
    public boolean isAugmented() {
        return this._augmentation != null;
    }
    
    public VariationInstance getAugmentation() {
        return this._augmentation;
    }
    
    public boolean setAugmentation(final VariationInstance augmentation, final boolean updateDatabase) {
        if (this._augmentation != null) {
            Item.LOGGER.info(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;I)Ljava/lang/String;, this.getObjectId(), this.getName(), this._ownerId));
            return false;
        }
        this._augmentation = augmentation;
        if (updateDatabase) {
            this.updateItemOptions();
        }
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerAugment(this.getActingPlayer(), this, augmentation, true), this.getTemplate());
        return true;
    }
    
    public void removeAugmentation() {
        if (this._augmentation == null) {
            return;
        }
        final VariationInstance augment = this._augmentation;
        this._augmentation = null;
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement ps = con.prepareStatement("DELETE FROM item_variations WHERE itemId = ?");
                try {
                    ps.setInt(1, this.getObjectId());
                    ps.executeUpdate();
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (Throwable t) {
                    if (ps != null) {
                        try {
                            ps.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t2) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception2) {
                        t2.addSuppressed(exception2);
                    }
                }
                throw t2;
            }
        }
        catch (Exception e) {
            Item.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.toString()), (Throwable)e);
        }
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerAugment(this.getActingPlayer(), this, augment, false), this.getTemplate());
    }
    
    public void restoreAttributes() {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement ps1 = con.prepareStatement("SELECT mineralId,option1,option2 FROM item_variations WHERE itemId=?");
                try {
                    final PreparedStatement ps2 = con.prepareStatement("SELECT elemType,elemValue FROM item_elementals WHERE itemId=?");
                    try {
                        ps1.setInt(1, this.getObjectId());
                        ResultSet rs = ps1.executeQuery();
                        try {
                            if (rs.next()) {
                                final int mineralId = rs.getInt("mineralId");
                                final int option1 = rs.getInt("option1");
                                final int option2 = rs.getInt("option2");
                                if (option1 != -1 && option2 != -1) {
                                    this._augmentation = new VariationInstance(mineralId, option1, option2);
                                }
                            }
                            if (rs != null) {
                                rs.close();
                            }
                        }
                        catch (Throwable t) {
                            if (rs != null) {
                                try {
                                    rs.close();
                                }
                                catch (Throwable exception) {
                                    t.addSuppressed(exception);
                                }
                            }
                            throw t;
                        }
                        ps2.setInt(1, this.getObjectId());
                        rs = ps2.executeQuery();
                        try {
                            while (rs.next()) {
                                final byte attributeType = rs.getByte(1);
                                final int attributeValue = rs.getInt(2);
                                if (attributeType != -1 && attributeValue != -1) {
                                    this.applyAttribute(new AttributeHolder(AttributeType.findByClientId(attributeType), attributeValue));
                                }
                            }
                            if (rs != null) {
                                rs.close();
                            }
                        }
                        catch (Throwable t2) {
                            if (rs != null) {
                                try {
                                    rs.close();
                                }
                                catch (Throwable exception2) {
                                    t2.addSuppressed(exception2);
                                }
                            }
                            throw t2;
                        }
                        if (ps2 != null) {
                            ps2.close();
                        }
                    }
                    catch (Throwable t3) {
                        if (ps2 != null) {
                            try {
                                ps2.close();
                            }
                            catch (Throwable exception3) {
                                t3.addSuppressed(exception3);
                            }
                        }
                        throw t3;
                    }
                    if (ps1 != null) {
                        ps1.close();
                    }
                }
                catch (Throwable t4) {
                    if (ps1 != null) {
                        try {
                            ps1.close();
                        }
                        catch (Throwable exception4) {
                            t4.addSuppressed(exception4);
                        }
                    }
                    throw t4;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t5) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception5) {
                        t5.addSuppressed(exception5);
                    }
                }
                throw t5;
            }
        }
        catch (Exception e) {
            Item.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.toString()), (Throwable)e);
        }
    }
    
    public void updateItemOptions() {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                this.updateItemOptions(con);
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                }
                throw t;
            }
        }
        catch (SQLException e) {
            Item.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.toString()), (Throwable)e);
        }
    }
    
    private void updateItemOptions(final Connection con) {
        try {
            final PreparedStatement ps = con.prepareStatement("REPLACE INTO item_variations VALUES(?,?,?,?)");
            try {
                ps.setInt(1, this.getObjectId());
                ps.setInt(2, (this._augmentation != null) ? this._augmentation.getMineralId() : 0);
                ps.setInt(3, (this._augmentation != null) ? this._augmentation.getOption1Id() : -1);
                ps.setInt(4, (this._augmentation != null) ? this._augmentation.getOption2Id() : -1);
                ps.executeUpdate();
                if (ps != null) {
                    ps.close();
                }
            }
            catch (Throwable t) {
                if (ps != null) {
                    try {
                        ps.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                }
                throw t;
            }
        }
        catch (SQLException e) {
            Item.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.toString()), (Throwable)e);
        }
    }
    
    public void updateItemElementals() {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                this.updateItemElements(con);
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                }
                throw t;
            }
        }
        catch (SQLException e) {
            Item.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.toString()), (Throwable)e);
        }
    }
    
    private void updateItemElements(final Connection con) {
        try {
            final PreparedStatement ps = con.prepareStatement("DELETE FROM item_elementals WHERE itemId = ?");
            try {
                ps.setInt(1, this.getObjectId());
                ps.executeUpdate();
                if (ps != null) {
                    ps.close();
                }
            }
            catch (Throwable t) {
                if (ps != null) {
                    try {
                        ps.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                }
                throw t;
            }
        }
        catch (SQLException e) {
            Item.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.toString()), (Throwable)e);
        }
        if (this._elementals == null) {
            return;
        }
        try {
            final PreparedStatement ps = con.prepareStatement("INSERT INTO item_elementals VALUES(?,?,?)");
            try {
                for (final AttributeHolder attribute : this._elementals.values()) {
                    ps.setInt(1, this.getObjectId());
                    ps.setByte(2, attribute.getType().getClientId());
                    ps.setInt(3, attribute.getValue());
                    ps.executeUpdate();
                    ps.clearParameters();
                }
                if (ps != null) {
                    ps.close();
                }
            }
            catch (Throwable t2) {
                if (ps != null) {
                    try {
                        ps.close();
                    }
                    catch (Throwable exception2) {
                        t2.addSuppressed(exception2);
                    }
                }
                throw t2;
            }
        }
        catch (SQLException e) {
            Item.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.toString()), (Throwable)e);
        }
    }
    
    public Collection<AttributeHolder> getAttributes() {
        return (this._elementals != null) ? this._elementals.values() : null;
    }
    
    public boolean hasAttributes() {
        return this._elementals != null && !this._elementals.isEmpty();
    }
    
    public AttributeHolder getAttribute(final AttributeType type) {
        return (this._elementals != null) ? this._elementals.get(type) : null;
    }
    
    public AttributeHolder getAttackAttribute() {
        if (this.isWeapon()) {
            if (this.template.getAttributes() != null) {
                return this.template.getAttributes().stream().findFirst().orElse(null);
            }
            if (this._elementals != null) {
                return this._elementals.values().stream().findFirst().orElse(null);
            }
        }
        return null;
    }
    
    public AttributeType getAttackAttributeType() {
        final AttributeHolder holder = this.getAttackAttribute();
        return (holder != null) ? holder.getType() : AttributeType.NONE;
    }
    
    public int getAttackAttributePower() {
        final AttributeHolder holder = this.getAttackAttribute();
        return (holder != null) ? holder.getValue() : 0;
    }
    
    public int getDefenceAttribute(final AttributeType element) {
        if (this.isArmor()) {
            if (this.template.getAttributes() != null) {
                final AttributeHolder attribute = this.template.getAttribute(element);
                if (attribute != null) {
                    return attribute.getValue();
                }
            }
            else if (this._elementals != null) {
                final AttributeHolder attribute = this.getAttribute(element);
                if (attribute != null) {
                    return attribute.getValue();
                }
            }
        }
        return 0;
    }
    
    private synchronized void applyAttribute(final AttributeHolder holder) {
        if (this._elementals == null) {
            (this._elementals = new LinkedHashMap<AttributeType, AttributeHolder>(3)).put(holder.getType(), holder);
        }
        else {
            final AttributeHolder attribute = this.getAttribute(holder.getType());
            if (attribute != null) {
                attribute.setValue(holder.getValue());
            }
            else {
                this._elementals.put(holder.getType(), holder);
            }
        }
    }
    
    public void setAttribute(final AttributeHolder holder, final boolean updateDatabase) {
        this.applyAttribute(holder);
        if (updateDatabase) {
            this.updateItemElementals();
        }
    }
    
    public void clearAttribute(final AttributeType type) {
        if (this._elementals == null || this.getAttribute(type) == null) {
            return;
        }
        synchronized (this._elementals) {
            this._elementals.remove(type);
        }
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement ps = con.prepareStatement("DELETE FROM item_elementals WHERE itemId = ? AND elemType = ?");
                try {
                    ps.setInt(1, this.getObjectId());
                    ps.setByte(2, type.getClientId());
                    ps.executeUpdate();
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (Throwable t) {
                    if (ps != null) {
                        try {
                            ps.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t2) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception2) {
                        t2.addSuppressed(exception2);
                    }
                }
                throw t2;
            }
        }
        catch (Exception e) {
            Item.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.toString()), (Throwable)e);
        }
    }
    
    public void clearAllAttributes() {
        if (this._elementals == null) {
            return;
        }
        synchronized (this._elementals) {
            this._elementals.clear();
        }
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement ps = con.prepareStatement("DELETE FROM item_elementals WHERE itemId = ?");
                try {
                    ps.setInt(1, this.getObjectId());
                    ps.executeUpdate();
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (Throwable t) {
                    if (ps != null) {
                        try {
                            ps.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t2) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception2) {
                        t2.addSuppressed(exception2);
                    }
                }
                throw t2;
            }
        }
        catch (Exception e) {
            Item.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.toString()), (Throwable)e);
        }
    }
    
    @Override
    public boolean isAutoAttackable(final Creature attacker) {
        return false;
    }
    
    public void updateDatabase() {
        this.updateDatabase(false);
    }
    
    public void updateDatabase(final boolean force) {
        this._dbLock.lock();
        try {
            if (this._existsInDb) {
                if (this._ownerId == 0 || this.loc == ItemLocation.VOID || this.loc == ItemLocation.REFUND || (this._count == 0L && this.loc != ItemLocation.LEASE)) {
                    this.removeFromDb();
                }
                else if (!Config.LAZY_ITEMS_UPDATE || force) {
                    this.updateInDb();
                }
            }
            else {
                if (this._ownerId == 0 || this.loc == ItemLocation.VOID || this.loc == ItemLocation.REFUND || (this._count == 0L && this.loc != ItemLocation.LEASE)) {
                    return;
                }
                this.insertIntoDb();
            }
        }
        finally {
            this._dbLock.unlock();
        }
    }
    
    public final void dropMe(final Creature dropper, final int x, final int y, final int z) {
        ThreadPool.execute((Runnable)new ItemDropTask(this, dropper, x, y, z));
        if (GameUtils.isPlayer(dropper)) {
            EventDispatcher.getInstance().notifyEventAsync(new OnPlayerItemDrop(dropper.getActingPlayer(), this, new Location(x, y, z)), this.getTemplate());
        }
    }
    
    private void updateInDb() {
        if (!this._existsInDb || this._wear || this._storedInDb) {
            return;
        }
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement ps = con.prepareStatement("UPDATE items SET owner_id=?,count=?,loc=?,loc_data=?,enchant_level=?,time=? WHERE object_id = ?");
                try {
                    ps.setInt(1, this._ownerId);
                    ps.setLong(2, this._count);
                    ps.setString(3, this.loc.name());
                    ps.setInt(4, this._locData);
                    ps.setInt(5, this.enchantLevel);
                    ps.setLong(6, this._time);
                    ps.setInt(7, this.getObjectId());
                    ps.executeUpdate();
                    this._existsInDb = true;
                    this._storedInDb = true;
                    if (this._augmentation != null) {
                        this.updateItemOptions(con);
                    }
                    if (this._elementals != null) {
                        this.updateItemElements(con);
                    }
                    if (!this._ensoulOptions.isEmpty() || !this._ensoulSpecialOptions.isEmpty()) {
                        this.updateSpecialAbilities(con);
                    }
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (Throwable t) {
                    if (ps != null) {
                        try {
                            ps.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t2) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception2) {
                        t2.addSuppressed(exception2);
                    }
                }
                throw t2;
            }
        }
        catch (Exception e) {
            Item.LOGGER.error(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/item/instance/Item;Ljava/lang/String;)Ljava/lang/String;, this, e.getMessage()), (Throwable)e);
        }
    }
    
    private void insertIntoDb() {
        if (this._existsInDb || this.getObjectId() == 0 || this._wear) {
            return;
        }
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement ps = con.prepareStatement("INSERT INTO items (owner_id,item_id,count,loc,loc_data,enchant_level,object_id,time) VALUES (?,?,?,?,?,?,?,?)");
                try {
                    ps.setInt(1, this._ownerId);
                    ps.setInt(2, this.itemId);
                    ps.setLong(3, this._count);
                    ps.setString(4, this.loc.name());
                    ps.setInt(5, this._locData);
                    ps.setInt(6, this.enchantLevel);
                    ps.setInt(7, this.getObjectId());
                    ps.setLong(8, this._time);
                    ps.executeUpdate();
                    this._existsInDb = true;
                    this._storedInDb = true;
                    if (this._augmentation != null) {
                        this.updateItemOptions(con);
                    }
                    if (this._elementals != null) {
                        this.updateItemElements(con);
                    }
                    if (this._ensoulOptions != null || this._ensoulSpecialOptions != null) {
                        this.updateSpecialAbilities(con);
                    }
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (Throwable t) {
                    if (ps != null) {
                        try {
                            ps.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t2) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception2) {
                        t2.addSuppressed(exception2);
                    }
                }
                throw t2;
            }
        }
        catch (Exception e) {
            Item.LOGGER.error(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/item/instance/Item;Ljava/lang/String;)Ljava/lang/String;, this, e.getMessage()), (Throwable)e);
        }
    }
    
    private void removeFromDb() {
        if (!this._existsInDb || this._wear) {
            return;
        }
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                PreparedStatement ps = con.prepareStatement("DELETE FROM items WHERE object_id = ?");
                try {
                    ps.setInt(1, this.getObjectId());
                    ps.executeUpdate();
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (Throwable t) {
                    if (ps != null) {
                        try {
                            ps.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
                ps = con.prepareStatement("DELETE FROM item_variations WHERE itemId = ?");
                try {
                    ps.setInt(1, this.getObjectId());
                    ps.executeUpdate();
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (Throwable t2) {
                    if (ps != null) {
                        try {
                            ps.close();
                        }
                        catch (Throwable exception2) {
                            t2.addSuppressed(exception2);
                        }
                    }
                    throw t2;
                }
                ps = con.prepareStatement("DELETE FROM item_elementals WHERE itemId = ?");
                try {
                    ps.setInt(1, this.getObjectId());
                    ps.executeUpdate();
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (Throwable t3) {
                    if (ps != null) {
                        try {
                            ps.close();
                        }
                        catch (Throwable exception3) {
                            t3.addSuppressed(exception3);
                        }
                    }
                    throw t3;
                }
                ps = con.prepareStatement("DELETE FROM item_special_abilities WHERE objectId = ?");
                try {
                    ps.setInt(1, this.getObjectId());
                    ps.executeUpdate();
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (Throwable t4) {
                    if (ps != null) {
                        try {
                            ps.close();
                        }
                        catch (Throwable exception4) {
                            t4.addSuppressed(exception4);
                        }
                    }
                    throw t4;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t5) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception5) {
                        t5.addSuppressed(exception5);
                    }
                }
                throw t5;
            }
        }
        catch (Exception e) {
            Item.LOGGER.error(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/item/instance/Item;)Ljava/lang/String;, this), (Throwable)e);
        }
        finally {
            this._existsInDb = false;
            this._storedInDb = false;
        }
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/item/ItemTemplate;I)Ljava/lang/String;, this.template, this.getObjectId());
    }
    
    public void resetOwnerTimer() {
        if (this.itemLootShedule != null) {
            this.itemLootShedule.cancel(true);
            this.itemLootShedule = null;
        }
    }
    
    public ScheduledFuture<?> getItemLootShedule() {
        return this.itemLootShedule;
    }
    
    public void setItemLootShedule(final ScheduledFuture<?> sf) {
        this.itemLootShedule = sf;
    }
    
    public boolean isProtected() {
        return this._protected;
    }
    
    public void setProtected(final boolean isProtected) {
        this._protected = isProtected;
    }
    
    public boolean isAvailable() {
        if (!this.template.isConditionAttached()) {
            return true;
        }
        if (this.loc == ItemLocation.PET || this.loc == ItemLocation.PET_EQUIP) {
            return true;
        }
        final Creature owner = this.getActingPlayer();
        if (owner != null) {
            for (final Condition condition : this.template.getConditions()) {
                if (condition == null) {
                    continue;
                }
                try {
                    if (!condition.test(owner, owner, null, null)) {
                        return false;
                    }
                    continue;
                }
                catch (Exception ex) {}
            }
        }
        return true;
    }
    
    public boolean getCountDecrease() {
        return this._decrease;
    }
    
    public void setCountDecrease(final boolean decrease) {
        this._decrease = decrease;
    }
    
    public long getInitCount() {
        return this._initCount;
    }
    
    public void setInitCount(final int InitCount) {
        this._initCount = InitCount;
    }
    
    public void restoreInitCount() {
        if (this._decrease) {
            this.setCount(this._initCount);
        }
    }
    
    public boolean isTimeLimitedItem() {
        return this._time > 0L;
    }
    
    public long getTime() {
        return this._time;
    }
    
    public long getRemainingTime() {
        return this._time - System.currentTimeMillis();
    }
    
    public void endOfLife() {
        final Player player = this.getActingPlayer();
        if (player != null) {
            if (this.isEquipped()) {
                final Set<Item> unequiped = player.getInventory().unEquipItemInSlotAndRecord(InventorySlot.fromId(this.getLocationSlot()));
                final InventoryUpdate iu = new InventoryUpdate();
                for (final Item item : unequiped) {
                    iu.addModifiedItem(item);
                }
                player.sendInventoryUpdate(iu);
            }
            if (this.loc != ItemLocation.WAREHOUSE) {
                player.getInventory().destroyItem("Item", this, player, null);
                final InventoryUpdate iu2 = new InventoryUpdate();
                iu2.addRemovedItem(this);
                player.sendInventoryUpdate(iu2);
            }
            else {
                player.getWarehouse().destroyItem("Item", this, player, null);
            }
            player.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_EXPIRED)).addItemName(this.itemId));
        }
    }
    
    public void scheduleLifeTimeTask() {
        if (!this.isTimeLimitedItem()) {
            return;
        }
        if (this.getRemainingTime() <= 0L) {
            this.endOfLife();
        }
        else {
            if (this._lifeTimeTask != null) {
                this._lifeTimeTask.cancel(true);
            }
            this._lifeTimeTask = (ScheduledFuture<?>)ThreadPool.schedule((Runnable)new ScheduleLifeTimeTask(this), this.getRemainingTime());
        }
    }
    
    public void setDropperObjectId(final int id) {
        this._dropperObjectId = id;
    }
    
    @Override
    public void sendInfo(final Player activeChar) {
        if (this._dropperObjectId != 0) {
            activeChar.sendPacket(new DropItem(this, this._dropperObjectId));
        }
        else {
            activeChar.sendPacket(new SpawnItem(this));
        }
    }
    
    public final DropProtection getDropProtection() {
        return this._dropProtection;
    }
    
    public boolean isPublished() {
        return this._published;
    }
    
    public void publish() {
        this._published = true;
    }
    
    @Override
    public boolean decayMe() {
        if (((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).saveDroppedItems()) {
            ItemsOnGroundManager.getInstance().removeObject(this);
        }
        return super.decayMe();
    }
    
    public boolean isQuestItem() {
        return this.template.isQuestItem();
    }
    
    public boolean isFreightable() {
        return this.template.isFreightable();
    }
    
    public boolean hasPassiveSkills() {
        return (this.template.getItemType() == EtcItemType.RUNE || this.template.getItemType() == EtcItemType.NONE) && this.loc == ItemLocation.INVENTORY && this._ownerId > 0 && this.template.getSkills(ItemSkillType.NORMAL) != null;
    }
    
    public void giveSkillsToOwner() {
        if (!this.hasPassiveSkills()) {
            return;
        }
        final Skill skill;
        Util.doIfNonNull((Object)this.getActingPlayer(), player -> this.template.forEachSkill(ItemSkillType.NORMAL, holder -> {
            skill = holder.getSkill();
            if (skill.isPassive()) {
                player.addSkill(skill, false);
            }
        }));
    }
    
    public void removeSkillsFromOwner() {
        if (!this.hasPassiveSkills()) {
            return;
        }
        final IntSet removedSkills;
        final Skill oldSkill;
        final IntSet set;
        Util.doIfNonNull((Object)this.getActingPlayer(), player -> {
            removedSkills = (IntSet)new HashIntSet();
            this.template.forEachSkill(ItemSkillType.NORMAL, Skill::isPassive, skill -> {
                oldSkill = player.removeSkill(skill, false, true);
                if (Objects.nonNull(oldSkill)) {
                    set.add(oldSkill.getId());
                }
                return;
            });
            if (!removedSkills.isEmpty()) {
                player.getInventory().forEachItem(this.hasRemovedSkill(removedSkills), Item::giveSkillsToOwner);
            }
        });
    }
    
    public Predicate<Item> hasRemovedSkill(final IntSet removedSkills) {
        return item -> item != this && item.hasPassiveSkills() && item.getTemplate().checkAnySkill(ItemSkillType.NORMAL, sk -> removedSkills.contains(sk.getSkillId()));
    }
    
    @Override
    public Player getActingPlayer() {
        return World.getInstance().findPlayer(this.getOwnerId());
    }
    
    public int getEquipReuseDelay() {
        return this.template.getEquipReuseDelay();
    }
    
    public void onBypassFeedback(final Player activeChar, final String command) {
        if (command.startsWith("Quest")) {
            final String questName = command.substring(6);
            String event = null;
            final int idx = questName.indexOf(32);
            if (idx > 0) {
                event = questName.substring(idx).trim();
            }
            if (event != null) {
                EventDispatcher.getInstance().notifyEventAsync(new OnItemBypassEvent(this, activeChar, event), this.getTemplate());
            }
            else {
                EventDispatcher.getInstance().notifyEventAsync(new OnItemTalk(this, activeChar), this.getTemplate());
            }
        }
    }
    
    public int[] getEnchantOptions() {
        final EnchantOptions op = EnchantItemOptionsData.getInstance().getOptions(this);
        if (op != null) {
            return op.getOptions();
        }
        return Item.DEFAULT_ENCHANT_OPTIONS;
    }
    
    public Collection<EnsoulOption> getSpecialAbilities() {
        return Collections.unmodifiableCollection((Collection<? extends EnsoulOption>)this._ensoulOptions.values());
    }
    
    public EnsoulOption getSpecialAbility(final int index) {
        return this._ensoulOptions.get(index);
    }
    
    public Collection<EnsoulOption> getAdditionalSpecialAbilities() {
        return Collections.unmodifiableCollection((Collection<? extends EnsoulOption>)this._ensoulSpecialOptions.values());
    }
    
    public EnsoulOption getAdditionalSpecialAbility(final int index) {
        return this._ensoulSpecialOptions.get(index);
    }
    
    public void addSpecialAbility(final EnsoulOption option, final int position, final int type, final boolean updateInDB) {
        if (type == 1) {
            final EnsoulOption oldOption = this._ensoulOptions.put(position, option);
            if (oldOption != null) {
                this.removeSpecialAbility(oldOption);
            }
        }
        else if (type == 2) {
            final EnsoulOption oldOption = this._ensoulSpecialOptions.put(position, option);
            if (oldOption != null) {
                this.removeSpecialAbility(oldOption);
            }
        }
        if (updateInDB) {
            this.updateSpecialAbilities();
        }
    }
    
    public void removeSpecialAbility(final int position, final int type) {
        if (type == 1) {
            final EnsoulOption option = this._ensoulOptions.get(position);
            if (option != null) {
                this.removeSpecialAbility(option);
                this._ensoulOptions.remove(position);
                if (position == 0) {
                    final EnsoulOption secondEnsoul = this._ensoulOptions.get(1);
                    if (secondEnsoul != null) {
                        this.removeSpecialAbility(secondEnsoul);
                        this._ensoulOptions.remove(1);
                        this.addSpecialAbility(secondEnsoul, 0, 1, true);
                    }
                }
            }
        }
        else if (type == 2) {
            final EnsoulOption option = this._ensoulSpecialOptions.get(position);
            if (option != null) {
                this.removeSpecialAbility(option);
                this._ensoulSpecialOptions.remove(position);
            }
        }
    }
    
    public void clearSpecialAbilities() {
        this._ensoulOptions.values().forEach(this::clearSpecialAbility);
        this._ensoulSpecialOptions.values().forEach(this::clearSpecialAbility);
    }
    
    public void applySpecialAbilities() {
        if (!this.isEquipped()) {
            return;
        }
        this._ensoulOptions.values().forEach(this::applySpecialAbility);
        this._ensoulSpecialOptions.values().forEach(this::applySpecialAbility);
    }
    
    private void removeSpecialAbility(final EnsoulOption option) {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement ps = con.prepareStatement("DELETE FROM item_special_abilities WHERE objectId = ? AND optionId = ?");
                try {
                    ps.setInt(1, this.getObjectId());
                    ps.setInt(2, option.getId());
                    ps.execute();
                    final Skill skill = option.getSkill();
                    if (skill != null) {
                        final Player player = this.getActingPlayer();
                        if (player != null) {
                            player.removeSkill(skill.getId());
                        }
                    }
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (Throwable t) {
                    if (ps != null) {
                        try {
                            ps.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t2) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception2) {
                        t2.addSuppressed(exception2);
                    }
                }
                throw t2;
            }
        }
        catch (Exception e) {
            Item.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/item/instance/Item;)Ljava/lang/String;, this), (Throwable)e);
        }
    }
    
    private void applySpecialAbility(final EnsoulOption option) {
        final Skill skill = option.getSkill();
        if (skill != null) {
            final Player player = this.getActingPlayer();
            if (player != null && player.getSkillLevel(skill.getId()) != skill.getLevel()) {
                player.addSkill(skill, false);
            }
        }
    }
    
    private void clearSpecialAbility(final EnsoulOption option) {
        final Skill skill = option.getSkill();
        if (skill != null) {
            final Player player = this.getActingPlayer();
            if (player != null) {
                player.removeSkill(skill, false, true);
            }
        }
    }
    
    private void restoreSpecialAbilities() {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement ps = con.prepareStatement("SELECT * FROM item_special_abilities WHERE objectId = ? ORDER BY position");
                try {
                    ps.setInt(1, this.getObjectId());
                    final ResultSet rs = ps.executeQuery();
                    try {
                        while (rs.next()) {
                            final int optionId = rs.getInt("optionId");
                            final int type = rs.getInt("type");
                            final int position = rs.getInt("position");
                            final EnsoulOption option = EnsoulData.getInstance().getOption(optionId);
                            if (option != null) {
                                this.addSpecialAbility(option, position, type, false);
                            }
                        }
                        if (rs != null) {
                            rs.close();
                        }
                    }
                    catch (Throwable t) {
                        if (rs != null) {
                            try {
                                rs.close();
                            }
                            catch (Throwable exception) {
                                t.addSuppressed(exception);
                            }
                        }
                        throw t;
                    }
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (Throwable t2) {
                    if (ps != null) {
                        try {
                            ps.close();
                        }
                        catch (Throwable exception2) {
                            t2.addSuppressed(exception2);
                        }
                    }
                    throw t2;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t3) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception3) {
                        t3.addSuppressed(exception3);
                    }
                }
                throw t3;
            }
        }
        catch (Exception e) {
            Item.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/item/instance/Item;)Ljava/lang/String;, this), (Throwable)e);
        }
    }
    
    public void updateSpecialAbilities() {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                this.updateSpecialAbilities(con);
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                }
                throw t;
            }
        }
        catch (Exception e) {
            Item.LOGGER.warn("Couldn't update item special abilities", (Throwable)e);
        }
    }
    
    private void updateSpecialAbilities(final Connection con) {
        try {
            final PreparedStatement ps = con.prepareStatement("INSERT INTO item_special_abilities (`objectId`, `type`, `optionId`, `position`) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE type = ?, optionId = ?, position = ?");
            try {
                ps.setInt(1, this.getObjectId());
                for (final Map.Entry<Integer, EnsoulOption> entry : this._ensoulOptions.entrySet()) {
                    ps.setInt(2, 1);
                    ps.setInt(3, entry.getValue().getId());
                    ps.setInt(4, entry.getKey());
                    ps.setInt(5, 1);
                    ps.setInt(6, entry.getValue().getId());
                    ps.setInt(7, entry.getKey());
                    ps.execute();
                }
                for (final Map.Entry<Integer, EnsoulOption> entry : this._ensoulSpecialOptions.entrySet()) {
                    ps.setInt(2, 2);
                    ps.setInt(3, entry.getValue().getId());
                    ps.setInt(4, entry.getKey());
                    ps.setInt(5, 2);
                    ps.setInt(6, entry.getValue().getId());
                    ps.setInt(7, entry.getKey());
                    ps.execute();
                }
                if (ps != null) {
                    ps.close();
                }
            }
            catch (Throwable t) {
                if (ps != null) {
                    try {
                        ps.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                }
                throw t;
            }
        }
        catch (Exception e) {
            Item.LOGGER.warn("Couldn't update item special abilities", (Throwable)e);
        }
    }
    
    public void clearEnchantStats() {
        final Player player = this.getActingPlayer();
        if (player == null) {
            this._enchantOptions.clear();
            return;
        }
        for (final Options op : this._enchantOptions) {
            op.remove(player);
        }
        this._enchantOptions.clear();
    }
    
    public void applyEnchantStats() {
        final Player player = this.getActingPlayer();
        if (!this.isEquipped() || player == null || this.getEnchantOptions() == Item.DEFAULT_ENCHANT_OPTIONS) {
            return;
        }
        for (final int id : this.getEnchantOptions()) {
            final Options options = AugmentationEngine.getInstance().getOptions(id);
            if (options != null) {
                options.apply(player);
                this._enchantOptions.add(options);
            }
            else if (id != 0) {
                Item.LOGGER.info(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, id));
            }
        }
    }
    
    @Override
    public void setHeading(final int heading) {
    }
    
    public void deleteMe() {
        if (this._lifeTimeTask != null && !this._lifeTimeTask.isDone()) {
            this._lifeTimeTask.cancel(false);
            this._lifeTimeTask = null;
        }
    }
    
    public BodyPart getBodyPart() {
        return (this.template instanceof EquipableItem) ? this.template.getBodyPart() : BodyPart.NONE;
    }
    
    public int getItemMask() {
        return this.template.getItemMask();
    }
    
    public void forEachSkill(final ItemSkillType type, final Consumer<ItemSkillHolder> action) {
        this.template.forEachSkill(type, action);
    }
    
    public double getStats(final Stat stat, final int defaultValue) {
        return this.template.getStats(stat, defaultValue);
    }
    
    public boolean isAutoPotion() {
        return this.template instanceof EtcItem && ((EtcItem)this.template).isAutoPotion();
    }
    
    public boolean isAutoSupply() {
        return this.template instanceof EtcItem && ((EtcItem)this.template).isAutoSupply();
    }
    
    public ActionType getAction() {
        return this.template.getDefaultAction();
    }
    
    public List<ItemSkillHolder> getSkills(final ItemSkillType type) {
        return this.template.getSkills(type);
    }
    
    public boolean isSelfResurrection() {
        final ItemTemplate template = this.template;
        final EtcItem etcTemplate;
        return template instanceof EtcItem && (etcTemplate = (EtcItem)template) == template && etcTemplate.isSelfResurrection();
    }
    
    public CrystalType getCrystalType() {
        return this.template.getCrystalType();
    }
    
    public boolean isMagicWeapon() {
        final ItemTemplate template = this.template;
        final Weapon w;
        return template instanceof Weapon && (w = (Weapon)template) == template && w.isMagicWeapon();
    }
    
    public boolean isInfinite() {
        final ItemTemplate template = this.template;
        final EtcItem etcItem;
        return template instanceof EtcItem && (etcItem = (EtcItem)template) == template && etcItem.isInfinite();
    }
    
    static {
        DEFAULT_ENCHANT_OPTIONS = new int[] { 0, 0, 0 };
        LOGGER = LoggerFactory.getLogger((Class)Item.class);
        LOG_ITEMS = LoggerFactory.getLogger("item");
    }
    
    static class ScheduleLifeTimeTask implements Runnable
    {
        private static final Logger LOGGER;
        private final Item _limitedItem;
        
        ScheduleLifeTimeTask(final Item item) {
            this._limitedItem = item;
        }
        
        @Override
        public void run() {
            try {
                if (this._limitedItem != null) {
                    this._limitedItem.endOfLife();
                }
            }
            catch (Exception e) {
                ScheduleLifeTimeTask.LOGGER.error("", (Throwable)e);
            }
        }
        
        static {
            LOGGER = LoggerFactory.getLogger((Class)ScheduleLifeTimeTask.class);
        }
    }
    
    public class ItemDropTask implements Runnable
    {
        private final Creature _dropper;
        private final Item _it\u0435m;
        private int _x;
        private int _y;
        private int _z;
        
        public ItemDropTask(final Item item, final Creature dropper, final int x, final int y, final int z) {
            this._x = x;
            this._y = y;
            this._z = z;
            this._dropper = dropper;
            this._it\u0435m = item;
        }
        
        @Override
        public final void run() {
            if (this._dropper != null) {
                final Instance instance = this._dropper.getInstanceWorld();
                final Location dropDest = GeoEngine.getInstance().canMoveToTargetLoc(this._dropper.getX(), this._dropper.getY(), this._dropper.getZ(), this._x, this._y, this._z, instance);
                this._x = dropDest.getX();
                this._y = dropDest.getY();
                this._z = dropDest.getZ();
                Item.this.setInstance(instance);
            }
            else {
                Item.this.setInstance(null);
            }
            synchronized (this._it\u0435m) {
                this._it\u0435m.setSpawned(true);
                this._it\u0435m.setXYZ(this._x, this._y, this._z);
            }
            this._it\u0435m.setDropTime(System.currentTimeMillis());
            this._it\u0435m.setDropperObjectId((this._dropper != null) ? this._dropper.getObjectId() : 0);
            World.getInstance().addVisibleObject(this._it\u0435m, this._it\u0435m.getWorldRegion());
            if (((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).saveDroppedItems()) {
                ItemsOnGroundManager.getInstance().save(this._it\u0435m);
            }
            this._it\u0435m.setDropperObjectId(0);
        }
    }
}
