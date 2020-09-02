// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.item.container;

import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.LoggerFactory;
import org.l2j.gameserver.Config;
import java.util.function.DoubleBinaryOperator;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Collection;
import java.util.function.Predicate;
import org.l2j.gameserver.model.ArmorSet;
import org.l2j.gameserver.data.xml.impl.ArmorSetsData;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.commons.database.DatabaseFactory;
import java.util.EnumSet;
import org.l2j.gameserver.model.item.type.ArmorType;
import org.l2j.gameserver.network.serverpackets.ExUserInfoEquipSlot;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.WorldObject;
import java.util.function.Function;
import org.l2j.gameserver.model.VariationInstance;
import org.l2j.commons.util.Util;
import java.util.Objects;
import java.util.Iterator;
import java.util.Map;
import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.enums.ItemLocation;
import java.util.function.Consumer;
import java.util.ServiceLoader;
import java.util.HashSet;
import org.l2j.gameserver.api.item.InventoryListener;
import java.util.Set;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.enums.InventorySlot;
import java.util.EnumMap;
import org.slf4j.Logger;

public abstract class Inventory extends ItemContainer
{
    public static final int BEAUTY_TICKET_ID = 36308;
    public static final long MAX_ADENA;
    protected static final Logger LOGGER;
    private final EnumMap<InventorySlot, Item> paperdoll;
    private final Set<InventoryListener> listeners;
    protected int _totalWeight;
    private int wearedMask;
    private int blockedItemSlotsMask;
    
    protected Inventory() {
        this.paperdoll = new EnumMap<InventorySlot, Item>(InventorySlot.class);
        this.listeners = new HashSet<InventoryListener>();
        ServiceLoader.load(InventoryListener.class).forEach(this::addPaperdollListener);
    }
    
    protected abstract ItemLocation getEquipLocation();
    
    private ChangeRecorder newRecorder() {
        return new ChangeRecorder(this);
    }
    
    public Item dropItem(final String process, final Item item, final Player actor, final Object reference) {
        if (item == null) {
            return null;
        }
        synchronized (item) {
            if (!this.items.containsKey(item.getObjectId())) {
                return null;
            }
            this.removeItem(item);
            item.setOwnerId(process, 0, actor, reference);
            item.setItemLocation(ItemLocation.VOID);
            item.setLastChange(3);
            item.updateDatabase();
            this.refreshWeight();
        }
        return item;
    }
    
    public Item dropItem(final String process, final int objectId, final long count, final Player actor, final Object reference) {
        Item item = this.getItemByObjectId(objectId);
        if (item == null) {
            return null;
        }
        synchronized (item) {
            if (!this.items.containsKey(item.getObjectId())) {
                return null;
            }
            if (item.getCount() > count) {
                item.changeCount(process, -count, actor, reference);
                item.setLastChange(2);
                item.updateDatabase();
                item = ItemEngine.getInstance().createItem(process, item.getId(), count, actor, reference);
                item.updateDatabase();
                this.refreshWeight();
                return item;
            }
        }
        return this.dropItem(process, item, actor, reference);
    }
    
    @Override
    protected void addItem(final Item item) {
        super.addItem(item);
        if (item.isEquipped() && item.getBodyPart() != BodyPart.NONE) {
            this.equipItem(item);
        }
    }
    
    @Override
    protected boolean removeItem(final Item item) {
        if (item.isEquipped()) {
            for (final Map.Entry<InventorySlot, Item> entry : this.paperdoll.entrySet()) {
                if (entry.getValue().equals(item)) {
                    this.unEquipItemInSlot(entry.getKey());
                    break;
                }
            }
        }
        return super.removeItem(item);
    }
    
    public Item getPaperdollItem(final InventorySlot slot) {
        return this.paperdoll.get(slot);
    }
    
    public boolean isPaperdollSlotEmpty(final InventorySlot slot) {
        return !this.paperdoll.containsKey(slot);
    }
    
    public Item getItemByBodyPart(final BodyPart bodyPart) {
        if (Objects.isNull(bodyPart.slot())) {
            return null;
        }
        return this.paperdoll.get(bodyPart.slot());
    }
    
