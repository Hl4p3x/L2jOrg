// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.api.elemental;

import org.l2j.gameserver.network.serverpackets.AbstractMessagePacket;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.ElementalSpiritDAO;
import org.l2j.gameserver.model.holders.ItemHolder;
import java.util.List;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.OnElementalSpiritUpgrade;
import org.l2j.gameserver.model.events.EventDispatcher;
import java.util.Iterator;
import org.l2j.gameserver.engine.elemental.AbsorbItem;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ExElementalSpiritGetExp;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.network.serverpackets.UserInfo;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ElementalSpiritInfo;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.engine.elemental.ElementalSpiritEngine;
import org.l2j.gameserver.data.database.data.ElementalSpiritData;
import org.l2j.gameserver.engine.elemental.ElementalSpiritTemplate;
import org.l2j.gameserver.model.actor.instance.Player;

public class ElementalSpirit
{
    private static final int[] DEFENSE_MOD_POINTS;
    private static final int[] DEFENSE_MOD_VALUES;
    private static final int[] ATTACK_MOD_POINTS;
    private static final int[] ATTACK_MOD_VALUES;
    private static final int[] CRITIC_MOD_POINTS;
    private static final int[] CRITIC_MOD_VALUES;
    private final Player owner;
    private ElementalSpiritTemplate template;
    private ElementalSpiritData data;
    
    public ElementalSpirit(final ElementalType type, final Player owner) {
        this.data = new ElementalSpiritData(type.getId(), owner.getObjectId());
        this.template = ElementalSpiritEngine.getInstance().getSpirit(type.getId(), this.data.getStage());
        this.owner = owner;
    }
    
    public ElementalSpirit(final ElementalSpiritData data, final Player owner) {
        this.owner = owner;
        this.data = data;
        this.template = ElementalSpiritEngine.getInstance().getSpirit(data.getType(), data.getStage());
    }
    
