// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

@Table("player_variables")
public class PlayerVariableData
{
    public static final int REVENGE_USABLE_FUNCTIONS = 5;
    @Column("player_id")
    private int playerId;
    @Column("revenge_teleports")
    private byte revengeTeleports;
    @Column("revenge_locations")
    private byte revengeLocations;
    @Column("hair_accessory_enabled")
    private boolean hairAccessoryEnabled;
    @Column("world_chat_used")
    private int worldChatUsed;
    @Column("vitality_items_used")
    private int vitalityItemsUsed;
    @Column("ability_points_main_class_used")
    private int abilityPointsMainClassUsed;
    @Column("ability_points_dual_class_used")
    private int abilityPointsDualClassUsed;
    @Column("revelation_skill_main_class_1")
    private int revelationSkillMainClass1;
    @Column("revelation_skill_main_class_2")
    private int revelationSkillMainClass2;
    @Column("revelation_skill_dual_class_1")
    private int revelationSkillDualClass1;
    @Column("revelation_skill_dual_class_2")
    private int revelationSkillDualClass2;
    @Column("extend_drop")
    private String extendDrop;
    @Column("fortune_telling")
    private int fortuneTelling;
    @Column("fortune_telling_black_cat")
    private boolean fortuneTellingBlackCat;
    @Column("hunting_zone_reset_time")
    private String huntingZoneResetTime;
    private int autoCp;
    private int autoHp;
    private int autoMp;
    @Column("exp_off")
    private boolean expOff;
    @Column("items_rewarded")
    private boolean itemsRewarded;
    @Column("henna1_duration")
    private long henna1Duration;
    @Column("henna2_duration")
    private long henna2Duration;
    @Column("henna3_duration")
    private long henna3Duration;
    @Column("visual_hair_id")
    private int visualHairId;
    @Column("visual_hair_color_id")
    private int visualHairColorId;
    @Column("visual_face_id")
    private int visualFaceId;
    @Column("instance_origin")
    private String instanceOrigin;
    @Column("instance_restore")
    private int instanceRestore;
    @Column("claimed_clan_rewards")
    private int claimedClanRewards;
    @Column("cond_override_key")
    private String condOverrideKey;
    @Column("ui_key_mapping")
    private String uiKeyMapping;
    @Column("attendance_date")
    private long attendanceDate;
    @Column("attendance_index")
    private int attendanceIndex;
    @Column("unclaimed_olympiad_points")
    private int unclaimedOlympiadPoints;
    @Column("monster_return")
    private int monsterReturn;
    
    public static PlayerVariableData init(final int playerId, final byte face, final byte hairStyle, final byte hairColor) {
        final PlayerVariableData data = new PlayerVariableData();
        data.revengeTeleports = 5;
        data.revengeLocations = 5;
        data.playerId = playerId;
        data.visualFaceId = face;
        data.visualHairId = hairStyle;
        data.visualHairColorId = hairColor;
        return data;
    }
    
    public boolean isHairAccessoryEnabled() {
        return this.hairAccessoryEnabled;
    }
    
    public int getWorldChatUsed() {
        return this.worldChatUsed;
    }
    
    public int getVitalityItemsUsed() {
        return this.vitalityItemsUsed;
    }
    
    public int getAbilityPointsMainClassUsed() {
        return this.abilityPointsMainClassUsed;
    }
    
    public int getAbilityPointsDualClassUsed() {
        return this.abilityPointsDualClassUsed;
    }
    
    public int getRevelationSkillMainClass1() {
        return this.revelationSkillMainClass1;
    }
    
    public int getRevelationSkillMainClass2() {
        return this.revelationSkillMainClass2;
    }
    
    public int getRevelationSkillDualClass1() {
        return this.revelationSkillDualClass1;
    }
    
    public int getRevelationSkillDualClass2() {
        return this.revelationSkillDualClass2;
    }
    
    public String getExtendDrop() {
        return this.extendDrop;
    }
    
    public int getFortuneTelling() {
        return this.fortuneTelling;
    }
    
    public boolean isFortuneTellingBlackCat() {
        return this.fortuneTellingBlackCat;
    }
    
    public String getHuntingZoneResetTime() {
        return this.huntingZoneResetTime;
    }
    
    public int getAutoCp() {
        return this.autoCp;
    }
    
    public int getAutoHp() {
        return this.autoHp;
    }
    
    public int getAutoMp() {
        return this.autoMp;
    }
    
    public boolean getExpOff() {
        return this.expOff;
    }
    
    public boolean isItemsRewarded() {
        return this.itemsRewarded;
    }
    
    public long getHenna1Duration() {
        return this.henna1Duration;
    }
    
    public long getHenna2Duration() {
        return this.henna2Duration;
    }
    
    public long getHenna3Duration() {
        return this.henna3Duration;
    }
    
    public int getVisualHairId() {
        return this.visualHairId;
    }
    
    public int getVisualHairColorId() {
        return this.visualHairColorId;
    }
    
    public int getVisualFaceId() {
        return this.visualFaceId;
    }
    
    public int[] getInstanceOrigin() {
        final String[] instanceOriginString = this.instanceOrigin.split(";");
        final int[] instanceOriginInt = new int[instanceOriginString.length];
        for (int i = 0; i < instanceOriginString.length; ++i) {
            instanceOriginInt[i] = Integer.parseInt(instanceOriginString[i]);
        }
        return instanceOriginInt;
    }
    
    public int getInstanceRestore() {
        return this.instanceRestore;
    }
    
