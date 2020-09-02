// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.data;

import java.time.LocalDate;
import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

@Table("characters")
public class PlayerData
{
    private int charId;
    @Column("account_name")
    private String accountName;
    @Column("char_name")
    private String name;
    private byte level;
    private double maxHp;
    @Column("curHp")
    private double hp;
    private double maxCp;
    @Column("curCp")
    private double currentCp;
    private double maxMp;
    @Column("curMp")
    private double mp;
    private byte face;
    private byte hairStyle;
    private byte hairColor;
    @Column("sex")
    private boolean female;
    private int heading;
    private int x;
    private int y;
    private int z;
    private long exp;
    private long expBeforeDeath;
    private long sp;
    private int reputation;
    private int fame;
    private int raidBossPoints;
    @Column("pvpkills")
    private int pvp;
    @Column("pkkills")
    private int pk;
    private int clanId;
    private int race;
    private int classId;
    @Column("base_class")
    private int baseClass;
    private long deletetime;
    private String title;
    @Column("title_color")
    private int titleColor;
    private int accessLevel;
    private long onlineTime;
    private long lastAccess;
    private boolean wantsPeace;
    @Column("power_grade")
    private int powerGrade;
    private boolean nobless;
    private int subPledge;
    @Column("lvl_joined_academy")
    private int levelJoinedAcademy;
    private int apprentice;
    private int sponsor;
    @Column("clan_join_expiry_time")
    private long clanJoinExpiryTime;
    @Column("clan_create_expiry_time")
    private long clanCreateExpiryTime;
    private int bookMarkSlot;
    @Column("vitality_points")
    private int vitalityPoints;
    private LocalDate createDate;
    private String language;
    @Column("pccafe_points")
    private int pcCafePoints;
    
    public static PlayerData of(final String accountName, final String name, final int classId, final byte face, final byte hairColor, final byte hairStyle, final boolean female) {
        final PlayerData data = new PlayerData();
        data.accountName = accountName;
        data.name = name;
        data.classId = classId;
        data.baseClass = classId;
        data.face = face;
        data.hairStyle = hairStyle;
        data.hairColor = hairColor;
        data.female = female;
        data.level = 1;
        return data;
    }
    
    public int getCharId() {
        return this.charId;
    }
    
    public void setId(final int charId) {
        this.charId = charId;
    }
    
    public boolean isFemale() {
        return this.female;
    }
    
    public void setFemale(final boolean female) {
        this.female = female;
    }
    
    public byte getFace() {
        return this.face;
    }
    
    public void setFace(final byte face) {
        this.face = face;
    }
    
    public byte getHairColor() {
        return this.hairColor;
    }
    
    public void setHairColor(final byte hairColor) {
        this.hairColor = hairColor;
    }
    
    public byte getHairStyle() {
        return this.hairStyle;
    }
    
    public void setHairStyle(final byte hairStyle) {
        this.hairStyle = hairStyle;
    }
    
    public int getClassId() {
        return this.classId;
    }
    
    public void setClassId(final int classId) {
        this.classId = classId;
    }
    
    public String getAccountName() {
        return this.accountName;
    }
    
