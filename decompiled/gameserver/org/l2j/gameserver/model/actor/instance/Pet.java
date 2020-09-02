// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import java.util.List;
import org.l2j.commons.util.Rnd;
import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.actor.stat.PlayableStats;
import org.l2j.gameserver.model.actor.stat.SummonStats;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.data.xml.impl.LevelData;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.skills.EffectScope;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.engine.skill.api.Skill;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.skills.BuffInfo;
import java.util.HashSet;
import java.util.Collections;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.data.sql.impl.SummonEffectsTable;
import java.util.Collection;
import java.util.Iterator;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.model.item.container.ItemContainer;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.taskmanager.DecayTaskManager;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.data.sql.impl.PlayerSummonTable;
import org.l2j.gameserver.handler.IItemHandler;
import org.l2j.gameserver.network.serverpackets.PetItemList;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.handler.ItemHandler;
import org.l2j.gameserver.instancemanager.ItemsOnGroundManager;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.enums.PartyDistributionType;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.serverpackets.StopMove;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.PetInventoryUpdate;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.item.Weapon;
import java.util.function.Predicate;
import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.network.serverpackets.ExChangeNpcState;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.stat.CreatureStats;
import org.l2j.gameserver.model.actor.stat.PetStats;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.data.xml.impl.PetDataTable;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.PetLevelData;
import org.l2j.gameserver.model.PetData;
import java.util.concurrent.Future;
import org.l2j.gameserver.model.item.container.PetInventory;
import org.slf4j.Logger;
import org.l2j.gameserver.model.actor.Summon;

public class Pet extends Summon
{
    protected static final Logger LOGGER_PET;
    private static final String ADD_SKILL_SAVE = "INSERT INTO character_pet_skills_save (petObjItemId,skill_id,skill_level,skill_sub_level,remaining_time,buff_index) VALUES (?,?,?,?,?,?)";
    private static final String RESTORE_SKILL_SAVE = "SELECT petObjItemId,skill_id,skill_level,skill_sub_level,remaining_time,buff_index FROM character_pet_skills_save WHERE petObjItemId=? ORDER BY buff_index ASC";
    private static final String DELETE_SKILL_SAVE = "DELETE FROM character_pet_skills_save WHERE petObjItemId=?";
    final PetInventory _inventory;
    private final int _controlObjectId;
    private final boolean _mountable;
    int _curFed;
    private boolean _respawned;
    private Future<?> _feedTask;
    private PetData _data;
    private PetLevelData _leveldata;
    private long _expBeforeDeath;
    private int _curWeightPenalty;
    
    public Pet(final NpcTemplate template, final Player owner, final Item control) {
        this(template, owner, control, (byte)((template.getDisplayId() == 12564) ? owner.getLevel() : template.getLevel()));
    }
    
    public Pet(final NpcTemplate template, final Player owner, final Item control, final byte level) {
        super(template, owner);
        this._expBeforeDeath = 0L;
        this._curWeightPenalty = 0;
        this.setInstanceType(InstanceType.L2PetInstance);
        this._controlObjectId = control.getObjectId();
        this.getStats().setLevel((byte)Math.max(level, PetDataTable.getInstance().getPetMinLevel(template.getId())));
        (this._inventory = new PetInventory(this)).restore();
        final int npcId = template.getId();
        this._mountable = PetDataTable.isMountable(npcId);
        this.getPetData();
        this.getPetLevelData();
    }
    
    public static synchronized Pet spawnPet(final NpcTemplate template, final Player owner, final Item control) {
        if (World.getInstance().findPet(owner.getObjectId()) != null) {
            return null;
        }
        final PetData data = PetDataTable.getInstance().getPetData(template.getId());
        final Pet pet = restore(control, template, owner);
        if (pet != null) {
            pet.setTitle(owner.getName());
            if (data.isSynchLevel() && pet.getLevel() != owner.getLevel()) {
                final byte availableLevel = (byte)Math.min(data.getMaxLevel(), owner.getLevel());
                pet.getStats().setLevel(availableLevel);
                pet.getStats().setExp(pet.getStats().getExpForLevel(availableLevel));
            }
            World.getInstance().addPet(owner.getObjectId(), pet);
        }
        return pet;
    }
    
