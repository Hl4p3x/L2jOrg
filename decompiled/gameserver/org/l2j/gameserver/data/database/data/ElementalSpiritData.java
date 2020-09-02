// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

@Table("character_spirits")
public class ElementalSpiritData
{
    private int charId;
    private byte type;
    private byte level;
    private byte stage;
    private long experience;
    @Column("attack_points")
    private byte attackPoints;
    @Column("defense_points")
    private byte defensePoints;
    @Column("crit_rate_points")
    private byte critRatePoints;
    @Column("crit_damage_points")
    private byte critDamagePoints;
    @Column("in_use")
    private boolean inUse;
    
    public ElementalSpiritData() {
        this.level = 1;
        this.stage = 1;
    }
    
    public ElementalSpiritData(final byte type, final int objectId) {
        this.level = 1;
        this.stage = 1;
        this.charId = objectId;
        this.type = type;
    }
    
    public int getCharId() {
        return this.charId;
    }
    
    public void setCharId(final int charId) {
        this.charId = charId;
    }
    
    public byte getType() {
        return this.type;
    }
    
    public void setType(final byte type) {
        this.type = type;
    }
    
    public byte getLevel() {
        return this.level;
    }
    
    public void setLevel(final byte level) {
        this.level = level;
    }
    
    public byte getStage() {
        return this.stage;
    }
    
    public void setStage(final byte stage) {
        this.stage = stage;
    }
    
    public long getExperience() {
        return this.experience;
    }
    
    public void setExperience(final long experience) {
        this.experience = experience;
    }
    
    public byte getAttackPoints() {
        return this.attackPoints;
    }
    
    public void setAttackPoints(final byte attackPoints) {
        this.attackPoints = attackPoints;
    }
    
    public byte getDefensePoints() {
        return this.defensePoints;
    }
    
    public void setDefensePoints(final byte defensePoints) {
        this.defensePoints = defensePoints;
    }
    
    public byte getCritRatePoints() {
        return this.critRatePoints;
    }
    
    public void setCritRatePoints(final byte critRatePoints) {
        this.critRatePoints = critRatePoints;
    }
    
    public byte getCritDamagePoints() {
        return this.critDamagePoints;
    }
    
    public void setCritDamagePoints(final byte critDamagePoints) {
        this.critDamagePoints = critDamagePoints;
    }
    
    public void addExperience(final long experience) {
        this.experience += experience;
    }
    
    public void increaseLevel() {
        ++this.level;
    }
    
    public boolean isInUse() {
        return this.inUse;
    }
    
    public void addAttackPoints(final byte attackPoints) {
        this.attackPoints += attackPoints;
    }
    
    public void addDefensePoints(final byte defensePoints) {
        this.defensePoints += defensePoints;
    }
    
    public void addCritRatePoints(final byte critRatePoints) {
        this.critRatePoints = critRatePoints;
    }
    
    public void addCritDamagePoints(final byte critDamagePoints) {
        this.critDamagePoints += critDamagePoints;
    }
    
    public void increaseStage() {
        ++this.stage;
    }
}
