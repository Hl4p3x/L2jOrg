// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.interfaces.IUpdateTypeComponent;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.data.database.data.PlayerStatsData;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.stats.Stat;
import java.util.Optional;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.LevelData;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.Objects;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.enums.UserInfoType;

public class UserInfo extends AbstractMaskPacket<UserInfoType>
{
    private final Player player;
    private final int relation;
    private final int runSpd;
    private final int walkSpd;
    private final int swimRunSpd;
    private final int swimWalkSpd;
    private final int mountRunSpd = 0;
    private final int mountWalkSpd = 0;
    private final int flyRunSpd;
    private final int flyWalkSpd;
    private final double _moveMultiplier;
    private final int enchantLevel;
    private final int armorEnchant;
    private final byte[] mask;
    private String title;
    private int initSize;
    
    public UserInfo(final Player cha) {
        this(cha, true);
    }
    
    public UserInfo(final Player cha, final UserInfoType... infoTypes) {
        this(cha, false);
        if (Objects.nonNull(infoTypes)) {
            for (final UserInfoType infoType : infoTypes) {
                this.addComponentType(infoType);
            }
        }
    }
    
    public UserInfo(final Player cha, final boolean addAll) {
        this.mask = new byte[] { 0, 0, 0, 0 };
        this.initSize = 5;
        this.player = cha;
        this.relation = this.calculateRelation(cha);
        this._moveMultiplier = cha.getMovementSpeedMultiplier();
        this.runSpd = (int)Math.round(cha.getRunSpeed() / this._moveMultiplier);
        this.walkSpd = (int)Math.round(cha.getWalkSpeed() / this._moveMultiplier);
        this.swimRunSpd = (int)Math.round(cha.getSwimRunSpeed() / this._moveMultiplier);
        this.swimWalkSpd = (int)Math.round(cha.getSwimWalkSpeed() / this._moveMultiplier);
        this.flyRunSpd = (cha.isFlying() ? this.runSpd : 0);
        this.flyWalkSpd = (cha.isFlying() ? this.walkSpd : 0);
        this.enchantLevel = cha.getInventory().getWeaponEnchant();
        this.armorEnchant = cha.getInventory().getArmorMaxEnchant();
        this.title = cha.getTitle();
        if (cha.isGM() && cha.isInvisible()) {
            this.title = "[Invisible]";
        }
        if (addAll) {
            this.addComponentType(UserInfoType.values());
        }
    }
    
    @Override
    protected byte[] getMasks() {
        return this.mask;
    }
    
    @Override
    protected void onNewMaskAdded(final UserInfoType component) {
        this.calcBlockSize(component);
    }
    
