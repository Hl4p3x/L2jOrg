// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.transform;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.model.item.type.WeaponType;
import org.l2j.gameserver.model.holders.AdditionalSkillHolder;
import org.l2j.gameserver.network.serverpackets.ExUserInfoEquipSlot;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.network.serverpackets.ExBasicActionList;
import java.util.Iterator;
import io.github.joealisson.primitive.IntSet;
import java.util.stream.Stream;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerTransform;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.network.serverpackets.SkillCoolTime;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import io.github.joealisson.primitive.IntCollection;
import org.l2j.gameserver.enums.InventoryBlockType;
import org.l2j.gameserver.model.holders.AdditionalItemHolder;
import io.github.joealisson.primitive.HashIntSet;
import java.util.function.Consumer;
import java.util.Objects;
import java.util.function.Function;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.enums.Sex;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.slf4j.Logger;
import org.l2j.gameserver.model.interfaces.IIdentifiable;

public final class Transform implements IIdentifiable
{
    protected static final Logger LOGGER;
    private final int _id;
    private final int _displayId;
    private final TransformType _type;
    private final boolean _canSwim;
    private final int _spawnHeight;
    private final boolean _canAttack;
    private final String _name;
    private final String _title;
    private TransformTemplate _maleTemplate;
    private TransformTemplate _femaleTemplate;
    
    public Transform(final StatsSet set) {
        this._id = set.getInt("id");
        this._displayId = set.getInt("displayId", this._id);
        this._type = set.getEnum("type", TransformType.class, TransformType.COMBAT);
        this._canSwim = (set.getInt("can_swim", 0) == 1);
        this._canAttack = (set.getInt("normal_attackable", 1) == 1);
        this._spawnHeight = set.getInt("spawn_height", 0);
        this._name = set.getString("setName", null);
        this._title = set.getString("setTitle", null);
    }
    
    @Override
    public int getId() {
        return this._id;
    }
    
    public int getDisplayId() {
        return this._displayId;
    }
    
    public TransformType getType() {
        return this._type;
    }
    
    public boolean canSwim() {
        return this._canSwim;
    }
    
    public boolean canAttack() {
        return this._canAttack;
    }
    
    public int getSpawnHeight() {
        return this._spawnHeight;
    }
    
    public String getName() {
        return this._name;
    }
    
    public String getTitle() {
        return this._title;
    }
    
    private TransformTemplate getTemplate(final Creature creature) {
        if (GameUtils.isPlayer(creature)) {
            return creature.getActingPlayer().getAppearance().isFemale() ? this._femaleTemplate : this._maleTemplate;
        }
        if (GameUtils.isNpc(creature)) {
            return (((Npc)creature).getTemplate().getSex() == Sex.FEMALE) ? this._femaleTemplate : this._maleTemplate;
        }
        return null;
    }
    
    public void setTemplate(final boolean male, final TransformTemplate template) {
        if (male) {
            this._maleTemplate = template;
        }
        else {
            this._femaleTemplate = template;
        }
    }
    
    public boolean isStance() {
        return this._type == TransformType.MODE_CHANGE;
    }
    
    public boolean isCombat() {
        return this._type == TransformType.COMBAT;
    }
    
    public boolean isNonCombat() {
        return this._type == TransformType.NON_COMBAT;
    }
    
    public boolean isFlying() {
        return this._type == TransformType.FLYING;
    }
    
    public boolean isCursed() {
        return this._type == TransformType.CURSED;
    }
    
    public boolean isRiding() {
        return this._type == TransformType.RIDING_MODE;
    }
    
    public boolean isPureStats() {
        return this._type == TransformType.PURE_STAT;
    }
    
    public double getCollisionHeight(final Creature creature, final double defaultCollisionHeight) {
        final TransformTemplate template = this.getTemplate(creature);
        if (template != null && template.getCollisionHeight() != null) {
            return template.getCollisionHeight();
        }
        return defaultCollisionHeight;
    }
    
    public double getCollisionRadius(final Creature creature, final double defaultCollisionRadius) {
        final TransformTemplate template = this.getTemplate(creature);
        if (template != null && template.getCollisionRadius() != null) {
            return template.getCollisionRadius();
        }
        return defaultCollisionRadius;
    }
    