    public void setAccountName(final String accountName) {
        this.accountName = accountName;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public long getLastAccess() {
        return this.lastAccess;
    }
    
    public long getExp() {
        return this.exp;
    }
    
    public long getExpBeforeDeath() {
        return this.expBeforeDeath;
    }
    
    public byte getLevel() {
        return this.level;
    }
    
    public long getSp() {
        return this.sp;
    }
    
    public boolean wantsPeace() {
        return this.wantsPeace;
    }
    
    public int getHeading() {
        return this.heading;
    }
    
    public int getReputation() {
        return this.reputation;
    }
    
    public int getFame() {
        return this.fame;
    }
    
    public int getRaidBossPoints() {
        return this.raidBossPoints;
    }
    
    public void setRaidbossPoints(final int points) {
        this.raidBossPoints = points;
    }
    
    public int getPvP() {
        return this.pvp;
    }
    
    public int getPk() {
        return this.pk;
    }
    
    public long getOnlineTime() {
        return this.onlineTime;
    }
    
    public boolean isNobless() {
        return this.nobless;
    }
    
    public long getClanJoinExpiryTime() {
        return this.clanJoinExpiryTime;
    }
    
    public long getClanCreateExpiryTime() {
        return this.clanCreateExpiryTime;
    }
    
    public int getPcCafePoints() {
        return this.pcCafePoints;
    }
    
    public int getClanId() {
        return this.clanId;
    }
    
    public int getPowerGrade() {
        return this.powerGrade;
    }
    
    public int getVitalityPoints() {
        return this.vitalityPoints;
    }
    
    public int getSubPledge() {
        return this.subPledge;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public int getAccessLevel() {
        return this.accessLevel;
    }
    
    public int getTitleColor() {
        return this.titleColor;
    }
    
    public double getMaxtHp() {
        return this.maxHp;
    }
    
    public double getHp() {
        return this.hp;
    }
    
    public double getCurrentCp() {
        return this.currentCp;
    }
    
    public double getMaxtCp() {
        return this.maxCp;
    }
    
    public double getMp() {
        return this.mp;
    }
    
    public double getMaxtMp() {
        return this.maxMp;
    }
    
    public int getBaseClass() {
        return this.baseClass;
    }
    
    public long getDeleteTime() {
        return this.deletetime;
    }
    
    public void setBaseClass(final int classId) {
        this.baseClass = classId;
    }
    
    public int getApprentice() {
        return this.apprentice;
    }
    
    public int getSponsor() {
        return this.sponsor;
    }
    
    public int getLevelJoinedAcademy() {
        return this.levelJoinedAcademy;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public int getZ() {
        return this.z;
    }
    
    public int getBookMarkSlot() {
        return this.bookMarkSlot;
    }
    
    public LocalDate getCreateDate() {
        return this.createDate;
    }
    
    public void setCreateDate(final LocalDate date) {
        this.createDate = date;
    }
    
    public String getLanguage() {
        return this.language;
    }
    
    public void setExpBeforeDeath(final long exp) {
        this.expBeforeDeath = exp;
    }
    
    public void setClanJoinExpiryTime(final long time) {
        this.clanJoinExpiryTime = time;
    }
    
    public void setClanCreateExpiryTime(final long time) {
        this.clanCreateExpiryTime = time;
    }
    
    public void setPcCafePoints(final int pcCafePoints) {
        this.pcCafePoints = pcCafePoints;
    }
    
    public void setPowerGrade(final int powerGrade) {
        this.powerGrade = powerGrade;
    }
    
    public void setSubPledge(final int subPledge) {
        this.subPledge = subPledge;
    }
    
    public void setApprentice(final int apprentice) {
        this.apprentice = apprentice;
    }
    
    public void setSponsor(final int sponsor) {
        this.sponsor = sponsor;
    }
    
    public void setLevelJoinedAcademy(final int level) {
        this.levelJoinedAcademy = level;
    }
    
    public void setObjectId(final int objectId) {
        this.charId = objectId;
    }
    
    public int getRace() {
        return this.race;
    }
    
    public void setMaxHp(final double hp) {
        this.maxHp = hp;
    }
    
    public void setHp(final double hp) {
        this.hp = hp;
    }
    
    public void setMaxMp(final double mp) {
        this.maxMp = mp;
    }
    
    public void setMp(final double mp) {
        this.mp = mp;
    }
    
    public void setRace(final int race) {
        this.race = race;
    }
    
    public void setTitleColor(final int color) {
        this.titleColor = color;
    }
    
    public void setX(final int x) {
        this.x = x;
    }
    
    public void setY(final int y) {
        this.y = y;
    }
    
    public void setZ(final int z) {
        this.z = z;
    }
    
    public void setLevel(final byte level) {
        this.level = level;
    }
    
    public void setExperience(final long experience) {
        this.exp = experience;
    }
    
    public void setSp(final int sp) {
        this.sp = sp;
    }
    
    public void setDeleteTime(final long deleteTime) {
        this.deletetime = deleteTime;
    }
}