    private void calcBlockSize(final UserInfoType type) {
        switch (type) {
            case BASIC_INFO: {
                this.initSize += type.getBlockLength() + this.player.getAppearance().getVisibleName().length() * 2;
            }
            case CLAN: {
                this.initSize += type.getBlockLength() + this.title.length() * 2;
                break;
            }
        }
        this.initSize += type.getBlockLength();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.USER_INFO);
        this.writeInt(this.player.getObjectId());
        this.writeInt(this.initSize);
        this.writeShort(27);
        this.writeBytes(this.mask);
        if (this.containsMask(UserInfoType.RELATION)) {
            this.writeInt(this.relation);
        }
        if (this.containsMask(UserInfoType.BASIC_INFO)) {
            this.writeShort(UserInfoType.BASIC_INFO.getBlockLength() + this.player.getAppearance().getVisibleName().length() * 2);
            this.writeSizedString((CharSequence)this.player.getName());
            this.writeByte(this.player.isGM());
            this.writeByte(this.player.getRace().ordinal());
            this.writeByte(this.player.getAppearance().isFemale());
            this.writeInt(this.player.getBaseTemplate().getClassId().getRootClassId().getId());
            this.writeInt(this.player.getClassId().getId());
            this.writeInt(this.player.getLevel());
        }
        if (this.containsMask(UserInfoType.BASE_STATS)) {
            this.writeShort(UserInfoType.BASE_STATS.getBlockLength());
            this.writeShort(this.player.getSTR());
            this.writeShort(this.player.getDEX());
            this.writeShort(this.player.getCON());
            this.writeShort(this.player.getINT());
            this.writeShort(this.player.getWIT());
            this.writeShort(this.player.getMEN());
            this.writeShort(1);
            this.writeShort(1);
        }
        if (this.containsMask(UserInfoType.MAX_HPCPMP)) {
            this.writeShort(UserInfoType.MAX_HPCPMP.getBlockLength());
            this.writeInt(this.player.getMaxHp());
            this.writeInt(this.player.getMaxMp());
            this.writeInt(this.player.getMaxCp());
        }
        if (this.containsMask(UserInfoType.CURRENT_HPMPCP_EXP_SP)) {
            this.writeShort(UserInfoType.CURRENT_HPMPCP_EXP_SP.getBlockLength());
            this.writeInt((int)Math.round(this.player.getCurrentHp()));
            this.writeInt((int)Math.round(this.player.getCurrentMp()));
            this.writeInt((int)Math.round(this.player.getCurrentCp()));
            this.writeLong(this.player.getSp());
            this.writeLong(this.player.getExp());
            this.writeDouble((double)((this.player.getExp() - LevelData.getInstance().getExpForLevel(this.player.getLevel())) / (float)(LevelData.getInstance().getExpForLevel(this.player.getLevel() + 1) - LevelData.getInstance().getExpForLevel(this.player.getLevel()))));
        }
        if (this.containsMask(UserInfoType.ENCHANTLEVEL)) {
            this.writeShort(UserInfoType.ENCHANTLEVEL.getBlockLength());
            this.writeByte(this.enchantLevel);
            this.writeByte(this.armorEnchant);
        }
        if (this.containsMask(UserInfoType.APPAREANCE)) {
            this.writeShort(UserInfoType.APPAREANCE.getBlockLength());
            this.writeInt(this.player.getVisualHair());
            this.writeInt(this.player.getVisualHairColor());
            this.writeInt(this.player.getVisualFace());
            this.writeByte(this.player.isHairAccessoryEnabled());
        }
        if (this.containsMask(UserInfoType.STATUS)) {
            this.writeShort(UserInfoType.STATUS.getBlockLength());
            this.writeByte(this.player.getMountType().ordinal());
            this.writeByte(this.player.getPrivateStoreType().getId());
            this.writeByte(this.player.hasDwarvenCraft() || this.player.getSkillLevel(248) > 0);
            this.writeByte(0);
        }
        if (this.containsMask(UserInfoType.STATS)) {
            this.writeShort(UserInfoType.STATS.getBlockLength());
            this.writeShort((this.player.getActiveWeaponItem() != null) ? 40 : 20);
            this.writeInt(this.player.getPAtk());
            this.writeInt(this.player.getPAtkSpd());
            this.writeInt(this.player.getPDef());
            this.writeInt(this.player.getEvasionRate());
            this.writeInt(this.player.getAccuracy());
            this.writeInt(this.player.getCriticalHit());
            this.writeInt(this.player.getMAtk());
            this.writeInt(this.player.getMAtkSpd());
            this.writeInt(this.player.getPAtkSpd());
            this.writeInt(this.player.getMagicEvasionRate());
            this.writeInt(this.player.getMDef());
            this.writeInt(this.player.getMagicAccuracy());
            this.writeInt(this.player.getMCriticalHit());
            this.writeInt(0);
            this.writeInt(0);
        }
        if (this.containsMask(UserInfoType.ELEMENTALS)) {
            this.writeShort(UserInfoType.ELEMENTALS.getBlockLength());
            this.writeShort(0);
            this.writeShort(0);
            this.writeShort(0);
            this.writeShort(0);
            this.writeShort(0);
            this.writeShort(0);
        }
        if (this.containsMask(UserInfoType.POSITION)) {
            this.writeShort(UserInfoType.POSITION.getBlockLength());
            this.writeInt(this.player.getX());
            this.writeInt(this.player.getY());
            this.writeInt(this.player.getZ());
            this.writeInt(this.player.isInVehicle() ? this.player.getVehicle().getObjectId() : 0);
        }
        if (this.containsMask(UserInfoType.SPEED)) {
            this.writeShort(UserInfoType.SPEED.getBlockLength());
            this.writeShort(this.runSpd);
            this.writeShort(this.walkSpd);
            this.writeShort(this.swimRunSpd);
            this.writeShort(this.swimWalkSpd);
            this.writeShort(0);
            this.writeShort(0);
            this.writeShort(this.flyRunSpd);
            this.writeShort(this.flyWalkSpd);
        }
        if (this.containsMask(UserInfoType.MULTIPLIER)) {
            this.writeShort(UserInfoType.MULTIPLIER.getBlockLength());
            this.writeDouble(this._moveMultiplier);
            this.writeDouble(this.player.getAttackSpeedMultiplier());
        }
        if (this.containsMask(UserInfoType.COL_RADIUS_HEIGHT)) {
            this.writeShort(UserInfoType.COL_RADIUS_HEIGHT.getBlockLength());
            this.writeDouble(this.player.getCollisionRadius());
            this.writeDouble(this.player.getCollisionHeight());
        }
        if (this.containsMask(UserInfoType.ATK_ELEMENTAL)) {
            this.writeShort(UserInfoType.ATK_ELEMENTAL.getBlockLength());
            this.writeByte(-2);
            this.writeShort(0);
        }
        if (this.containsMask(UserInfoType.CLAN)) {
            this.writeShort(UserInfoType.CLAN.getBlockLength() + this.title.length() * 2);
            this.writeSizedString((CharSequence)this.title);
            this.writeShort(this.player.getPledgeType());
            this.writeInt(this.player.getClanId());
            this.writeInt(this.player.getClanCrestLargeId());
            this.writeInt(this.player.getClanCrestId());
            this.writeInt(this.player.getClanPrivileges().getBitmask());
            this.writeByte(this.player.isClanLeader());
            this.writeInt(this.player.getAllyId());
            this.writeInt(this.player.getAllyCrestId());
            this.writeByte(this.player.isInMatchingRoom());
        }
        if (this.containsMask(UserInfoType.SOCIAL)) {
            this.writeShort(UserInfoType.SOCIAL.getBlockLength());
            this.writeByte(this.player.getPvpFlag());
            this.writeInt(this.player.getReputation());
            this.writeByte(this.player.isNoble());
            this.writeByte((this.player.isHero() || (this.player.isGM() && Config.GM_HERO_AURA)) ? 2 : 0);
            this.writeByte(this.player.getPledgeClass());
            this.writeInt(this.player.getPkKills());
            this.writeInt(this.player.getPvpKills());
            this.writeShort(this.player.getRecomLeft());
            this.writeShort(this.player.getRecomHave());
            this.writeInt(0);
            this.writeInt(0);
        }
        if (this.containsMask(UserInfoType.VITA_FAME)) {
            this.writeShort(UserInfoType.VITA_FAME.getBlockLength());
            this.writeInt(this.player.getVitalityPoints());
            this.writeByte(0);
            this.writeInt(this.player.getFame());
            this.writeInt(this.player.getRaidbossPoints());
            this.writeByte(0);
            this.writeByte(0);
            this.writeShort(0);
        }
        if (this.containsMask(UserInfoType.SLOTS)) {
            this.writeShort(UserInfoType.SLOTS.getBlockLength());
            this.writeByte(this.player.getInventory().getTalismanSlots());
            this.writeByte(this.player.getInventory().getBroochJewelSlots());
            this.writeByte(this.player.getTeam().getId());
            this.writeInt(0);
            if (this.player.getInventory().getAgathionSlots() > 0) {
                this.writeByte(1);
                this.writeByte(this.player.getInventory().getAgathionSlots() - 1);
            }
            else {
                this.writeByte(0);
                this.writeByte(0);
            }
            this.writeByte(this.player.getInventory().getArtifactSlots());
        }
        if (this.containsMask(UserInfoType.MOVEMENTS)) {
            this.writeShort(UserInfoType.MOVEMENTS.getBlockLength());
            this.writeByte(this.player.isInsideZone(ZoneType.WATER) ? 1 : (this.player.isFlyingMounted() ? 2 : 0));
            this.writeByte(this.player.isRunning());
        }
        if (this.containsMask(UserInfoType.COLOR)) {
            this.writeShort(UserInfoType.COLOR.getBlockLength());
            this.writeInt(this.player.getAppearance().getNameColor());
            this.writeInt(this.player.getAppearance().getTitleColor());
        }
        if (this.containsMask(UserInfoType.INVENTORY_LIMIT)) {
            this.writeShort(UserInfoType.INVENTORY_LIMIT.getBlockLength());
            this.writeInt(0);
            this.writeShort(this.player.getInventoryLimit());
            this.writeByte(0);
            this.writeInt(0);
        }
        if (this.containsMask(UserInfoType.TRUE_HERO)) {
            this.writeShort(UserInfoType.TRUE_HERO.getBlockLength());
            this.writeByte(1);
            this.writeInt(0);
            this.writeByte(0);
            this.writeByte(0);
        }
        if (this.containsMask(UserInfoType.SPIRITS)) {
            this.writeShort(UserInfoType.SPIRITS.getBlockLength());
            this.writeInt((int)this.player.getActiveElementalSpiritAttack());
            this.writeInt((int)this.player.getFireSpiritDefense());
            this.writeInt((int)this.player.getWaterSpiritDefense());
            this.writeInt((int)this.player.getWindSpiritDefense());
            this.writeInt((int)this.player.getEarthSpiritDefense());
            this.writeInt((int)this.player.getActiveElementalSpiritType());
        }
        if (this.containsMask(UserInfoType.RANKER)) {
            this.writeShort(UserInfoType.RANKER.getBlockLength());
            this.writeInt((this.player.getRank() == 1) ? 1 : ((this.player.getRankRace() == 1) ? 2 : 0));
        }
        if (this.containsMask(UserInfoType.STATS_POINTS)) {
            this.writeShort(UserInfoType.STATS_POINTS.getBlockLength());
            final PlayerStatsData statsData = this.player.getStatsData();
            this.writeShort(statsData.getPoints());
            this.writeShort(statsData.getValue(BaseStats.STR));
            this.writeShort(statsData.getValue(BaseStats.DEX));
            this.writeShort(statsData.getValue(BaseStats.CON));
            this.writeShort(statsData.getValue(BaseStats.INT));
            this.writeShort(statsData.getValue(BaseStats.WIT));
            this.writeShort(statsData.getValue(BaseStats.MEN));
        }
        if (this.containsMask(UserInfoType.STATS_ABILITIES)) {
            this.writeShort(UserInfoType.STATS_ABILITIES.getBlockLength());
            this.writeShort((short)Stat.defaultValue(this.player, Optional.empty(), Stat.STAT_STR) + this.player.getHennaValue(BaseStats.STR));
            this.writeShort((short)Stat.defaultValue(this.player, Optional.empty(), Stat.STAT_DEX) + this.player.getHennaValue(BaseStats.DEX));
            this.writeShort((short)Stat.defaultValue(this.player, Optional.empty(), Stat.STAT_CON) + this.player.getHennaValue(BaseStats.CON));
            this.writeShort((short)Stat.defaultValue(this.player, Optional.empty(), Stat.STAT_INT) + this.player.getHennaValue(BaseStats.INT));
            this.writeShort((short)Stat.defaultValue(this.player, Optional.empty(), Stat.STAT_WIT) + this.player.getHennaValue(BaseStats.WIT));
            this.writeShort((short)Stat.defaultValue(this.player, Optional.empty(), Stat.STAT_MEN) + this.player.getHennaValue(BaseStats.MEN));
            this.writeShort(1);
            this.writeShort(1);
        }
    }
    
    private int calculateRelation(final Player activeChar) {
        int relation = 0;
        final Party party = activeChar.getParty();
        final Clan clan = activeChar.getClan();
        if (party != null) {
            relation |= 0x8;
            if (party.getLeader() == this.player) {
                relation |= 0x10;
            }
        }
        if (clan != null) {
            relation |= 0x20;
            if (clan.getLeaderId() == activeChar.getObjectId()) {
                relation |= 0x40;
            }
        }
        if (activeChar.isInSiege()) {
            relation |= 0x80;
        }
        return relation;
    }
}