    public void onTransform(final Creature creature, final boolean addSkills) {
        creature.abortAttack();
        creature.abortCast();
        final Player player = creature.getActingPlayer();
        if (GameUtils.isPlayer(creature) && player.isMounted()) {
            player.dismount();
        }
        final TransformTemplate template = this.getTemplate(creature);
        if (template != null) {
            if (this.isFlying()) {
                creature.setIsFlying(true);
            }
            creature.setXYZ(creature.getX(), creature.getY(), (int)(creature.getZ() + this.getCollisionHeight(creature, 0.0)));
            if (GameUtils.isPlayer(creature)) {
                if (this._name != null) {
                    player.getAppearance().setVisibleName(this._name);
                }
                if (this._title != null) {
                    player.getAppearance().setVisibleTitle(this._title);
                }
                if (addSkills) {
                    final Stream<Object> map = template.getSkills().stream().map((Function<? super Object, ?>)SkillHolder::getSkill);
                    final Player obj = player;
                    Objects.requireNonNull(obj);
                    map.forEach((Consumer<? super Object>)obj::addTransformSkill);
                    final Stream<Object> map2 = template.getAdditionalSkills().stream().filter(h -> player.getLevel() >= h.getMinLevel()).map((Function<? super Object, ?>)SkillHolder::getSkill);
                    final Player obj2 = player;
                    Objects.requireNonNull(obj2);
                    map2.forEach((Consumer<? super Object>)obj2::addTransformSkill);
                }
                if (!template.getAdditionalItems().isEmpty()) {
                    final IntSet allowed = (IntSet)new HashIntSet();
                    final IntSet notAllowed = (IntSet)new HashIntSet();
                    for (final AdditionalItemHolder holder : template.getAdditionalItems()) {
                        if (holder.isAllowedToUse()) {
                            allowed.add(holder.getId());
                        }
                        else {
                            notAllowed.add(holder.getId());
                        }
                    }
                    if (!allowed.isEmpty()) {
                        player.getInventory().setInventoryBlock((IntCollection)allowed, InventoryBlockType.WHITELIST);
                    }
                    if (!notAllowed.isEmpty()) {
                        player.getInventory().setInventoryBlock((IntCollection)notAllowed, InventoryBlockType.BLACKLIST);
                    }
                }
                if (template.hasBasicActionList()) {
                    player.sendPacket(template.getBasicActionList());
                }
                player.getEffectList().stopAllToggles();
                if (player.hasTransformSkills()) {
                    player.sendSkillList();
                    player.sendPacket(new SkillCoolTime(player));
                }
                player.broadcastUserInfo();
                EventDispatcher.getInstance().notifyEventAsync(new OnPlayerTransform(player, this.getId()), player);
            }
            else {
                creature.broadcastInfo();
            }
            creature.updateAbnormalVisualEffects();
        }
    }
    
    public void onUntransform(final Creature creature) {
        creature.abortAttack();
        creature.abortCast();
        final TransformTemplate template = this.getTemplate(creature);
        if (template != null) {
            if (this.isFlying()) {
                creature.setIsFlying(false);
            }
            if (GameUtils.isPlayer(creature)) {
                final Player player = creature.getActingPlayer();
                final boolean hasTransformSkills = player.hasTransformSkills();
                if (this._name != null) {
                    player.getAppearance().setVisibleName(null);
                }
                if (this._title != null) {
                    player.getAppearance().setVisibleTitle(null);
                }
                player.removeAllTransformSkills();
                if (!template.getAdditionalItems().isEmpty()) {
                    player.getInventory().unblock();
                }
                player.sendPacket(ExBasicActionList.STATIC_PACKET);
                player.getEffectList().stopEffects(AbnormalType.TRANSFORM);
                player.getEffectList().stopEffects(AbnormalType.CHANGEBODY);
                player.getEffectList().stopEffects(AbnormalType.SPECIAL_RIDE);
                if (hasTransformSkills) {
                    player.sendSkillList();
                    player.sendPacket(new SkillCoolTime(player));
                }
                player.broadcastUserInfo();
                player.sendPacket(new ExUserInfoEquipSlot(player));
                EventDispatcher.getInstance().notifyEventAsync(new OnPlayerTransform(player, 0), player);
            }
            else {
                creature.broadcastInfo();
            }
        }
    }
    
    public void onLevelUp(final Player player) {
        final TransformTemplate template = this.getTemplate(player);
        if (template != null && !template.getAdditionalSkills().isEmpty()) {
            for (final AdditionalSkillHolder holder : template.getAdditionalSkills()) {
                if (player.getLevel() >= holder.getMinLevel() && player.getSkillLevel(holder.getSkillId()) < holder.getLevel()) {
                    player.addTransformSkill(holder.getSkill());
                }
            }
        }
    }
    
    public WeaponType getBaseAttackType(final Creature creature, final WeaponType defaultAttackType) {
        final TransformTemplate template = this.getTemplate(creature);
        if (template != null) {
            final WeaponType weaponType = template.getBaseAttackType();
            if (weaponType != null) {
                return weaponType;
            }
        }
        return defaultAttackType;
    }
    
    public double getStats(final Creature creature, final Stat stat, final double defaultValue) {
        double val = defaultValue;
        final TransformTemplate template = this.getTemplate(creature);
        if (template != null) {
            val = template.getStats(stat, defaultValue);
            final TransformLevelData data = template.getData(creature.getLevel());
            if (data != null) {
                val = data.getStats(stat, defaultValue);
            }
        }
        return val;
    }
    
    public int getBaseDefBySlot(final Player player, final InventorySlot slot) {
        final int defaultValue = player.getTemplate().getBaseDefBySlot(slot);
        final TransformTemplate template = this.getTemplate(player);
        return (template == null) ? defaultValue : template.getDefense(slot, defaultValue);
    }
    
    public double getLevelMod(final Creature creature) {
        double val = 1.0;
        final TransformTemplate template = this.getTemplate(creature);
        if (template != null) {
            final TransformLevelData data = template.getData(creature.getLevel());
            if (data != null) {
                val = data.getLevelMod();
            }
        }
        return val;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)Transform.class);
    }
}