    public int getPaperdollItemId(final InventorySlot slot) {
        return Util.zeroIfNullOrElse((Object)this.paperdoll.get(slot), Item::getId);
    }
    
    public int getPaperdollItemDisplayId(final InventorySlot slot) {
        return Util.zeroIfNullOrElse((Object)this.paperdoll.get(slot), Item::getDisplayId);
    }
    
    public VariationInstance getPaperdollAugmentation(final InventorySlot slot) {
        return (VariationInstance)Util.computeIfNonNull((Object)this.paperdoll.get(slot), (Function)Item::getAugmentation);
    }
    
    public int getPaperdollObjectId(final InventorySlot slot) {
        return Util.zeroIfNullOrElse((Object)this.paperdoll.get(slot), WorldObject::getObjectId);
    }
    
    protected void addPaperdollListener(final InventoryListener listener) {
        this.listeners.add(listener);
    }
    
    public synchronized void removePaperdollListener(final InventoryListener listener) {
        this.listeners.remove(listener);
    }
    
    public synchronized Item setPaperdollItem(final InventorySlot slot, final Item item) {
        final Item old = this.paperdoll.get(slot);
        if (old != item) {
            if (Objects.nonNull(old)) {
                this.paperdoll.remove(slot);
                old.setItemLocation(this.getBaseLocation());
                old.setLastChange(2);
                this.wearedMask &= ~old.getItemMask();
                this.listeners.forEach(l -> l.notifyUnequiped(slot, old, this));
                old.updateDatabase();
            }
            if (Objects.nonNull(item)) {
                this.paperdoll.put(slot, item);
                item.setItemLocation(this.getEquipLocation(), slot.getId());
                item.setLastChange(2);
                this.wearedMask |= item.getItemMask();
                this.listeners.forEach(l -> l.notifyEquiped(slot, item, this));
                item.updateDatabase();
            }
            if (GameUtils.isPlayer(this.getOwner())) {
                this.getOwner().sendPacket(new ExUserInfoEquipSlot(this.getOwner().getActingPlayer()));
            }
        }
        return old;
    }
    
    public int getWearedMask() {
        return this.wearedMask;
    }
    
    public Set<Item> unEquipItemInBodySlotAndRecord(final BodyPart slot) {
        final ChangeRecorder recorder = this.newRecorder();
        try {
            this.unEquipItemInBodySlot(slot);
        }
        finally {
            this.removePaperdollListener(recorder);
        }
        return recorder.getChangedItems();
    }
    
    public Item unEquipItemInSlot(final InventorySlot pdollSlot) {
        return this.setPaperdollItem(pdollSlot, null);
    }
    
    public Set<Item> unEquipItemInSlotAndRecord(final InventorySlot slot) {
        final ChangeRecorder recorder = this.newRecorder();
        try {
            this.unEquipItemInSlot(slot);
        }
        finally {
            this.removePaperdollListener(recorder);
        }
        return recorder.getChangedItems();
    }
    
    public Item unEquipItemInBodySlot(final BodyPart bodyPart) {
        InventorySlot inventorySlot = null;
        switch (bodyPart) {
            case HAIR_ALL: {
                inventorySlot = InventorySlot.HAIR;
                break;
            }
            case ALL_DRESS:
            case FULL_ARMOR: {
                inventorySlot = InventorySlot.CHEST;
                break;
            }
            default: {
                inventorySlot = bodyPart.slot();
                break;
            }
        }
        InventorySlot pdollSlot = inventorySlot;
        if (Objects.nonNull(pdollSlot)) {
            if (pdollSlot == InventorySlot.TWO_HAND) {
                this.paperdoll.remove(InventorySlot.TWO_HAND);
                pdollSlot = InventorySlot.RIGHT_HAND;
            }
            return this.setPaperdollItem(pdollSlot, null);
        }
        return null;
    }
    
    public Set<Item> equipItemAndRecord(final Item item) {
        final ChangeRecorder recorder = this.newRecorder();
        try {
            this.equipItem(item);
        }
        finally {
            this.removePaperdollListener(recorder);
        }
        return recorder.getChangedItems();
    }
    