    public void addExperience(final long experience) {
        this.data.addExperience(experience);
        this.owner.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_ACQUIRED_S1S_S2_SKILL_XP).addInt((int)experience)).addElementalSpirit(this.getType()));
        if (this.data.getExperience() > this.getExperienceToNextLevel()) {
            this.levelUp();
            this.owner.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.S1_ATTACK_SPIRITS_HAVE_REACHED_LEVEL_S2).addElementalSpirit(this.getType())).addByte(this.data.getLevel()));
            this.owner.sendPacket(new ElementalSpiritInfo(this.owner.getActiveElementalSpiritType(), (byte)0));
            final UserInfo userInfo = new UserInfo(this.owner);
            userInfo.addComponentType(UserInfoType.SPIRITS);
            this.owner.sendPacket(userInfo);
        }
        this.owner.sendPacket(new ExElementalSpiritGetExp(this.getType(), this.data.getExperience()));
    }
    
    private void levelUp() {
        do {
            if (this.data.getLevel() < this.getMaxLevel()) {
                this.data.increaseLevel();
            }
            else {
                this.data.setExperience(this.getExperienceToNextLevel());
            }
        } while (this.data.getExperience() > this.getExperienceToNextLevel());
    }
    
    public int getAvailableCharacteristicsPoints() {
        final byte stage = this.data.getStage();
        final byte level = this.data.getLevel();
        final int points = (stage - 1) * 11 + ((stage > 2) ? ((level - 1) * 2) : (level - 1));
        return Math.max(points - this.data.getAttackPoints() - this.data.getDefensePoints() - this.data.getCritDamagePoints() - this.data.getCritRatePoints(), 0);
    }
    
    public AbsorbItem getAbsorbItem(final int itemId) {
        for (final AbsorbItem absorbItem : this.getAbsorbItems()) {
            if (absorbItem.getId() == itemId) {
                return absorbItem;
            }
        }
        return null;
    }
    
    public int getExtractAmount() {
        return Math.round((this.data.getExperience() - this.getExperienceToPreviousLevel()) / 50000.0f);
    }
    
    public void resetLevel() {
        this.data.setLevel((byte)Math.max(1, this.data.getLevel() - 1));
        this.data.setExperience(0L);
        this.resetCharacteristics();
    }
    
    public boolean canEvolve() {
        return this.getStage() < 5 && this.getLevel() == 10 && this.getExperience() == this.getExperienceToNextLevel();
    }
    
    public void upgrade() {
        this.data.increaseStage();
        this.data.setLevel((byte)1);
        this.data.setExperience(0L);
        this.template = ElementalSpiritEngine.getInstance().getSpirit(this.data.getType(), this.data.getStage());
        EventDispatcher.getInstance().notifyEventAsync(new OnElementalSpiritUpgrade(this.owner, this), this.owner);
    }
    
    public void resetCharacteristics() {
        this.data.setAttackPoints((byte)0);
        this.data.setDefensePoints((byte)0);
        this.data.setCritRatePoints((byte)0);
        this.data.setCritDamagePoints((byte)0);
    }
    
    public byte getType() {
        return this.template.getType();
    }
    
    public byte getStage() {
        return this.template.getStage();
    }
    
    public int getNpcId() {
        return this.template.getNpcId();
    }
    
    public long getExperience() {
        return this.data.getExperience();
    }
    
    public long getExperienceToNextLevel() {
        return this.template.getMaxExperienceAtLevel(this.data.getLevel());
    }
    
    private long getExperienceToPreviousLevel() {
        return (this.data.getLevel() < 2) ? 0L : this.template.getMaxExperienceAtLevel((byte)(this.data.getLevel() - 1));
    }
    
    public byte getLevel() {
        return this.data.getLevel();
    }
    
    public int getMaxLevel() {
        return this.template.getMaxLevel();
    }
    
    public int getAttack() {
        return this.template.getAttackAtLevel(this.data.getLevel()) + this.calcCharacteristicPoints(this.data.getAttackPoints(), ElementalSpirit.ATTACK_MOD_POINTS, ElementalSpirit.ATTACK_MOD_VALUES);
    }
    
    public int getDefense() {
        return this.template.getDefenseAtLevel(this.data.getLevel()) + this.calcCharacteristicPoints(this.data.getDefensePoints(), ElementalSpirit.DEFENSE_MOD_POINTS, ElementalSpirit.DEFENSE_MOD_VALUES);
    }
    
    private int calcCharacteristicPoints(int base, final int[] points, final int[] values) {
        int amount = 0;
        for (int i = 0; i < points.length; ++i) {
            if (base > points[i]) {
                amount += (base - points[i]) * values[i];
                base -= base - points[i];
            }
        }
        return amount;
    }
    
    public int getCriticalRate() {
        return this.template.getCriticalRateAtLevel(this.data.getLevel()) + this.calcCharacteristicPoints(this.data.getCritRatePoints(), ElementalSpirit.CRITIC_MOD_POINTS, ElementalSpirit.CRITIC_MOD_VALUES);
    }
    
    public int getCriticalDamage() {
        return this.template.getCriticalDamageAtLevel(this.data.getLevel()) + this.calcCharacteristicPoints(this.data.getCritDamagePoints(), ElementalSpirit.CRITIC_MOD_POINTS, ElementalSpirit.CRITIC_MOD_VALUES);
    }
    
    public int getMaxCharacteristics() {
        return this.template.getMaxCharacteristics();
    }
    
    public int getAttackPoints() {
        return this.data.getAttackPoints();
    }
    
    public int getDefensePoints() {
        return this.data.getDefensePoints();
    }
    
    public int getCriticalRatePoints() {
        return this.data.getCritRatePoints();
    }
    
    public int getCriticalDamagePoints() {
        return this.data.getCritDamagePoints();
    }
    
    public List<ItemHolder> getItemsToEvolve() {
        return this.template.getItemsToEvolve();
    }
    
    public List<AbsorbItem> getAbsorbItems() {
        return this.template.getAbsorbItems();
    }
    
    public int getExtractItem() {
        return this.template.getExtractItem();
    }
    
    public void save() {
        ((ElementalSpiritDAO)DatabaseAccess.getDAO((Class)ElementalSpiritDAO.class)).save((Object)this.data);
    }
    
    public void addAttackPoints(final byte attackPoints) {
        this.data.addAttackPoints(attackPoints);
    }
    
    public void addDefensePoints(final byte defensePoints) {
        this.data.addDefensePoints(defensePoints);
    }
    
    public void addCritRatePoints(final byte critRatePoints) {
        this.data.addCritRatePoints(critRatePoints);
    }
    
    public void addCritDamage(final byte critDamagePoints) {
        this.data.addCritDamagePoints(critDamagePoints);
    }
    
    static {
        DEFENSE_MOD_POINTS = new int[] { 15, 10, 5, 0 };
        DEFENSE_MOD_VALUES = new int[] { 9, 8, 7, 6 };
        ATTACK_MOD_POINTS = new int[] { 15, 2, 0 };
        ATTACK_MOD_VALUES = new int[] { 5, 4, 3 };
        CRITIC_MOD_POINTS = new int[] { 15, 0 };
        CRITIC_MOD_VALUES = new int[] { 2, 1 };
    }
}