    public int getClaimedClanRewards() {
        return this.claimedClanRewards;
    }
    
    public String getCondOverrideKey() {
        return this.condOverrideKey;
    }
    
    public String getUiKeyMapping() {
        return this.uiKeyMapping;
    }
    
    public long getAttendanceDate() {
        return this.attendanceDate;
    }
    
    public int getAttendanceIndex() {
        return this.attendanceIndex;
    }
    
    public int getUnclaimedOlympiadPoints() {
        return this.unclaimedOlympiadPoints;
    }
    
    public int getMonsterReturn() {
        return this.monsterReturn;
    }
    
    public void setHairAccessoryEnabled(final boolean hairAccessoryEnabled) {
        this.hairAccessoryEnabled = hairAccessoryEnabled;
    }
    
    public void setWorldChatUsed(final int worldChatUsed) {
        this.worldChatUsed = worldChatUsed;
    }
    
    public void setVitalityItemsUsed(final int vitalityItemsUsed) {
        this.vitalityItemsUsed = vitalityItemsUsed;
    }
    
    public void setAbilityPointsMainClassUsed(final int abilityPointsMainClassUsed) {
        this.abilityPointsMainClassUsed = abilityPointsMainClassUsed;
    }
    
    public void setAbilityPointsDualClassUsed(final int abilityPointsDualClassUsed) {
        this.abilityPointsDualClassUsed = abilityPointsDualClassUsed;
    }
    
    public void setRevelationSkillMainClass1(final int revelationSkillMainClass1) {
        this.revelationSkillMainClass1 = revelationSkillMainClass1;
    }
    
    public void setRevelationSkillMainClass2(final int revelationSkillMainClass2) {
        this.revelationSkillMainClass2 = revelationSkillMainClass2;
    }
    
    public void setRevelationSkillDualClass1(final int revelationSkillDualClass1) {
        this.revelationSkillDualClass1 = revelationSkillDualClass1;
    }
    
    public void setRevelationSkillDualClass2(final int revelationSkillDualClass2) {
        this.revelationSkillDualClass2 = revelationSkillDualClass2;
    }
    
    public void setExtendDrop(final String extendDrop) {
        this.extendDrop = extendDrop;
    }
    
    public void setFortuneTelling(final int fortuneTelling) {
        this.fortuneTelling = fortuneTelling;
    }
    
    public void setFortuneTellingBlackCat(final boolean fortuneTellingBlackCat) {
        this.fortuneTellingBlackCat = fortuneTellingBlackCat;
    }
    
    public void setHuntingZoneResetTime(final String huntingZoneResetTime) {
        this.huntingZoneResetTime = huntingZoneResetTime;
    }
    
    public void setAutoCp(final int autoCp) {
        this.autoCp = autoCp;
    }
    
    public void setAutoHp(final int autoHp) {
        this.autoHp = autoHp;
    }
    
    public void setAutoMp(final int autoMp) {
        this.autoMp = autoMp;
    }
    
    public void setExpOff(final boolean expOff) {
        this.expOff = expOff;
    }
    
    public void setItemsRewarded(final boolean itemsRewarded) {
        this.itemsRewarded = itemsRewarded;
    }
    
    public void setHenna1Duration(final long hennaDuration) {
        this.henna1Duration = hennaDuration;
    }
    
    public void setHenna2Duration(final long hennaDuration) {
        this.henna2Duration = hennaDuration;
    }
    
    public void setHenna3Duration(final long hennaDuration) {
        this.henna3Duration = hennaDuration;
    }
    
    public void setVisualHairId(final int visualHairId) {
        this.visualHairId = visualHairId;
    }
    
    public void setVisualHairColorId(final int visualHairColorId) {
        this.visualHairColorId = visualHairColorId;
    }
    
    public void setVisualFaceId(final int visualFaceId) {
        this.visualFaceId = visualFaceId;
    }
    
    public void setInstanceOrigin(final String instanceOrigin) {
        this.instanceOrigin = instanceOrigin;
    }
    
    public void setInstanceRestore(final int instanceRestore) {
        this.instanceRestore = instanceRestore;
    }
    
    public void setClaimedClanRewards(final int claimedClanRewards) {
        this.claimedClanRewards = claimedClanRewards;
    }
    
    public void setCondOverrideKey(final String condOverrideKey) {
        this.condOverrideKey = condOverrideKey;
    }
    
    public void setAttendanceDate(final long attendanceDate) {
        this.attendanceDate = attendanceDate;
    }
    
    public void setAttendanceIndex(final int attendanceIndex) {
        this.attendanceIndex = attendanceIndex;
    }
    
    public void setUnclaimedOlympiadPoints(final int unclaimedOlympiadPoints) {
        this.unclaimedOlympiadPoints = unclaimedOlympiadPoints;
    }
    
    public void setMonsterReturn(final int monsterReturn) {
        this.monsterReturn = monsterReturn;
    }
    
    public void setUiKeyMapping(final String uiKeyMapping) {
        this.uiKeyMapping = uiKeyMapping;
    }
    
    public byte getRevengeTeleports() {
        return this.revengeTeleports;
    }
    
    public byte getRevengeLocations() {
        return this.revengeLocations;
    }
    
    public void useRevengeLocation() {
        --this.revengeLocations;
    }
    
    public void useRevengeTeleport() {
        --this.revengeTeleports;
    }
    
    public void resetRevengeData() {
        this.revengeTeleports = 5;
        this.revengeLocations = 5;
    }
}