    public void equipItem(final Item item) {
        final BodyPart bodyPart = item.getBodyPart();
        if (bodyPart.isAnyOf(BodyPart.TWO_HAND, BodyPart.LEFT_HAND, BodyPart.RIGHT_HAND, BodyPart.LEGS, BodyPart.FEET, BodyPart.GLOVES, BodyPart.HEAD)) {
            final Item formal = this.getPaperdollItem(InventorySlot.CHEST);
            if (Objects.nonNull(formal) && item.getId() != 21163 && formal.getBodyPart() == BodyPart.ALL_DRESS) {
                return;
            }
        }
        switch (bodyPart) {
            case TWO_HAND: {
                this.equipTwoHand(item);
                break;
            }
            case LEFT_HAND: {
                this.equipLeftHand(item);
                break;
            }
            case RIGHT_HAND: {
                this.equipRightHand(item);
                break;
            }
            case FEET:
            case HEAD:
            case GLOVES: {
                this.equipCheckingDress(bodyPart, item);
                break;
            }
            case EAR: {
                this.equipEar(item);
                break;
            }
            case FINGER: {
                this.equipFinger(item);
                break;
            }
            case FULL_ARMOR: {
                this.equipFullArmor(item);
                break;
            }
            case LEGS: {
                this.equipLeg(item);
                break;
            }
            case HAIR: {
                this.equipHair(item);
                break;
            }
            case HAIR2: {
                this.equipHair2(item);
                break;
            }
            case HAIR_ALL: {
                this.equipHairAll(item);
                break;
            }
            case TALISMAN: {
                this.equipTalisman(item);
                break;
            }
            case ALL_DRESS: {
                this.equipAllDress(item);
                break;
            }
            case BROOCH_JEWEL: {
                this.equipBroochJewel(item);
                break;
            }
            case AGATHION: {
                this.equipAgathion(item);
                break;
            }
            case ARTIFACT: {
                this.equipArtifact(item);
                break;
            }
            case NECK:
            case CHEST:
            case PENDANT:
            case BACK:
            case LEFT_BRACELET:
            case RIGHT_BRACELET:
            case BELT:
            case BROOCH:
            case ARTIFACT_BOOK: {
                this.setPaperdollItem(bodyPart.slot(), item);
                break;
            }
            default: {
                Inventory.LOGGER.warn("Unknown body slot {} for Item ID: {}", (Object)bodyPart, (Object)item.getId());
                break;
            }
        }
    }
    
    private void equipCheckingDress(final BodyPart bodyPart, final Item item) {
        this.checkEquippedDress();
        this.setPaperdollItem(bodyPart.slot(), item);
    }
    
    private void equipRightHand(final Item item) {
        this.checkEquippedDress();
        if (!this.isPaperdollSlotEmpty(InventorySlot.TWO_HAND)) {
            this.setPaperdollItem(InventorySlot.TWO_HAND, null);
        }
        this.setPaperdollItem(InventorySlot.RIGHT_HAND, item);
    }
    
    private void equipAllDress(final Item item) {
        this.setPaperdollItem(InventorySlot.LEGS, null);
        this.setPaperdollItem(InventorySlot.LEFT_HAND, null);
        this.setPaperdollItem(InventorySlot.RIGHT_HAND, null);
        this.setPaperdollItem(InventorySlot.HEAD, null);
        this.setPaperdollItem(InventorySlot.FEET, null);
        this.setPaperdollItem(InventorySlot.GLOVES, null);
        this.setPaperdollItem(InventorySlot.TWO_HAND, null);
        this.setPaperdollItem(InventorySlot.CHEST, item);
    }
    
    private void equipHairAll(final Item item) {
        this.setPaperdollItem(InventorySlot.HAIR2, null);
        this.setPaperdollItem(InventorySlot.HAIR, item);
    }
    
    private void equipHair2(final Item item) {
        final Item hair = this.getPaperdollItem(InventorySlot.HAIR);
        if (Objects.nonNull(hair) && hair.getBodyPart() == BodyPart.HAIR_ALL) {
            this.setPaperdollItem(InventorySlot.HAIR, null);
        }
        this.setPaperdollItem(InventorySlot.HAIR2, item);
    }
    
