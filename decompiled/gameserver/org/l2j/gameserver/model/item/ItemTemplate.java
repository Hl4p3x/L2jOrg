// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.item;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.stats.functions.FuncSet;
import org.l2j.gameserver.model.stats.functions.FuncAdd;
import org.l2j.gameserver.model.stats.Stat;
import java.util.Iterator;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import java.util.function.Function;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.engine.skill.api.Skill;
import java.util.function.Consumer;
import org.l2j.gameserver.enums.ItemSkillType;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Predicate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.item.type.EtcItemType;
import java.util.Objects;
import java.util.LinkedHashMap;
import java.util.Collection;
import org.l2j.gameserver.enums.ItemGrade;
import org.l2j.gameserver.model.item.type.ItemType;
import org.l2j.gameserver.model.commission.CommissionItemType;
import org.l2j.gameserver.model.holders.ItemSkillHolder;
import org.l2j.gameserver.model.item.enchant.attribute.AttributeHolder;
import org.l2j.gameserver.enums.AttributeType;
import java.util.Map;
import org.l2j.gameserver.model.item.type.ActionType;
import org.l2j.gameserver.model.item.type.CrystalType;
import org.l2j.gameserver.model.conditions.Condition;
import org.l2j.gameserver.model.stats.functions.FuncTemplate;
import java.util.List;
import org.slf4j.Logger;
import org.l2j.gameserver.model.interfaces.IIdentifiable;
import org.l2j.gameserver.model.events.ListenersContainer;

public abstract class ItemTemplate extends ListenersContainer implements IIdentifiable
{
    public static final int TYPE1_WEAPON_RING_EARRING_NECKLACE = 0;
    public static final int TYPE1_SHIELD_ARMOR = 1;
    public static final int TYPE1_ITEM_QUESTITEM_ADENA = 4;
    public static final int TYPE2_WEAPON = 0;
    public static final int TYPE2_SHIELD_ARMOR = 1;
    public static final int TYPE2_ACCESSORY = 2;
    public static final int TYPE2_QUEST = 3;
    public static final int TYPE2_MONEY = 4;
    public static final int TYPE2_OTHER = 5;
    protected static final Logger LOGGER;
    protected int type1;
    protected int type2;
    protected List<FuncTemplate> _funcTemplates;
    protected List<Condition> _preConditions;
    private int id;
    private int displayId;
    private String name;
    private String icon;
    private int weight;
    private boolean stackable;
    protected CrystalType crystalType;
    protected int equipReuseDelay;
    private long time;
    private int _autoDestroyTime;
    private long price;
    protected int crystalCount;
    private boolean sellable;
    private boolean dropable;
    private boolean destroyable;
    private boolean tradable;
    private boolean depositable;
    protected boolean enchantable;
    protected boolean questItem;
    private boolean freightable;
    private boolean olympiadRestricted;
    private boolean cocRestricted;
    private boolean forNpc;
    private boolean _common;
    private boolean _heroItem;
    private boolean _pvpItem;
    protected boolean immediateEffect;
    protected boolean exImmediateEffect;
    protected ActionType _defaultAction;
    private Map<AttributeType, AttributeHolder> _elementals;
    private List<ItemSkillHolder> skills;
    private int reuseDelay;
    private int reuseGroup;
    private CommissionItemType commissionType;
    protected BodyPart bodyPart;
    
    public ItemTemplate(final int id, final String name) {
        this._autoDestroyTime = -1;
        this._defaultAction = ActionType.NONE;
        this._elementals = null;
        this.id = id;
        this.name = name;
        this._common = (id >= 11605 && id <= 12361);
        this._heroItem = ((id >= 6611 && id <= 6621) || (id >= 9388 && id <= 9390) || id == 6842);
        this._pvpItem = ((id >= 10667 && id <= 10835) || (id >= 12852 && id <= 12977) || (id >= 14363 && id <= 14525) || id == 14528 || id == 14529 || id == 14558 || (id >= 15913 && id <= 16024) || (id >= 16134 && id <= 16147) || id == 16149 || id == 16151 || id == 16153 || id == 16155 || id == 16157 || id == 16159 || (id >= 16168 && id <= 16176) || (id >= 16179 && id <= 16220));
    }
    
    public abstract ItemType getItemType();
    
