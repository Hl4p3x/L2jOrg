// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.stat;

import org.l2j.gameserver.model.actor.transform.Transform;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.serverpackets.ExUserBoostStat;
import org.l2j.gameserver.model.skills.AbnormalType;
import java.util.Objects;
import org.l2j.gameserver.api.elemental.ElementalType;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.enums.PartySmallWindowUpdateType;
import org.l2j.gameserver.network.serverpackets.PartySmallWindowUpdate;
import org.l2j.gameserver.network.serverpackets.ExVitalityPointInfo;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.model.base.SubClass;
import org.l2j.gameserver.network.serverpackets.mission.ExOneDayReceiveRewardList;
import org.l2j.gameserver.network.serverpackets.ExVoteSystemInfo;
import org.l2j.gameserver.network.serverpackets.AcquireSkillList;
import org.l2j.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLevelChanged;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.data.xml.impl.LevelData;
import org.l2j.gameserver.model.actor.instance.Pet;
import java.util.Iterator;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.holders.ItemSkillHolder;
import org.l2j.gameserver.model.item.type.WeaponType;
import org.l2j.gameserver.network.serverpackets.UserInfo;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerStats extends PlayableStats
{
    public static final int MAX_VITALITY_POINTS = 140000;
    public static final int MIN_VITALITY_POINTS = 0;
    private static final int FANCY_FISHING_ROD_SKILL = 21484;
    private final AtomicInteger _talismanSlots;
    private long _startingXp;
    private boolean _cloakSlot;
    private int _vitalityPoints;
    
    public PlayerStats(final Player activeChar) {
        super(activeChar);
        this._talismanSlots = new AtomicInteger();
        this._cloakSlot = false;
        this._vitalityPoints = 0;
    }
    
    @Override
    public boolean addExp(final long value) {
        final Player activeChar = this.getCreature();
        if (!activeChar.getAccessLevel().canGainExp()) {
            return false;
        }
        if (!super.addExp(value)) {
            return false;
        }
        if (activeChar.getReputation() < 0 && (activeChar.isGM() || !activeChar.isInsideZone(ZoneType.PVP))) {
            final int karmaLost = Formulas.calculateKarmaLost(activeChar, (double)value);
            if (karmaLost > 0) {
                activeChar.setReputation(Math.min(activeChar.getReputation() + karmaLost, 0));
            }
        }
        activeChar.sendPacket(new UserInfo(activeChar, new UserInfoType[] { UserInfoType.CURRENT_HPMPCP_EXP_SP }));
        return true;
    }
    
    public void addExpAndSp(double addToExp, double addToSp, final boolean useBonuses) {
        final Player activeChar = this.getCreature();
        if (!activeChar.getAccessLevel().canGainExp()) {
            return;
        }
        final double baseExp = addToExp;
        final double baseSp = addToSp;
        double bonusExp = 1.0;
        double bonusSp = 1.0;
        if (useBonuses) {
            if (activeChar.isFishing()) {
                final Item rod = activeChar.getActiveWeaponInstance();
                if (rod != null && rod.getItemType() == WeaponType.FISHING_ROD && rod.getTemplate().getAllSkills() != null) {
                    for (final ItemSkillHolder s : rod.getTemplate().getAllSkills()) {
                        if (s.getSkill().getId() == 21484) {
                            bonusExp *= 1.5;
                            bonusSp *= 1.5;
                        }
                    }
                }
            }
            else {
                bonusExp = this.getExpBonusMultiplier();
                bonusSp = this.getSpBonusMultiplier();
            }
        }
        addToExp *= bonusExp;
        addToSp *= bonusSp;
        double ratioTakenByPlayer = 0.0;
        final Pet pet = activeChar.getPet();
        if (pet != null && GameUtils.checkIfInShortRange(Config.ALT_PARTY_RANGE, activeChar, pet, false)) {
            ratioTakenByPlayer = pet.getPetLevelData().getOwnerExpTaken() / 100.0f;
            if (ratioTakenByPlayer > 1.0) {
                ratioTakenByPlayer = 1.0;
            }
            if (!pet.isDead()) {
                pet.addExpAndSp(addToExp * (1.0 - ratioTakenByPlayer), addToSp * (1.0 - ratioTakenByPlayer));
            }
            addToExp *= ratioTakenByPlayer;
            addToSp *= ratioTakenByPlayer;
        }
        final long finalExp = Math.round(addToExp);
        final long finalSp = Math.round(addToSp);
        final boolean expAdded = this.addExp(finalExp);
        final boolean spAdded = this.addSp(finalSp);
        SystemMessage sm = null;
        if (!expAdded && spAdded) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_ACQUIRED_S1_SP);
            sm.addLong(finalSp);
        }
        else if (expAdded && !spAdded) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S1_XP);
            sm.addLong(finalExp);
        }
        else {
            sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_ACQUIRED_S1_XP_BONUS_S2_AND_S3_SP_BONUS_S4);
            sm.addLong(finalExp);
            sm.addLong(Math.round(addToExp - baseExp));
            sm.addLong(finalSp);
            sm.addLong(Math.round(addToSp - baseSp));
        }
        activeChar.sendPacket(sm);
    }
    
    @Override
    public boolean removeExpAndSp(final long addToExp, final long addToSp) {
        return this.removeExpAndSp(addToExp, addToSp, true);
    }
    
    public boolean removeExpAndSp(final long addToExp, final long addToSp, final boolean sendMessage) {
        final int level = this.getLevel();
        if (!super.removeExpAndSp(addToExp, addToSp)) {
            return false;
        }
        if (sendMessage) {
            SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOUR_XP_HAS_DECREASED_BY_S1);
            sm.addLong(addToExp);
            this.getCreature().sendPacket(sm);
            sm = SystemMessage.getSystemMessage(SystemMessageId.YOUR_SP_HAS_DECREASED_BY_S1);
            sm.addLong(addToSp);
            this.getCreature().sendPacket(sm);
            if (this.getLevel() < level) {
                this.getCreature().broadcastStatusUpdate();
            }
        }
        return true;
    }
    
    @Override
    public final boolean addLevel(final byte value) {
        if (this.getLevel() + value > LevelData.getInstance().getMaxLevel()) {
            return false;
        }
        final Player player = this.getCreature();
        final boolean levelIncreased = super.addLevel(value);
        if (levelIncreased) {
            player.broadcastPacket(new SocialAction(player.getObjectId(), 2122));
            player.setCurrentCp(this.getMaxCp());
            player.sendPacket(SystemMessageId.YOUR_LEVEL_HAS_INCREASED);
            player.notifyFriends(2);
        }
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerLevelChanged(player, this.getLevel() - value, this.getLevel()), player);
        player.rewardSkills();
        if (player.getClan() != null) {
            player.getClan().updateClanMember(player);
            player.getClan().broadcastToOnlineMembers(new PledgeShowMemberListUpdate(player));
        }
        if (player.isInParty()) {
            player.getParty().recalculatePartyLevel();
        }
        player.getTransformation().ifPresent(transform -> transform.onLevelUp(player));
        final Pet sPet = player.getPet();
        if (sPet != null) {
            final Pet pet = sPet;
            if (pet.getPetData().isSynchLevel() && pet.getLevel() != this.getLevel()) {
                final byte availableLevel = (byte)Math.min(pet.getPetData().getMaxLevel(), this.getLevel());
                pet.getStats().setLevel(availableLevel);
                pet.getStats().getExpForLevel(availableLevel);
                pet.setCurrentHp(pet.getMaxHp());
                pet.setCurrentMp(pet.getMaxMp());
                pet.broadcastPacket(new SocialAction(player.getObjectId(), 2122));
                pet.updateAndBroadcastStatus(1);
            }
        }
        if (this.getLevel() >= 40) {
            player.initElementalSpirits();
        }
        player.updateCharacteristicPoints();
        player.broadcastStatusUpdate();
        player.refreshOverloaded(true);
        player.sendPacket(new UserInfo(player));
        player.sendPacket(new AcquireSkillList(player));
        player.sendPacket(new ExVoteSystemInfo(player));
        player.sendPacket(new ExOneDayReceiveRewardList(player, true));
        return levelIncreased;
    }
    
    @Override
    public boolean addSp(final long value) {
        if (!super.addSp(value)) {
            return false;
        }
        this.getCreature().broadcastUserInfo(UserInfoType.CURRENT_HPMPCP_EXP_SP);
        return true;
    }
    
    @Override
    public final long getExpForLevel(final int level) {
        return LevelData.getInstance().getExpForLevel(level);
    }
    
    @Override
    public final Player getCreature() {
        return (Player)super.getCreature();
    }
    
    @Override
    public final long getExp() {
        if (this.getCreature().isSubClassActive()) {
            return ((SubClass)this.getCreature().getSubClasses().get(this.getCreature().getClassIndex())).getExp();
        }
        return super.getExp();
    }
    
    @Override
    public final void setExp(final long value) {
        if (this.getCreature().isSubClassActive()) {
            ((SubClass)this.getCreature().getSubClasses().get(this.getCreature().getClassIndex())).setExp(value);
        }
        else {
            super.setExp(value);
        }
    }
    
    public final long getBaseExp() {
        return super.getExp();
    }
    
    public long getStartingExp() {
        return this._startingXp;
    }
    
    public void setStartingExp(final long value) {
        if (Config.BOTREPORT_ENABLE) {
            this._startingXp = value;
        }
    }
    
    public int getTalismanSlots() {
        return this._talismanSlots.get();
    }
    
    public void addTalismanSlots(final int count) {
        this._talismanSlots.addAndGet(count);
    }
    
    public boolean canEquipCloak() {
        return this._cloakSlot;
    }
    
    public void setCloakSlotStatus(final boolean cloakSlot) {
        this._cloakSlot = cloakSlot;
    }
    
    @Override
    public final byte getLevel() {
        if (this.getCreature().isDualClassActive()) {
            return this.getCreature().getDualClass().getLevel();
        }
        if (this.getCreature().isSubClassActive()) {
            return ((SubClass)this.getCreature().getSubClasses().get(this.getCreature().getClassIndex())).getLevel();
        }
        return super.getLevel();
    }
    
    @Override
    public final void setLevel(byte value) {
        if (value > LevelData.getInstance().getMaxLevel()) {
            value = LevelData.getInstance().getMaxLevel();
        }
        if (this.getCreature().isSubClassActive()) {
            ((SubClass)this.getCreature().getSubClasses().get(this.getCreature().getClassIndex())).setLevel(value);
        }
        else {
            super.setLevel(value);
        }
    }
    
    public final byte getBaseLevel() {
        return super.getLevel();
    }
    
    @Override
    public final long getSp() {
        if (this.getCreature().isSubClassActive()) {
            return ((SubClass)this.getCreature().getSubClasses().get(this.getCreature().getClassIndex())).getSp();
        }
        return super.getSp();
    }
    
    @Override
    public final void setSp(final long value) {
        if (this.getCreature().isSubClassActive()) {
            ((SubClass)this.getCreature().getSubClasses().get(this.getCreature().getClassIndex())).setSp(value);
        }
        else {
            super.setSp(value);
        }
    }
    
    public final long getBaseSp() {
        return super.getSp();
    }
    
    public int getVitalityPoints() {
        if (this.getCreature().isSubClassActive()) {
            return Math.min(140000, ((SubClass)this.getCreature().getSubClasses().get(this.getCreature().getClassIndex())).getVitalityPoints());
        }
        return Math.min(Math.max(this._vitalityPoints, 0), 140000);
    }
    
    public void setVitalityPoints(final int value) {
        if (this.getCreature().isSubClassActive()) {
            ((SubClass)this.getCreature().getSubClasses().get(this.getCreature().getClassIndex())).setVitalityPoints(value);
            return;
        }
        this._vitalityPoints = Math.min(Math.max(value, 0), 140000);
    }
    
    public int getBaseVitalityPoints() {
        return Math.min(Math.max(this._vitalityPoints, 0), 140000);
    }
    
    public double getVitalityExpBonus() {
        return (this.getVitalityPoints() > 0) ? this.getValue(Stat.VITALITY_EXP_RATE, Config.RATE_VITALITY_EXP_MULTIPLIER) : 1.0;
    }
    
    public void setVitalityPoints(int points, final boolean quiet) {
        points = Math.min(Math.max(points, 0), 140000);
        if (points == this.getVitalityPoints()) {
            return;
        }
        if (!quiet) {
            if (points < this.getVitalityPoints()) {
                this.getCreature().sendPacket(SystemMessageId.YOUR_VITALITY_HAS_DECREASED);
            }
            else {
                this.getCreature().sendPacket(SystemMessageId.YOUR_VITALITY_HAS_INCREASED);
            }
        }
        this.setVitalityPoints(points);
        if (points == 0) {
            this.getCreature().sendPacket(SystemMessageId.YOUR_VITALITY_IS_FULLY_EXHAUSTED);
        }
        else if (points == 140000) {
            this.getCreature().sendPacket(SystemMessageId.YOUR_VITALITY_IS_AT_MAXIMUM);
        }
        final Player player = this.getCreature();
        player.sendPacket(new ExVitalityPointInfo(this.getVitalityPoints()));
        player.broadcastUserInfo(UserInfoType.VITA_FAME);
        final Party party = player.getParty();
        if (party != null) {
            final PartySmallWindowUpdate partyWindow = new PartySmallWindowUpdate(player, false);
            partyWindow.addComponentType(PartySmallWindowUpdateType.VITALITY_POINTS);
            party.broadcastToPartyMembers(player, partyWindow);
        }
    }
    
    public synchronized void updateVitalityPoints(int points, final boolean useRates, final boolean quiet) {
        if (points == 0 || !Config.ENABLE_VITALITY) {
            return;
        }
        if (useRates) {
            if (this.getCreature().isLucky()) {
                return;
            }
            if (points < 0) {
                final int stat = (int)this.getValue(Stat.VITALITY_CONSUME_RATE, 1.0);
                if (stat == 0) {
                    return;
                }
                if (stat < 0) {
                    points = -points;
                }
            }
            if (points > 0) {
                points *= (int)Config.RATE_VITALITY_GAIN;
            }
            else {
                points *= (int)Config.RATE_VITALITY_LOST;
            }
        }
        if (points > 0) {
            points = Math.min(this.getVitalityPoints() + points, 140000);
        }
        else {
            points = Math.max(this.getVitalityPoints() + points, 0);
        }
        if (Math.abs(points - this.getVitalityPoints()) <= 1.0E-6) {
            return;
        }
        this.setVitalityPoints(points);
    }
    
    public double getExpBonusMultiplier() {
        double bonus = 1.0;
        double vitality = 1.0;
        double bonusExp = 1.0;
        vitality = this.getVitalityExpBonus();
        bonusExp = this.getValue(Stat.BONUS_EXP, 1.0);
        if (vitality > 1.0) {
            bonus += vitality - 1.0;
        }
        if (bonusExp > 1.0) {
            bonus += bonusExp - 1.0;
        }
        bonus = Math.max(bonus, 1.0);
        if (Config.MAX_BONUS_EXP > 0.0) {
            bonus = Math.min(bonus, Config.MAX_BONUS_EXP);
        }
        return bonus;
    }
    
    public double getSpBonusMultiplier() {
        double bonus = 1.0;
        double vitality = 1.0;
        double bonusSp = 1.0;
        vitality = this.getVitalityExpBonus();
        bonusSp = 1.0 + this.getValue(Stat.BONUS_SP, 0.0) / 100.0;
        if (vitality > 1.0) {
            bonus += vitality - 1.0;
        }
        if (bonusSp > 1.0) {
            bonus += bonusSp - 1.0;
        }
        bonus = Math.max(bonus, 1.0);
        if (Config.MAX_BONUS_SP > 0.0) {
            bonus = Math.min(bonus, Config.MAX_BONUS_SP);
        }
        return bonus;
    }
    
    public int getBroochJewelSlots() {
        return (int)this.getValue(Stat.BROOCH_JEWELS, 0.0);
    }
    
    public int getAgathionSlots() {
        return (int)this.getValue(Stat.AGATHION_SLOTS, 0.0);
    }
    
    public int getArtifactSlots() {
        return (int)this.getValue(Stat.ARTIFACT_SLOTS, 0.0);
    }
    
    public double getElementalSpiritXpBonus() {
        return this.getValue(Stat.ELEMENTAL_SPIRIT_BONUS_XP, 1.0);
    }
    
    public double getElementalSpiritPower(final ElementalType type, final double base) {
        return Objects.isNull(type) ? 0.0 : this.getValue(type.getAttackStat(), base);
    }
    
    public double getElementalSpiritCriticalRate(final int base) {
        return this.getValue(Stat.ELEMENTAL_SPIRIT_CRITICAL_RATE, base);
    }
    
    public double getElementalSpiritCriticalDamage(final double base) {
        return this.getValue(Stat.ELEMENTAL_SPIRIT_CRITICAL_DAMAGE, base);
    }
    
    public double getElementalSpiritDefense(final ElementalType type, final double base) {
        return Objects.isNull(type) ? 0.0 : this.getValue(type.getDefenseStat(), base);
    }
    
    public double getEnchantRateBonus() {
        return this.getValue(Stat.ENCHANT_RATE_BONUS, 0.0);
    }
    
    @Override
    protected void onRecalculateStats(final boolean broadcast) {
        super.onRecalculateStats(broadcast);
        final Player player = this.getCreature();
        if (player.hasAbnormalType(AbnormalType.ABILITY_CHANGE) && player.hasServitors()) {
            player.getServitors().values().forEach(servitor -> servitor.getStats().recalculateStats(broadcast));
        }
        player.sendPacket(new ExUserBoostStat(ExUserBoostStat.BoostStatType.STAT, (short)(Math.round(this.getExpBonusMultiplier() * 100.0) - 100L)));
    }
}