    private void equipHair(final Item item) {
        final Item hair = this.getPaperdollItem(InventorySlot.HAIR);
        if (Objects.nonNull(hair) && hair.getBodyPart() == BodyPart.HAIR_ALL) {
            this.setPaperdollItem(InventorySlot.HAIR2, null);
        }
        this.setPaperdollItem(InventorySlot.HAIR, item);
    }
    
    private void equipLeg(final Item item) {
        final Item chest = this.getPaperdollItem(InventorySlot.CHEST);
        if (Objects.nonNull(chest) && chest.getBodyPart().isAnyOf(BodyPart.FULL_ARMOR, BodyPart.ALL_DRESS)) {
            this.setPaperdollItem(InventorySlot.CHEST, null);
        }
        this.setPaperdollItem(InventorySlot.LEGS, item);
    }
    
    private void equipFullArmor(final Item item) {
        this.setPaperdollItem(InventorySlot.LEGS, null);
        this.setPaperdollItem(InventorySlot.CHEST, item);
    }
    
    private void equipFinger(final Item item) {
        if (this.isPaperdollSlotEmpty(InventorySlot.LEFT_FINGER) || !this.isPaperdollSlotEmpty(InventorySlot.RIGHT_FINGER)) {
            this.setPaperdollItem(InventorySlot.LEFT_FINGER, item);
        }
        else {
            this.setPaperdollItem(InventorySlot.RIGHT_FINGER, item);
        }
    }
    
    private void equipEar(final Item item) {
        if (this.isPaperdollSlotEmpty(InventorySlot.LEFT_EAR) || !this.isPaperdollSlotEmpty(InventorySlot.RIGHT_EAR)) {
            this.setPaperdollItem(InventorySlot.LEFT_EAR, item);
        }
        else {
            this.setPaperdollItem(InventorySlot.RIGHT_EAR, item);
        }
    }
    
    private void equipLeftHand(final Item item) {
        if (item.getItemType() != ArmorType.SIGIL && Objects.nonNull(this.getPaperdollItem(InventorySlot.TWO_HAND))) {
            this.setPaperdollItem(InventorySlot.TWO_HAND, null);
            this.setPaperdollItem(InventorySlot.RIGHT_HAND, null);
        }
        this.checkEquippedDress();
        this.setPaperdollItem(InventorySlot.LEFT_HAND, item);
    }
    
    private void checkEquippedDress() {
        final Item dress = this.getPaperdollItem(InventorySlot.CHEST);
        if (Objects.nonNull(dress) && dress.getBodyPart() == BodyPart.ALL_DRESS) {
            this.setPaperdollItem(InventorySlot.CHEST, null);
        }
    }
    
    private void equipTwoHand(final Item item) {
        this.checkEquippedDress();
        if (Util.falseIfNullOrElse((Object)this.getPaperdollItem(InventorySlot.LEFT_HAND), i -> i.getItemType() != ArmorType.SIGIL)) {
            this.setPaperdollItem(InventorySlot.LEFT_HAND, null);
        }
        this.setPaperdollItem(InventorySlot.RIGHT_HAND, item);
        this.paperdoll.put(InventorySlot.TWO_HAND, item);
    }
    
    private void equipArtifact(final Item item) {
        if (this.getArtifactSlots() == 0) {
            return;
        }
        if (Util.isBetween(item.getId(), 48969, 48985)) {
            if (this.checkEquipArtifact(item, InventorySlot.balanceArtifacts())) {
                return;
            }
            this.setPaperdollItem(InventorySlot.ARTIFACT1, item);
        }
        else if (Util.isBetween(item.getId(), 48957, 48960)) {
            if (this.checkEquipArtifact(item, InventorySlot.spiritArtifacts())) {
                return;
            }
            this.setPaperdollItem(InventorySlot.ARTIFACT13, item);
        }
        else if (Util.isBetween(item.getId(), 48961, 48964)) {
            if (this.checkEquipArtifact(item, InventorySlot.protectionArtifacts())) {
                return;
            }
            this.setPaperdollItem(InventorySlot.ARTIFACT16, item);
        }
        else if (Util.isBetween(item.getId(), 48965, 48968)) {
            if (this.checkEquipArtifact(item, InventorySlot.supportArtifact())) {
                return;
            }
            this.setPaperdollItem(InventorySlot.ARTIFACT19, item);
        }
    }
    