    public boolean isMagicWeapon() {
        return false;
    }
    
    public int getEquipReuseDelay() {
        return this.equipReuseDelay;
    }
    
    public final long getTime() {
        return this.time;
    }
    
    public final int getAutoDestroyTime() {
        return this._autoDestroyTime;
    }
    
    @Override
    public final int getId() {
        return this.id;
    }
    
    public final int getDisplayId() {
        return this.displayId;
    }
    
    public abstract int getItemMask();
    
    public final int getType2() {
        return this.type2;
    }
    
    public final int getWeight() {
        return this.weight;
    }
    
    public final boolean isCrystallizable() {
        return this.crystalType != CrystalType.NONE && this.crystalCount > 0;
    }
    
    public ItemGrade getItemGrade() {
        return ItemGrade.valueOf(this.crystalType);
    }
    
    public final CrystalType getCrystalType() {
        return this.crystalType;
    }
    
    public final int getCrystalItemId() {
        return this.crystalType.getCrystalId();
    }
    
    public final int getCrystalCount() {
        return this.crystalCount;
    }
    
    public final int getCrystalCount(final int enchantLevel) {
        if (enchantLevel > 3) {
            switch (this.type2) {
                case 1:
                case 2: {
                    return this.crystalCount + this.crystalType.getCrystalEnchantBonusArmor() * (3 * enchantLevel - 6);
                }
                case 0: {
                    return this.crystalCount + this.crystalType.getCrystalEnchantBonusWeapon() * (2 * enchantLevel - 3);
                }
                default: {
                    return this.crystalCount;
                }
            }
        }
        else {
            if (enchantLevel <= 0) {
                return this.crystalCount;
            }
            switch (this.type2) {
                case 1:
                case 2: {
                    return this.crystalCount + this.crystalType.getCrystalEnchantBonusArmor() * enchantLevel;
                }
                case 0: {
                    return this.crystalCount + this.crystalType.getCrystalEnchantBonusWeapon() * enchantLevel;
                }
                default: {
                    return this.crystalCount;
                }
            }
        }
    }
    
    public final String getName() {
        return this.name;
    }
    
    public Collection<AttributeHolder> getAttributes() {
        return (this._elementals != null) ? this._elementals.values() : null;
    }
    
    public void setAttributes(final AttributeHolder holder) {
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
    
    public AttributeHolder getAttribute(final AttributeType type) {
        return (this._elementals != null) ? this._elementals.get(type) : null;
    }
    
    public final BodyPart getBodyPart() {
        return Objects.requireNonNullElse(this.bodyPart, BodyPart.NONE);
    }
    
    public final int getType1() {
        return this.type1;
    }
    
    public final boolean isStackable() {
        return this.stackable;
    }
    
    public boolean isEquipable() {
        return this.bodyPart != BodyPart.NONE && !(this.getItemType() instanceof EtcItemType);
    }
    
    public final long getReferencePrice() {
        return this.price;
    }
    
    public final boolean isSellable() {
        return this.sellable;
    }
    
    public final boolean isDropable() {
        return this.dropable;
    }
    
    public final boolean isDestroyable() {
        return this.destroyable;
    }
    
    public final boolean isTradeable() {
        return this.tradable;
    }
    
    public final boolean isDepositable() {
        return this.depositable;
    }
    
    public void setDepositable(final boolean depositable) {
        this.depositable = depositable;
    }
    
    public final boolean isEnchantable() {
        return Arrays.binarySearch(Config.ENCHANT_BLACKLIST, this.id) < 0 && this.enchantable;
    }
    
    public final boolean isCommon() {
        return this._common;
    }
    
    public final boolean isHeroItem() {
        return this._heroItem;
    }
    
    public final boolean isPvpItem() {
        return this._pvpItem;
    }
    
    public boolean isPotion() {
        return this.getItemType() == EtcItemType.POTION;
    }
    
    public boolean isElixir() {
        return this.getItemType() == EtcItemType.ELIXIR;
    }
    
    public boolean isScroll() {
        return this.getItemType() == EtcItemType.SCROLL;
    }
    
    public List<FuncTemplate> getFunctionTemplates() {
        return (this._funcTemplates != null) ? this._funcTemplates : Collections.emptyList();
    }
    
    public void addFunctionTemplate(final FuncTemplate template) {
        switch (template.getStat()) {
            case FIRE_RES:
            case FIRE_POWER: {
                this.setAttributes(new AttributeHolder(AttributeType.FIRE, (int)template.getValue()));
                break;
            }
            case WATER_RES:
            case WATER_POWER: {
                this.setAttributes(new AttributeHolder(AttributeType.WATER, (int)template.getValue()));
                break;
            }
            case WIND_RES:
            case WIND_POWER: {
                this.setAttributes(new AttributeHolder(AttributeType.WIND, (int)template.getValue()));
                break;
            }
            case EARTH_RES:
            case EARTH_POWER: {
                this.setAttributes(new AttributeHolder(AttributeType.EARTH, (int)template.getValue()));
                break;
            }
            case HOLY_RES:
            case HOLY_POWER: {
                this.setAttributes(new AttributeHolder(AttributeType.HOLY, (int)template.getValue()));
                break;
            }
            case DARK_RES:
            case DARK_POWER: {
                this.setAttributes(new AttributeHolder(AttributeType.DARK, (int)template.getValue()));
                break;
            }
        }
        if (this._funcTemplates == null) {
            this._funcTemplates = new ArrayList<FuncTemplate>();
        }
        this._funcTemplates.add(template);
    }
    
    public final void attachCondition(final Condition c) {
        if (this._preConditions == null) {
            this._preConditions = new ArrayList<Condition>();
        }
        this._preConditions.add(c);
    }
    
    public List<Condition> getConditions() {
        return this._preConditions;
    }
    
    public final List<ItemSkillHolder> getAllSkills() {
        return this.skills;
    }
    
    public final List<ItemSkillHolder> getSkills(final Predicate<ItemSkillHolder> condition) {
        return (List<ItemSkillHolder>)((this.skills != null) ? this.skills.stream().filter((Predicate<? super Object>)condition).collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList()) : null);
    }
    