    private static Pet restore(final Item control, final NpcTemplate template, final Player owner) {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement statement = con.prepareStatement("SELECT item_obj_id, name, level, curHp, curMp, exp, sp, fed FROM pets WHERE item_obj_id=?");
                try {
                    statement.setInt(1, control.getObjectId());
                    final ResultSet rset = statement.executeQuery();
                    Pet pet;
                    try {
                        if (!rset.next()) {
                            final Pet pet2 = new Pet(template, owner, control);
                            if (rset != null) {
                                rset.close();
                            }
                            if (statement != null) {
                                statement.close();
                            }
                            if (con != null) {
                                con.close();
                            }
                            return pet2;
                        }
                        pet = new Pet(template, owner, control, rset.getByte("level"));
                        pet._respawned = true;
                        pet.setName(rset.getString("name"));
                        long exp = rset.getLong("exp");
                        final PetLevelData info = PetDataTable.getInstance().getPetLevelData(pet.getId(), pet.getLevel());
                        if (info != null && exp < info.getPetMaxExp()) {
                            exp = info.getPetMaxExp();
                        }
                        pet.getStats().setExp(exp);
                        pet.getStats().setSp(rset.getInt("sp"));
                        pet.getStatus().setCurrentHp(rset.getInt("curHp"));
                        pet.getStatus().setCurrentMp(rset.getInt("curMp"));
                        pet.getStatus().setCurrentCp(pet.getMaxCp());
                        if (rset.getDouble("curHp") < 1.0) {
                            pet.setIsDead(true);
                            pet.stopHpMpRegeneration();
                        }
                        pet.setCurrentFed(rset.getInt("fed"));
                        if (rset != null) {
                            rset.close();
                        }
                    }
                    catch (Throwable t) {
                        if (rset != null) {
                            try {
                                rset.close();
                            }
                            catch (Throwable exception) {
                                t.addSuppressed(exception);
                            }
                        }
                        throw t;
                    }
                    final Pet pet3 = pet;
                    if (statement != null) {
                        statement.close();
                    }
                    if (con != null) {
                        con.close();
                    }
                    return pet3;
                }
                catch (Throwable t2) {
                    if (statement != null) {
                        try {
                            statement.close();
                        }
                        catch (Throwable exception2) {
                            t2.addSuppressed(exception2);
                        }
                    }
                    throw t2;
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
            Pet.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;Ljava/lang/String;)Ljava/lang/String;, owner, e.getMessage()), (Throwable)e);
            return null;
        }
    }
    
    public final PetLevelData getPetLevelData() {
        if (this._leveldata == null) {
            this._leveldata = PetDataTable.getInstance().getPetLevelData(this.getTemplate().getId(), this.getStats().getLevel());
        }
        return this._leveldata;
    }
    
    public final PetData getPetData() {
        if (this._data == null) {
            this._data = PetDataTable.getInstance().getPetData(this.getTemplate().getId());
        }
        return this._data;
    }
    
    public final void setPetData(final PetLevelData value) {
        this._leveldata = value;
    }
    
    @Override
    public PetStats getStats() {
        return (PetStats)super.getStats();
    }
    
    @Override
    public void initCharStat() {
        this.setStat(new PetStats(this));
    }
    
    public boolean isRespawned() {
        return this._respawned;
    }
    
    @Override
    public int getSummonType() {
        return 2;
    }
    
    @Override
    public int getControlObjectId() {
        return this._controlObjectId;
    }
    
    public Item getControlItem() {
        return this.getOwner().getInventory().getItemByObjectId(this._controlObjectId);
    }
    
    public int getCurrentFed() {
        return this._curFed;
    }
    
    public void setCurrentFed(final int num) {
        if (num <= 0) {
            this.sendPacket(new ExChangeNpcState(this.getObjectId(), 100));
        }
        else if (this._curFed <= 0 && num > 0) {
            this.sendPacket(new ExChangeNpcState(this.getObjectId(), 101));
        }
        this._curFed = ((num > this.getMaxFed()) ? this.getMaxFed() : num);
    }
    
    @Override
    public Item getActiveWeaponInstance() {
        if (this._inventory != null) {
            return this._inventory.getItems(item -> item.getItemLocation() == ItemLocation.PET_EQUIP && item.getBodyPart() == BodyPart.RIGHT_HAND, (Predicate<Item>[])new Predicate[0]).stream().findAny().orElse(null);
        }
        return null;
    }
    
    @Override
    public Weapon getActiveWeaponItem() {
        final Item weapon = this.getActiveWeaponInstance();
        if (weapon == null) {
            return null;
        }
        return (Weapon)weapon.getTemplate();
    }
    
    @Override
    public Item getSecondaryWeaponInstance() {
        return null;
    }
    
    @Override
    public Weapon getSecondaryWeaponItem() {
        return null;
    }
    
    @Override
    public PetInventory getInventory() {
        return this._inventory;
    }
    
    @Override
    public boolean destroyItem(final String process, final int objectId, final long count, final WorldObject reference, final boolean sendMessage) {
        final Item item = this._inventory.destroyItem(process, objectId, count, this.getOwner(), reference);
        if (item == null) {
            if (sendMessage) {
                this.sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
            }
            return false;
        }
        final PetInventoryUpdate petIU = new PetInventoryUpdate();
        petIU.addItem(item);
        this.sendPacket(petIU);
        if (sendMessage) {
            if (count > 1L) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S2_S1_S_DISAPPEARED);
                sm.addItemName(item.getId());
                sm.addLong(count);
                this.sendPacket(sm);
            }
            else {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DISAPPEARED);
                sm.addItemName(item.getId());
                this.sendPacket(sm);
            }
        }
        return true;
    }
    
    @Override
    public boolean destroyItemByItemId(final String process, final int itemId, final long count, final WorldObject reference, final boolean sendMessage) {
        final Item item = this._inventory.destroyItemByItemId(process, itemId, count, this.getOwner(), reference);
        if (item == null) {
            if (sendMessage) {
                this.sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
            }
            return false;
        }
        final PetInventoryUpdate petIU = new PetInventoryUpdate();
        petIU.addItem(item);
        this.sendPacket(petIU);
        if (sendMessage) {
            if (count > 1L) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S2_S1_S_DISAPPEARED);
                sm.addItemName(item.getId());
                sm.addLong(count);
                this.sendPacket(sm);
            }
            else {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DISAPPEARED);
                sm.addItemName(item.getId());
                this.sendPacket(sm);
            }
        }
        return true;
    }
    
    @Override
    public void doPickupItem(final WorldObject object) {
        if (this.isDead()) {
            return;
        }
        this.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
        this.broadcastPacket(new StopMove(this));
        if (!GameUtils.isItem(object)) {
            Pet.LOGGER_PET.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Pet;Lorg/l2j/gameserver/model/WorldObject;)Ljava/lang/String;, this, object));
            this.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        final boolean follow = this.getFollowStatus();
        final Item target = (Item)object;
        SystemMessage smsg = null;
        synchronized (target) {
            if (!target.isSpawned()) {
                this.sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }
            if (!target.getDropProtection().tryPickUp(this)) {
                this.sendPacket(ActionFailed.STATIC_PACKET);
                smsg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_FAILED_TO_PICK_UP_S1);
                smsg.addItemName(target);
                this.sendPacket(smsg);
                return;
            }
            if (((this.isInParty() && this.getParty().getDistributionType() == PartyDistributionType.FINDERS_KEEPERS) || !this.isInParty()) && !this._inventory.validateCapacity(target)) {
                this.sendPacket(ActionFailed.STATIC_PACKET);
                this.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_ANY_MORE_ITEMS);
                return;
            }
            if (target.getOwnerId() != 0 && target.getOwnerId() != this.getOwner().getObjectId() && !this.getOwner().isInLooterParty(target.getOwnerId())) {
                if (target.getId() == 57) {
                    smsg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_FAILED_TO_PICK_UP_S1_ADENA);
                    smsg.addLong(target.getCount());
                }
                else if (target.getCount() > 1L) {
                    smsg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_FAILED_TO_PICK_UP_S2_S1_S);
                    smsg.addItemName(target);
                    smsg.addLong(target.getCount());
                }
                else {
                    smsg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_FAILED_TO_PICK_UP_S1);
                    smsg.addItemName(target);
                }
                this.sendPacket(ActionFailed.STATIC_PACKET);
                this.sendPacket(smsg);
                return;
            }
            if (target.getItemLootShedule() != null && (target.getOwnerId() == this.getOwner().getObjectId() || this.getOwner().isInLooterParty(target.getOwnerId()))) {
                target.resetOwnerTimer();
            }
            target.pickupMe(this);
            if (((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).saveDroppedItems()) {
                ItemsOnGroundManager.getInstance().removeObject(target);
            }
        }
        if (target.getTemplate().hasExImmediateEffect()) {
            final IItemHandler handler = ItemHandler.getInstance().getHandler(target.getEtcItem());
            if (handler == null) {
                Pet.LOGGER.warn(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, target.getId()));
            }
            else {
                handler.useItem(this, target, false);
            }
            ItemEngine.getInstance().destroyItem("Consume", target, this.getOwner(), null);
            this.broadcastStatusUpdate();
        }
        else {
            if (target.getId() == 57) {
                smsg = SystemMessage.getSystemMessage(SystemMessageId.YOUR_PET_PICKED_UP_S1_ADENA);
                smsg.addLong(target.getCount());
                this.sendPacket(smsg);
            }
            else if (target.getEnchantLevel() > 0) {
                smsg = SystemMessage.getSystemMessage(SystemMessageId.YOUR_PET_PICKED_UP_S1_S2);
                smsg.addInt(target.getEnchantLevel());
                smsg.addItemName(target);
                this.sendPacket(smsg);
            }
            else if (target.getCount() > 1L) {
                smsg = SystemMessage.getSystemMessage(SystemMessageId.YOUR_PET_PICKED_UP_S2_S1_S);
                smsg.addLong(target.getCount());
                smsg.addItemName(target);
                this.sendPacket(smsg);
            }
            else {
                smsg = SystemMessage.getSystemMessage(SystemMessageId.YOUR_PET_PICKED_UP_S1);
                smsg.addItemName(target);
                this.sendPacket(smsg);
            }
            if (this.getOwner().isInParty() && this.getOwner().getParty().getDistributionType() != PartyDistributionType.FINDERS_KEEPERS) {
                this.getOwner().getParty().distributeItem(this.getOwner(), target);
            }
            else {
                final Item item = this._inventory.addItem("Pickup", target, this.getOwner(), this);
                if (item != null) {
                    this.getOwner().sendPacket(new PetItemList(this.getInventory().getItems()));
                }
            }
        }
        this.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
        if (follow) {
            this.followOwner();
        }
    }
    
    @Override
    public void deleteMe(final Player owner) {
        this._inventory.transferItemsToOwner();
        super.deleteMe(owner);
        this.destroyControlItem(owner, false);
        PlayerSummonTable.getInstance().getPets().remove(this.getOwner().getObjectId());
    }
    
    @Override
    public boolean doDie(final Creature killer) {
        if (!super.doDie(killer, true)) {
            return false;
        }
        final Player owner = this.getOwner();
        if (owner != null && !owner.isInDuel() && (!this.isInsideZone(ZoneType.PVP) || this.isInsideZone(ZoneType.SIEGE))) {
            this.deathPenalty();
        }
        this.stopFeed();
        this.sendPacket(SystemMessageId.THE_PET_HAS_BEEN_KILLED_IF_YOU_DON_T_RESURRECT_IT_WITHIN_24_HOURS_THE_PET_S_BODY_WILL_DISAPPEAR_ALONG_WITH_ALL_THE_PET_S_ITEMS);
        return true;
    }
    
    @Override
    public void doRevive() {
        this.getOwner().removeReviving();
        super.doRevive();
        DecayTaskManager.getInstance().cancel(this);
        this.startFeed();
        if (!this.isHungry()) {
            this.setRunning();
        }
        this.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
    }
    
    @Override
    public void doRevive(final double revivePower) {
        this.restoreExp(revivePower);
        this.doRevive();
    }
    
    public Item transferItem(final String process, final int objectId, final long count, final Inventory target, final Player actor, final WorldObject reference) {
        final Item oldItem = this._inventory.getItemByObjectId(objectId);
        final Item playerOldItem = target.getItemByItemId(oldItem.getId());
        final Item newItem = this._inventory.transferItem(process, objectId, count, target, actor, reference);
        if (newItem == null) {
            return null;
        }
        final PetInventoryUpdate petIU = new PetInventoryUpdate();
        if (oldItem.getCount() > 0L && oldItem != newItem) {
            petIU.addModifiedItem(oldItem);
        }
        else {
            petIU.addRemovedItem(oldItem);
        }
        this.sendPacket(petIU);
        if (playerOldItem != null && newItem.isStackable()) {
            final InventoryUpdate iu = new InventoryUpdate();
            iu.addModifiedItem(newItem);
            this.sendInventoryUpdate(iu);
        }
        return newItem;
    }
    
    public void destroyControlItem(final Player owner, final boolean evolve) {
        World.getInstance().removePet(owner.getObjectId());
        try {
            Item removedItem;
            if (evolve) {
                removedItem = owner.getInventory().destroyItem("Evolve", this._controlObjectId, 1L, this.getOwner(), this);
            }
            else {
                removedItem = owner.getInventory().destroyItem("PetDestroy", this._controlObjectId, 1L, this.getOwner(), this);
                if (removedItem != null) {
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DISAPPEARED);
                    sm.addItemName(removedItem);
                    owner.sendPacket(sm);
                }
            }
            if (removedItem == null) {
                Pet.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;Lorg/l2j/gameserver/model/actor/instance/Pet;Z)Ljava/lang/String;, owner, this, evolve));
            }
            else {
                final InventoryUpdate iu = new InventoryUpdate();
                iu.addRemovedItem(removedItem);
                owner.sendInventoryUpdate(iu);
                owner.broadcastUserInfo();
            }
        }
        catch (Exception e) {
            Pet.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
        }
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement statement = con.prepareStatement("DELETE FROM pets WHERE item_obj_id = ?");
                try {
                    statement.setInt(1, this._controlObjectId);
                    statement.execute();
                    if (statement != null) {
                        statement.close();
                    }
                }
                catch (Throwable t) {
                    if (statement != null) {
                        try {
                            statement.close();
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
            Pet.LOGGER_PET.error(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.getObjectId()), (Throwable)e);
        }
    }
    
    public void dropAllItems() {
        try {
            for (final Item item : this._inventory.getItems()) {
                this.dropItemHere(item);
            }
        }
        catch (Exception e) {
            Pet.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
        }
    }
    
    public void dropItemHere(Item dropit, final boolean protect) {
        dropit = this._inventory.dropItem("Drop", dropit.getObjectId(), dropit.getCount(), this.getOwner(), this);
        if (dropit != null) {
            if (protect) {
                dropit.getDropProtection().protect(this.getOwner());
            }
            Pet.LOGGER_PET.debug(invokedynamic(makeConcatWithConstants:(IJ)Ljava/lang/String;, dropit.getId(), dropit.getCount()));
            dropit.dropMe(this, this.getX(), this.getY(), this.getZ() + 100);
        }
    }
    
    public void dropItemHere(final Item dropit) {
        this.dropItemHere(dropit, false);
    }
    
    @Override
    public boolean isMountable() {
        return this._mountable;
    }
    
    @Override
    public void setRestoreSummon(final boolean val) {
        this._restoreSummon = val;
    }
    
    @Override
    public final void stopSkillEffects(final boolean removed, final int skillId) {
        super.stopSkillEffects(removed, skillId);
        final Collection<SummonEffectsTable.SummonEffect> effects = SummonEffectsTable.getInstance().getPetEffects().get(this.getControlObjectId());
        if (effects != null && !effects.isEmpty()) {
            for (final SummonEffectsTable.SummonEffect effect : effects) {
                if (effect.getSkill().getId() == skillId) {
                    SummonEffectsTable.getInstance().getPetEffects().get(this.getControlObjectId()).remove(effect);
                }
            }
        }
    }
    
    @Override
    public void storeMe() {
        if (this._controlObjectId == 0) {
            return;
        }
        if (!((CharacterSettings)Configurator.getSettings((Class)CharacterSettings.class)).restoreSummonOnReconnect()) {
            this._restoreSummon = false;
        }
        String req;
        if (!this._respawned) {
            req = "INSERT INTO pets (name,level,curHp,curMp,exp,sp,fed,ownerId,restore,item_obj_id) VALUES (?,?,?,?,?,?,?,?,?,?)";
        }
        else {
            req = "UPDATE pets SET name=?,level=?,curHp=?,curMp=?,exp=?,sp=?,fed=?,ownerId=?,restore=? WHERE item_obj_id = ?";
        }
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement statement = con.prepareStatement(req);
                try {
                    statement.setString(1, this.getName());
                    statement.setInt(2, this.getStats().getLevel());
                    statement.setDouble(3, this.getStatus().getCurrentHp());
                    statement.setDouble(4, this.getStatus().getCurrentMp());
                    statement.setLong(5, this.getStats().getExp());
                    statement.setLong(6, this.getStats().getSp());
                    statement.setInt(7, this._curFed);
                    statement.setInt(8, this.getOwner().getObjectId());
                    statement.setString(9, String.valueOf(this._restoreSummon));
                    statement.setInt(10, this._controlObjectId);
                    statement.executeUpdate();
                    this._respawned = true;
                    if (this._restoreSummon) {
                        PlayerSummonTable.getInstance().getPets().put(this.getOwner().getObjectId(), this.getControlObjectId());
                    }
                    else {
                        PlayerSummonTable.getInstance().getPets().remove(this.getOwner().getObjectId());
                    }
                    if (statement != null) {
                        statement.close();
                    }
                }
                catch (Throwable t) {
                    if (statement != null) {
                        try {
                            statement.close();
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
            Pet.LOGGER_PET.error(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.getObjectId()), (Throwable)e);
        }
        final Item itemInst = this.getControlItem();
        if (itemInst != null && itemInst.getEnchantLevel() != this.getStats().getLevel()) {
            itemInst.setEnchantLevel(this.getStats().getLevel());
            itemInst.updateDatabase();
        }
    }
    
    @Override
    public void storeEffect(final boolean storeEffects) {
        if (!Config.SUMMON_STORE_SKILL_COOLTIME) {
            return;
        }
        SummonEffectsTable.getInstance().getPetEffects().getOrDefault(this.getControlObjectId(), (Collection<SummonEffectsTable.SummonEffect>)Collections.emptyList()).clear();
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement ps1 = con.prepareStatement("DELETE FROM character_pet_skills_save WHERE petObjItemId=?");
                try {
                    final PreparedStatement ps2 = con.prepareStatement("INSERT INTO character_pet_skills_save (petObjItemId,skill_id,skill_level,skill_sub_level,remaining_time,buff_index) VALUES (?,?,?,?,?,?)");
                    try {
                        ps1.setInt(1, this._controlObjectId);
                        ps1.execute();
                        int buff_index = 0;
                        final Set<Long> storedSkills = new HashSet<Long>();
                        if (storeEffects) {
                            for (final BuffInfo info : this.getEffectList().getEffects()) {
                                if (info == null) {
                                    continue;
                                }
                                final Skill skill = info.getSkill();
                                if (skill.isDeleteAbnormalOnLeave()) {
                                    continue;
                                }
                                if (skill.getAbnormalType() == AbnormalType.LIFE_FORCE_OTHERS) {
                                    continue;
                                }
                                if (skill.isToggle()) {
                                    continue;
                                }
                                if (skill.isDance() && !Config.ALT_STORE_DANCES) {
                                    continue;
                                }
                                if (!storedSkills.add(skill.getReuseHashCode())) {
                                    continue;
                                }
                                ps2.setInt(1, this._controlObjectId);
                                ps2.setInt(2, skill.getId());
                                ps2.setInt(3, skill.getLevel());
                                ps2.setInt(4, skill.getSubLevel());
                                ps2.setInt(5, info.getTime());
                                ps2.setInt(6, ++buff_index);
                                ps2.addBatch();
                                SummonEffectsTable.getInstance().getPetEffects().computeIfAbsent(Integer.valueOf(this.getControlObjectId()), k -> ConcurrentHashMap.newKeySet()).add(new SummonEffectsTable.SummonEffect(skill, info.getTime()));
                            }
                            ps2.executeBatch();
                        }
                        if (ps2 != null) {
                            ps2.close();
                        }
                    }
                    catch (Throwable t) {
                        if (ps2 != null) {
                            try {
                                ps2.close();
                            }
                            catch (Throwable exception) {
                                t.addSuppressed(exception);
                            }
                        }
                        throw t;
                    }
                    if (ps1 != null) {
                        ps1.close();
                    }
                }
                catch (Throwable t2) {
                    if (ps1 != null) {
                        try {
                            ps1.close();
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
            Pet.LOGGER.warn("Could not store pet effect data: ", (Throwable)e);
        }
    }
    
    @Override
    public void restoreEffects() {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement ps1 = con.prepareStatement("SELECT petObjItemId,skill_id,skill_level,skill_sub_level,remaining_time,buff_index FROM character_pet_skills_save WHERE petObjItemId=? ORDER BY buff_index ASC");
                try {
                    final PreparedStatement ps2 = con.prepareStatement("DELETE FROM character_pet_skills_save WHERE petObjItemId=?");
                    try {
                        if (!SummonEffectsTable.getInstance().getPetEffects().containsKey(this.getControlObjectId())) {
                            ps1.setInt(1, this._controlObjectId);
                            final ResultSet rset = ps1.executeQuery();
                            try {
                                while (rset.next()) {
                                    final int effectCurTime = rset.getInt("remaining_time");
                                    final Skill skill = SkillEngine.getInstance().getSkill(rset.getInt("skill_id"), rset.getInt("skill_level"));
                                    if (skill == null) {
                                        continue;
                                    }
                                    if (!skill.hasEffects(EffectScope.GENERAL)) {
                                        continue;
                                    }
                                    SummonEffectsTable.getInstance().getPetEffects().computeIfAbsent(Integer.valueOf(this.getControlObjectId()), k -> ConcurrentHashMap.newKeySet()).add(new SummonEffectsTable.SummonEffect(skill, effectCurTime));
                                }
                                if (rset != null) {
                                    rset.close();
                                }
                            }
                            catch (Throwable t) {
                                if (rset != null) {
                                    try {
                                        rset.close();
                                    }
                                    catch (Throwable exception) {
                                        t.addSuppressed(exception);
                                    }
                                }
                                throw t;
                            }
                        }
                        ps2.setInt(1, this._controlObjectId);
                        ps2.executeUpdate();
                        if (ps2 != null) {
                            ps2.close();
                        }
                    }
                    catch (Throwable t2) {
                        if (ps2 != null) {
                            try {
                                ps2.close();
                            }
                            catch (Throwable exception2) {
                                t2.addSuppressed(exception2);
                            }
                        }
                        throw t2;
                    }
                    if (ps1 != null) {
                        ps1.close();
                    }
                }
                catch (Throwable t3) {
                    if (ps1 != null) {
                        try {
                            ps1.close();
                        }
                        catch (Throwable exception3) {
                            t3.addSuppressed(exception3);
                        }
                    }
                    throw t3;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t4) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception4) {
                        t4.addSuppressed(exception4);
                    }
                }
                throw t4;
            }
        }
        catch (Exception e) {
            Pet.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Pet;Ljava/lang/String;)Ljava/lang/String;, this, e.getMessage()), (Throwable)e);
        }
        finally {
            if (SummonEffectsTable.getInstance().getPetEffects().get(this.getControlObjectId()) == null) {
                return;
            }
            for (final SummonEffectsTable.SummonEffect se : SummonEffectsTable.getInstance().getPetEffects().get(this.getControlObjectId())) {
                if (se != null) {
                    se.getSkill().applyEffects(this, this, false, se.getEffectCurTime());
                }
            }
        }
    }
    
    public synchronized void stopFeed() {
        if (this._feedTask != null) {
            this._feedTask.cancel(false);
            this._feedTask = null;
        }
    }
    
    public synchronized void startFeed() {
        this.stopFeed();
        if (!this.isDead() && this.getOwner().getPet() == this) {
            this._feedTask = (Future<?>)ThreadPool.scheduleAtFixedRate((Runnable)new FeedTask(), 10000L, 10000L);
        }
    }
    
    @Override
    public synchronized void unSummon(final Player owner) {
        this.stopFeed();
        this.stopHpMpRegeneration();
        super.unSummon(owner);
        if (!this.isDead()) {
            if (this._inventory != null) {
                this._inventory.deleteMe();
            }
            World.getInstance().removePet(owner.getObjectId());
        }
    }
    
    public void restoreExp(final double restorePercent) {
        if (this._expBeforeDeath > 0L) {
            this.getStats().addExp(Math.round((this._expBeforeDeath - this.getStats().getExp()) * restorePercent / 100.0));
            this._expBeforeDeath = 0L;
        }
    }
    
    private void deathPenalty() {
        final int lvl = this.getStats().getLevel();
        final double percentLost = -0.07 * lvl + 6.5;
        final long lostExp = Math.round((this.getStats().getExpForLevel(lvl + 1) - this.getStats().getExpForLevel(lvl)) * percentLost / 100.0);
        this._expBeforeDeath = this.getStats().getExp();
        this.getStats().addExp(-lostExp);
    }
    
    @Override
    public void addExpAndSp(final double addToExp, final double addToSp) {
        if (this.getId() == 12564) {
            this.getStats().addExpAndSp(addToExp * Config.SINEATER_XP_RATE, addToSp);
        }
        else {
            this.getStats().addExpAndSp(addToExp * Config.PET_XP_RATE, addToSp);
        }
    }
    
    @Override
    public long getExpForThisLevel() {
        if (this.getLevel() >= LevelData.getInstance().getMaxLevel()) {
            return 0L;
        }
        return this.getStats().getExpForLevel(this.getLevel());
    }
    
    @Override
    public long getExpForNextLevel() {
        if (this.getLevel() >= LevelData.getInstance().getMaxLevel()) {
            return 0L;
        }
        return this.getStats().getExpForLevel(this.getLevel() + 1);
    }
    
    @Override
    public final int getLevel() {
        return this.getStats().getLevel();
    }
    
    public int getMaxFed() {
        return this.getStats().getMaxFeed();
    }
    
    @Override
    public int getCriticalHit() {
        return this.getStats().getCriticalHit();
    }
    
    @Override
    public int getMAtk() {
        return this.getStats().getMAtk();
    }
    
    @Override
    public int getMDef() {
        return this.getStats().getMDef();
    }
    
    @Override
    public final int getSkillLevel(final int skillId) {
        if (this.getKnownSkill(skillId) == null) {
            return 0;
        }
        final int lvl = this.getLevel();
        return (lvl > 70) ? (7 + (lvl - 70) / 5) : (lvl / 10);
    }
    
    public void updateRefOwner(final Player owner) {
        final int oldOwnerId = this.getOwner().getObjectId();
        this.setOwner(owner);
        World.getInstance().removePet(oldOwnerId);
        World.getInstance().addPet(oldOwnerId, this);
    }
    
    public int getInventoryLimit() {
        return Config.INVENTORY_MAXIMUM_PET;
    }
    
    public void refreshOverloaded() {
        final int maxLoad = this.getMaxLoad();
        if (maxLoad > 0) {
            final long weightproc = (this.getCurrentLoad() - this.getBonusWeightPenalty()) * 1000 / maxLoad;
            int newWeightPenalty;
            if (weightproc < 500L || this.getOwner().getDietMode()) {
                newWeightPenalty = 0;
            }
            else if (weightproc < 666L) {
                newWeightPenalty = 1;
            }
            else if (weightproc < 800L) {
                newWeightPenalty = 2;
            }
            else if (weightproc < 1000L) {
                newWeightPenalty = 3;
            }
            else {
                newWeightPenalty = 4;
            }
            if (this._curWeightPenalty != newWeightPenalty) {
                if ((this._curWeightPenalty = newWeightPenalty) > 0) {
                    this.addSkill(SkillEngine.getInstance().getSkill(4270, newWeightPenalty));
                    this.setIsOverloaded(this.getCurrentLoad() >= maxLoad);
                }
                else {
                    this.removeSkill(this.getKnownSkill(4270), true);
                    this.setIsOverloaded(false);
                }
            }
        }
    }
    
    @Override
    public void updateAndBroadcastStatus(final int val) {
        this.refreshOverloaded();
        super.updateAndBroadcastStatus(val);
    }
    
    @Override
    public final boolean isHungry() {
        return this._curFed < this.getPetData().getHungryLimit() / 100.0f * this.getPetLevelData().getPetMaxFeed();
    }
    
    public boolean isUncontrollable() {
        return this._curFed <= 0;
    }
    
    @Override
    public final int getWeapon() {
        final Item weapon = this._inventory.getPaperdollItem(InventorySlot.RIGHT_HAND);
        if (weapon != null) {
            return weapon.getId();
        }
        return 0;
    }
    
    @Override
    public final int getArmor() {
        final Item weapon = this._inventory.getPaperdollItem(InventorySlot.CHEST);
        if (weapon != null) {
            return weapon.getId();
        }
        return 0;
    }
    
    public final int getJewel() {
        final Item weapon = this._inventory.getPaperdollItem(InventorySlot.NECK);
        if (weapon != null) {
            return weapon.getId();
        }
        return 0;
    }
    
    @Override
    public short getSoulShotsPerHit() {
        return this.getPetLevelData().getPetSoulShot();
    }
    
    @Override
    public short getSpiritShotsPerHit() {
        return this.getPetLevelData().getPetSpiritShot();
    }
    
    @Override
    public void setName(final String name) {
        final Item controlItem = this.getControlItem();
        if (controlItem != null) {
            if (controlItem.getType2() == ((name == null) ? 1 : 0)) {
                controlItem.updateDatabase();
                final InventoryUpdate iu = new InventoryUpdate();
                iu.addModifiedItem(controlItem);
                this.sendInventoryUpdate(iu);
            }
        }
        else {
            Pet.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.toString()));
        }
        super.setName(name);
    }
    
    public boolean canEatFoodId(final int itemId) {
        return this._data.getFood().contains(itemId);
    }
    
    @Override
    public final double getRunSpeed() {
        return super.getRunSpeed() * (this.isUncontrollable() ? 0.5 : 1.0);
    }
    
    @Override
    public final double getWalkSpeed() {
        return super.getWalkSpeed() * (this.isUncontrollable() ? 0.5 : 1.0);
    }
    
    @Override
    public final double getMovementSpeedMultiplier() {
        return super.getMovementSpeedMultiplier() * (this.isUncontrollable() ? 0.5 : 1.0);
    }
    
    @Override
    public final double getMoveSpeed() {
        if (this.isInsideZone(ZoneType.WATER)) {
            return this.isRunning() ? this.getSwimRunSpeed() : this.getSwimWalkSpeed();
        }
        return this.isRunning() ? this.getRunSpeed() : this.getWalkSpeed();
    }
    
    static {
        LOGGER_PET = LoggerFactory.getLogger(Pet.class.getName());
    }
    
    class FeedTask implements Runnable
    {
        @Override
        public void run() {
            try {
                final Summon pet = Pet.this.getOwner().getPet();
                if (Pet.this.getOwner() == null || pet == null || pet.getObjectId() != Pet.this.getObjectId()) {
                    Pet.this.stopFeed();
                    return;
                }
                if (Pet.this._curFed > this.getFeedConsume()) {
                    Pet.this.setCurrentFed(Pet.this._curFed - this.getFeedConsume());
                }
                else {
                    Pet.this.setCurrentFed(0);
                }
                Pet.this.broadcastStatusUpdate();
                final List<Integer> foodIds = Pet.this.getPetData().getFood();
                if (foodIds.isEmpty()) {
                    if (Pet.this.isUncontrollable()) {
                        if (Pet.this.getTemplate().getId() == 16050 && Pet.this.getOwner() != null) {
                            Pet.this.getOwner().setPkKills(Math.max(0, Pet.this.getOwner().getPkKills() - Rnd.get(1, 6)));
                        }
                        Pet.this.sendPacket(SystemMessageId.THE_PET_IS_NOW_LEAVING);
                        Pet.this.deleteMe(Pet.this.getOwner());
                    }
                    else if (Pet.this.isHungry()) {
                        Pet.this.sendPacket(SystemMessageId.THERE_IS_NOT_MUCH_TIME_REMAINING_UNTIL_THE_PET_LEAVES);
                    }
                    return;
                }
                Item food = null;
                for (final int id : foodIds) {
                    food = Pet.this._inventory.getItemByItemId(id);
                    if (food != null) {
                        break;
                    }
                }
                if (food != null && Pet.this.isHungry()) {
                    final IItemHandler handler = ItemHandler.getInstance().getHandler(food.getEtcItem());
                    if (handler != null) {
                        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOUR_PET_WAS_HUNGRY_SO_IT_ATE_S1);
                        sm.addItemName(food.getId());
                        Pet.this.sendPacket(sm);
                        handler.useItem(Pet.this, food, false);
                    }
                }
                if (Pet.this.isUncontrollable()) {
                    Pet.this.sendPacket(SystemMessageId.YOUR_PET_IS_STARVING_AND_WILL_NOT_OBEY_UNTIL_IT_GETS_IT_S_FOOD_FEED_YOUR_PET);
                }
            }
            catch (Exception e) {
                Pet.LOGGER_PET.error(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Pet.this.getObjectId()), (Throwable)e);
            }
        }
        
        private int getFeedConsume() {
            if (Pet.this.isAttackingNow()) {
                return Pet.this.getPetLevelData().getPetFeedBattle();
            }
            return Pet.this.getPetLevelData().getPetFeedNormal();
        }
    }
}