    private boolean checkEquipArtifact(final Item item, final EnumSet<InventorySlot> slots) {
        for (final InventorySlot slot : slots) {
            if (this.isPaperdollSlotEmpty(slot)) {
                this.setPaperdollItem(slot, item);
                return true;
            }
        }
        return false;
    }
    
    private void equipAgathion(final Item item) {
        this.equipOnSameOrEmpty(item, InventorySlot.agathions(), this.getAgathionSlots());
    }
    
    private void equipBroochJewel(final Item item) {
        this.equipOnSameOrEmpty(item, InventorySlot.brochesJewel(), this.getBroochJewelSlots());
    }
    
    private void equipTalisman(final Item item) {
        this.equipOnSameOrEmpty(item, InventorySlot.talismans(), this.getTalismanSlots());
    }
    
    private void equipOnSameOrEmpty(final Item item, final EnumSet<InventorySlot> slots, final int availableSlots) {
        if (availableSlots < 1 || slots.isEmpty()) {
            return;
        }
        InventorySlot emptySlot = null;
        int i = 0;
        for (final InventorySlot slot : slots) {
            if (i++ >= availableSlots) {
                break;
            }
            if (!this.isPaperdollSlotEmpty(slot) && item.getId() == this.getPaperdollItemId(slot)) {
                this.setPaperdollItem(slot, item);
                return;
            }
            if (!Objects.isNull(emptySlot) || !this.isPaperdollSlotEmpty(slot)) {
                continue;
            }
            emptySlot = slot;
        }
        if (Objects.nonNull(emptySlot)) {
            this.setPaperdollItem(emptySlot, item);
        }
        else {
            this.setPaperdollItem(slots.iterator().next(), item);
        }
    }
    
    @Override
    protected void refreshWeight() {
        long weight = 0L;
        for (final Item item : this.items.values()) {
            if (item != null && item.getTemplate() != null) {
                weight += item.getTemplate().getWeight() * item.getCount();
            }
        }
        this._totalWeight = (int)Math.min(weight, 2147483647L);
    }
    
    public int getTotalWeight() {
        return this._totalWeight;
    }
    