    public final List<ItemSkillHolder> getSkills(final ItemSkillType type) {
        return (List<ItemSkillHolder>)(Objects.nonNull(this.skills) ? this.skills.stream().filter(sk -> sk.getType() == type).collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList()) : Collections.emptyList());
    }
    
    public final void forEachSkill(final ItemSkillType type, final Consumer<ItemSkillHolder> action) {
        if (Objects.nonNull(this.skills)) {
            this.skills.stream().filter(sk -> sk.getType() == type).forEach((Consumer<? super Object>)action);
        }
    }
    
    public final void forEachSkill(final ItemSkillType type, final Predicate<Skill> filter, final Consumer<Skill> action) {
        if (Objects.nonNull(this.skills)) {
            this.skills.stream().filter(sk -> sk.getType() == type).map((Function<? super Object, ?>)SkillHolder::getSkill).filter((Predicate<? super Object>)filter).forEach((Consumer<? super Object>)action);
        }
    }
    
    public boolean checkAnySkill(final ItemSkillType type, final Predicate<ItemSkillHolder> predicate) {
        return Objects.nonNull(this.skills) && this.skills.stream().filter(sk -> sk.getType() == type).anyMatch((Predicate<? super Object>)predicate);
    }
    
    public void addSkill(final ItemSkillHolder holder) {
        if (this.skills == null) {
            this.skills = new ArrayList<ItemSkillHolder>();
        }
        this.skills.add(holder);
    }
    
    public boolean checkCondition(final Creature activeChar, final WorldObject object, final boolean sendMessage) {
        if (activeChar.canOverrideCond(PcCondOverride.ITEM_CONDITIONS) && !Config.GM_ITEM_RESTRICTION) {
            return true;
        }
        if ((this.isOlyRestrictedItem() || this._heroItem) && GameUtils.isPlayer(activeChar) && activeChar.getActingPlayer().isInOlympiadMode()) {
            if (this.isEquipable()) {
                activeChar.sendPacket(SystemMessageId.YOU_CANNOT_EQUIP_THAT_ITEM_IN_A_OLYMPIAD_MATCH);
            }
            else {
                activeChar.sendPacket(SystemMessageId.YOU_CANNOT_USE_THAT_ITEM_IN_A_OLYMPIAD_MATCH);
            }
            return false;
        }
        if (!this.isConditionAttached()) {
            return true;
        }
        final Creature target = GameUtils.isCreature(object) ? ((Creature)object) : null;
        for (final Condition preCondition : this._preConditions) {
            if (preCondition == null) {
                continue;
            }
            if (preCondition.test(activeChar, target, null, null)) {
                continue;
            }
            if (GameUtils.isSummon(activeChar)) {
                activeChar.sendPacket(SystemMessageId.THIS_PET_CANNOT_USE_THIS_ITEM);
                return false;
            }
            if (sendMessage) {
                final String msg = preCondition.getMessage();
                final int msgId = preCondition.getMessageId();
                if (msg != null) {
                    activeChar.sendMessage(msg);
                }
                else if (msgId != 0) {
                    final SystemMessage sm = SystemMessage.getSystemMessage(msgId);
                    if (preCondition.isAddName()) {
                        sm.addItemName(this.id);
                    }
                    activeChar.sendPacket(sm);
                }
            }
            return false;
        }
        return true;
    }
    
    public boolean isConditionAttached() {
        return this._preConditions != null && !this._preConditions.isEmpty();
    }
    
    public boolean isQuestItem() {
        return this.questItem;
    }
    
    public boolean isFreightable() {
        return this.freightable;
    }
    
    public boolean isOlyRestrictedItem() {
        return this.olympiadRestricted || Config.LIST_OLY_RESTRICTED_ITEMS.contains(this.id);
    }
    
    public boolean isForNpc() {
        return this.forNpc;
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.name, this.id);
    }
    
    public boolean hasExImmediateEffect() {
        return this.exImmediateEffect;
    }
    
    public boolean hasImmediateEffect() {
        return this.immediateEffect;
    }
    
    public ActionType getDefaultAction() {
        return this._defaultAction;
    }
    
    public int getReuseDelay() {
        return this.reuseDelay;
    }
    
    public int getSharedReuseGroup() {
        return this.reuseGroup;
    }
    
    public CommissionItemType getCommissionItemType() {
        return this.commissionType;
    }
    
    public String getIcon() {
        return this.icon;
    }
    
    public boolean isPetItem() {
        return this.getItemType() == EtcItemType.PET_COLLAR;
    }
    
    public double getStats(final Stat stat, final double defaultValue) {
        if (this._funcTemplates != null) {
            final FuncTemplate template = this._funcTemplates.stream().filter(func -> func.getStat() == stat && (func.getFunctionClass() == FuncAdd.class || func.getFunctionClass() == FuncSet.class)).findFirst().orElse(null);
            if (template != null) {
                return template.getValue();
            }
        }
        return defaultValue;
    }
    
    public void setIcon(final String icon) {
        this.icon = icon;
    }
    
    public void setDisplayId(final int displayId) {
        this.displayId = displayId;
    }
    
    public void setFreightable(final boolean freightable) {
        this.freightable = freightable;
    }
    
    public void setOlympiadRestricted(final boolean olympiadRestricted) {
        this.olympiadRestricted = olympiadRestricted;
    }
    
    public void setStackable(final boolean stackable) {
        this.stackable = stackable;
    }
    
    public void setDestroyable(final boolean destroyable) {
        this.destroyable = destroyable;
    }
    
    public void setTradable(final boolean tradable) {
        this.tradable = tradable;
    }
    
    public void setDropable(final boolean dropable) {
        this.dropable = dropable;
    }
    
    public void setSellable(final boolean sellable) {
        this.sellable = sellable;
    }
    
    public void setWeight(final int weight) {
        this.weight = weight;
    }
    
    public void setPrice(final long price) {
        this.price = price;
    }
    
    public void setCommissionType(final CommissionItemType commissionType) {
        this.commissionType = commissionType;
    }
    
    public void setReuseDelay(final int reuseDelay) {
        this.reuseDelay = reuseDelay;
    }
    
    public void setReuseGroup(final int reuseGroup) {
        this.reuseGroup = reuseGroup;
    }
    
    public void setDuration(final long duration) {
        this.time = duration;
    }
    
    public void setForNpc(final Boolean forNpc) {
        this.forNpc = forNpc;
    }
    
    public void setCrystalType(final CrystalType type) {
        this.crystalType = type;
    }
    
    public void setCrystalCount(final int count) {
        this.crystalCount = count;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ItemTemplate.class);
    }
}