    @Override
    public void restore() {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement ps = con.prepareStatement("SELECT * FROM items WHERE owner_id=? AND (loc=? OR loc=?) ORDER BY loc_data");
                try {
                    ps.setInt(1, this.getOwnerId());
                    ps.setString(2, this.getBaseLocation().name());
                    ps.setString(3, this.getEquipLocation().name());
                    final ResultSet rs = ps.executeQuery();
                    try {
                        while (rs.next()) {
                            try {
                                final Item item = new Item(rs);
                                if (GameUtils.isPlayer(this.getOwner())) {
                                    final Player player = (Player)this.getOwner();
                                    if (!player.canOverrideCond(PcCondOverride.ITEM_CONDITIONS) && !player.isHero() && item.isHeroItem()) {
                                        item.setItemLocation(ItemLocation.INVENTORY);
                                    }
                                }
                                World.getInstance().addObject(item);
                                if (item.isStackable() && this.getItemByItemId(item.getId()) != null) {
                                    this.addItem("Restore", item, this.getOwner().getActingPlayer(), null);
                                }
                                else {
                                    this.addItem(item);
                                }
                            }
                            catch (Exception e2) {
                                Inventory.LOGGER.warn("Could not restore item {}  for {}", (Object)rs.getInt("item_id"), (Object)this.getOwner());
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
                    this.refreshWeight();
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
            Inventory.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
        }
    }
    
    public int getTalismanSlots() {
        return this.getOwner().getActingPlayer().getStats().getTalismanSlots();
    }
    
    public int getArtifactSlots() {
        return Math.min(this.getOwner().getActingPlayer().getStats().getArtifactSlots(), 3);
    }
    
    public int getBroochJewelSlots() {
        return this.getOwner().getActingPlayer().getStats().getBroochJewelSlots();
    }
    
    public int getAgathionSlots() {
        return this.getOwner().getActingPlayer().getStats().getAgathionSlots();
    }
    
    public boolean canEquipCloak() {
        return this.getOwner().getActingPlayer().getStats().canEquipCloak();
    }
    
    public void reloadEquippedItems() {
        this.paperdoll.forEach((slot, item) -> this.listeners.forEach(l -> {
            l.notifyUnequiped(slot, item, this);
            l.notifyEquiped(slot, item, this);
        }));
        if (GameUtils.isPlayer(this.getOwner())) {
            this.getOwner().sendPacket(new ExUserInfoEquipSlot(this.getOwner().getActingPlayer()));
        }
    }
    
    public void reloadEquippedItem(final Item item) {
        this.paperdoll.entrySet().stream().filter(entry -> entry.getValue() == item).findFirst().ifPresent(e -> this.listeners.forEach(l -> {
            l.notifyUnequiped(e.getKey(), (Item)e.getValue(), this);
            l.notifyEquiped(e.getKey(), (Item)e.getValue(), this);
        }));
    }
    
    public int getArmorMaxEnchant() {
        if (!GameUtils.isPlayer(this.getOwner())) {
            return 0;
        }
        final Player player = this.getOwner().getActingPlayer();
        int maxSetEnchant = 0;
        for (final Item item : this.paperdoll.values()) {
            for (final ArmorSet set : ArmorSetsData.getInstance().getSets(item.getId())) {
                final int enchantEffect = set.getLowestSetEnchant(player);
                if (enchantEffect > maxSetEnchant) {
                    maxSetEnchant = enchantEffect;
                }
            }
        }
        return maxSetEnchant;
    }
    
    public int getWeaponEnchant() {
        final Item item = this.getPaperdollItem(InventorySlot.RIGHT_HAND);
        return (item != null) ? item.getEnchantLevel() : 0;
    }
    
    public boolean isItemSlotBlocked(final BodyPart bodyPart) {
        final long slot;
        return Util.falseIfNullOrElse((Object)bodyPart, part -> {
            slot = part.getId();
            return ((long)this.blockedItemSlotsMask & slot) == slot;
        });
    }
    
    @SafeVarargs
    public final Collection<Item> getPaperdollItems(final Predicate<Item>... filters) {
        Predicate<Item> filter = Objects::nonNull;
        for (final Predicate<Item> additionalFilter : filters) {
            filter = filter.and(additionalFilter);
        }
        return this.paperdoll.values().stream().filter(filter).collect((Collector<? super Item, ?, Collection<Item>>)Collectors.toList());
    }
    
    public double calcForEachEquippedItem(final ToDoubleFunction<Item> function, final double identity, final DoubleBinaryOperator accumulator) {
        return this.paperdoll.values().stream().mapToDouble(function).reduce(identity, accumulator);
    }
    
    public void forEachEquippedItem(final Consumer<Item> action) {
        this.paperdoll.values().forEach(action);
    }
    
    public void forEachEquippedItem(final Consumer<Item> action, final Predicate<Item> predicate) {
        this.paperdoll.values().stream().filter(predicate).forEach(action);
    }
    
    public int countEquippedItems(final Predicate<Item> predicate) {
        return (int)this.paperdoll.values().stream().filter(predicate).count();
    }
    
    public boolean existsEquippedItem(final Predicate<Item> predicate) {
        return this.paperdoll.values().stream().anyMatch(predicate);
    }
    
    static {
        MAX_ADENA = Config.MAX_ADENA;
        LOGGER = LoggerFactory.getLogger((Class)Inventory.class);
    }
    
    private static final class ChangeRecorder implements InventoryListener
    {
        private final Set<Item> changed;
        
        private ChangeRecorder(final Inventory inventory) {
            this.changed = (Set<Item>)ConcurrentHashMap.newKeySet();
            inventory.addPaperdollListener(this);
        }
        
        @Override
        public void notifyEquiped(final InventorySlot slot, final Item item, final Inventory inventory) {
            this.changed.add(item);
        }
        
        @Override
        public void notifyUnequiped(final InventorySlot slot, final Item item, final Inventory inventory) {
            this.changed.add(item);
        }
        
        private Set<Item> getChangedItems() {
            return this.changed;
        }
    }
}
